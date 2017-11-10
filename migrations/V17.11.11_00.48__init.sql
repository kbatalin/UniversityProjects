CREATE TABLE departments
(
  id   BIGINT       NOT NULL
    PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE inventory
(
  team_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  PRIMARY KEY (item_id, team_id)
);

CREATE INDEX inventory_teams_id_fk
  ON inventory (team_id);

CREATE TABLE items
(
  id          BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  description TEXT         NULL,
  cost        BIGINT       NOT NULL,
  count       INT          NULL,
  img         VARCHAR(255) NULL
);

ALTER TABLE inventory
  ADD CONSTRAINT inventory_items_id_fk
FOREIGN KEY (item_id) REFERENCES items (id);

CREATE TABLE logs
(
  id       BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  log      TEXT         NOT NULL,
  log_date DATETIME     NOT NULL,
  user_id  BIGINT       NULL,
  user_ip  VARCHAR(255) NOT NULL,
  url      TEXT         NOT NULL
);

CREATE TABLE pages
(
  id          BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  page        VARCHAR(255) NOT NULL,
  title       VARCHAR(255) NOT NULL,
  description TEXT         NULL
);

CREATE TABLE tasks
(
  id       BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  name     VARCHAR(255) NOT NULL,
  text     TEXT         NOT NULL,
  points   INT          NOT NULL,
  answer   TEXT         NOT NULL,
  visible  INT          NOT NULL,
  deadline DATETIME     NULL,
  task     TEXT         NULL,
  active   INT          NOT NULL
);

CREATE TABLE team_tasks
(
  team_id     BIGINT NOT NULL,
  task_id     BIGINT NOT NULL,
  last_answer TEXT   NOT NULL,
  status      INT    NOT NULL,
  PRIMARY KEY (team_id, task_id),
  CONSTRAINT team_tasks_tasks_id_fk
  FOREIGN KEY (task_id) REFERENCES tasks (id)
);

CREATE INDEX team_tasks_tasks_id_fk
  ON team_tasks (task_id);

CREATE TABLE teams
(
  id         BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  name       VARCHAR(255) NOT NULL,
  points     BIGINT       NULL,
  step2_text TEXT         NULL
);

ALTER TABLE inventory
  ADD CONSTRAINT inventory_teams_id_fk
FOREIGN KEY (team_id) REFERENCES teams (id);

ALTER TABLE team_tasks
  ADD CONSTRAINT team_tasks_teams_id_fk
FOREIGN KEY (team_id) REFERENCES teams (id);

CREATE TABLE users
(
  id            BIGINT AUTO_INCREMENT
    PRIMARY KEY,
  login         VARCHAR(255) NOT NULL,
  email         VARCHAR(255) NULL,
  pass          VARCHAR(255) NOT NULL,
  firstname     VARCHAR(255) NOT NULL,
  lastname      VARCHAR(255) NOT NULL,
  department_id BIGINT       NOT NULL,
  status        INT          NOT NULL,
  permissions   INT          NOT NULL,
  team_id       BIGINT       NULL,
  auth_hash     VARCHAR(255) NULL,
  restore_hash  VARCHAR(255) NULL,
  CONSTRAINT dusers___fk
  FOREIGN KEY (department_id) REFERENCES departments (id),
  CONSTRAINT users_teams_id_fk
  FOREIGN KEY (team_id) REFERENCES teams (id)
);

CREATE INDEX users_teams_id_fk
  ON users (team_id);

CREATE INDEX dusers___fk
  ON users (department_id);

