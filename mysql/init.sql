SET NAMES utf8mb4; -- 한글 지원 --

USE `tulipmetric`;

CREATE TABLE `company` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `itmsnm` varchar(45) NOT NULL,
                           `clpr` int NOT NULL,
                           `vs` int NOT NULL,
                           `fltrt` double NOT NULL,
                           `trqu` int NOT NULL,
                           `mrkttotamt` bigint NOT NULL,
                           `market` varchar(20) NOT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=856 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



CREATE TABLE `market` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `name` varchar(45) NOT NULL,
                          `totalmarketcap` bigint NOT NULL,
                          `marketper` int NOT NULL,
                          `stockcount` int NOT NULL,
                          `chart` varchar(100) NOT NULL,
                          `growth_rate30d` double NOT NULL,
                          `market_status` varchar(20) NOT NULL,
                          `trending` bit(1) NOT NULL,
                          `description` varchar(255) NOT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `member` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `nickname` varchar(20) NOT NULL,
                          `password` varchar(61) NOT NULL,
                          `email` varchar(100) NOT NULL,
                          `loginid` varchar(100) NOT NULL,
                          `creatdatetime` varchar(255) NOT NULL,
                          `jointype` enum('FORM','GITHUB','GOOGLE','KAKAO','NAVER') NOT NULL,
                          `role` enum('LOOT','USER') NOT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `UniqueLoginid` (`loginid`),
                          UNIQUE KEY `UniqueNickname` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `post` (
                        `commentnum` int NOT NULL,
                        `likenum` int NOT NULL,
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `category` varchar(10) NOT NULL,
                        `nickname` varchar(20) NOT NULL,
                        `industry_tag` varchar(30) NOT NULL,
                        `title` varchar(50) NOT NULL,
                        `dateminute` varchar(61) NOT NULL,
                        `content` text NOT NULL,
                        `role` enum('LOOT','USER') NOT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `wishmarket` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `loginid` varchar(100) NOT NULL,
                              `marketid` bigint DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              KEY `marketfk_idx` (`marketid`),
                              CONSTRAINT `marketfk` FOREIGN KEY (`marketid`) REFERENCES `market` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `comment` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `postid` bigint DEFAULT NULL,
                           `nickname` varchar(20) NOT NULL,
                           `dateminute` varchar(61) NOT NULL,
                           `content` text NOT NULL,
                           PRIMARY KEY (`id`),
                           KEY `FKlovp15gs1pi5dvo88erc45sk7` (`postid`),
                           CONSTRAINT `FKlovp15gs1pi5dvo88erc45sk7` FOREIGN KEY (`postid`) REFERENCES `post` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `like_entity` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `postid` bigint DEFAULT NULL,
                               `loginid` varchar(100) NOT NULL,
                               PRIMARY KEY (`id`),
                               KEY `FK93fhuqo714ifrnj1qbjjlkbfr` (`postid`),
                               CONSTRAINT `FK93fhuqo714ifrnj1qbjjlkbfr` FOREIGN KEY (`postid`) REFERENCES `post` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;