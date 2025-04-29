SET GLOBAL time_zone = 'Asia/Bangkok';
SET time_zone = 'Asia/Bangkok';
SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;
create database if not exists INT221 CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
use INT221;
create table if not exists brands(
  brand_id integer AUTO_INCREMENT PRIMARY KEY,
  name varchar(30) not null,
  websiteUrl varchar(40),
  isActive boolean not null default false,
  countryOfOrigin varchar(80),
  createdOn datetime not null default current_timestamp,
  updatedOn datetime not null default current_timestamp on update current_timestamp
);

create table if not exists products (
  product_id integer AUTO_INCREMENT PRIMARY KEY,
  brand_id integer not null,
  model varchar(60) not null,
  description varchar(400),
  price integer not null,
  ramGb integer,
  screenSizeInch decimal(3,1),
  storageGb integer,
  color varchar(40),
  quantity integer not null default 1,
  createdOn datetime not null default current_timestamp,
  updatedOn datetime not null default current_timestamp on update current_timestamp,
  CHECK (price >= 0),
  CHECK (quantity >= 0),
  foreign key (brand_id) references brands(brand_id)
);



INSERT INTO brands (name, countryOfOrigin, websiteUrl, isActive) VALUES
('Samsung', 'South Korea', 'https://www.samsung.com', 1),
('Apple', 'United States', 'https://www.apple.com', 1),
('Xiaomi', 'China', 'https://www.mi.com', 1),
('Huawei', 'China', 'https://www.huawei.com', 1),
('OnePlus', 'China', 'https://www.oneplus.com', 1),
('Sony', 'Japan', 'https://www.sony.com', 1),
('LG', 'South Korea', 'https://www.lg.com', 1),
('Nokia', 'Finland', 'https://www.nokia.com', 0),
('Motorola', 'United States', 'https://www.motorola.com', 0),
('OPPO', 'China', 'https://www.oppo.com', 1),
('Vivo', 'China', 'https://www.vivo.com', 1),
('ASUS', 'Taiwan', 'https://www.asus.com', 1),
('Google', 'United States', 'https://store.google.com', 1),
('Realme', 'China', 'https://www.realme.com', 1),
('BlackBerry', 'Canada', 'https://www.blackberry.com', 1),
('HTC', 'Taiwan', 'https://www.htc.com', 1),
('ZTE', 'China', 'https://www.zte.com', 1),
('Lenovo', 'China', 'https://www.lenovo.com', 1),
('Honor', 'China', 'https://www.hihonor.com', 1),
('Nothing', 'United Kingdom', 'https://nothing.tech', 1);


INSERT INTO products (brand_id, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color) VALUES
(2, 'iPhone 14 Pro Max', 'ไอโฟนเรือธงรุ่นล่าสุด มาพร้อม Dynamic Island จอใหญ่สุดในตระกูล กล้องระดับโปร', 5, 42900, 6.7, 6, 512, 'Space Black'),
(2, 'iPhone 14', 'ไอโฟนรุ่นใหม่ล่าสุด รองรับ 5G เร็วแรง ถ่ายภาพสวยทุกสภาพแสง', 8, 29700, 6.1, 6, 256, 'Midnight'),
(2, 'iPhone 13 Pro', 'ไอโฟนรุ่นโปร จอ ProMotion 120Hz กล้องระดับมืออาชีพ', 3, 33000, 6.1, 6, 256, 'Sierra Blue'),
(2, 'iPhone SE 2022', 'Budget-friendly model', 15, 14190, 4.7, 4, 64, 'Starlight'),
(2, 'iPhone 14 Plus', 'iPhone 14 Plus 128GB สี Starlight เครื่องศูนย์ไทย โมเดล TH แบต 100 % มีกล่องครบ ประกันศูนย์ ถึง พ.ย. 68 ส่งฟรี', 7, 29700, 6.7, 6, 256, 'Blue'),
(1, 'Galaxy S23 Ultra', 'Samsung Galaxy S23 Ultra 512GB สีดำปีศาจ สภาพนางฟ้า 99 % ไร้รอย แถมเคสแท้ แบตอึดสุดๆ รองรับ ปากกา S-Pen', 1, 32900, 7.6, NULL, 512, NULL);

