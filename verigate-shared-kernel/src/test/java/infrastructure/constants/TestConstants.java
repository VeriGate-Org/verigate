/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.constants;

import infrastructure.utility.MavenPom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.utility.DockerImageName;

/**
 * Convenience constants for test cases.
 */
public class TestConstants {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestConstants.class);

  /**
   * The system property which will be used to look up the Localstack docker image version to
   * use by default. This is configured in Maven's pom file.
   */
  private static final String LOCALSTACK_DOCKER_IMAGE_VERSION_PROPERTY =
      "localstack.docker-image.version";

  /**
   * The default Localstack docker image version to use for testing.
   * This is lazy loaded by {@link #getLocalstackDefaultDockerImage()}
   */
  private static DockerImageName LOCALSTACK_DEFAULT_DOCKER_IMAGE = null;

  /**
   * Retrieve the default docker image to use for Localstack containers.
   * Instead of hard-coding the version, it is retrieved from a Maven property in order to keep
   * version configuration centralized.
   * The logic supports two mechanisms to retrieve the value of the Maven property:
   * 1. Primary and preferred: A system property lookup. The Maven build plugin injects the
   *    property as a system property for quick lookups.
   * 2. Fall-back: In cases where Maven is not controlling the test runtime (e.g. direct IDE
   *    test runs), the property will be looked up by manually parsing Maven pom files.
   */
  public static DockerImageName getLocalstackDefaultDockerImage() {
    if (LOCALSTACK_DEFAULT_DOCKER_IMAGE == null) {
      String dockerImageVersion = System.getProperty(LOCALSTACK_DOCKER_IMAGE_VERSION_PROPERTY);
      LOGGER.info(
          "Resolved Localstack docker image version from system property ({}): {}",
          LOCALSTACK_DOCKER_IMAGE_VERSION_PROPERTY,
          dockerImageVersion);
      if (dockerImageVersion == null) {
        LOGGER.warn(
            "Failed to find Localstack docker image version via system property ({}), "
                + "falling back to Maven pom parsing",
            LOCALSTACK_DOCKER_IMAGE_VERSION_PROPERTY);
        dockerImageVersion = MavenPom.getProperty(LOCALSTACK_DOCKER_IMAGE_VERSION_PROPERTY);
        LOGGER.info(
            "Resolved Localstack docker image version from Maven pom property ({}): {}",
            LOCALSTACK_DOCKER_IMAGE_VERSION_PROPERTY,
            dockerImageVersion);
      }
      LOCALSTACK_DEFAULT_DOCKER_IMAGE =
          DockerImageName.parse("localstack/localstack:" + dockerImageVersion);
    }
    return LOCALSTACK_DEFAULT_DOCKER_IMAGE;
  }
}
