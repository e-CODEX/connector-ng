/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

create table CONNECTOR_BUSINESS_DOMAINS
(
    ID          NUMBER(19) generated as identity
        primary key,
    CREATED_AT  TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT  TIMESTAMP(9) WITH TIME ZONE not null,
    DESCRIPTION VARCHAR2(512 char),
    ENABLED     BOOLEAN,
    IDENTIFIER  VARCHAR2(255 char)          not null,
    SOURCE      VARCHAR2(255 char)
        check ((source in ('DATABASE', 'IMPLEMENTATION', 'ENVIRONMENT', 'APPLICATION'))),
    UUID        VARCHAR2(255 char)          not null
        constraint UKN51PA7N16YDUXRHWE74JQSHNM
            unique
)
/

create index IDX_CONNECTOR_BUSINESS_DOMAINS_IDENTIFIER
    on CONNECTOR_BUSINESS_DOMAINS (IDENTIFIER)
/

alter table CONNECTOR_BUSINESS_DOMAINS
    add constraint UKMY79MO8V7CAYNL1438IGQDOYM
        unique (IDENTIFIER)
/

create table CONNECTOR_MESSAGE_TRANSPORT_STEPS
(
    ID                             NUMBER(19) generated as identity
        primary key,
    CREATED_AT                     TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT                     TIMESTAMP(9) WITH TIME ZONE not null,
    IDENTIFIER                     VARCHAR2(255 char)          not null
        constraint UKRBGPQCXB3UM58NMMS24ETX093
            unique,
    LINK_PARTNER_NAME              VARCHAR2(255 char)          not null,
    NUMBER_OF_ATTEMPTS             NUMBER(10)                  not null,
    REMOTE_SYSTEM_IDENTIFIER       VARCHAR2(255 char),
    STATUS                         VARCHAR2(255 char)          not null
        check ((status in ('DELIVERED', 'FAILED', 'SUBMITTED', 'DOWNLOADED', 'READY_FOR_DOWNLOAD'))),
    TRANSPORTED_MESSAGE            CLOB                        not null,
    TRANSPORTED_MESSAGE_IDENTIFIER VARCHAR2(255 char)          not null
)
/

create table CONNECTOR_MESSAGE_TRANSPORT_STEP_STATUSES
(
    ID                NUMBER(19) generated as identity
        primary key,
    CREATED_AT        TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT        TIMESTAMP(9) WITH TIME ZONE not null,
    STATUS            VARCHAR2(255 char)          not null
        check ((status in ('DELIVERED', 'FAILED', 'SUBMITTED', 'DOWNLOADED', 'READY_FOR_DOWNLOAD'))),
    TRANSPORT_STEP_ID NUMBER(19)
        constraint FK6KE4GT642IVTVI0F6C53HMUOO
            references CONNECTOR_MESSAGE_TRANSPORT_STEPS
)
/

create index IDX_CONNECTOR_MESSAGE_TRANSPORT_STEPS_REMOTE_SYSTEM_IDENTIFIER
    on CONNECTOR_MESSAGE_TRANSPORT_STEPS (REMOTE_SYSTEM_IDENTIFIER)
/

create index IDX_CONNECTOR_MESSAGE_TRANSPORT_STEPS_LINK_PARTNER_NAME
    on CONNECTOR_MESSAGE_TRANSPORT_STEPS (LINK_PARTNER_NAME)
/

create index IDX_CONNECTOR_MESSAGE_TRANSPORT_STEPS_STATUS
    on CONNECTOR_MESSAGE_TRANSPORT_STEPS (STATUS)
/

