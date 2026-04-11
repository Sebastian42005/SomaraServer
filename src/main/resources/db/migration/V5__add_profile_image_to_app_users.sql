ALTER TABLE app_users
    ADD COLUMN IF NOT EXISTS profile_image_content_type VARCHAR(255);

ALTER TABLE app_users
    ADD COLUMN IF NOT EXISTS profile_image_content BYTEA;
