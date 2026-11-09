-- Adds the "show on factory page" flag used by the image manager.
-- Safe to run on existing databases; skip if the column already exists.
ALTER TABLE factory_image
  ADD COLUMN show_on_factory TINYINT(1) NOT NULL DEFAULT 0 AFTER page;
