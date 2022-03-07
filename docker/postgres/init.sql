CREATE USER influencer WITH PASSWORD 'influencer';
CREATE DATABASE influencer OWNER influencer;

\c influencer influencer

CREATE TABLE post (
  id TEXT PRIMARY KEY,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  uri TEXT NOT NULL
);


-- TEST DB


\c postgres postgres

CREATE USER "influencer-test" WITH PASSWORD 'influencer-test';
CREATE DATABASE "influencer-test" OWNER "influencer-test";

\c influencer-test influencer-test

CREATE TABLE post (
  id TEXT PRIMARY KEY,
  created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  uri TEXT NOT NULL
);
