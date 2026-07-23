create table connector_business_domains
(
    id          bigint auto_increment
        primary key,
    created_at  datetime(6)                                                       not null,
    updated_at  datetime(6)                                                       not null,
    description varchar(255)                                                      null,
    enabled     bit                                                               null,
    identifier  varchar(255)                                                      not null,
    source      enum ('APPLICATION', 'DATABASE', 'ENVIRONMENT', 'IMPLEMENTATION') null,
    uuid        varchar(255)                                                      not null,
    constraint UKmy79mo8v7caynl1438igqdoym
        unique (identifier),
    constraint UKn51pa7n16yduxrhwe74jqshnm
        unique (uuid)
);

create table connector_message_transport_steps
(
    id                             bigint auto_increment
        primary key,
    created_at                     datetime(6)                                                                   not null,
    updated_at                     datetime(6)                                                                   not null,
    identifier                     varchar(255)                                                                  not null,
    link_partner_name              varchar(255)                                                                  null,
    number_of_attempts             int                                                                           not null,
    remote_system_identifier       varchar(255)                                                                  null,
    status                         enum ('DELIVERED', 'DOWNLOADED', 'FAILED', 'READY_FOR_DOWNLOAD', 'SUBMITTED') not null,
    transported_message            mediumtext                                                                    not null,
    transported_message_identifier varchar(255)                                                                  not null
);

create table connector_message_transport_step_statuses
(
    id                bigint auto_increment
        primary key,
    created_at        datetime(6)                                                                   not null,
    updated_at        datetime(6)                                                                   not null,
    status            enum ('DELIVERED', 'DOWNLOADED', 'FAILED', 'READY_FOR_DOWNLOAD', 'SUBMITTED') not null,
    transport_step_id bigint                                                                        null,
    constraint FK6ke4gt642ivtvi0f6c53hmuoo
        foreign key (transport_step_id) references connector_message_transport_steps (id)
);

create table connector_messages
(
    id                                      bigint auto_increment
        primary key,
    created_at                              datetime(6)                                       not null,
    updated_at                              datetime(6)                                       not null,
    backend_message_identifier              varchar(255)                                      null,
    backend_name                            varchar(255)                                      null,
    confirmed_at                            datetime(6)                                       null,
    deleted_at                              datetime(6)                                       null,
    delivered_to_link_partner_at            datetime(6)                                       null,
    direction                               enum ('BACKEND_TO_GATEWAY', 'GATEWAY_TO_BACKEND') not null,
    gateway_name                            varchar(255)                                      null,
    identifier                              varchar(255)                                      not null,
    reference_to_backend_message_identifier varchar(255)                                      null,
    rejected_at                             datetime(6)                                       null,
    business_domain_id                      bigint                                            not null,
    constraint FKa44v5lab5jt09sum4j9xfrlen
        foreign key (business_domain_id) references connector_business_domains (id)
);

