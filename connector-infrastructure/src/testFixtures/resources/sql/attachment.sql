insert into connector_message_attachments (
                                           id,
                                           content_type,
                                           description,
                                           identifier,
                                           name,
                                           size,
                                           storage,
                                           type,
                                           message_id,
                                           created_at,
                                           updated_at
)
values (
        1,
        'application/pdf',
        'Persisting file to S3 bucket',
        'd98a621a-4d14-4cfb-be00-0feae9f9b277_fake_file',
        'fake_file.pdf',
        157286400,
        'S3_BUCKET',
        'ATTACHMENT',
        null,
        now(),
        now()
       ),
       (
           2,
           'application/pdf',
           'Persisting file to S3 bucket',
           '6aeef356-d580-4b94-a569-250435ac3ec5_fake_file',
           'fake_file.pdf',
           200000000,
           'S3_BUCKET',
           'ATTACHMENT',
           1,
           now(),
           now()
       ),
       (
           3,
           'application/pdf',
           'Persisting file to S3 bucket',
           'c12f879b-3c9a-4d26-b36c-b6d67a84f0ed_test_attachment',
           'fake_file.pdf',
           300000000,
           'S3_BUCKET',
           'ATTACHMENT',
           1,
           now(),
           now()
       );
