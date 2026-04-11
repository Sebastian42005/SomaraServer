ALTER TABLE app_users
    DROP CONSTRAINT IF EXISTS chk_app_users_role;

ALTER TABLE app_users
    ADD CONSTRAINT chk_app_users_role
        CHECK (role IN ('USER', 'TEACHER', 'ADMIN'));
