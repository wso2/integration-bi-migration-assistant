package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class DatabaseTest extends AbstractBlockTest {

    @Test
    public void testBasicDbSelect() {
        testMule3ToBal("database/basic_db_select.xml", "database/basic_db_select.bal");
    }

    @Test
    public void testDbSelectQueryFromTemplate() {
        testMule3ToBal("database/db_select_query_from_template.xml", "database/db_select_query_from_template.bal");
    }
}
