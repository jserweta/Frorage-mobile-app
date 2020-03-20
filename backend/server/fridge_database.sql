CREATE DATABASE frorage;

CREATE TABLE frorage.user_table (
    id bigint AUTO_INCREMENT,
	username varchar(20) UNIQUE NOT NULL,
	email varchar(50) UNIQUE NOT NULL,
	password varchar(255) NOT NULL,
    enabled tinyint(1) NOT NULL,
    created_at datetime NOT NULL,
    updated_at datetime NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE frorage.confirmation_token (
    id bigint AUTO_INCREMENT,
	user_id bigint,
	status varchar(10) NOT NULL,
    token varchar(255) NOT NULL,
    expiration_date_time datetime NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES frorage.user_table(id) on delete CASCADE
);

CREATE TABLE frorage.kitchen (

    kitchen_id bigint NOT NULL AUTO_INCREMENT,
    kitchen_name varchar(20) NOT NULL,
    kitchen_password varchar(128) NOT NULL,
    PRIMARY KEY (kitchen_id)
);


CREATE TABLE frorage.users_group (
    group_id bigint NOT NULL AUTO_INCREMENT,
    user_id bigint NOT NULL,
    kitchen_id bigint NOT NULL,
    PRIMARY KEY (group_id),
    FOREIGN KEY (kitchen_id) REFERENCES frorage.kitchen(kitchen_id) on delete CASCADE
);


CREATE TABLE frorage.product (
    product_id bigint NOT NULL AUTO_INCREMENT,
    kitchen_id bigint NOT NULL,
    product_name varchar(40) NOT NULL,
    amount float(10,4),
    unit varchar(15),
    expiration_date date,
    is_running_out boolean DEFAULT FALSE,
    favourite boolean DEFAULT FALSE,
    to_buy boolean DEFAULT FALSE,
    PRIMARY KEY (product_id),
    FOREIGN KEY (kitchen_id) REFERENCES frorage.kitchen(kitchen_id) on delete CASCADE
);


CREATE TABLE frorage.recipe (
    recipe_id bigint NOT NULL AUTO_INCREMENT,
    kitchen_id bigint NOT NULL,
    recipe_name varchar(50) NOT NULL,
    recipe_description varchar(50),
    PRIMARY KEY (recipe_id),
    FOREIGN KEY (kitchen_id) REFERENCES frorage.kitchen(kitchen_id) on delete CASCADE
);


CREATE TABLE frorage.ingredients (
    ingredient_id bigint NOT NULL AUTO_INCREMENT,
    recipe_id bigint NOT NULL, 
    product_id bigint NOT NULL,
    ingredient_name varchar(50) NOT NULL,
    amount float(10,4),
    unit varchar(15),
    PRIMARY KEY (ingredient_id),
    FOREIGN KEY (recipe_id) REFERENCES frorage.recipe(recipe_id) on delete CASCADE,
    FOREIGN KEY (product_id) REFERENCES frorage.product(product_id) on delete CASCADE
);