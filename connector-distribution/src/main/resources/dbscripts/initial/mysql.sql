/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

create table connector_business_domains
(
    id          bigint auto_increment
        primary key,
    created_at  datetime(6)                                         not null,
    updated_at  datetime(6)                                         not null,
    description varchar(512)                                        null,
    enabled     bit                                                 null,
    identifier  varchar(255)                                        not null,
    source      enum (
        'APPLICATION', 'DATABASE', 'ENVIRONMENT', 'IMPLEMENTATION') null,
    uuid        varchar(255)                                        not null,
    constraint UKmy79mo8v7caynl1438igqdoym
        unique (identifier),
    constraint UKn51pa7n16yduxrhwe74jqshnm
        unique (uuid)
);

create index IDX_CONNECTOR_BUSINESS_DOMAINS_IDENTIFIER
    on connector_business_domains (identifier);

create table connector_message_transport_steps
(
    id                             bigint auto_increment
        primary key,
    created_at                     datetime(6)                                  not null,
    updated_at                     datetime(6)                                  not null,
    identifier                     varchar(255)                                 not null,
    link_partner_name              varchar(255)                                 not null,
    number_of_attempts             int                                          not null,
    remote_system_identifier       varchar(255)                                 null,
    status                         enum (
        'DELIVERED', 'DOWNLOADED', 'FAILED', 'READY_FOR_DOWNLOAD', 'SUBMITTED') not null,
    transported_message            mediumtext                                   not null,
    transported_message_identifier varchar(255)                                 not null,
    constraint UKrbgpqcxb3um58nmms24etx093
        unique (identifier)
);

create table connector_message_transport_step_statuses
(
    id                bigint auto_increment
        primary key,
    created_at        datetime(6)                                               not null,
    updated_at        datetime(6)                                               not null,
    status            enum (
        'DELIVERED', 'DOWNLOADED', 'FAILED', 'READY_FOR_DOWNLOAD', 'SUBMITTED') not null,
    transport_step_id bigint                                                    null,
    constraint FK6ke4gt642ivtvi0f6c53hmuoo
        foreign key (transport_step_id) references connector_message_transport_steps (id)
);

create index IDX_CONNECTOR_MESSAGE_TRANSPORT_STEPS_LINK_PARTNER_NAME
    on connector_message_transport_steps (link_partner_name);

create index IDX_CONNECTOR_MESSAGE_TRANSPORT_STEPS_REMOTE_SYSTEM_IDENTIFIER
    on connector_message_transport_steps (remote_system_identifier);

create index IDX_CONNECTOR_MESSAGE_TRANSPORT_STEPS_STATUS
    on connector_message_transport_steps (status);

create table connector_messages
(
    id                                      bigint auto_increment
        primary key,
    created_at                              datetime(6)  not null,
    updated_at                              datetime(6)  not null,
    backend_message_identifier              varchar(255) null,
    backend_name                            varchar(255) null,
    confirmed_at                            datetime(6)  null,
    deleted_at                              datetime(6)  null,
    delivered_to_link_partner_at            datetime(6)  null,
    direction                               enum (
        'BACKEND_TO_GATEWAY', 'GATEWAY_TO_BACKEND')      not null,
    gateway_name                            varchar(255) null,
    identifier                              varchar(255) not null,
    reference_to_backend_message_identifier varchar(255) null,
    rejected_at                             datetime(6)  null,
    business_domain_id                      bigint       not null,
    constraint UK3wmhiv10xqp6wibk0cj74k8a2
        unique (identifier),
    constraint FKa44v5lab5jt09sum4j9xfrlen
        foreign key (business_domain_id) references connector_business_domains (id)
);

create table connector_message_attachments
(
    id           bigint auto_increment
        primary key,
    created_at   datetime(6)                                            not null,
    updated_at   datetime(6)                                            not null,
    content_type varchar(255)                                           not null,
    description  varchar(255)                                           not null,
    identifier   varchar(255)                                           not null,
    name         varchar(255)                                           not null,
    file_size    bigint                                                 not null,
    storage      enum ('S3_BUCKET')                                     not null,
    type         enum (
        'ASICS', 'ATTACHMENT', 'BUSINESS_CONTENT', 'BUSINESS_DOCUMENT',
        'DETACHED_SIGNATURE', 'EVIDENCE_XML', 'PDF_TOKEN', 'XML_TOKEN') null,
    message_id   bigint                                                 null,
    constraint UKqkwekyau6mx7fqymuavj4bkqw
        unique (identifier),
    constraint FK2p3mqcv3o24e94ngrm3lgppm1
        foreign key (message_id) references connector_messages (id)
);

