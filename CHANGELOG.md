# Changelog

All notable changes to this project will be documented in this file.

## [7.2.0-SNAPSHOT] - [0000-00-00]

### Release Notes

#### Added
- **JMS queues** — Pending and dead-letter messages can now be listed for each JMS queue.
- **JMS message details** — The details of the messages held in a JMS queue can now be retrieved.
- **DLQ replay** — Messages in a JMS dead-letter queue can be replayed to their original destination (`ADMIN` role only).
- **DLQ message deletion** — Messages in a JMS dead-letter queue can be deleted (`ADMIN` role only).
- **Transport steps** — Transport steps now expose a `retryable` flag that tells whether a manual retry is currently possible.
- **Transport steps** — Retryable transport steps can be resent to the link partner with `POST /api/admin/transport-steps/{identifier}/retry`.

#### Fixes
- **JMS queues** — Fix how dead-letter messages are counted in the connector's JMS queue statistics.
- **Authentication** — A disabled user can no longer use its access token or refresh it, and its refresh tokens are revoked when it is disabled.
- **Authentication** — Changing a password revokes all the refresh tokens of the user, so a refresh token which leaked before the change can no longer be used.
- **Authentication** — A replaced refresh token that is presented again revokes all the refresh tokens of its user, as it may have been stolen. A client retrying right after the replacement is only refused: the grace period is set by `connector.auth.security.jwt.refresh-token.reuse-grace` (`10s` by default).
- **Authentication** — A failed login now gets the same `Invalid username or password` answer whether the account is unknown, disabled or the password is wrong, so the login no longer tells which accounts exist. The reason is only written to the connector log.
- **Authentication** — An access token whose user no longer exists, for instance because the account was deleted, is now refused with `401` instead of failing with `500`.
- **User administration** — The last enabled administrator can no longer be deleted, disabled or stripped of the `ADMIN` role: the API answers `409`, so the administration API cannot be locked out.
- **Access control** — The whole administration API (`/api/v1/admin/**`) is now restricted to users with the `ADMIN` role. Before, many endpoints, such as the update of a processing mode truststore, the attachment and processing mode downloads, the configuration views and the test message, were open to every authenticated user. The other roles can only read the message statistics, the message reports and the list of the business domains. An account of another role that used them now gets `403`.
- **CORS** — No origin is allowed by default: list the origin of the administration UI in `connector.cors.allowed-origins`. Credentials are off by default, the allowed request headers default to `Authorization`, `Content-Type` and `Accept`, and CORS applies to the REST API (`/api/**`) and the actuator (`/actuator/**`) only.
- **Evidences** — Downloading an evidence is restricted to users with the `ADMIN` role. The unused public `/api/v1/evidences/{uuid}/download` path is no longer open.
- **Downloads** — The attachment and processing mode downloads build a well-formed `Content-Disposition` header from the file name, and an attachment with a missing or malformed content type is served as `application/octet-stream` instead of failing.
- **Statistics** — The CSV export of the message report no longer lets a party or service name starting with `=`, `+`, `-` or `@` run as a formula when the file is opened in a spreadsheet.
- **Logging** — A value containing line breaks, such as a username or an identifier sent by a client, can no longer forge log lines: the line breaks are written as `\r` and `\n` in the log message.
- **Error responses** — An unexpected error while receiving an attachment or a SOAP submission no longer returns the internal exception message (which can contain file paths or database details) to the client: the REST API answers with a fixed message and the SOAP `resultMessage` is generic, except for the messages of the connector's own errors, such as an unknown service. The cause is written to the connector log.

#### Technical
- **API security** — Protect the auto-generated Swagger documentation and the Actuator endpoints with `Basic` authentication.
- **Swagger documentation** — Fix a bug that prevented the success response body from being displayed.
- **MySQL driver** — The MySQL JDBC driver is now `com.mysql:mysql-connector-j` 9.7.0, replacing the relocated and no longer updated `mysql:mysql-connector-java` 8.0.33. The driver class names do not change. If you copy your own driver into `lib/`, remove the old `mysql-connector-java-*.jar`.

## [7.1.0] - 2026-11-12

### Release Notes

#### Added

- **Delivery Evidence** — Support automatic `delivery evidence` submission.
- **Statistics** — Added statistics for the connector messages. Supported formats: `csv`, `json`, `xlsx`.
- **Search** — Filter messages by `business domain`.
- **Message** — Missing security validation for incoming business message containers.
- **Administration** — REST API endpoints to support the administration portal.
- **Authentication enabled** — REST API endpoints require a valid JWT bearer token unless explicitly listed public endpoints.
- ***An admin account is created by default*** — Check the deployment properties for its credentials.
- ***Admin-only endpoints*** — `/api/v1/admin/users/**` requires the caller to hold the `ADMIN` role, in addition to being authenticated.
- ***Public endpoints*** — require no authentication.
- **Message** — Support Connector-to-Connector test message.
- **Orphaned Attachments Cleanup** — Support automatic deletion of `orphaned attachment` files.
- **Unresolved Messages Cleanup** — Support automatic purge of `unresolved messages`: their attachment files are deleted from the storage, then the messages and their attachments are permanently removed.
- - **Resolved Messages Cleanup** — Support automatic deletion of attachment files when a message is `confirmed` or `rejected`.
- **S3 Bucket Auto-Creation** — Support automatic creation of the S3 bucket at startup via `connector.s3.auto-create-bucket` (enabled by default).

#### Fixes
- **Ebms** — Set the correct message description for `ebms:description` for outbound messages.
- **Pmode** — Party roles in a processing mode are now resolved from the definition's `<roles>` and
  `<process>` declarations instead of a fixed value.
- **Stream Management** — Fixed improper stream closure that could lead to resource leaks.
- **Message** — Inbound message payload parsing.
- **Message** — Restore backward compatibility between the 7.x and 4.x/6.x series.
- **Message** — Fix an issue that sometimes prevented the `business content` from being submitted to the backend via `SOAP`.

### Technical

- **Database** — Support Oracle DBMS.
- **Transaction Management** — Replaced **Narayana** with **Atomikos** for JTA transaction management.
- **Spring Boot** — Bump to Spring Boot 4.1.0
- **Open Telemetry** — Support for Open Telemetry.
- **Spring Boot** — Enable Spring Boot Actuator.
- **Web** — Support Cross-Origin Resource Sharing (CORS).

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
- **Dependency Updates** — Bump libraries to their latest versions.
