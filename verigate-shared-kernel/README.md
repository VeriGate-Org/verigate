# VeriGate Shared Kernel

This repository contains shared code and utilities used across the VeriGate Services.

## Publishing a New Package

### Prerequisites
- JDK 21
- Maven
- GitHub account with write access to this repository
- GitHub Personal Access Token with `packages:write` permission

### Steps to Publish

1. **Update Version**
   ```bash
   # Remove SNAPSHOT from version for release
   mvn versions:set -DremoveSnapshot
   
   # For next development version
   mvn versions:set -DnewVersion=X.Y.Z-SNAPSHOT
   ```

2. **Configure GitHub Authentication**
   - Create `~/.m2/settings.xml` if it doesn't exist:
   ```xml
   <settings>
     <servers>
       <server>
         <id>github</id>
         <username>YOUR_GITHUB_USERNAME</username>
         <password>YOUR_GITHUB_TOKEN</password>
       </server>
     </servers>
   </settings>
   ```

3. **Build and Test**
   ```bash
   mvn clean install
   ```

4. **Publish to GitHub Packages**
   ```bash
   mvn deploy
   ```

### Release Process

1. **Create Release in Github**
   - Create the Release in Github to Kick-off the Publishing of the Package
   - Make sure you name your Release according to the Version Number in the pom.xml file

### Version Naming Convention
- Release versions: `X.Y.Z` (e.g., `1.0.0`)
- Development versions: `X.Y.Z-SNAPSHOT` (e.g., `1.0.1-SNAPSHOT`)

### Dependencies
The package uses the following major dependencies:
- AWS SDK v${awssdk.version}
- Jackson v${jackson.version}
- Resilience4j v${resilience4j.version}
- Apache Avro v${avro.version}

### Notes
- Always update the CHANGELOG.md with your changes
- Ensure all tests pass before publishing
- Follow semantic versioning (MAJOR.MINOR.PATCH)
  - MAJOR: Breaking changes
  - MINOR: New features, backward compatible
  - PATCH: Bug fixes, backward compatible