create index IDX_CONNECTOR_MESSAGE_ATTACHMENTS_IDENTIFIER
    on connector_message_attachments (identifier);

create index IDX_CONNECTOR_MESSAGE_ATTACHMENTS_NAME
    on connector_message_attachments (name);

create index IDX_CONNECTOR_MESSAGE_ATTACHMENTS_TYPE
    on connector_message_attachments (type);

create table connector_message_business_contents
(
    id            bigint auto_increment
        primary key,
    created_at    datetime(6)  not null,
    updated_at    datetime(6)  not null,
    uuid          varchar(255) not null,
    message_id    bigint       not null,
    attachment_id bigint       not null,
    constraint UKal934nq46hlifnrn8bjq1boa0
        unique (attachment_id),
    constraint UKbxhk118v0541xmdd7ixts5kdi
        unique (message_id),
    constraint UKpt6k5iywuwh9uy4mxuh9xs6jg
        unique (uuid),
    constraint FK2jh116b11s4cr5788pmnsit4q
        foreign key (message_id) references connector_messages (id),
    constraint FK40gtdmg3v5o97xy94frhhtwt
        foreign key (attachment_id) references connector_message_attachments (id)
);

create table connector_message_business_documents
(
    id                  bigint auto_increment
        primary key,
    created_at          datetime(6)                                      not null,
    updated_at          datetime(6)                                      not null,
    aes_type            enum ('AUTHENTICATION_BASED', 'SIGNATURE_BASED') null,
    uuid                varchar(255)                                     not null,
    attachment_id       bigint                                           not null,
    business_content_id bigint                                           not null,
    constraint UK65cpf4kjdovq65tbq48t5eprf
        unique (attachment_id),
    constraint UKa7dnlykk50vcil6l7fuo3gxkx
        unique (business_content_id),
    constraint UKe3edkhmc1qbmtwx4c5ggclvah
        unique (uuid),
    constraint FK8br14reyq9odt8xiddk5l9bbn
        foreign key (attachment_id) references connector_message_attachments (id),
    constraint FKrqcpkxbe9fjrqa2if6gxhasiq
        foreign key (business_content_id) references connector_message_business_contents (id)
);

create table connector_message_business_document_signatures
(
    id                   bigint auto_increment
        primary key,
    created_at           datetime(6)                     not null,
    updated_at           datetime(6)                     not null,
    mime_type            enum ('BINARY', 'PKCS7', 'XML') not null,
    name                 varchar(255)                    not null,
    signature            mediumblob                      not null,
    uuid                 varchar(255)                    not null,
    business_document_id bigint                          not null,
    constraint UK8agxfbqui5o0nbrqwve6lciw1
        unique (uuid),
    constraint UKgv33cadw3gesixne6mnfbagon
        unique (business_document_id),
    constraint FK4ips0utvql5lind3yd3sfgsak
        foreign key (business_document_id) references connector_message_business_documents (id)
);

create index IDX_CONNECTOR_MESSAGE_BUSINESS_DOCUMENTS_AES_TYPE
    on connector_message_business_documents (aes_type);

create table connector_message_errors
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)  not null,
    updated_at datetime(6)  not null,
    details    varchar(255) null,
    label      varchar(255) null,
    source     varchar(255) not null,
    message_id bigint       not null,
    constraint UKarhi6ty11tag5u92q1bcnu352
        unique (message_id),
    constraint FKk1rsqfg9r8mptygobt5i35r1y
        foreign key (message_id) references connector_messages (id)
);

create table connector_message_evidences
(
    id                           bigint auto_increment
        primary key,
    created_at                   datetime(6)                          not null,
    updated_at                   datetime(6)                          not null,
    content                      mediumtext                           not null,
    delivered_to_link_partner_at datetime(6)                          null,
    type                         enum ('DELIVERY', 'NON_DELIVERY', 'NON_RETRIEVAL',
        'RELAY_REMMD_ACCEPTANCE', 'RELAY_REMMD_FAILURE', 'RELAY_REMMD_REJECTION',
        'RETRIEVAL', 'SUBMISSION_ACCEPTANCE', 'SUBMISSION_REJECTION') not null,
    uuid                         varchar(255)                         not null,
    message_id                   bigint                               null,
    constraint UKelxrnuc4ssw3t7phloahld1ty
        unique (uuid),
    constraint FKp2bdwb4xr1kc9ibgv7dkrtimf
        foreign key (message_id) references connector_messages (id)
);

create index IDX_CONNECTOR_MESSAGE_EVIDENCES_UUID
    on connector_message_evidences (uuid);

