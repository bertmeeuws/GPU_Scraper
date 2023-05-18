CREATE TABLE public.role
(
    id          serial       NOT NULL PRIMARY KEY,
    name        varchar(255) NOT NULL UNIQUE,
    description varchar(255) NOT NULL,
    created_at  timestamp    NOT NULL,
    updated_at  timestamp    NOT NULL
);

CREATE TABLE public.role_assignments (
    id          serial       NOT NULL PRIMARY KEY,
    user_id     integer      NOT NULL REFERENCES public.user(id),
    role_id     integer      NOT NULL REFERENCES public.role(id),
    created_at  timestamp    NOT NULL,
    updated_at  timestamp    NOT NULL
);

INSERT INTO public.role (name, description, created_at, updated_at) VALUES ('admin', 'Administrator', NOW(), NOW());

INSERT INTO public.role (name, description, created_at, updated_at) VALUES ('user', 'A regular user', NOW(), NOW());