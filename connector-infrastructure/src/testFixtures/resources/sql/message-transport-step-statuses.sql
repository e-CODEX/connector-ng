insert into connector_message_transport_step_statuses (
    uuid,
    status,
    created_at,
    updated_at,
    transport_step_id
)
values (
           '4d0da006-b6e0-4323-a364-b3d99bc9da14',
           'SUBMITTED',
           now(),
           now(),
           1
       ),
       (
           'f17a3575-a79f-4847-9550-aff012cab1c7',
           'PENDING',
           now(),
           now(),
           2
       );
