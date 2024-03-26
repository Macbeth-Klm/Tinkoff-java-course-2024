-- Файл миграции: создание таблицы ссылок и таблицы чатов
create table if not exists link
(
    id    bigserial,
    url        varchar(255)                        not null,
    updated_at timestamp with time zone            not null,
    checked_at timestamp with time zone            not null,

    primary key (id),
    unique (url)
);

create table if not exists chat
(
    id bigint not null,

    primary key (id)
);

-- Связывающая таблица для отслеживания связей между ссылками и чатами
create table if not exists chat_link
(
    chat_id bigint not null,
    link_id bigint not null,

    primary key (chat_id, link_id),
    foreign key (link_id) references link (id) on delete cascade,
    foreign key (chat_id) references chat (id) on delete cascade
);
