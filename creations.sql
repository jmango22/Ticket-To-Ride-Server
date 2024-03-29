CREATE TABLE IF NOT EXISTS player (
    user_id SERIAL not null,
    username VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(20) NOT NULL,
    access_token VARCHAR(20) UNIQUE,
    PRIMARY KEY(user_id)
);

CREATE TABLE IF NOT EXISTS participants (
    user_id INTEGER NOT NULL,
    game_id INTEGER NOT NULL,
    player_number INTEGER,
    points INTEGER,
    trains INTEGER,
    PRIMARY Key(user_id, game_id),
    FOREIGN KEY(user_id)
      REFERENCES player
      ON DELETE CASCADE,
    FOREIGN KEY(game_id)
      REFERENCES game
      ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS game (
    game_id SERIAL NOT NULL,
    name VARCHAR(20) NOT NULL,
    started BOOLEAN NOT NULL,
    PRIMARY KEY(game_id)
);

CREATE TABLE IF NOT EXISTS route (
    route_id SERIAL NOT NULL,
    city_1 UNIQUE INTEGER NOT NULL,
    city_2 UNIQUE INTEGER NOT NULL,
    route_color VARCHAR(10) NOT NULL,
    route_length INTEGER NOT NULL,
    PRIMARY KEY(route_id),
    FOREIGN KEY(city_1)
      REFERENCES city
      ON DELETE CASCADE,
    FOREIGN KEY(city_2)
      REFERENCES city
      ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS city (
    city_id SERIAL NOT NULL,
    city_name VARCHAR(20) UNIQUE,
    point_x DECIMAL NOT NULL,
    point_y DECIMAL NOT NULL
    PRIMARY KEY(city_id)
);

CREATE TABLE IF NOT EXISTS claimed_route (
    route_id INTEGER NOT NULL,
    game_id INTEGER NOT NULL,
    player_id INTEGER NOT NULL,
    PRIMARY KEY(route_id, game_id),
    FOREIGN KEY(route_id)
      REFERENCES route
      ON DELETE CASCADE,
    FOREIGN KEY(user_id)
      REFERENCES player
      ON DELETE CASCADE,
    FOREIGN KEY(game_id)
      REFERENCES game
      ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS train_card (
    train_card_id SERIAL NOT NULL,
    game_id INTEGER NOT NULL,
    player_id INTEGER,
    train_type VARCHAR(10),
    discarded BOOLEAN,
    PRIMARY KEY(train_card_id),
    FOREIGN KEY(player_id)
      REFERENCES player
      ON DELETE CASCADE,
    FOREIGN KEY(game_id)
      REFERENCES game
      ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS destination_card (
    destination_card_id SERIAL NOT NULL,
    game_id INTEGER NOT NULL,
    player_id INTEGER,
    city_1 INTEGER NOT NULL,
    city_2 INTEGER NOT NULL,
    discarded BOOLEAN,
    PRIMARY KEY(destination_card_id),
    FOREIGN KEY(player_id)
      REFERENCES player
      on delete CASCADE,
    FOREIGN KEY(game_id)
      REFERENCES game
      on delete CASCADE,
    FOREIGN KEY(city_1)
      REFERENCES city
      ON DELETE CASCADE,
    FOREIGN KEY(city_2)
      REFERENCES city
      ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_message (
    message_id SERIAL NOT NULL,
    game_id INTEGER NOT NULL,
    player_id INTEGER NOT NULL,
    message VARCHAR(120) NOT NULL,
    PRIMARY KEY(message_id),
    FOREIGN KEY(game_id)
      REFERENCES game
      ON DELETE CASCADE,
    FOREIGN KEY(player_id)
      REFERENCES player
      ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS command (
    command_id SERIAL NOT NULL,
    game_id INTEGER NOT NULL,
    player_id INTEGER NOT NULL,
    metadata VARCHAR(200) NOT NULL,
    visible_to_self BOOLEAN NOT NULL,
    visible_to_all BOOLEAN NOT NULL,
    PRIMARY KEY(command_id),
    FOREIGN KEY(game_id)
      REFERENCES game
      ON DELETE CASCADE,
    FOREIGN KEY(player_id)
      REFERENCES player
      ON DELETE CASCADE
);