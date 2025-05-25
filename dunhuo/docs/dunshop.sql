SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for item_config
-- ----------------------------
CREATE TABLE IF NOT EXISTS `goods` (
    `id` INTEGER NOT NULL,
    `categoryId` INTEGER NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `save_place` VARCHAR(255) NOT NULL,
    `save_time` VARCHAR(255) NOT NULL,
    `price` INTEGER NOT NULL,
    `abradabilityId` INTEGER NOT NULL,
    `images` JSON NOT NULL,
    `status` INTEGER NOT NULL COMMENT '状态',
    `create_Time` VARCHAR(255) NOT NULL,
    `createdAt` DATETIME NOT NULL,
    `updatedAt` DATETIME NOT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `users` (
    `id` INTEGER NOT NULL auto_increment,
    `nick_name` VARCHAR(255) ,
    `username` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `gender` VARCHAR(255) NOT NULL,
    `portrait` VARCHAR(255) NOT NULL,
    `phone_number` VARCHAR(255) ,
    `open_id` VARCHAR(255) NOT NULL,
    `wx_unionid` VARCHAR(255) ,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB;