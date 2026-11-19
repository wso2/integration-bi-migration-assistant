/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package describe.mule;

import common.BallerinaModel.OnFailClause;
import common.BallerinaModel.Statement;
import describe.model.DescriptionModel.Migration;
import describe.model.DescriptionModel.MigrationReason;
import describe.model.DescriptionModel.MigrationStatus;
import describe.model.DescriptionModel.ReasonKind;
import mule.common.MigrationMetrics;
import mule.v4.Context;
import mule.v4.converter.MuleConfigConverter.ConversionResult.WorkerStatementResult;
import mule.v4.model.MuleModel.MuleRecord;
import mule.v4.model.MuleModel.UnsupportedBlock;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gives each converted element a migration status. Children finish before their parent and claim their own
 * problems first; the parent keeps only what is left.
 */
final class ConversionRecorder implements Context.ConversionObserver {

    private static final Pattern FIRST_TAG = Pattern.compile("<([^\\s/>]+)");
    private static final String MANUAL_WORK_MARKER = "TODO";

    private final MigrationMetrics<?> metrics;
    private final Deque<Frame> frames = new ArrayDeque<>();
    // Identity maps: records and statements compare by value, so equal ones would share an entry
    private final Map<MuleRecord, Migration> migrations = new IdentityHashMap<>();
    private final Map<Statement, Boolean> claimedTodos = new IdentityHashMap<>();

    ConversionRecorder(MigrationMetrics<?> metrics) {
        this.metrics = metrics;
    }

    Optional<Migration> migrationOf(MuleRecord muleRecord) {
        return Optional.ofNullable(migrations.get(muleRecord));
    }

    @Override
    public void blockStarted(MuleRecord muleRecord) {
        frames.push(new Frame(muleRecord, metrics.failedBlocks.size(),
                metrics.dwConversionStats.failedDWExpressions.size()));
    }

    @Override
    public void blockConverted(MuleRecord muleRecord, WorkerStatementResult result) {
        Frame frame = finish(muleRecord);
        List<MigrationReason> reasons = new ArrayList<>();

        int ownFailedBlocks = frame.failedBlocksAdded - frame.childFailedBlocks;
        if (ownFailedBlocks > 0 && frame.childFailedBlocks == 0) {
            for (String failedBlock : metrics.failedBlocks.subList(frame.failedBlocksBefore,
                    frame.failedBlocksBefore + frame.failedBlocksAdded)) {
                addReason(reasons, ReasonKind.UNSUPPORTED_ELEMENT, firstTag(failedBlock));
            }
        } else if (ownFailedBlocks > 0) {
            addReason(reasons, ReasonKind.UNSUPPORTED_ELEMENT, ownFailedBlocks + " unsupported element(s)");
        }

        int ownDataWeave = frame.dataWeaveAdded - frame.childDataWeave;
        if (ownDataWeave > 0) {
            addReason(reasons, ReasonKind.DATAWEAVE, ownDataWeave + " DataWeave expression(s) need manual conversion");
        }

        List<Statement> todos = new ArrayList<>();
        collectTodos(result.statements(), todos);
        for (Statement.NamedWorkerDecl worker : result.workers()) {
            collectTodos(worker.statements(), todos);
        }
        for (Statement todo : todos) {
            if (!claimedTodos.containsKey(todo)) {
                claimedTodos.put(todo, true);
                addReason(reasons, ReasonKind.TODO, todoText(todo));
            }
        }

        MigrationStatus status;
        if (muleRecord instanceof UnsupportedBlock && ownFailedBlocks > 0) {
            status = MigrationStatus.NOT_CONVERTED;
        } else if (reasons.isEmpty()) {
            status = MigrationStatus.CONVERTED;
        } else {
            status = MigrationStatus.PARTLY_CONVERTED;
        }
        migrations.put(muleRecord, new Migration(status, reasons));
    }