create table CONNECTOR_MESSAGES
(
    ID                                      NUMBER(19) generated as identity
        primary key,
    CREATED_AT                              TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT                              TIMESTAMP(9) WITH TIME ZONE not null,
    BACKEND_MESSAGE_IDENTIFIER              VARCHAR2(255 char),
    BACKEND_NAME                            VARCHAR2(255 char),
    CONFIRMED_AT                            TIMESTAMP(9) WITH TIME ZONE,
    DELETED_AT                              TIMESTAMP(9) WITH TIME ZONE,
    DELIVERED_TO_LINK_PARTNER_AT            TIMESTAMP(9) WITH TIME ZONE,
    DIRECTION                               VARCHAR2(255 char)          not null
        check ((direction in ('BACKEND_TO_GATEWAY', 'GATEWAY_TO_BACKEND'))),
    GATEWAY_NAME                            VARCHAR2(255 char),
    IDENTIFIER                              VARCHAR2(255 char)          not null,
    REFERENCE_TO_BACKEND_MESSAGE_IDENTIFIER VARCHAR2(255 char),
    REJECTED_AT                             TIMESTAMP(9) WITH TIME ZONE,
    BUSINESS_DOMAIN_ID                      NUMBER(19)                  not null
        constraint FKA44V5LAB5JT09SUM4J9XFRLEN
            references CONNECTOR_BUSINESS_DOMAINS
)
/

create table CONNECTOR_MESSAGE_ATTACHMENTS
(
    ID           NUMBER(19) generated as identity
        primary key,
    CREATED_AT   TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT   TIMESTAMP(9) WITH TIME ZONE not null,
    CONTENT_TYPE VARCHAR2(255 char)          not null,
    DESCRIPTION  VARCHAR2(255 char)          not null,
    IDENTIFIER   VARCHAR2(255 char)          not null,
    NAME         VARCHAR2(255 char)          not null,
    FILE_SIZE    NUMBER(19)                  not null,
    STORAGE      VARCHAR2(255 char)          not null
        check ((storage in ('S3_BUCKET'))),
    TYPE         VARCHAR2(255 char)
        check ((type in
                ('ATTACHMENT', 'ASICS', 'BUSINESS_CONTENT', 'BUSINESS_DOCUMENT',
                 'DETACHED_SIGNATURE', 'EVIDENCE_XML', 'PDF_TOKEN', 'XML_TOKEN'))),
    MESSAGE_ID   NUMBER(19)
        constraint FK2P3MQCV3O24E94NGRM3LGPPM1
            references CONNECTOR_MESSAGES
)
/

create index IDX_CONNECTOR_MESSAGE_ATTACHMENTS_IDENTIFIER
    on CONNECTOR_MESSAGE_ATTACHMENTS (IDENTIFIER)
/

create index IDX_CONNECTOR_MESSAGE_ATTACHMENTS_NAME
    on CONNECTOR_MESSAGE_ATTACHMENTS (NAME)
/

create index IDX_CONNECTOR_MESSAGE_ATTACHMENTS_TYPE
    on CONNECTOR_MESSAGE_ATTACHMENTS (TYPE)
/

alter table CONNECTOR_MESSAGE_ATTACHMENTS
    add constraint UKQKWEKYAU6MX7FQYMUAVJ4BKQW
        unique (IDENTIFIER)
/

create table CONNECTOR_MESSAGE_BUSINESS_CONTENTS
(
    ID            NUMBER(19) generated as identity
        primary key,
    CREATED_AT    TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT    TIMESTAMP(9) WITH TIME ZONE not null,
    UUID          VARCHAR2(255 char)          not null
        constraint UKPT6K5IYWUWH9UY4MXUH9XS6JG
            unique,
    MESSAGE_ID    NUMBER(19)                  not null
        constraint UKBXHK118V0541XMDD7IXTS5KDI
            unique
        constraint FK2JH116B11S4CR5788PMNSIT4Q
            references CONNECTOR_MESSAGES,
    ATTACHMENT_ID NUMBER(19)                  not null
        constraint UKAL934NQ46HLIFNRN8BJQ1BOA0
            unique
        constraint FK40GTDMG3V5O97XY94FRHHTWT
            references CONNECTOR_MESSAGE_ATTACHMENTS
)
/

create table CONNECTOR_MESSAGE_BUSINESS_DOCUMENTS
(
    ID                  NUMBER(19) generated as identity
        primary key,
    CREATED_AT          TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT          TIMESTAMP(9) WITH TIME ZONE not null,
    AES_TYPE            VARCHAR2(255 char)
        check ((aes_type in ('AUTHENTICATION_BASED', 'SIGNATURE_BASED'))),
    UUID                VARCHAR2(255 char)          not null
        constraint UKE3EDKHMC1QBMTWX4C5GGCLVAH
            unique,
    ATTACHMENT_ID       NUMBER(19)                  not null
        constraint UK65CPF4KJDOVQ65TBQ48T5EPRF
            unique
        constraint FK8BR14REYQ9ODT8XIDDK5L9BBN
            references CONNECTOR_MESSAGE_ATTACHMENTS,
    BUSINESS_CONTENT_ID NUMBER(19)                  not null
        constraint UKA7DNLYKK50VCIL6L7FUO3GXKX
            unique
        constraint FKRQCPKXBE9FJRQA2IF6GXHASIQ
            references CONNECTOR_MESSAGE_BUSINESS_CONTENTS
)
/

