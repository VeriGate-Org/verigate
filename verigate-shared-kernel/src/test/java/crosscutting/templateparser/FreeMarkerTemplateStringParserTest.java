/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.templateparser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

/**
 * The FreeMarkerTemplateStringParserTest class.
 */
public class FreeMarkerTemplateStringParserTest {

  @Test
  public void parseTest() throws IOException {
    String json =
        new String(
            Files.readAllBytes(
                Paths.get("src/test/resources/templateparser/TemplateStringParserTest.json")));

    var parser = new FreeMarkerTemplateParser();

    var templateString =
        "Hello, ${foo}! Nested foo: ${fooObj.foo}. Foo ${fooArray[0].foo} in a list";

    var result = parser.processTemplate(templateString, json);

    System.out.println("result: " + result);

    assertEquals("Hello, bar1! Nested foo: bar2. Foo bar3 in a list", result);
  }

  @Test
  public void parseConditionalSectionTest() throws IOException {
    String json =
        new String(
            Files.readAllBytes(
                Paths.get("src/test/resources/templateparser/TemplateStringParserTest.json")));

    var parser = new FreeMarkerTemplateParser();

    var templateString =
        "Hello, <#if foo == \"bar1\">Conditional if text</#if>! Conditional foo: <#if foo =="
            + " \"bar2\">Conditional not text</#if>";

    var result = parser.processTemplate(templateString, json);

    assertEquals("Hello, Conditional if text! Conditional foo: ", result);
  }

  @Test
  public void parseConditionalSectionTest2() throws IOException {
    String json =
        new String(
            Files.readAllBytes(
                Paths.get("src/test/resources/templateparser/TemplateStringParserTest.json")));

    var parser = new FreeMarkerTemplateParser();

    var templateString = "Hello, <#if (five > 4)>conditional if bigger than 4</#if>!";

    var result = parser.processTemplate(templateString, json);

    assertEquals("Hello, conditional if bigger than 4!", result);
  }

  @Test
  public void parseArrayLoopTest() throws IOException {
    String json =
        new String(
            Files.readAllBytes(
                Paths.get("src/test/resources/templateparser/TemplateStringParserTest.json")));

    var parser = new FreeMarkerTemplateParser();

    var templateString = "Hello, <#list fooArray as item>${item.foo}</#list>!";

    var result = parser.processTemplate(templateString, json);

    assertEquals("Hello, bar3bar4bar5!", result);
  }
}
