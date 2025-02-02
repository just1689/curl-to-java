package splitter;

import codeexecutor.CodeExecutor;
import datastructures.ComponentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import testgenerator.JsonToTests;


import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CurlToComponentsTest {

    private static CodeExecutor codeExecutor;
    @BeforeAll
    public static void beforeAll() {
        codeExecutor =  new CodeExecutor();
    }

    @Test
    public void extractGetComponentsTest() throws Exception {
        String curl = "curl -d \"param1=value1&param2=value2&param3=http://test.com\" " +
                "-H 'Accept-Language: en-US,en;q=0.5' " +
                "-H \"Content-Type: application/x-www-form-urlencoded\" " +
                "-H 'Referer: https://www.google.com/' " +
                "-X GET " +
                "https://jsonplaceholder.typicode.com/users";

        Map<ComponentType, List<String>> componentList = CurlToComponents.extractComponents(curl);

        List<String> requestTypes = componentList.get(ComponentType.REQUEST_TYPE);
        assertEquals(1, requestTypes.size());
        assertEquals("GET", requestTypes.get(0));

        List<String> urls = componentList.get(ComponentType.URL);
        assertEquals(1, urls.size());
        assertEquals("https://jsonplaceholder.typicode.com/users", urls.get(0));

        List<String> headers = componentList.get(ComponentType.HEADER);
        assertEquals(3, headers.size());

        String restTemplateBlock = String.format(
                        "HttpEntity<String> requestEntity = new HttpEntity<String>(\"\");\n" +
                        "ResponseEntity<String> responseEntity = restTemplate.exchange(\"%s\", HttpMethod.%s, requestEntity, String.class);\n",
                urls.get(0), requestTypes.get(0));


        String restResponse = codeExecutor.runCode("GeneratedTestCode", codeExecutor.getContainingClass(),restTemplateBlock, "String", "responseEntity.getBody()");
        String tests = JsonToTests.jsonToTests(restResponse);

        String jsonNodeConversionBlock = "final JsonNode jsonNode = mapper.readValue(responseEntity.getBody(), JsonNode.class);\n";
        String fullCodeBlockWithTests =
                        restTemplateBlock +
                        jsonNodeConversionBlock +
                        tests +
                        "boolean success = true;\n";

        String output = codeExecutor.runCode("GeneratedTestCode", codeExecutor.getContainingClass(), fullCodeBlockWithTests, "boolean", "success");
        assertEquals("true",  output);

    }

    @Test
    public void extractPOSTComponentsTest() throws Exception {
        Map<ComponentType, List<String>> componentList = CurlToComponents.extractComponents(
                "curl -d \"param1=value1&param2=value2&param3=http://test.com\" " +
                        "-H 'Accept-Language: en-US,en;q=0.5' " +
                        "-H \"Content-Type: application/x-www-form-urlencoded\" " +
                        "-H 'Referer: https://www.google.com/' " +
                        "-X POST " +
                        "https://jsonplaceholder.typicode.com/users");

        List<String> requestTypes = componentList.get(ComponentType.REQUEST_TYPE);
        assertEquals(1, requestTypes.size());
        assertEquals("POST", requestTypes.get(0));

        List<String> headers = componentList.get(ComponentType.HEADER);
        assertEquals(3, headers.size());

        List<String> dataList = componentList.get(ComponentType.DATA);
        assertEquals(1, dataList.size());
        assertEquals("param1=value1&param2=value2&param3=http://test.com", dataList.get(0));
    }

    @Test
    public void enclosingQuoteTest() throws Exception {
        char enclosingQuote = CurlToComponents.getEnclosingQuote(
                "curl -d \"param1=value1&param2=value2&param3=http://test.com\" ' \" -H 'Referer: https://www.google.com/' -X POST https://jsonplaceholder.typicode.com/users",
                65);

        assertEquals('\'', enclosingQuote);
    }
}