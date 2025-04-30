package blocks.mule3;

import blocks.AbstractBlockTest;
import org.testng.annotations.Test;

public class HttpRequestTest extends AbstractBlockTest {

    @Test
    public void testBasicHttpRequest() {
        testMule3ToBal("http-request/basic_http_request.xml", "http-request/basic_http_request.bal");
    }

    @Test
    public void testHttpRequestWithPathHavingSpecialCharacters() {
        testMule3ToBal("http-request/http_request_path_with_special_characters.xml",
                "http-request/http_request_path_with_special_characters.bal");
    }

    @Test
    public void testHttpRequestWithHttpSource() {
        testMule3ToBal("http-request/http_request_with_http_source.xml",
                "http-request/http_request_with_http_source.bal");
    }
}
