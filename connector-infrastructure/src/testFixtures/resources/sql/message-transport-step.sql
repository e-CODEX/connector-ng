insert into connector_message_transport_steps (
    id,
    identifier,
    number_of_attemps,
    status,
    created_at,
    updated_at,
    link_partner_name,
    transported_message_identifier,
    transported_message
)
values (
           1,
           '8af8af19-839a-4594-a19d-40d67868474c@connector.ecodex.eu_backend_alice',
           1,
           'SUBMITTED',
           now(),
           now(),
           'backend_alice',
           '7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu',
           '{"businessDomainIdentifier":{"messageLaneIdentifier":"default_business_domain"},"uuid":null,"identifier":"7b70aa96-dadc-4bca-87d8-5765846bf9ca@connector.ecodex.eu","backendMessageIdentifier":"6e3320bb-6724-4387-822c-a2914dba559a","referenceToBackendMessageIdentifier":"6e3320bb-6724-4387-822c-a2914dba559a","backendName":"backend_alice","gatewayName":"default_gateway","as4Properties":{"ebmsMessageIdentifier":"dabbe716-3566-4a0f-8dab-6c7396e9e659@connector.ecodex.eu","referenceToIdentifier":"dabbe716-3566-4a0f-8dab-6c7396e9e659@connector.ecodex.eu","conversationIdentifier":"1f355adf-8a03-4387-92fe-06ab17fa7baa","originalSender":"bob","finalRecipient":"alice","service":{"name":"EPO","type":"urn:e-codex:services:"},"action":{"name":"SubmissionAcceptanceRejection"},"fromParty":{"name":"service_red_ecodex","identifier":"RE","identifierType":"urn:oasis:names:tc:ebcore:partyid-type:ecodex","role":"GW","roleType":"INITIATOR","isHome":false},"toParty":{"name":"service_blue_ecodex","identifier":"BL","identifierType":"urn:oasis:names:tc:ebcore:partyid-type:ecodex","role":"GW","roleType":"RESPONDER","isHome":true}},"direction":"GATEWAY_TO_BACKEND","createdAt":null,"updatedAt":null,"deletedAt":null,"rejectedAt":null,"confirmedAt":null,"deliveredToGatewayAt":null,"deliveredToBackendAt":null,"businessContent":null,"attachments":null,"errors":null,"evidences":null,"transportedEvidences":[{"uuid":"00a2dab2-c315-415e-b150-082d4f70d269","type":"SUBMISSION_ACCEPTANCE","attachment":{"identifier":"c3e18064-e0da-4170-9733-1e7e2768e0bb_SUBMISSION_ACCEPTANCE","name":"SUBMISSION_ACCEPTANCE.xml","contentType":"text/xml","size":7,"description":"Evidence of SUBMISSION_ACCEPTANCE","storage":"S3_BUCKET","type":"EVIDENCE_XML","createdAt":1780659573.929653,"updatedAt":1780659573.9426575},"createdAt":1780659573.9456744,"updatedAt":1780659573.9456744}]}'

       ),
       (
           2,
           'b0f19c4c-ac3e-438c-9951-8e3a5211fed4@connector.ecodex.eu_backend_alice',
           1,
           'PENDING',
           now(),
           now(),
           'backend_alice',
           '7a169fa8-1f0d-4a2c-aade-796b0b02fe58@connector.ecodex.eu',
           '{"businessDomainIdentifier":{"messageLaneIdentifier":"default_business_domain"},"uuid":null,"identifier":"7a169fa8-1f0d-4a2c-aade-796b0b02fe58@connector.ecodex.eu","backendMessageIdentifier":"a2fa729a-ac73-4577-88d5-c03319882500","referenceToBackendMessageIdentifier":"6e3320bb-6724-4387-822c-a2914dba559a","backendName":"backend_alice","gatewayName":"default_gateway","as4Properties":{"ebmsMessageIdentifier":"6e6280d9-62ff-46c8-8b1d-d6ac78ccbd28@connector.ecodex.eu","referenceToIdentifier":"6e6280d9-62ff-46c8-8b1d-d6ac78ccbd28@connector.ecodex.eu","conversationIdentifier":"d70f2d04-adb5-43da-b25c-0644767f5f51","originalSender":"bob","finalRecipient":"alice","service":{"name":"EPO","type":"urn:e-codex:services:"},"action":{"name":"SubmissionAcceptanceRejection"},"fromParty":{"name":"service_red_ecodex","identifier":"RE","identifierType":"urn:oasis:names:tc:ebcore:partyid-type:ecodex","role":"GW","roleType":"INITIATOR","isHome":false},"toParty":{"name":"service_blue_ecodex","identifier":"BL","identifierType":"urn:oasis:names:tc:ebcore:partyid-type:ecodex","role":"GW","roleType":"RESPONDER","isHome":true}},"direction":"GATEWAY_TO_BACKEND","createdAt":null,"updatedAt":null,"deletedAt":null,"rejectedAt":null,"confirmedAt":null,"deliveredToGatewayAt":null,"deliveredToBackendAt":null,"businessContent":null,"attachments":null,"errors":null,"evidences":null,"transportedEvidences":[{"uuid":"a2b2735e-de2a-4362-bbb0-01de7002e622","type":"SUBMISSION_ACCEPTANCE","attachment":{"identifier":"c3e18064-e0da-4170-9733-1e7e2768e0bb_SUBMISSION_ACCEPTANCE","name":"SUBMISSION_ACCEPTANCE.xml","contentType":"text/xml","size":7,"description":"Evidence of SUBMISSION_ACCEPTANCE","storage":"S3_BUCKET","type":"EVIDENCE_XML","createdAt":1780659573.929653,"updatedAt":1780659573.9426575},"createdAt":1780659573.9456744,"updatedAt":1780659573.9456744}]}'
       ),
       (
           3,
           '6676c97f-7463-43af-a800-ee560e1c3ed0@connector.ecodex.eu_backend_alice',
           1,
           'DOWNLOADED',
           now(),
           now(),
           'backend_alice',
           '3fae4358-7cc9-4929-a17b-4432cbb8b9cc@connector.ecodex.eu',
           '{"businessDomainIdentifier":{"messageLaneIdentifier":"default_business_domain"},"uuid":null,"identifier":"3fae4358-7cc9-4929-a17b-4432cbb8b9cc@connector.ecodex.eu","backendMessageIdentifier":"afbd0c90-4752-4d72-b3ea-f21b74f86a09","referenceToBackendMessageIdentifier":"99c9496d-8412-4e81-8ab9-ecd0166cd627","backendName":"backend_alice","gatewayName":"default_gateway","as4Properties":{"ebmsMessageIdentifier":"bafab69f-af22-44e4-aaed-8931a0cc7cb0@connector.ecodex.eu","referenceToIdentifier":"bafab69f-af22-44e4-aaed-8931a0cc7cb0@connector.ecodex.eu","conversationIdentifier":"fa91cbce-28c9-48f7-88e0-253847ca1dd7","originalSender":"bob","finalRecipient":"alice","service":{"name":"EPO","type":"urn:e-codex:services:"},"action":{"name":"SubmissionAcceptanceRejection"},"fromParty":{"name":"service_red_ecodex","identifier":"RE","identifierType":"urn:oasis:names:tc:ebcore:partyid-type:ecodex","role":"GW","roleType":"INITIATOR","isHome":false},"toParty":{"name":"service_blue_ecodex","identifier":"BL","identifierType":"urn:oasis:names:tc:ebcore:partyid-type:ecodex","role":"GW","roleType":"RESPONDER","isHome":true}},"direction":"GATEWAY_TO_BACKEND","createdAt":null,"updatedAt":null,"deletedAt":null,"rejectedAt":null,"confirmedAt":null,"deliveredToGatewayAt":null,"deliveredToBackendAt":null,"businessContent":null,"attachments":null,"errors":null,"evidences":null,"transportedEvidences":[{"uuid":"f6cb9e83-4283-4255-8bbf-9cb0920fc1ef","type":"SUBMISSION_ACCEPTANCE","attachment":{"identifier":"c3e18064-e0da-4170-9733-1e7e2768e0bb_SUBMISSION_ACCEPTANCE","name":"SUBMISSION_ACCEPTANCE.xml","contentType":"text/xml","size":7,"description":"Evidence of SUBMISSION_ACCEPTANCE","storage":"S3_BUCKET","type":"EVIDENCE_XML","createdAt":1780659573.929653,"updatedAt":1780659573.9426575},"createdAt":1780659573.9456744,"updatedAt":1780659573.9456744}]}'
       );
