-- create employees

INSERT INTO employee (last_name, name, middle_name, position, job_title, account, email, status) VALUES ('Dictus', 'Kathe', 'Hadrian', 'SENIOR', 'MANAGER', 'hdictus0', 'hdictus0@archive.org', 'ACTIVE');
INSERT INTO employee (last_name, name, middle_name, position, job_title, account, email, status) VALUES ('Damiral', 'Cristiano', 'Jeramey', 'MIDDLE', 'MANAGER', 'jdamiral1', 'jdamiral1@cisco.com', 'ACTIVE');
INSERT INTO employee (last_name, name, middle_name, position, job_title, account, email, status) VALUES ('Billings', 'Englebert', 'Orbadiah', 'JUNIOR', 'TESTER', 'obillings4', 'obillings4@diigo.com', 'ACTIVE');
INSERT INTO employee (last_name, name, middle_name, position, job_title, account, email, status) VALUES ('Boar', 'Zelig', 'Wat', 'INTERN', 'ACCOUNTANT', 'wboar5', 'wboar5@baidu.com', 'ACTIVE');
INSERT INTO employee (last_name, name, middle_name, position, job_title, account, email, status) VALUES ('Cuningham', 'Letta', 'Brit', 'INTERN', 'ACCOUNTANT', 'bcuningham6', 'bcuningham6@a8.net', 'ACTIVE');



-- create projects

insert into project (project_code, project_name, description, project_status) values ('ee7871d3-ec53-4537-9c45-83ca9126cccc', 'Duis consequat dui nec nisi volutpat eleifend.', 'Aliquam quis turpis eget elit sodales scelerisque. Mauris sit amet eros.', 'TESTING');
insert into project (project_code, project_name, description, project_status) values ('7b908743-5a40-4692-a9a2-f26f3fbc69af', 'Sed accumsan felis.', 'Nam dui.', 'DRAFT');
insert into project (project_code, project_name, description, project_status) values ('e088f1bc-a082-42b0-9e40-33a557a20309', 'Nulla suscipit ligula in lacus.', 'Mauris ullamcorper purus sit amet nulla. Quisque arcu libero, rutrum ac, lobortis vel, dapibus at, diam.', 'TESTING');
insert into project (project_code, project_name, description, project_status) values ('defa6c11-5b26-46ff-a09a-8cd79401d4b8', 'Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros.', 'Aliquam sit amet diam in magna bibendum imperdiet.', 'DRAFT');
insert into project (project_code, project_name, description, project_status) values ('17eeca92-4df7-4aef-b485-e8b4430e6e9e', 'Curabitur at ipsum ac tellus semper interdum.', 'Maecenas rhoncus aliquam lacus.', 'IN_WORK');


-- create project_employee_role join table

insert into project_employee_role (project_id, employee_id, role) values (5, 1, 'CHIEF');
insert into project_employee_role (project_id, employee_id, role) values (4, 2, 'ANALYST');
insert into project_employee_role (project_id, employee_id, role) values (2, 3, 'DEVELOPER');
insert into project_employee_role (project_id, employee_id, role) values (1, 3, 'TESTER');
insert into project_employee_role (project_id, employee_id, role) values (1, 2, 'DEVELOPER');


-- create task


insert into task (task_name, description, responsible_id, amount_of_hours_needed, deadline, task_status, created_by_id, created_when, last_modified_when) values ('Maecenas pulvinar lobortis est.', 'Nam tristique tortor eu pede.', 1, 21, '28-May-2023', 'NEW', 5, '12/8/2022', '2/8/2023');
insert into task (task_name, description, responsible_id, amount_of_hours_needed, deadline, task_status, created_by_id, created_when, last_modified_when) values ('Curabitur at ipsum ac tellus semper interdum.', 'Vivamus vel nulla eget eros elementum pellentesque.', 2, 94, '13-Jul-2023', 'FINISHED', 4, '6/25/2022', '5/3/2023');
insert into task (task_name, description, responsible_id, amount_of_hours_needed, deadline, task_status, created_by_id, created_when, last_modified_when) values ('Duis aliquam convallis nunc.', 'Nulla ut erat id mauris vulputate elementum.', 3, 23, '15-Sep-2023', 'IN_WORK', 3, '6/3/2022', '4/18/2023');
insert into task (task_name, description, responsible_id, amount_of_hours_needed, deadline, task_status, created_by_id, created_when, last_modified_when) values ('Nulla nisl.', 'Duis bibendum, felis sed interdum venenatis, turpis enim blandit mi, in porttitor pede justo eu massa. Donec dapibus.', 4, 4, '03-Aug-2023', 'CLOSED', 2, '9/30/2022', '4/23/2023');
insert into task (task_name, description, responsible_id, amount_of_hours_needed, deadline, task_status, created_by_id, created_when, last_modified_when) values ('Praesent blandit lacinia erat.', 'Nullam sit amet turpis elementum ligula vehicula consequat. Morbi a ipsum.', 5, 13, '16-Oct-2023', 'FINISHED', 1, '10/1/2022', '3/1/2023');

-- create task_dependency

insert into task_dependency (dependent_task_id, depends_on_id) values (1, 2);
insert into task_dependency (dependent_task_id, depends_on_id) values (3, 2);
insert into task_dependency (dependent_task_id, depends_on_id) values (2, 4);
insert into task_dependency (dependent_task_id, depends_on_id) values (3, 4);
insert into task_dependency (dependent_task_id, depends_on_id) values (1, 5);
