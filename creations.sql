CREATE TABLE player IF NOT EXISTS (
    user_id SERIAL INTEGER not null,
    username VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(20) NOT NULL,
    access_token VARCHAR(20) UNIQUE,
    PRIMARY KEY(user_id)
);

CREATE TABLE participants IF NOT EXISTS (
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

CREATE TABLE game IF NOT EXISTS (
    game_id SERIAL INTEGER NOT NULL,
    name VARCHAR(20) NOT NULL,
    started BOOLEAN NOT NULL,
    PRIMARY KEY(game_id)
);

CREATE TABLE route IF NOT EXISTS (
    route_id INTEGER NOT NULL,
    city_1 INTEGER NOT NULL,
    city_2 INTEGER NOT NULL,
    length INTEGER NOT NULL,
    PRIMARY KEY route_id
);

CREATE TABLE claimed_routes IF NOT EXISTS (
    route_id INTEGER NOT NULL,
    game_id INTEGER NOT NULL,
    player_id INTEGER NOT NULL,
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

CREATE TABLE train_cards IF NOT EXISTS (
    train_card_id SERIAL INTEGER NOT NULL,
    game_id INTEGER NOT NULL,
    player_id INTEGER,
    discarded BOOLEAN,
    PRIMARY KEY train_card_id,
    FOREIGN KEY(player_id)
      REFERENCES player
      ON DELETE CASCADE,
    FOREIGN KEY(game_id)
      REFERENCES game
      ON DELETE CASCADE
);

CREATE TABLE destination_cards IF NOT EXISTS (
    destination_card_id SERIAL INTEGER NOT NULL,
    game_id INTEGER NOT NULL,
    player_id INTEGER,
    discarded BOOLEAN,
    PRIMARY KEY destination_card_id,
    FOREIGN KEY(player_id)
      REFERENCES player
      on delete CASCADE,
    FOREIGN KEY(game_id)
      REFERENCES game
      on delete CASCADE
);

CREATE TABLE chat_messages IF NOT EXISTS (
    message_id SERIAL INTEGER NOT NULL,
    game_id INTEGER NOT NULL,
    player_id INTEGER NOT NULL,
    message VARCHAR(120) NOT NULL,
    PRIMARY KEY message_id,
    FOREIGN KEY(game_id)
      REFERENCES game
      ON DELETE CASCADE,
    FOREIGN KEY(player_id)
      REFERENCES player
      ON DELETE CASCADE
);