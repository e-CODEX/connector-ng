insert into connector_message_transport_steps (
    id,
    identifier,
    number_of_attemps,
    status,
    created_at,
    updated_at,
    message_id
)
values (
           1,
           '8af8af19-839a-4594-a19d-40d67868474c@connector.ecodex.eu_backend_alice',
           1,
           'SUBMITTED',
           now(),
           now(),
           2
       );
