CREATE TABLE player if not exists (
    user_id SERIAL INTEGER not null,
    username VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(20) NOT NULL,
    access_token VARCHAR(20) UNIQUE,
    PRIMARY KEY(user_id)
);

CREATE TABLE participants if not exists (
    user_id INTEGER NOT NULL,
    game_id INTEGER NOT NULL,
    player_number INTEGER,
    PRIMARY Key(user_id, game_id),
    FOREIGN KEY(user_id)
      references player
      on delete CASCADE,
    FOREIGN KEY(game_id)
      references game
      on delete CASCADE
);

CREATE TABLE game if not exists (
    game_id SERIAL INTEGER NOT NULL,
    name VARCHAR(20) NOT NULL,
    started BOOLEAN NOT NULL,
    PRIMARY KEY(game_id)
);