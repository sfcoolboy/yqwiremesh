-- Add show_on_factory column to factory_image table
ALTER TABLE factory_image ADD COLUMN show_on_factory TINYINT(1) NULL DEFAULT 0;
UPDATE factory_image SET show_on_factory = 1 WHERE type = 'factory' AND caption IS NOT NULL AND caption != '';