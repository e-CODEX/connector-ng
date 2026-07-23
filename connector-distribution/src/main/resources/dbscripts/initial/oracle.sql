/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

create table CONNECTOR.CONNECTOR_BUSINESS_DOMAINS
(
    ID          NUMBER(19) generated as identity
        primary key,
    CREATED_AT  TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT  TIMESTAMP(9) WITH TIME ZONE not null,
    DESCRIPTION VARCHAR2(255 char),
    ENABLED     BOOLEAN,
    IDENTIFIER  VARCHAR2(255 char)          not null
        constraint UKMY79MO8V7CAYNL1438IGQDOYM
            unique,
    SOURCE      VARCHAR2(255 char)
        check ((source in ('DATABASE', 'IMPLEMENTATION', 'ENVIRONMENT', 'APPLICATION'))),
    UUID        VARCHAR2(255 char)          not null
        constraint UKN51PA7N16YDUXRHWE74JQSHNM
            unique
)
    /

create table CONNECTOR.CONNECTOR_MESSAGE_TRANSPORT_STEPS
(
    ID                             NUMBER(19) generated as identity
        primary key,
    CREATED_AT                     TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT                     TIMESTAMP(9) WITH TIME ZONE not null,
    IDENTIFIER                     VARCHAR2(255 char)          not null,
    LINK_PARTNER_NAME              VARCHAR2(255 char),
    NUMBER_OF_ATTEMPTS             NUMBER(10)                  not null,
    REMOTE_SYSTEM_IDENTIFIER       VARCHAR2(255 char),
    STATUS                         VARCHAR2(255 char)          not null
        check ((status in ('DELIVERED', 'FAILED', 'SUBMITTED', 'DOWNLOADED', 'READY_FOR_DOWNLOAD'))),
    TRANSPORTED_MESSAGE            CLOB                        not null,
    TRANSPORTED_MESSAGE_IDENTIFIER VARCHAR2(255 char)          not null
)
    /

create table CONNECTOR.CONNECTOR_MESSAGE_TRANSPORT_STEP_STATUSES
(
    ID                NUMBER(19) generated as identity
        primary key,
    CREATED_AT        TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT        TIMESTAMP(9) WITH TIME ZONE not null,
    STATUS            VARCHAR2(255 char)          not null
        check ((status in ('DELIVERED', 'FAILED', 'SUBMITTED', 'DOWNLOADED', 'READY_FOR_DOWNLOAD'))),
    TRANSPORT_STEP_ID NUMBER(19)
        constraint FK6KE4GT642IVTVI0F6C53HMUOO
            references CONNECTOR.CONNECTOR_MESSAGE_TRANSPORT_STEPS
)
    /

create table CONNECTOR.CONNECTOR_MESSAGES
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
            references CONNECTOR.CONNECTOR_BUSINESS_DOMAINS
)
    /

create table CONNECTOR.CONNECTOR_MESSAGE_ERRORS
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
            references CONNECTOR.CONNECTOR_MESSAGES
)
    /

create table CONNECTOR.CONNECTOR_MESSAGE_EVIDENCES
(
    ID                           NUMBER(19) generated as identity
        primary key,
    CREATED_AT                   TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT                   TIMESTAMP(9) WITH TIME ZONE not null,
    CONTENT                      CLOB,
    DELIVERED_TO_LINK_PARTNER_AT TIMESTAMP(9) WITH TIME ZONE,
    TYPE                         VARCHAR2(255 char)          not null
        check ((type in
                ('SUBMISSION_ACCEPTANCE', 'SUBMISSION_REJECTION', 'RELAY_REMMD_ACCEPTANCE', 'RELAY_REMMD_REJECTION',
                 'RELAY_REMMD_FAILURE', 'DELIVERY', 'NON_DELIVERY', 'RETRIEVAL', 'NON_RETRIEVAL'))),
    UUID                         VARCHAR2(255 char)          not null
        constraint UKELXRNUC4SSW3T7PHLOAHLD1TY
            unique,
    MESSAGE_ID                   NUMBER(19)
        constraint FKP2BDWB4XR1KC9IBGV7DKRTIMF
            references CONNECTOR.CONNECTOR_MESSAGES
)
    /

create table CONNECTOR.CONNECTOR_PROCESSING_MODES
(
    ID                 NUMBER(19) generated as identity
        primary key,
    CREATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    CONTENT            CLOB                        not null,
    DESCRIPTION        VARCHAR2(255 char)          not null,
    FILENAME           VARCHAR2(255 char)          not null,
    UUID               VARCHAR2(255 char)          not null
        constraint UKAN0EYBVHH8000JPLNWEHVLOQR
            unique,
    BUSINESS_DOMAIN_ID NUMBER(19)                  not null
        constraint FK2CWYNU0UVTYUD1Y5T8OLUKDJ4
            references CONNECTOR.CONNECTOR_BUSINESS_DOMAINS
)
    /

create table CONNECTOR.CONNECTOR_ACTIONS
(
    ID                 NUMBER(19) generated as identity
        primary key,
    CREATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    NAME               VARCHAR2(255 char)          not null,
    UUID               VARCHAR2(255 char)          not null
        constraint UKJNBBWQ3Q6LY14MCFXBY4AX7LW
            unique,
    PROCESSING_MODE_ID NUMBER(19)                  not null
        constraint FKNC17V09YJFH75XQGKV3165S94
            references CONNECTOR.CONNECTOR_PROCESSING_MODES
)
    /

