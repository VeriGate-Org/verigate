/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.ai.common.infrastructure.prompts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Utility class for loading and interpolating prompt templates from classpath resources.
 */
public final class PromptTemplates {

  private PromptTemplates() {
  }

  /**
   * Loads a prompt template from the classpath and interpolates variables.
   *
   * @param templatePath the classpath path to the template (e.g., "prompts/negative-news.txt")
   * @param variables map of variable names to their values (e.g., "subjectName" -> "John Doe")
   * @return the interpolated prompt string
   */
  public static String load(String templatePath, Map<String, String> variables) {
    String template = loadRaw(templatePath);
    return interpolate(template, variables);
  }

  /**
   * Loads a raw prompt template from the classpath without interpolation.
   *
   * @param templatePath the classpath path to the template
   * @return the raw template string
   */
  public static String loadRaw(String templatePath) {
    try (InputStream is = PromptTemplates.class.getClassLoader()
        .getResourceAsStream(templatePath)) {
      if (is == null) {
        throw new RuntimeException("Prompt template not found: " + templatePath);
      }
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load prompt template: " + templatePath, e);
    }
  }

  /**
   * Interpolates variables in a template string. Variables are in the format ${variableName}.
   *
   * @param template the template string
   * @param variables map of variable names to values
   * @return the interpolated string
   */
  public static String interpolate(String template, Map<String, String> variables) {
    String result = template;
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      result = result.replace("${" + entry.getKey() + "}", entry.getValue());
    }
    return result;
  }
}
