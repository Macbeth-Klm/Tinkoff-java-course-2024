-- Файл миграции: создание таблицы ссылок и таблицы чатов
create table if not exists link
(
    link_id    bigint generated always as identity not null,
    url        varchar(255)                        not null,
    updated_at timestamp with time zone            not null,
    checked_at timestamp with time zone            not null,

    primary key (link_id),
    unique (url)
);

create table if not exists chat
(
    chat_id bigint not null,

    primary key (chat_id)
);

-- Связывающая таблица для отслеживания связей между ссылками и чатами
create table if not exists link_chat_join_table
(
    chat_id bigint not null,
    link_id bigint not null,

    primary key (chat_id, link_id),
    foreign key (link_id) references link (link_id) on delete cascade,
    foreign key (chat_id) references chat (chat_id) on delete cascade
);
