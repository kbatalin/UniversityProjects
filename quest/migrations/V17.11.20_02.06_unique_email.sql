ALTER TABLE users
  MODIFY login VARCHAR(190) NOT NULL;
CREATE UNIQUE INDEX users_login_uindex
  ON users (login);
