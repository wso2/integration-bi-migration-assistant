package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class DatabaseConnectorTest extends AbstractBlockTest {

    @Test
    public void testBasicDbSelect() {
        testMule3ToBal("database-connector/basic_db_select.xml", "database-connector/basic_db_select.bal");
    }

    @Test
    public void testDbSelectQueryFromTemplate() {
        testMule3ToBal("database-connector/db_select_query_from_template.xml",
                "database-connector/db_select_query_from_template.bal");
    }

    @Test
    public void testOracleDbSelect() {
        testMule3ToBal("database-connector/oracle_db_select.xml", "database-connector/oracle_db_select.bal");
    }
}
