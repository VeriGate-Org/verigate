/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.utility;

import java.io.FileReader;
import java.io.IOException;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Utility class to interrogate Maven pom files. The initial use case is to encode version
 * information for non-Maven dependencies with other versions as properties in order to keep
 * them all together (easier to discover).
 * This is not intended for production runtime use, but rather compile / test time only.
 */
public class MavenPom {

  private static final String POM_FILE_NAME = "pom.xml";

  /**
   * Retrieve a property by name from Maven pom files. This will search for the property in
   * all Maven pom files recursively (from the specific module walking the tree up to the root).
   */
  public static String getProperty(String propertyName) {
    return findPropertyRecursive(propertyName, POM_FILE_NAME);
  }

  private static String findPropertyRecursive(String propertyName, String pomFilePath) {
    try {
      final String propertyValue = getPropertyFromPomFile(propertyName, pomFilePath);
      if (propertyValue != null) {
        return propertyValue;
      }
      return findPropertyRecursive(
          propertyName, getParentPomRelativePath(pomFilePath) + pomFilePath);
    } catch (Exception e) {
      throw new RuntimeException("Failed to find [" + propertyName + "] in Maven pom tree", e);
    }
  }

  private static String getPropertyFromPomFile(String propertyName, String pomFilePath)
      throws IOException, XmlPullParserException {
    Model pomModel = loadPomModel(pomFilePath);
    return pomModel.getProperties().getProperty(propertyName);
  }

  private static String getParentPomRelativePath(String pomFilePath)
      throws IOException, XmlPullParserException {
    Model pomModel = loadPomModel(pomFilePath);
    Parent parent = pomModel.getParent();
    if (parent == null) {
      throw new RuntimeException("Reached root of Maven pom tree at: [" + pomFilePath + "]");
    }
    final String parentRelativePath = parent.getRelativePath();
    if (parentRelativePath == null) {
      // Defining a relative path to the parent pom is optional. If not defined, default to
      // one level up.
      return "../";
    }
    // But if a relative path is defined, use that. We just remove the pom name, because we know
    // what it is and are only interested in the relative path.
    return parentRelativePath.replace(POM_FILE_NAME, "");
  }

  private static Model loadPomModel(String pomFilePath) throws IOException, XmlPullParserException {
    FileReader pomFileReader = new FileReader(pomFilePath);
    MavenXpp3Reader reader = new MavenXpp3Reader();
    return reader.read(pomFileReader);
  }
}
