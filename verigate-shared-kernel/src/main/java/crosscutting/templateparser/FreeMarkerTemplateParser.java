/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.templateparser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import java.io.StringWriter;
import java.util.Map;

/**
 * The FreeMarkerTemplateParser class is the default implementation of the
 * TemplateStringParser interface.
 */
public class FreeMarkerTemplateParser implements TemplateStringParser {
  private Configuration cfg;

  /**
   * Instantiates a new FreeMarkerTemplateParser.
   */
  public FreeMarkerTemplateParser() {
    cfg = new Configuration(Configuration.VERSION_2_3_33);
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(true);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
  }

  @Override
  public String processTemplate(String templateString, String json) {
    try {
      Template template = new Template("temp", templateString, cfg);
      ObjectMapper mapper = new ObjectMapper();
      Map<String, Object> valuesMap = mapper.readValue(json, new TypeReference<>() {});
      StringWriter out = new StringWriter();
      template.process(valuesMap, out);
      return out.toString();
    } catch (Exception e) {
      throw new IllegalArgumentException("An error while processing the template", e);
    }
  }
}
