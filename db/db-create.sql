DROP SCHEMA IF EXISTS `epampractice8` ;

CREATE SCHEMA IF NOT EXISTS `epamPrictice8` DEFAULT CHARACTER SET utf8 ;

USE `epamPrictice8` ;

DROP TABLE IF EXISTS `epamprictice8`.`users`;

CREATE TABLE IF NOT EXISTS `epamprictice8`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(150) NOT NULL UNIQUE ,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB;

DROP TABLE IF EXISTS `epamprictice8`.`groups`;

CREATE TABLE IF NOT EXISTS `epamprictice8`.`groups` (
  `id`   INT          NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(150) NOT NULL UNIQUE ,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB;

DROP TABLE IF EXISTS `epamprictice8`.`users_groups`;

CREATE TABLE IF NOT EXISTS `epamprictice8`.`users_groups` (
  `user_id` INT NOT NULL,
  `group_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `group_id`),
  INDEX `fk_user` (`user_id` ASC),
  INDEX `fk_group` (`group_id` ASC),
  CONSTRAINT `fk_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `epamPrictice8`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE ,
  CONSTRAINT `fk_group`
  FOREIGN KEY (`group_id`)
  REFERENCES `epamPrictice8`.`groups` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE )
  ENGINE = InnoDB;

INSERT INTO users VALUES(DEFAULT , 'ivanov');
INSERT INTO groups VALUES(DEFAULT , 'teamA');