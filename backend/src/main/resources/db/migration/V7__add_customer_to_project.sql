ALTER TABLE projects
    ADD COLUMN customer_id UUID;

ALTER TABLE projects
    ADD CONSTRAINT fk_projects_customer
    FOREIGN KEY (customer_id) REFERENCES customers(id);

CREATE INDEX idx_projects_customer_id
    ON projects(customer_id);
