CREATE TABLE user_project (
      id BIGSERIAL PRIMARY KEY,
      user_id BIGINT NOT NULL,
      FOREIGN KEY (user_id) REFERENCES user_data(id),
      project_id BIGINT NOT NULL,
      FOREIGN KEY (project_id) REFERENCES project(id)
)