    @Override
    public void blockFailed(MuleRecord muleRecord) {
        finish(muleRecord);
        // convertTopLevelMuleBlocks catches the exception and drops the block, so no other signal fires
        migrations.put(muleRecord, new Migration(MigrationStatus.NOT_CONVERTED, List.of(new MigrationReason(
                ReasonKind.CONVERSION_ERROR, "The converter failed on this element and left it out"))));
    }

    private Frame finish(MuleRecord muleRecord) {
        Frame frame = frames.pop();
        assert frame.muleRecord == muleRecord : "conversion events are not nested";
        frame.failedBlocksAdded = metrics.failedBlocks.size() - frame.failedBlocksBefore;
        frame.dataWeaveAdded = metrics.dwConversionStats.failedDWExpressions.size() - frame.dataWeaveBefore;
        Frame parent = frames.peek();
        if (parent != null) {
            parent.childFailedBlocks += frame.failedBlocksAdded;
            parent.childDataWeave += frame.dataWeaveAdded;
        }
        return frame;
    }

    // The converter's to-do notes: a Comment statement, or a one-line comment written as a BallerinaStatement.
    // The multi-line notes of unsupported elements and DataWeave are counted by the other two signals instead.
    private static void collectTodos(List<Statement> statements, List<Statement> todos) {
        for (Statement statement : statements) {
            if (statement instanceof Statement.Comment comment) {
                if (comment.comment().strip().startsWith(MANUAL_WORK_MARKER)) {
                    todos.add(statement);
                }
            } else if (statement instanceof Statement.BallerinaStatement text) {
                String stripped = text.stmt().strip();
                if (stripped.startsWith("//") && stripped.substring(2).strip().startsWith(MANUAL_WORK_MARKER)
                        && !stripped.contains("\n")) {
                    todos.add(statement);
                }
            } else if (statement instanceof Statement.IfElseStatement ifElse) {
                collectTodos(ifElse.ifBody(), todos);
                for (Statement.ElseIfClause clause : ifElse.elseIfClauses()) {
                    collectTodos(clause.elseIfBody(), todos);
                }
                collectTodos(ifElse.elseBody(), todos);
            } else if (statement instanceof Statement.DoStatement doStatement) {
                collectTodos(doStatement.doBody(), todos);
                if (doStatement.onFailClause().isPresent()) {
                    OnFailClause onFail = doStatement.onFailClause().get();
                    collectTodos(onFail.onFailBody(), todos);
                }
            } else if (statement instanceof Statement.ForeachStatement foreach) {
                collectTodos(foreach.body(), todos);
            } else if (statement instanceof Statement.NamedWorkerDecl worker) {
                collectTodos(worker.statements(), todos);
            } else if (statement instanceof Statement.ForkStatement fork) {
                for (Statement.NamedWorkerDecl worker : fork.workers()) {
                    collectTodos(worker.statements(), todos);
                }
            }
        }
    }

    private static String todoText(Statement todo) {
        if (todo instanceof Statement.Comment comment) {
            return comment.comment().strip();
        }
        return ((Statement.BallerinaStatement) todo).stmt().strip().substring(2).strip();
    }

    private static String firstTag(String xmlBlock) {
        Matcher matcher = FIRST_TAG.matcher(xmlBlock);
        return matcher.find() ? matcher.group(1) : "unsupported element";
    }

    private static void addReason(List<MigrationReason> reasons, ReasonKind kind, String text) {
        MigrationReason reason = new MigrationReason(kind, text);
        if (!reasons.contains(reason)) {
            reasons.add(reason);
        }
    }

    private static final class Frame {
        private final MuleRecord muleRecord;
        private final int failedBlocksBefore;
        private final int dataWeaveBefore;
        private int failedBlocksAdded;
        private int dataWeaveAdded;
        private int childFailedBlocks;
        private int childDataWeave;

        private Frame(MuleRecord muleRecord, int failedBlocksBefore, int dataWeaveBefore) {
            this.muleRecord = muleRecord;
            this.failedBlocksBefore = failedBlocksBefore;
            this.dataWeaveBefore = dataWeaveBefore;
        }
    }
}
