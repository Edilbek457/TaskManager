CREATE TABLE Task (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(48) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'TODO',
    priority VARCHAR(32) NOT NULL,
    deadline TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    assigned_user_id BIGINT,
    project_id BIGINT NOT NULL,
    FOREIGN KEY (assigned_user_id) REFERENCES user_data(id),
    FOREIGN KEY (project_id) REFERENCES project(id)
);