-- The news.source column exists in the News entity (News.java) but was missing
-- from the original schema.sql CREATE TABLE statement. Existing databases are
-- not recreated by schema.sql, so backfill it separately.
-- Safe to run twice: MySQL 8.0.19+ ignores the column if it already exists.

ALTER TABLE news
  ADD COLUMN IF NOT EXISTS source VARCHAR(200) NULL
  COMMENT 'News source, e.g. the customer name or the trade platform that referred the article.'
  AFTER cover_image_url;
