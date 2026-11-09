-- Add specs columns for dynamic product parameters
ALTER TABLE category ADD COLUMN specs TEXT NULL AFTER description;
ALTER TABLE product ADD COLUMN specs TEXT NULL AFTER description;
