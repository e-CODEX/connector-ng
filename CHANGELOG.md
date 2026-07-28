# Changelog

All notable changes to this project will be documented in this file.

## [7.0.1] - 0000-00-00

### Release Notes

#### Added

-**Delivery Evidence** — Support automatic `delivery evidence` submission.

### Technical

- **Database** — Support Oracle DBMS.
- **Transaction Management** — Replaced **Narayana** with **Atomikos** for JTA transaction management.

## [7.0.0] - 2026-06-30

### Release Notes

#### Added

- **Business message sending** — Added support for sending business messages.
- **Reception acknowledgment** — Added support for backend confirmation of business message reception.
- **ASiC-S container compatibility** — Generated ASiC-S containers remain fully backward-compatible with the existing connector.
- **Message processing performance** — Improved overall message processing performance.
- **S3 storage support** — Added support for S3-based file storage.
- **REST/JMS backend integration** — Laid the foundation for future REST and JMS backend integration.

### Technical

- **Docker-friendly deployment** — The new connector is designed to run smoothly in containerized environments.
- **Shared JMS broker** — The connector now shares the same JMS broker as the gateway.