create index IDX_CONNECTOR_MESSAGES_BACKEND_MESSAGE_IDENTIFIER
    on connector_messages (backend_message_identifier);

create index IDX_CONNECTOR_MESSAGES_BACKEND_NAME
    on connector_messages (backend_name);

create index IDX_CONNECTOR_MESSAGES_CREATED_AT
    on connector_messages (created_at);

create index IDX_CONNECTOR_MESSAGES_DELIVERED_TO_LINK_PARTNER_AT
    on connector_messages (delivered_to_link_partner_at);

create index IDX_CONNECTOR_MESSAGES_DIRECTION
    on connector_messages (direction);

create index IDX_CONNECTOR_MESSAGES_GATEWAY_NAME
    on connector_messages (gateway_name);

create index IDX_CONNECTOR_MESSAGES_IDENTIFIER
    on connector_messages (identifier);

create index IDX_CONNECTOR_MESSAGES_REFERENCE_TO_BACKEND_MESSAGE_IDENTIFIER
    on connector_messages (reference_to_backend_message_identifier);

create table connector_processing_modes
(
    id                 bigint auto_increment
        primary key,
    created_at         datetime(6)  not null,
    updated_at         datetime(6)  not null,
    content            mediumtext   not null,
    description        varchar(512) not null,
    filename           varchar(255) not null,
    uuid               varchar(255) not null,
    business_domain_id bigint       not null,
    constraint UKan0eybvhh8000jplnwehvloqr
        unique (uuid),
    constraint FK2cwynu0uvtyud1y5t8olukdj4
        foreign key (business_domain_id) references connector_business_domains (id)
);

create table connector_processing_mode_actions
(
    id                 bigint auto_increment
        primary key,
    created_at         datetime(6)  not null,
    updated_at         datetime(6)  not null,
    name               varchar(255) not null,
    uuid               varchar(255) not null,
    processing_mode_id bigint       not null,
    constraint UKi0igcjb6wk8bhfn5db9v4cf0v
        unique (uuid),
    constraint FKo637xfjpq443x29307iyeqj85
        foreign key (processing_mode_id) references connector_processing_modes (id)
);

create index IDX_CONNECTOR_PROCESSING_MODE_ACTIONS_NAME
    on connector_processing_mode_actions (name);

create table connector_processing_mode_parties
(
    id                 bigint auto_increment
        primary key,
    created_at         datetime(6)                     not null,
    updated_at         datetime(6)                     not null,
    identifier         varchar(255)                    not null,
    identifier_type    varchar(255)                    not null,
    is_home            bit                             null,
    name               varchar(255)                    not null,
    role               varchar(255)                    not null,
    role_type          enum ('INITIATOR', 'RESPONDER') not null,
    uuid               varchar(255)                    not null,
    processing_mode_id bigint                          not null,
    constraint UK8o5vs9y5jmlka569wsr6c0u8k
        unique (uuid),
    constraint FKa1x21selatt464y4do6nxr5dk
        foreign key (processing_mode_id) references connector_processing_modes (id)
);

create index IDX_CONNECTOR_PROCESSING_MODE_PARTIES_IDENTIFIER
    on connector_processing_mode_parties (identifier);

create index IDX_CONNECTOR_PROCESSING_MODE_PARTIES_IDENTIFIER_TYPE
    on connector_processing_mode_parties (identifier_type);

create index IDX_CONNECTOR_PROCESSING_MODE_PARTIES_ROLE
    on connector_processing_mode_parties (role);

create index IDX_CONNECTOR_PROCESSING_MODE_PARTIES_ROLE_TYPE
    on connector_processing_mode_parties (role_type);

create table connector_processing_mode_services
(
    id                 bigint auto_increment
        primary key,
    created_at         datetime(6)  not null,
    updated_at         datetime(6)  not null,
    name               varchar(255) not null,
    type               varchar(255) not null,
    uuid               varchar(255) not null,
    processing_mode_id bigint       not null,
    constraint UK6hrc2nnkkgswq2nwixnmc6geg
        unique (uuid),
    constraint FKjoostkr2q7mg80ojbqje1wb6j
        foreign key (processing_mode_id) references connector_processing_modes (id)
);

