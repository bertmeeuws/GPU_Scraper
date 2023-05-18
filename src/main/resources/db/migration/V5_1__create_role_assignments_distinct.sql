-- Create a function to generate the computed value, because we don't want duplicated role assignments
CREATE OR REPLACE FUNCTION generate_user_role_concat(user_id integer, role_id integer)
    RETURNS varchar(255) AS
$$
BEGIN
    RETURN CONCAT(user_id::text, '_', role_id::text);
END;
$$
    LANGUAGE plpgsql
    IMMUTABLE;

-- Create a function to update the user_role_concat column for existing records
CREATE OR REPLACE FUNCTION update_user_role_concat()
    RETURNS VOID AS
$$
BEGIN
    UPDATE public.role_assignments
    SET user_role_concat = generate_user_role_concat(user_id, role_id);
END;
$$
    LANGUAGE plpgsql;

-- Add the user_role_concat column
ALTER TABLE public.role_assignments
    ADD COLUMN user_role_concat varchar(255);

-- Update the user_role_concat column for existing records
SELECT update_user_role_concat();

-- Add the unique constraint on the user_role_concat column
ALTER TABLE public.role_assignments
    ALTER COLUMN user_role_concat SET NOT NULL,
    ADD CONSTRAINT unique_user_role_assignment UNIQUE (user_role_concat);
