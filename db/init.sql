create database INT221 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
use INT221;
create table products (
  product_id integer AUTO_INCREMENT PRIMARY KEY,
  name varchar(60) not null,
  description varchar(400),
  stock integer(10) not null,
  price integer(10) not null,
  brand varchar(100) not null,
  createdOn datetime not null default current_timestamp,
  updatedOn datetime not null default current_timestamp on update current_timestamp,
  check (name <> '' and description <> '')
);
