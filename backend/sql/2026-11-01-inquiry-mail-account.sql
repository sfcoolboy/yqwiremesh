-- Use the company sales mailbox as both the SMTP sender and inquiry recipient.
INSERT INTO sys_setting (setting_key, setting_value)
VALUES
  ('mail.username', 'sales@yqwiremesh.com'),
  ('mail.recipient', 'sales@yqwiremesh.com')
ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value);
