create database if not exists INT221 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
use INT221;
create table if not exists products (
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

INSERT INTO products (name, description, stock, price, brand)
VALUES 
  ('iPhone 15 Pro', 'สมาร์ทโฟนระดับเรือธงจาก Apple หน้าจอ OLED 6.1 นิ้ว', 50, 42900, 'Apple'),
  ('Galaxy S24 Ultra', 'โทรศัพท์จาก Samsung พร้อมกล้อง 200MP และ S Pen', 30, 41900, 'Samsung'),
  ('Xiaomi 14', 'สมาร์ทโฟนระดับพรีเมียม หน้าจอ AMOLED และกล้อง Leica', 40, 25990, 'Xiaomi'),
  ('Nothing Phone (2)', 'สมาร์ทโฟนดีไซน์โปร่งใสจาก Nothing รองรับ Glyph Interface', 20, 23900, 'Nothing'),
  ('ASUS ROG Phone 8', 'มือถือเกมมิ่งพร้อมระบบระบายความร้อนและจอ 165Hz', 15, 35990, 'ASUS');
