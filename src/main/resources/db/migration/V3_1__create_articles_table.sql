CREATE TABLE public.article (
                          id serial PRIMARY KEY,
                          product_name VARCHAR ( 50 ) UNIQUE NOT NULL
);