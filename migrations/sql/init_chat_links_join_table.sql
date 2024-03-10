create table if not exists Chat_Link_Join_Table
(
    chat_id bigint,
    link_id bigint,

    primary key (chat_id, link_id),
    foreign key (chat_id) references Chat (id) on delete cascade,
    foreign key (link_id) references Link (id) on delete cascade
);
