insert into connector_message_transport_step_statuses (
    status,
    created_at,
    updated_at,
    transport_step_id
)
values (
           'SUBMITTED',
           now(),
           now(),
           1
       ),
       (
           'PENDING',
           now(),
           now(),
           2
       );