create table connector_message_attachments
(
    id           bigint auto_increment
        primary key,
    created_at   datetime(6)                                                                                                                           not null,
    updated_at   datetime(6)                                                                                                                           not null,
    content_type varchar(255)                                                                                                                          not null,
    description  varchar(255)                                                                                                                          not null,
    identifier   varchar(255)                                                                                                                          not null,
    name         varchar(255)                                                                                                                          not null,
    file_size    bigint                                                                                                                                not null,
    storage      enum ('S3_BUCKET')                                                                                                                    not null,
    type         enum ('ASICS', 'ATTACHMENT', 'BUSINESS_CONTENT', 'BUSINESS_DOCUMENT', 'DETACHED_SIGNATURE', 'EVIDENCE_XML', 'PDF_TOKEN', 'XML_TOKEN') null,
    message_id   bigint                                                                                                                                null,
    constraint UKqkwekyau6mx7fqymuavj4bkqw
        unique (identifier),
    constraint FK2p3mqcv3o24e94ngrm3lgppm1
        foreign key (message_id) references connector_messages (id)
);

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
    signature            varbinary(32600)                not null,
    uuid                 varchar(255)                    not null,
    business_document_id bigint                          not null,
    constraint UK8agxfbqui5o0nbrqwve6lciw1
        unique (uuid),
    constraint UKgv33cadw3gesixne6mnfbagon
        unique (business_document_id),
    constraint FK4ips0utvql5lind3yd3sfgsak
        foreign key (business_document_id) references connector_message_business_documents (id)
);

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
    created_at                   datetime(6)                                                                                                                                                                                not null,
    updated_at                   datetime(6)                                                                                                                                                                                not null,
    content                      mediumtext                                                                                                                                                                                 null,
    delivered_to_link_partner_at datetime(6)                                                                                                                                                                                null,
    type                         enum ('DELIVERY', 'NON_DELIVERY', 'NON_RETRIEVAL', 'RELAY_REMMD_ACCEPTANCE', 'RELAY_REMMD_FAILURE', 'RELAY_REMMD_REJECTION', 'RETRIEVAL', 'SUBMISSION_ACCEPTANCE', 'SUBMISSION_REJECTION') not null,
    uuid                         varchar(255)                                                                                                                                                                               not null,
    message_id                   bigint                                                                                                                                                                                     null,
    constraint UKelxrnuc4ssw3t7phloahld1ty
        unique (uuid),
    constraint FKp2bdwb4xr1kc9ibgv7dkrtimf
        foreign key (message_id) references connector_messages (id)
);

create table connector_processing_modes
(
    id                 bigint auto_increment
        primary key,
    created_at         datetime(6)  not null,
    updated_at         datetime(6)  not null,
    content            mediumtext   not null,
    description        varchar(255) not null,
    filename           varchar(255) not null,
    uuid               varchar(255) not null,
    business_domain_id bigint       not null,
    constraint UKan0eybvhh8000jplnwehvloqr
        unique (uuid),
    constraint FK2cwynu0uvtyud1y5t8olukdj4
        foreign key (business_domain_id) references connector_business_domains (id)
);

create table connector_actions
(
    id                 bigint auto_increment
        primary key,
    created_at         datetime(6)  not null,
    updated_at         datetime(6)  not null,
    name               varchar(255) not null,
    uuid               varchar(255) not null,
    processing_mode_id bigint       not null,
    constraint UKjnbbwq3q6ly14mcfxby4ax7lw
        unique (uuid),
    constraint FKnc17v09yjfh75xqgkv3165s94
        foreign key (processing_mode_id) references connector_processing_modes (id)
);

create table connector_parties
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
    constraint UK555wod80p30wb11ohae3cs60g
        unique (uuid),
    constraint FKayi25j0b3fvbdli0c5iwbo3n
        foreign key (processing_mode_id) references connector_processing_modes (id)
);

create table connector_services
(
    id                 bigint auto_increment
        primary key,
    created_at         datetime(6)  not null,
    updated_at         datetime(6)  not null,
    name               varchar(255) not null,
    type               varchar(255) not null,
    uuid               varchar(255) not null,
    processing_mode_id bigint       not null,
    constraint UKs6bnkkagws812cm80fnqqssb6
        unique (uuid),
    constraint FKdrgiyb2sv0d5yvdiknhmng7bg
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
    constraint FK3fth7l1bm4xnlrxb58jsbu3oi
        foreign key (service_id) references connector_services (id),
    constraint FKjrt8qyo6gjb9iufmvfqd6bns3
        foreign key (from_party_id) references connector_parties (id),
    constraint FKkxtry3do0sru52ulipuhlaj7
        foreign key (to_party_id) references connector_parties (id),
    constraint FKm1cypu5jqljrss1l35ll3xj7u
        foreign key (message_id) references connector_messages (id),
    constraint FKnahunyl5ry75t6dfbv5b8p9ty
        foreign key (action_id) references connector_actions (id)
);

