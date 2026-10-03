CREATE TABLE projects (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    manager_id UUID NOT NULL,
    status VARCHAR(30) NOT NULL,
    priority VARCHAR(30) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    budget NUMERIC(19, 2),
    progress INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_projects_manager
        FOREIGN KEY (manager_id) REFERENCES employees(id),

    CONSTRAINT chk_projects_progress
        CHECK (progress BETWEEN 0 AND 100),

    CONSTRAINT chk_projects_dates
        CHECK (start_date <= end_date)
);

CREATE INDEX idx_projects_manager_id ON projects(manager_id);
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_priority ON projects(priority);
