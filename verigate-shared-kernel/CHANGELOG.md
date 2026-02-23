# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [1.0.7-SNAPSHOT] - 2025-06-06

### Added
- Added `ONCE_OFF` frequency to the `Frequency` enum to represent a one-time event. 

## [1.0.5-SNAPSHOT] - 2025-05-20

### Added
- `AbstractKinesisEventPublisher`. This gives consistent publishing behaviour
while allowing subclasses full control of mapping and serialization choices.

## [1.0.0-SNAPSHOT] - 2024-03-XX

### Added
- Initial project setup
- AWS SDK integration
- Resilience4j circuit breaker and retry functionality
- Parameter Store configuration
- Avro serialization support
- GitHub Actions workflow for automated releases
- Maven deployment configuration for GitHub Packages

### Changed
- N/A

### Deprecated
- N/A

### Removed
- N/A

### Fixed
- N/A

### Security
- N/A 