CREATE TABLE IF NOT EXISTS rescuer (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_rescuer_phone UNIQUE (phone)
);

CREATE TABLE IF NOT EXISTS ghost_net (
    id BIGINT NOT NULL AUTO_INCREMENT,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    estimated_size_m2 DOUBLE NOT NULL,
    status VARCHAR(40) NOT NULL,
    anonymous_report BIT NOT NULL,
    reporter_name VARCHAR(120) NULL,
    reporter_phone VARCHAR(30) NULL,
    reported_at DATETIME(6) NOT NULL,
    rescue_announced_at DATETIME(6) NULL,
    recovered_at DATETIME(6) NULL,
    assigned_rescuer_id BIGINT NULL,
    version BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_ghostnet_rescuer FOREIGN KEY (assigned_rescuer_id) REFERENCES rescuer (id)
);
