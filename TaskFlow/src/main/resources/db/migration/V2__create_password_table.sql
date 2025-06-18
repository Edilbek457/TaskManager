CREATE TABLE Password (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    password_strength_level SMALLINT NOT NULL CHECK (password_strength_level >= 4),
    password_strength_level_type VARCHAR(16) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (user_id) REFERENCES user_data(id)
);