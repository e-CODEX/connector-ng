SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE connector_message_transport_step_statuses;
TRUNCATE TABLE connector_message_transport_steps;
TRUNCATE TABLE connector_message_evidences;
TRUNCATE TABLE connector_message_errors;
TRUNCATE TABLE connector_message_business_document_signatures;
TRUNCATE TABLE connector_message_business_documents;
TRUNCATE TABLE connector_message_business_contents;
TRUNCATE TABLE connector_message_as4_properties;
TRUNCATE TABLE connector_messages;
TRUNCATE TABLE connector_message_attachments;
TRUNCATE TABLE connector_parties;
TRUNCATE TABLE connector_services;
TRUNCATE TABLE connector_actions;
TRUNCATE TABLE connector_processing_modes;
TRUNCATE TABLE connector_keystores;
TRUNCATE TABLE connector_business_domains;

SET FOREIGN_KEY_CHECKS = 1;
