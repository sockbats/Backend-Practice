drop table if exists killers;
drop table if exists survivors;
drop table if exists killer_perks;
drop table if exists survivor_perks;

create table killers (
    killer_id int primary key,
    name      text,
    title     text,
    image     text
);

create table survivors (
    survivor_id int primary key,
    name        text,
    image       text
);

create table killer_perks (
    perk_id     int,
    name        text,
    description text,
    icon       text,
    killer_id   int,
    primary key (perk_id),
    foreign key (killer_id) references killers (killer_id)
);

create table survivor_perks (
    perk_id     int,
    name        text,
    description text,
    icon       text,
    survivor_id int,
    primary key (perk_id),
    foreign key (survivor_id) references survivors (survivor_id)
);