CREATE TABLE tweets (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id    BIGINT         NOT NULL,
  text       VARCHAR(21845) NOT NULL,
  created_at TIMESTAMP      NOT NULL,
  updated_at TIMESTAMP      NOT NULL,
  deleted_at TIMESTAMP
);
CREATE TABLE users (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  role       INTEGER       NOT NULL,
  password   VARCHAR(1000) NOT NULL,
  skype_id   VARCHAR(1000) NOT NULL,
  name       VARCHAR(1000) NOT NULL,
  sex        INTEGER,
  prefecture VARCHAR(1000),
  bio        VARCHAR(5000),
  image      VARCHAR(1000),
  source     VARCHAR(1000),
  created_at TIMESTAMP     NOT NULL,
  updated_at TIMESTAMP     NOT NULL,
  deleted_at TIMESTAMP
);
ALTER TABLE tweets ADD CONSTRAINT su_tweets_user_id FOREIGN KEY (user_id) REFERENCES users (id);
