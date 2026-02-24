INSERT INTO "user" (id, first_name, last_name, email) VALUES
    (1, 'Weaver', 'Daemon', 'weaverdaemon@example.com'),
    (2, 'Sunless', 'Shadow', 'shadowslave@example.com');

INSERT INTO "profile" (id, name, description) VALUES
    (1, 'Admin', 'Manages everything');

INSERT INTO "user_profile" (id, user_id, profile_id) VALUES
    (1, 1, 1),
    (2, 2, 1);