INSERT INTO ghost_net (
    latitude, longitude, estimated_size_m2, status, anonymous_report,
    reporter_name, reporter_phone, reported_at, rescue_announced_at,
    recovered_at, assigned_rescuer_id, version
)
VALUES
    (54.321, 10.122, 32.5, 'GEMELDET', b'1', NULL, NULL, NOW(), NULL, NULL, NULL, 0),
    (43.210, 5.370, 18.0, 'GEMELDET', b'0', 'Kuestenwache Nord', '+49 431 555100', NOW(), NULL, NULL, NULL, 0),
    (58.978, -3.299, 25.0, 'BERGUNG_BEVORSTEHEND', b'0', 'Fischerverband Ost', '+49 40 333220', NOW(), NOW(), NULL, NULL, 0);
