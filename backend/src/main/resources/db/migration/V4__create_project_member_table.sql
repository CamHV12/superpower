CREATE TABLE project_members (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    role VARCHAR(100) NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_project_members_project
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,

    CONSTRAINT fk_project_members_employee
        FOREIGN KEY (employee_id) REFERENCES employees(id),

    CONSTRAINT uk_project_members_project_employee
        UNIQUE (project_id, employee_id)
);

CREATE INDEX idx_project_members_project_id
    ON project_members(project_id);

CREATE INDEX idx_project_members_employee_id
    ON project_members(employee_id);
