package tibco;

import java.util.Collection;

public sealed interface Process permits TibcoModel.Process5, TibcoModel.Process6 {

    String name();

    Collection<TibcoModel.NameSpace> nameSpaces();
}
