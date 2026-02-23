/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.templateparser;

/**
 * The TemplateStringParser interface defines a method for parsing a template string.
 */
public interface TemplateStringParser {
  public String processTemplate(String templateString, String json);
}