create table connector_message_as4_properties
(
    id                      bigint auto_increment
        primary key,
    created_at              datetime(6)  not null,
    updated_at              datetime(6)  not null,
    conversation_identifier varchar(255) null,
    ebms_message_identifier varchar(255) null,
    final_recipient         varchar(255) not null,
    original_sender         varchar(255) not null,
    reference_to_identifier varchar(255) null,
    action_id               bigint       not null,
    from_party_id           bigint       not null,
    message_id              bigint       not null,
    service_id              bigint       not null,
    to_party_id             bigint       not null,
    constraint UKo99vlibfh8nr9m7ykgveia1yb
        unique (message_id),
    constraint FK2tnkdk5eumsfn9yewkiuseow1
        foreign key (action_id) references connector_processing_mode_actions (id),
    constraint FK47e9betvbghge7brvikrsa4ot
        foreign key (to_party_id) references connector_processing_mode_parties (id),
    constraint FK82hcuwc9vifhjyyx5caa83bxa
        foreign key (service_id) references connector_processing_mode_services (id),
    constraint FKfquqhsgb3hn6u4xej3wvem1yr
        foreign key (from_party_id) references connector_processing_mode_parties (id),
    constraint FKm1cypu5jqljrss1l35ll3xj7u
        foreign key (message_id) references connector_messages (id)
);

create index IDX_CONNECTOR_MESSAGE_AS4_PROPERTIES_CONVERSATION_IDENTIFIER
    on connector_message_as4_properties (conversation_identifier);

create index IDX_CONNECTOR_MESSAGE_AS4_PROPERTIES_EBMS_MESSAGE_IDENTIFIER
    on connector_message_as4_properties (ebms_message_identifier);

create index IDX_CONNECTOR_MESSAGE_AS4_PROPERTIES_FINAL_RECIPIENT
    on connector_message_as4_properties (final_recipient);

create index IDX_CONNECTOR_MESSAGE_AS4_PROPERTIES_ORIGINAL_SENDER
    on connector_message_as4_properties (original_sender);

create index IDX_CONNECTOR_MESSAGE_AS4_PROPERTIES_REFERENCE_TO_IDENTIFIER
    on connector_message_as4_properties (reference_to_identifier);

create index IDX_CONNECTOR_PROCESSING_MODE_SERVICES_NAME
    on connector_processing_mode_services (name);

create index IDX_CONNECTOR_PROCESSING_MODE_SERVICES_TYPE
    on connector_processing_mode_services (type);

create table connector_processing_mode_truststores
(
    id                 bigint auto_increment
        primary key,
    created_at         datetime(6)            not null,
    updated_at         datetime(6)            not null,
    content            mediumblob             not null,
    filename           varchar(255)           not null,
    password           varchar(255)           not null,
    type               enum ('JKS', 'PKCS12') not null,
    processing_mode_id bigint                 not null,
    constraint UKcajeqdd49fjmu43rdu2e177m6
        unique (processing_mode_id),
    constraint FKky1tyc6ylq0tw7t9kskjl3udj
        foreign key (processing_mode_id) references connector_processing_modes (id)
);

create table connector_roles
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)  not null,
    updated_at datetime(6)  not null,
    name       varchar(255) not null,
    uuid       varchar(255) not null,
    constraint UK3qhpwlu126u22w7ub2re765tg
        unique (uuid),
    constraint UKd31o71ow16y3f23e35fp8s052
        unique (name)
);

create table connector_users
(
    id                   bigint auto_increment
        primary key,
    created_at           datetime(6)  not null,
    updated_at           datetime(6)  not null,
    email                varchar(255) null,
    enabled              bit          null,
    must_change_password bit          null,
    password             varchar(255) not null,
    username             varchar(255) not null,
    uuid                 varchar(255) not null,
    constraint UK1ro91noxd37mu3ajpos91iowq
        unique (email),
    constraint UKhom1h4thgtiw0gqfxlyyv8era
        unique (password),
    constraint UKnhbiwgayd8dg8fjksjvmfcx62
        unique (uuid),
    constraint UKp7wi6lwsgtii5pd7vwnmh8l5o
        unique (username)
);

create table connector_refresh_tokens
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)  not null,
    updated_at datetime(6)  not null,
    expires_at datetime(6)  not null,
    revoked    bit          not null,
    revoked_at datetime(6)  null,
    token      varchar(255) not null,
    user_id    bigint       not null,
    constraint UK7pfb1da42jx1u0kypt2olrpfs
        unique (token),
    constraint FKrd1g2nq4dfkeabqnmsasrr0g3
        foreign key (user_id) references connector_users (id)
);

create index IDX_CONNECTOR_REFRESH_TOKENS_TOKEN
    on connector_refresh_tokens (token);

create table connector_users_roles
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint FK42p3s7vb6m0sw9hho8hu7pq1e
        foreign key (user_id) references connector_users (id),
    constraint FKg1f9eo6qwob2a7msiew1cun6f
        foreign key (role_id) references connector_roles (id)
);
