drop table if exists killer_perks;
drop table if exists survivor_perks;
drop table if exists killers;
drop table if exists survivors;

create table killers (
    killer_id int,
    name      text,
    title     text,
    image     text,
    primary key (killer_id)
);

create table survivors (
    survivor_id int,
    name        text,
    image       text,
    primary key (survivor_id)
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

INSERT INTO killers VALUE (0, '', '', '');