-- Adds employee number and work email fields to user management.
ALTER TABLE sys_user
  ADD COLUMN employee_number VARCHAR(50) NULL AFTER display_name,
  ADD COLUMN work_email VARCHAR(150) NULL AFTER employee_number;
