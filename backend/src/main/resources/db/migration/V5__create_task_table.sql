CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    assignee_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    priority VARCHAR(30) NOT NULL,
    start_date DATE NOT NULL,
    due_date DATE NOT NULL,
    estimated_hours NUMERIC(10, 2) NOT NULL,
    actual_hours NUMERIC(10, 2) NOT NULL DEFAULT 0,
    progress INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_tasks_project
        FOREIGN KEY (project_id) REFERENCES projects(id),

    CONSTRAINT fk_tasks_assignee
        FOREIGN KEY (assignee_id) REFERENCES employees(id),

    CONSTRAINT chk_tasks_dates
        CHECK (start_date <= due_date),

    CONSTRAINT chk_tasks_estimated_hours
        CHECK (estimated_hours >= 0),

    CONSTRAINT chk_tasks_actual_hours
        CHECK (actual_hours >= 0),

    CONSTRAINT chk_tasks_progress
        CHECK (progress BETWEEN 0 AND 100)
);

CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_assignee_id ON tasks(assignee_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_priority ON tasks(priority);
