--liquibase formatted sql

--changeset davimf:1.0.1
--comment: user data for tests purposes
INSERT INTO "users"
(id, role, user_name)
VALUES
('812a53a1-6f6a-403e-ad02-86f0b980d8c7', 'USER', 'user@food-auth.com'),
('5efd3d99-e1a0-4aab-a911-8e6f4ef644ab', 'USER', 'user2@food-auth.com'),
('e961676b-c2a4-45eb-8bf6-274ab246adee', 'DRIVER', 'driver@food-auth.com');

INSERT INTO "user_authorizations"
(user_id, "password")
VALUES
('812a53a1-6f6a-403e-ad02-86f0b980d8c7', '$2a$10$53n81K2IwSULSvwjVSzrgO.R9lqCnGlKuQVRv6/O37Ep0XgogUYPe'),
('5efd3d99-e1a0-4aab-a911-8e6f4ef644ab', '$2a$10$yWNapQVe0/GGK7cWXgQUnOM4V.aOHLh8/9nH9BuG.yzEAjG7iLjWq'),
('e961676b-c2a4-45eb-8bf6-274ab246adee', '$2a$10$eXU.WDidm6j0yccJd6n0iOPlO.FNGnV0mJ47jQ1won838y25D0WFO');
