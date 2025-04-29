package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HttpListenerTest extends AbstractBlockTest {

    @Test
    public void testBasicHttpListener() {
        testMule3ToBal("http-listener/basic_http_listener.xml", "http-listener/basic_http_listener.bal");
    }

    @Test
    public void testEmptyAbsolutePath() {
        testMule3ToBal("http-listener/empty_absolute_path.xml", "http-listener/empty_absolute_path.bal");
    }

    @Test
    public void testEmptyResourcePath() {
        testMule3ToBal("http-listener/empty_resource_path.xml", "http-listener/empty_resource_path.bal");
    }

    @Test
    public void testEmptyAbsoluteAndResourcePaths() {
        testMule3ToBal("http-listener/empty_absolute_and_resource_paths.xml",
                "http-listener/empty_absolute_and_resource_paths.bal");
    }

    @Test
    public void testSpecialCharactersInResourcePath() {
        testMule3ToBal("http-listener/special_characters_in_resource_path.xml",
                "http-listener/special_characters_in_resource_path.bal");
    }

    @Test
    public void testResourcePathParams() {
        testMule3ToBal("http-listener/resource_path_params.xml", "http-listener/resource_path_params.bal");
    }

    @Test(dataProvider = "httpMethodsTestData")
    public void testAllowedResourceMethods(String inputFile, String outputFile) {
        testMule3ToBal(inputFile, outputFile);
    }

    @DataProvider(name = "httpMethodsTestData")
    public Object[][] httpMethodsTestData() {
        return new Object[][] {
                // test GET
                {"http-listener/allowed_resource_get_method.xml", "http-listener/allowed_resource_get_method.bal"},
                // test GET, POST, DELETE
                {"http-listener/allowed_resource_multiple_methods.xml",
                        "http-listener/allowed_resource_multiple_methods.bal"},
                // test default
                {"http-listener/allowed_resource_default_methods.xml",
                        "http-listener/allowed_resource_default_methods.bal"}
        };
    }
}
