--liquibase formatted sql

--changeset piomin:1
create table person (
  id serial primary key,
  name varchar(255),
  gender varchar(255),
  age int,
  externalId int
);
insert into person(name, age, gender, externalId) values('John Smith', 25, 'MALE', 100);
insert into person(name, age, gender, externalId) values('Paul Walker', 65, 'MALE', 101);
insert into person(name, age, gender, externalId) values('Lewis Hamilton', 35, 'MALE', 102);
insert into person(name, age, gender, externalId) values('Veronica Jones', 20, 'FEMALE', 103);
insert into person(name, age, gender, externalId) values('Anne Brown', 60, 'FEMALE', 104);
insert into person(name, age, gender, externalId) values('Felicia Scott', 45, 'FEMALE', 105);