CREATE TABLE IF NOT EXISTS users (
  user_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email varchar(100) NOT NULL,
  login varchar(100) NOT NULL,
  name varchar(100),
  birthday date,

  CONSTRAINT not_blank_email CHECK (email <> '' AND login <> '')
);

CREATE TABLE IF NOT EXISTS friends (
  from_id integer REFERENCES users ON DELETE CASCADE,
  to_id integer REFERENCES users ON DELETE CASCADE,
  confirmation bool DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS mpa (
  mpa_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar
);

CREATE TABLE IF NOT EXISTS genres (
  genre_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar
);

CREATE TABLE IF NOT EXISTS films (
  film_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar(100) NOT NULL,
  description varchar(200),
  releaseDate date,
  duration integer,
  mpa_id integer NOT NULL REFERENCES mpa ON DELETE CASCADE

  CONSTRAINT not_blank_name CHECK (name <> '')
);

CREATE TABLE IF NOT EXISTS user_liked_film (
  user_id integer REFERENCES users ON DELETE CASCADE,
  film_id integer REFERENCES films ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genre_film (
  film_id integer REFERENCES films (film_id) ON DELETE CASCADE,
  genre_id integer REFERENCES genres (genre_id) ON DELETE CASCADE
);