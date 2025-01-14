CREATE TABLE IF NOT EXISTS users(
id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
email VARCHAR(255) UNIQUE NOT NULL,
name VARCHAR(255) NOT NULL
);
ALTER SEQUENCE users_id_seq MINVALUE 0;
ALTER SEQUENCE users_id_seq RESTART WITH 0;
CREATE TABLE IF NOT EXISTS categories(
id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR(50) UNIQUE NOT NULL
);
ALTER SEQUENCE categories_id_seq MINVALUE 0;
ALTER SEQUENCE categories_id_seq RESTART WITH 0;
CREATE TABLE IF NOT EXISTS events(
id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
annotation VARCHAR(2000) NOT NULL,
category INTEGER NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
created_on TIMESTAMP NOT NULL,
description VARCHAR(7000) NOT NULL,
event_date TIMESTAMP NOT NULL,
initiator INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
location_lat FLOAT NOT NULL,
location_lon FLOAT NOT NULL,
paid BOOLEAN NOT NULL,
participant_limit INTEGER,
published_on TIMESTAMP,
request_moderation BOOLEAN,
state VARCHAR(50),
title VARCHAR(120)
);
ALTER SEQUENCE events_id_seq MINVALUE 0;
ALTER SEQUENCE events_id_seq RESTART WITH 0;
CREATE TABLE IF NOT EXISTS participation(
id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
created TIMESTAMP,
event INTEGER NOT NULL REFERENCES events(id) ON DELETE CASCADE,
requester INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
status VARCHAR(50)
);
ALTER SEQUENCE participation_id_seq MINVALUE 0;
ALTER SEQUENCE participation_id_seq RESTART WITH 0;
CREATE TABLE IF NOT EXISTS compilations(
id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
pinned BOOLEAN NOT NULL,
title VARCHAR(50) NOT NULL
);
ALTER SEQUENCE compilations_id_seq MINVALUE 0;
ALTER SEQUENCE compilations_id_seq RESTART WITH 0;
CREATE TABLE IF NOT EXISTS compilations_events(
compilation_id INTEGER NOT NULL REFERENCES compilations(id) ON DELETE CASCADE,
event_id INTEGER NOT NULL REFERENCES events(id) ON DELETE CASCADE,
PRIMARY KEY (compilation_id, event_id)
);
