CREATE TABLE Comment (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    task_id BIGINT NOT NULL,
    FOREIGN KEY (task_id) REFERENCES Task(id),
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_data(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);