create table CONNECTOR_MESSAGE_BUSINESS_DOCUMENT_SIGNATURES
(
    ID                   NUMBER(19) generated as identity
        primary key,
    CREATED_AT           TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT           TIMESTAMP(9) WITH TIME ZONE not null,
    MIME_TYPE            VARCHAR2(255 char)          not null
        check ((mime_type in ('BINARY', 'XML', 'PKCS7'))),
    NAME                 VARCHAR2(255 char)          not null,
    SIGNATURE            BLOB                        not null,
    UUID                 VARCHAR2(255 char)          not null
        constraint UK8AGXFBQUI5O0NBRQWVE6LCIW1
            unique,
    BUSINESS_DOCUMENT_ID NUMBER(19)                  not null
        constraint UKGV33CADW3GESIXNE6MNFBAGON
            unique
        constraint FK4IPS0UTVQL5LIND3YD3SFGSAK
            references CONNECTOR_MESSAGE_BUSINESS_DOCUMENTS
)
/

create unique index UKPT6K5IYWUWH9UY4MXUH9XS6JG
    on CONNECTOR_MESSAGE_BUSINESS_DOCUMENT_SIGNATURES (UUID)
/

create index IDX_CONNECTOR_MESSAGE_BUSINESS_DOCUMENTS_AES_TYPE
    on CONNECTOR_MESSAGE_BUSINESS_DOCUMENTS (AES_TYPE)
/

create table CONNECTOR_MESSAGE_ERRORS
(
    ID         NUMBER(19) generated as identity
        primary key,
    CREATED_AT TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT TIMESTAMP(9) WITH TIME ZONE not null,
    DETAILS    VARCHAR2(255 char),
    LABEL      VARCHAR2(255 char),
    SOURCE     VARCHAR2(255 char)          not null,
    MESSAGE_ID NUMBER(19)                  not null
        constraint UKARHI6TY11TAG5U92Q1BCNU352
            unique
        constraint FKK1RSQFG9R8MPTYGOBT5I35R1Y
            references CONNECTOR_MESSAGES
)
/

create unique index UKE3EDKHMC1QBMTWX4C5GGCLVAH
    on CONNECTOR_MESSAGE_ERRORS (UUID)
/

create table CONNECTOR_MESSAGE_EVIDENCES
(
    ID                           NUMBER(19) generated as identity
        primary key,
    CREATED_AT                   TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT                   TIMESTAMP(9) WITH TIME ZONE not null,
    CONTENT                      CLOB                        not null,
    DELIVERED_TO_LINK_PARTNER_AT TIMESTAMP(9) WITH TIME ZONE,
    TYPE                         VARCHAR2(255 char)          not null
        check ((type in
                ('SUBMISSION_ACCEPTANCE', 'SUBMISSION_REJECTION', 'RELAY_REMMD_ACCEPTANCE',
                 'RELAY_REMMD_REJECTION', 'RELAY_REMMD_FAILURE', 'DELIVERY', 'NON_DELIVERY',
                 'RETRIEVAL', 'NON_RETRIEVAL'))),
    UUID                         VARCHAR2(255 char)          not null,
    MESSAGE_ID                   NUMBER(19)
        constraint FKP2BDWB4XR1KC9IBGV7DKRTIMF
            references CONNECTOR_MESSAGES
)
/

create index IDX_CONNECTOR_MESSAGE_EVIDENCES_UUID
    on CONNECTOR_MESSAGE_EVIDENCES (UUID)
/

create unique index UKARHI6TY11TAG5U92Q1BCNU352
    on CONNECTOR_MESSAGE_EVIDENCES (MESSAGE_ID)
/

alter table CONNECTOR_MESSAGE_EVIDENCES
    add constraint UKELXRNUC4SSW3T7PHLOAHLD1TY
        unique (UUID)
