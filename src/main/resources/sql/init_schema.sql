CREATE TABLE users (
  id VARCHAR(36) PRIMARY KEY,
  email VARCHAR(50) NOT NULL,
  password VARCHAR(50) NOT NULL,
  role VARCHAR(10) NOT NULL,

  created_at TIMESTAMP DEFAULT now() NOT NULL,
  updated_at TIMESTAMP DEFAULT now() NOT NULL
);

CREATE INDEX index_user_email ON users (email);

CREATE TABLE pictures (
  id VARCHAR(36) PRIMARY KEY,
  owner_id VARCHAR(36) NOT NULL,
  price FLOAT NOT NULL,
  state VARCHAR(10) NOT NULL,
  media_type VARCHAR(15) NOT NULL,

  created_at TIMESTAMP DEFAULT now() NOT NULL,
  updated_at TIMESTAMP DEFAULT now() NOT NULL
);

CREATE INDEX index_picture_owner ON pictures (owner_id);
CREATE INDEX index_picture_state ON pictures (state);

CREATE TABLE user_like (
  user_id VARCHAR(36) NOT NULL,
  picture_id VARCHAR(36) NOT NULL
);

CREATE UNIQUE INDEX index_user_like_owner ON user_like (user_id, picture_id);
CREATE INDEX index_user_like_picture ON user_like (picture_id);

CREATE TABLE hashtags (
  picture_id VARCHAR(36) NOT NULL,
  hashtag VARCHAR(50) NOT NULL
);

CREATE UNIQUE INDEX index_hashtags_picture ON hashtags (picture_id, hashtag);
CREATE INDEX index_hashtags_hashtag ON hashtags (hashtag);

--HSQLDB doesn't know how to handle below

-- CREATE FUNCTION update_updated_at_column()
--   RETURNS TRIGGER AS
--   $BODY$
--   BEGIN
--     NEW.updated_at = now();
--     RETURN NEW;
--   END;
--   $BODY$
-- LANGUAGE plpgsql VOLATILE;
--
-- CREATE TRIGGER update_users
-- BEFORE UPDATE ON users FOR EACH ROW
-- EXECUTE PROCEDURE update_updated_at_column();
--
-- CREATE TRIGGER update_pictures BEFORE UPDATE
-- ON pictures FOR EACH ROW
-- EXECUTE PROCEDURE update_updated_at_column();
