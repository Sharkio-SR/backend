DROP TABLE IF EXISTS World;
DROP TABLE IF EXISTS Player;
DROP TABLE IF EXISTS World_Player;

CREATE TABLE World (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    x_dim FLOAT NOT NULL,
    y_dim FLOAT NOT NULL
);

CREATE TABLE Player (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    pos_x FLOAT NOT NULL,
    pos_y FLOAT NOT NULL
);

CREATE TABLE Food (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    pos_x FLOAT NOT NULL,
    pos_y FLOAT NOT NULL
);

CREATE TABLE World_Player (
    world_id INT NOT NULL,
    player_id INT NOT NULL,
    PRIMARY KEY (world_id, player_id),
    FOREIGN KEY (world_id) REFERENCES World(id),
    FOREIGN KEY (player_id) REFERENCES Player(id)
);

CREATE TABLE World_Food (
    world_id INT NOT NULL,
    food_id INT NOT NULL,
    PRIMARY KEY (world_id, food_id),
    FOREIGN KEY (world_id) REFERENCES World(id),
    FOREIGN KEY (food_id) REFERENCES Food(id)
);