/

create index IDX_CONNECTOR_MESSAGES_IDENTIFIER
    on CONNECTOR_MESSAGES (IDENTIFIER)
/

create index IDX_CONNECTOR_MESSAGES_BACKEND_NAME
    on CONNECTOR_MESSAGES (BACKEND_NAME)
/

create index IDX_CONNECTOR_MESSAGES_BACKEND_MESSAGE_IDENTIFIER
    on CONNECTOR_MESSAGES (BACKEND_MESSAGE_IDENTIFIER)
/

create index IDX_CONNECTOR_MESSAGES_REFERENCE_TO_BACKEND_MESSAGE_IDENTIFIER
    on CONNECTOR_MESSAGES (REFERENCE_TO_BACKEND_MESSAGE_IDENTIFIER)
/

create index IDX_CONNECTOR_MESSAGES_GATEWAY_NAME
    on CONNECTOR_MESSAGES (GATEWAY_NAME)
/

create index IDX_CONNECTOR_MESSAGES_DIRECTION
    on CONNECTOR_MESSAGES (DIRECTION)
/

create index IDX_CONNECTOR_MESSAGES_DELIVERED_TO_LINK_PARTNER_AT
    on CONNECTOR_MESSAGES (SYS_EXTRACT_UTC("DELIVERED_TO_LINK_PARTNER_AT"))
/

create index IDX_CONNECTOR_MESSAGES_CREATED_AT
    on CONNECTOR_MESSAGES (SYS_EXTRACT_UTC("CREATED_AT"))
/

alter table CONNECTOR_MESSAGES
    add constraint UK3WMHIV10XQP6WIBK0CJ74K8A2
        unique (IDENTIFIER)
/

create table CONNECTOR_PROCESSING_MODES
(
    ID                 NUMBER(19) generated as identity
        primary key,
    CREATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    CONTENT            CLOB                        not null,
    DESCRIPTION        VARCHAR2(512 char)          not null,
    FILENAME           VARCHAR2(255 char)          not null,
    UUID               VARCHAR2(255 char)          not null
        constraint UKAN0EYBVHH8000JPLNWEHVLOQR
            unique,
    BUSINESS_DOMAIN_ID NUMBER(19)                  not null
        constraint FK2CWYNU0UVTYUD1Y5T8OLUKDJ4
            references CONNECTOR_BUSINESS_DOMAINS
)
/

create table CONNECTOR_PROCESSING_MODE_ACTIONS
(
    ID                 NUMBER(19) generated as identity
        primary key,
    CREATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    NAME               VARCHAR2(255 char)          not null,
    UUID               VARCHAR2(255 char)          not null
        constraint UKI0IGCJB6WK8BHFN5DB9V4CF0V
            unique,
    PROCESSING_MODE_ID NUMBER(19)                  not null
        constraint FKO637XFJPQ443X29307IYEQJ85
            references CONNECTOR_PROCESSING_MODES
)
/

create index IDX_CONNECTOR_PROCESSING_MODE_ACTIONS_NAME
    on CONNECTOR_PROCESSING_MODE_ACTIONS (NAME)
/

create table CONNECTOR_PROCESSING_MODE_PARTIES
(
    ID                 NUMBER(19) generated as identity
        primary key,
    CREATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    IDENTIFIER         VARCHAR2(255 char)          not null,
    IDENTIFIER_TYPE    VARCHAR2(255 char)          not null,
    IS_HOME            BOOLEAN,
    NAME               VARCHAR2(255 char)          not null,
    ROLE               VARCHAR2(255 char)          not null,
    ROLE_TYPE          VARCHAR2(255 char)          not null
        check ((role_type in ('INITIATOR', 'RESPONDER'))),
    UUID               VARCHAR2(255 char)          not null
        constraint UK8O5VS9Y5JMLKA569WSR6C0U8K
            unique,
    PROCESSING_MODE_ID NUMBER(19)                  not null
        constraint FKA1X21SELATT464Y4DO6NXR5DK
            references CONNECTOR_PROCESSING_MODES
)
/

create index IDX_CONNECTOR_PROCESSING_MODE_PARTIES_ROLE
    on CONNECTOR_PROCESSING_MODE_PARTIES (ROLE)
