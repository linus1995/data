CREATE SCHEMA IF NOT EXISTS customers_data;

USE customers_data;



CREATE TABLE IF NOT EXISTS customer (
  `id` int NOT NULL AUTO_INCREMENT,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  `age` int DEFAULT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE IF NOT EXISTS address (
  `id` int NOT NULL AUTO_INCREMENT,
  `customerId` int NOT NULL,
  `streetAddress` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `postalCode` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`customerId`) REFERENCES customer(`id`)
);


CREATE TABLE IF NOT EXISTS phoneNumber (
  `id` int NOT NULL AUTO_INCREMENT,
  `customerId` int NOT NULL,
  `type` varchar(255) NOT NULL,
  `number` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`customerId`) REFERENCES customer(`id`)
);
