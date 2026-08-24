insert into connector_message_attachments (
    id,
    identifier,
    content_type,
    description,
    name,
    file_size,
    storage,
    type,
    message_id,
    created_at,
    updated_at
)
values
    -- pending attachments
    (
        1,
        '104ebc70-abd5-45da-8c74-940d687501b3_messageContent',
        'application/xml',
        'Pending business content',
        'messageContent.xml',
        6613,
        'S3_BUCKET',
        'BUSINESS_CONTENT',
        NULL,
        now(),
        now()
    ),
    (
        2,
        'f79623a9-3792-4c6e-a96b-819bd4b69879_messageContent',
        'application/pdf',
        'Pending business document',
        'Form_A.pdf',
        6613,
        'S3_BUCKET',
        'BUSINESS_DOCUMENT',
        NULL,
        now(),
        now()
    );