/

create index IDX_CONNECTOR_PROCESSING_MODE_PARTIES_IDENTIFIER
    on CONNECTOR_PROCESSING_MODE_PARTIES (IDENTIFIER)
/

create index IDX_CONNECTOR_PROCESSING_MODE_PARTIES_IDENTIFIER_TYPE
    on CONNECTOR_PROCESSING_MODE_PARTIES (IDENTIFIER_TYPE)
/

create index IDX_CONNECTOR_PROCESSING_MODE_PARTIES_ROLE_TYPE
    on CONNECTOR_PROCESSING_MODE_PARTIES (ROLE_TYPE)
/

create table CONNECTOR_PROCESSING_MODE_SERVICES
(
    ID                 NUMBER(19) generated as identity
        primary key,
    CREATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    NAME               VARCHAR2(255 char)          not null,
    TYPE               VARCHAR2(255 char)          not null,
    UUID               VARCHAR2(255 char)          not null
        constraint UK6HRC2NNKKGSWQ2NWIXNMC6GEG
            unique,
    PROCESSING_MODE_ID NUMBER(19)                  not null
        constraint FKJOOSTKR2Q7MG80OJBQJE1WB6J
            references CONNECTOR_PROCESSING_MODES
)
/

create table CONNECTOR_MESSAGE_AS4_PROPERTIES
(
    ID                      NUMBER(19) generated as identity
        primary key,
    CREATED_AT              TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT              TIMESTAMP(9) WITH TIME ZONE not null,
    CONVERSATION_IDENTIFIER VARCHAR2(255 char),
    EBMS_MESSAGE_IDENTIFIER VARCHAR2(255 char),
    FINAL_RECIPIENT         VARCHAR2(255 char)          not null,
    ORIGINAL_SENDER         VARCHAR2(255 char)          not null,
    REFERENCE_TO_IDENTIFIER VARCHAR2(255 char),
    ACTION_ID               NUMBER(19)                  not null
        constraint FK2TNKDK5EUMSFN9YEWKIUSEOW1
            references CONNECTOR_PROCESSING_MODE_ACTIONS,
    FROM_PARTY_ID           NUMBER(19)                  not null
        constraint FKFQUQHSGB3HN6U4XEJ3WVEM1YR
            references CONNECTOR_PROCESSING_MODE_PARTIES,
    MESSAGE_ID              NUMBER(19)                  not null
        constraint UKO99VLIBFH8NR9M7YKGVEIA1YB
            unique
        constraint FKM1CYPU5JQLJRSS1L35LL3XJ7U
            references CONNECTOR_MESSAGES,
    SERVICE_ID              NUMBER(19)                  not null
        constraint FK82HCUWC9VIFHJYYX5CAA83BXA
            references CONNECTOR_PROCESSING_MODE_SERVICES,
    TO_PARTY_ID             NUMBER(19)                  not null
        constraint FK47E9BETVBGHGE7BRVIKRSA4OT
            references CONNECTOR_PROCESSING_MODE_PARTIES
)
/

create index IDX_CONNECTOR_MESSAGE_AS4_PROPERTIES_EBMS_MESSAGE_IDENTIFIER
    on CONNECTOR_MESSAGE_AS4_PROPERTIES (EBMS_MESSAGE_IDENTIFIER)
/

create index IDX_CONNECTOR_MESSAGE_AS4_PROPERTIES_REFERENCE_TO_IDENTIFIER
    on CONNECTOR_MESSAGE_AS4_PROPERTIES (REFERENCE_TO_IDENTIFIER)
/

create index IDX_CONNECTOR_MESSAGE_AS4_PROPERTIES_CONVERSATION_IDENTIFIER
    on CONNECTOR_MESSAGE_AS4_PROPERTIES (CONVERSATION_IDENTIFIER)
/

create index IDX_CONNECTOR_MESSAGE_AS4_PROPERTIES_ORIGINAL_SENDER
    on CONNECTOR_MESSAGE_AS4_PROPERTIES (ORIGINAL_SENDER)
/

create index IDX_CONNECTOR_MESSAGE_AS4_PROPERTIES_FINAL_RECIPIENT
    on CONNECTOR_MESSAGE_AS4_PROPERTIES (FINAL_RECIPIENT)
/

