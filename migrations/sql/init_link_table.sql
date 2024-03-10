create table if not exists Link
(
    id   bigint generated always as identity,
    link varchar(255) not null,
    primary key (id),
    unique (link)
);