create table CONNECTOR.CONNECTOR_PARTIES
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
        constraint UK555WOD80P30WB11OHAE3CS60G
            unique,
    PROCESSING_MODE_ID NUMBER(19)                  not null
        constraint FKAYI25J0B3FVBDLI0C5IWBO3N
            references CONNECTOR.CONNECTOR_PROCESSING_MODES
)
    /

create table CONNECTOR.CONNECTOR_PROCESSING_MODE_TRUSTSTORES
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
            references CONNECTOR.CONNECTOR_PROCESSING_MODES
)
    /

create table CONNECTOR.CONNECTOR_SERVICES
(
    ID                 NUMBER(19) generated as identity
        primary key,
    CREATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT         TIMESTAMP(9) WITH TIME ZONE not null,
    NAME               VARCHAR2(255 char)          not null,
    TYPE               VARCHAR2(255 char)          not null,
    UUID               VARCHAR2(255 char)          not null
        constraint UKS6BNKKAGWS812CM80FNQQSSB6
            unique,
    PROCESSING_MODE_ID NUMBER(19)                  not null
        constraint FKDRGIYB2SV0D5YVDIKNHMNG7BG
            references CONNECTOR.CONNECTOR_PROCESSING_MODES
)
    /

create table CONNECTOR.CONNECTOR_MESSAGE_AS4_PROPERTIES
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
        constraint FKNAHUNYL5RY75T6DFBV5B8P9TY
            references CONNECTOR.CONNECTOR_ACTIONS,
    FROM_PARTY_ID           NUMBER(19)                  not null
        constraint FKJRT8QYO6GJB9IUFMVFQD6BNS3
            references CONNECTOR.CONNECTOR_PARTIES,
    MESSAGE_ID              NUMBER(19)                  not null
        constraint UKO99VLIBFH8NR9M7YKGVEIA1YB
            unique
        constraint FKM1CYPU5JQLJRSS1L35LL3XJ7U
            references CONNECTOR.CONNECTOR_MESSAGES,
    SERVICE_ID              NUMBER(19)                  not null
        constraint FK3FTH7L1BM4XNLRXB58JSBU3OI
            references CONNECTOR.CONNECTOR_SERVICES,
    TO_PARTY_ID             NUMBER(19)                  not null
        constraint FKKXTRY3DO0SRU52ULIPUHLAJ7
            references CONNECTOR.CONNECTOR_PARTIES
)
    /

create table CONNECTOR.CONNECTOR_MESSAGE_ATTACHMENTS
(
    ID           NUMBER(19) generated as identity
        primary key,
    CREATED_AT   TIMESTAMP(9) WITH TIME ZONE not null,
    UPDATED_AT   TIMESTAMP(9) WITH TIME ZONE not null,
    CONTENT_TYPE VARCHAR2(255 char)          not null,
    DESCRIPTION  VARCHAR2(255 char)          not null,
    IDENTIFIER   VARCHAR2(255 char)          not null
        constraint UKQKWEKYAU6MX7FQYMUAVJ4BKQW
            unique,
    NAME         VARCHAR2(255 char)          not null,
    FILE_SIZE    NUMBER(19)                  not null,
    STORAGE      VARCHAR2(255 char)          not null
        check ((storage in ('S3_BUCKET'))),
    TYPE         VARCHAR2(255 char)
        check ((type in
                ('ATTACHMENT', 'ASICS', 'BUSINESS_CONTENT', 'BUSINESS_DOCUMENT', 'DETACHED_SIGNATURE', 'EVIDENCE_XML',
                 'PDF_TOKEN', 'XML_TOKEN'))),
    MESSAGE_ID   NUMBER(19)
        constraint FK2P3MQCV3O24E94NGRM3LGPPM1
            references CONNECTOR.CONNECTOR_MESSAGES
)
    /

create table CONNECTOR.CONNECTOR_MESSAGE_BUSINESS_CONTENTS
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
            references CONNECTOR.CONNECTOR_MESSAGES,
    ATTACHMENT_ID NUMBER(19)                  not null
        constraint UKAL934NQ46HLIFNRN8BJQ1BOA0
            unique
        constraint FK40GTDMG3V5O97XY94FRHHTWT
            references CONNECTOR.CONNECTOR_MESSAGE_ATTACHMENTS
)
    /

create table CONNECTOR.CONNECTOR_MESSAGE_BUSINESS_DOCUMENTS
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
        references CONNECTOR.CONNECTOR_MESSAGE_ATTACHMENTS,
BUSINESS_CONTENT_ID NUMBER(19)                  not null
    constraint UKA7DNLYKK50VCIL6L7FUO3GXKX
        unique
    constraint FKRQCPKXBE9FJRQA2IF6GXHASIQ
        references CONNECTOR.CONNECTOR_MESSAGE_BUSINESS_CONTENTS
)
    /

create table CONNECTOR.CONNECTOR_MESSAGE_BUSINESS_DOCUMENT_SIGNATURES
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
            references CONNECTOR.CONNECTOR_MESSAGE_BUSINESS_DOCUMENTS
)
    /
