-- Add type and page columns to factory_image table
ALTER TABLE factory_image ADD COLUMN type VARCHAR(50) NULL;
ALTER TABLE factory_image ADD COLUMN page VARCHAR(50) NULL;

-- Set existing images to factory type if not set
UPDATE factory_image SET type = 'factory' WHERE type IS NULL;

-- Set existing hero image to page-hero type if exists
UPDATE factory_image SET type = 'page-hero' WHERE type IS NULL AND imageUrl LIKE '%hero%';
