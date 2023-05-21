create table employee
(
    id          bigserial       not null
        constraint pk_employee
            primary key,
    last_name   varchar(255) not null,
    name        varchar(255) not null,
    middle_name varchar(255),
    position    integer
        constraint verify_position
            check (("position" >= 0) AND ("position" <= 4)),
    job_title   integer
        constraint verify_job_title
            check ((job_title >= 0) AND (job_title <= 3)),
    account     varchar(255)
        constraint verify_account
            unique,
    email       varchar(255),
    status      integer      not null
        constraint verify_status
            check ((status = 0) OR (status = 1))
);


create table task
(
    id                     bigserial       not null
        constraint pk_task
            primary key,
    task_name              varchar(255) not null,
    description            varchar(255),
    responsible_id         bigint
        constraint fk_task_on_responsible
            references employee
            on delete set null,
    amount_of_hours_needed integer      not null,
    deadline               timestamp    not null,
    task_status            integer
        constraint verify_task_status
            check ((task_status >= 0) AND (task_status <= 3)),
    created_by_id          bigint
        constraint fk_task_on_createdby
            references employee
            on delete set null,
    created_when           timestamp,
    last_modified_when     timestamp
);


create table task_dependency
(
    dependent_task_id bigserial
        constraint fk_dependent_task
            references task
            on delete set null,
    depends_on_id     bigint
        constraint fk_depends_on
            references task
            on delete set null,
    constraint task_dep_constraint
        unique (dependent_task_id, depends_on_id)
);


create table project
(
    id             bigserial       not null
        constraint pk_project
            primary key,
    project_code   varchar(255) not null
        constraint proj_code
            unique,
    project_name   varchar(255) not null,
    description    varchar(255),
    project_status integer      not null
        constraint verify_proj_status
            check ((project_status >= 0) AND (project_status <= 3))
);


create table project_employee_role
(
    id          bigserial not null
        constraint pk_project_employee_role
            primary key,
    project_id  bigint
        constraint fk_project_employee_role_on_project
            references project
            on delete set null,
    employee_id bigint
        constraint fk_project_employee_role_on_employee
            references employee
            on delete set null,
    role        integer
        constraint verify_role
            check ((role >= 0) AND (role <= 3)),
    constraint proj_empl_constraint
        unique (project_id, employee_id)
);