create unique index SYS_C008839
    on CONNECTOR_PROCESSING_MODE_SERVICES (ID)
/

create index IDX_CONNECTOR_PROCESSING_MODE_SERVICES_NAME
    on CONNECTOR_PROCESSING_MODE_SERVICES (NAME)
/

create index IDX_CONNECTOR_PROCESSING_MODE_SERVICES_TYPE
    on CONNECTOR_PROCESSING_MODE_SERVICES (TYPE)
/

create table CONNECTOR_PROCESSING_MODE_TRUSTSTORES
(
    ID                 NUMBER(19) generated as identity
        primary key,
    CREATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    CONTENT            BLOB                        not null,
    FILENAME           VARCHAR2(255 char)          not null,
    PASSWORD           VARCHAR2(255 char)          not null,
    TYPE               VARCHAR2(255 char)          not null
        check ((type in ('JKS', 'PKCS12'))),
    PROCESSING_MODE_ID NUMBER(19)                  not null
        constraint UKCAJEQDD49FJMU43RDU2E177M6
            unique
        constraint FKKY1TYC6YLQ0TW7T9KSKJL3UDJ
            references CONNECTOR_PROCESSING_MODES
)
/

create table CONNECTOR_ROLES
(
    ID         NUMBER(19) generated as identity
        primary key,
    CREATED_AT TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT TIMESTAMP(9) WITH TIME ZONE not null,
    NAME       VARCHAR2(255 char)          not null
        constraint UKD31O71OW16Y3F23E35FP8S052
            unique,
    UUID       VARCHAR2(255 char)          not null
        constraint UK3QHPWLU126U22W7UB2RE765TG
            unique
)
/

create index IDX_CONNECTOR_SERVICES_NAME
    on CONNECTOR_ROLES (NAME)
/

create table CONNECTOR_USERS
(
    ID                   NUMBER(19) generated as identity
        primary key,
    CREATED_AT           TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT           TIMESTAMP(9) WITH TIME ZONE not null,
    EMAIL                VARCHAR2(255 char)
        constraint UK1RO91NOXD37MU3AJPOS91IOWQ
            unique,
    ENABLED              BOOLEAN,
    MUST_CHANGE_PASSWORD BOOLEAN,
    PASSWORD             VARCHAR2(255 char)          not null
        constraint UKHOM1H4THGTIW0GQFXLYYV8ERA
            unique,
    USERNAME             VARCHAR2(255 char)          not null
        constraint UKP7WI6LWSGTII5PD7VWNMH8L5O
            unique,
    UUID                 VARCHAR2(255 char)          not null
        constraint UKNHBIWGAYD8DG8FJKSJVMFCX62
            unique
)
/

create table CONNECTOR_REFRESH_TOKENS
(
    ID         NUMBER(19) generated as identity
        primary key,
    CREATED_AT TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT TIMESTAMP(9) WITH TIME ZONE not null,
    EXPIRES_AT TIMESTAMP(9) WITH TIME ZONE not null,
    REVOKED    BOOLEAN                     not null,
    REVOKED_AT TIMESTAMP(9) WITH TIME ZONE,
    TOKEN      VARCHAR2(255 char)          not null,
    USER_ID    NUMBER(19)                  not null
        constraint FKRD1G2NQ4DFKEABQNMSASRR0G3
            references CONNECTOR_USERS
)
/

create unique index UK3QHPWLU126U22W7UB2RE765TG
    on CONNECTOR_REFRESH_TOKENS (UUID)
/

create index IDX_CONNECTOR_REFRESH_TOKENS_TOKEN
    on CONNECTOR_REFRESH_TOKENS (TOKEN)
/

alter table CONNECTOR_REFRESH_TOKENS
    add constraint UK7PFB1DA42JX1U0KYPT2OLRPFS
        unique (TOKEN)
/

create table CONNECTOR_USERS_ROLES
(
    USER_ID NUMBER(19) not null
        constraint FK42P3S7VB6M0SW9HHO8HU7PQ1E
            references CONNECTOR_USERS,
    ROLE_ID NUMBER(19) not null
        constraint FKG1F9EO6QWOB2A7MSIEW1CUN6F
            references CONNECTOR_ROLES,
    primary key (USER_ID, ROLE_ID)
)
/
