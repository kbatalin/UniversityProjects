INSERT INTO quest17.departments (id, name) VALUES (1, 'ФИТ');
INSERT INTO quest17.departments (id, name) VALUES (2, 'ФИЯ');

INSERT INTO quest17.users (login, email, pass, firstname, lastname, department_id, status, permissions, team_id, auth_hash, restore_hash)
VALUES
  ('lobkov', 'lobkov@penis.soset', '$2a$10$GviBxXNe2hXFTJXzd5YeD.BevpCnGQ..BjcPyxpVd2dN/HsRJf1Sa', 'Ilya', 'Lobkov', 1,
             1, 1, NULL, NULL, NULL);
INSERT INTO quest17.users (login, email, pass, firstname, lastname, department_id, status, permissions, team_id, auth_hash, restore_hash)
VALUES
  ('batalin', 'kir55rus@yandex.ru', '$2a$10$GviBxXNe2hXFTJXzd5YeD.BevpCnGQ..BjcPyxpVd2dN/HsRJf1Sa', 'Kirill', 'Batalin',
              1, 1, 1, NULL, NULL, NULL);