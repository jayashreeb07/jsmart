-- Seed data (kept separate from migrations). BCrypt hashes generated at runtime by SeedHelper;
-- placeholder hashes below are replaced on first startup if they start with $PLACEHOLDER$.
-- Default demo accounts: admin@jsmart.local / Admin@123 | seller1@jsmart.local / Seller@123 | buyer1@jsmart.local / Buyer@123
MERGE INTO users (id, email, password_hash, full_name, user_role) KEY(id) VALUES
(1, 'admin@jsmart.local', '$2a$10$7EqYBGJ9hQA9k5p8p8p8puzxJxJxJxJxJxJxJxJxJxJxJxJxJxJx.', 'JS Mart Admin', 'ADMIN'),
(2, 'seller1@jsmart.local', '$2a$10$7EqYBGJ9hQA9k5p8p8p8puzxJxJxJxJxJxJxJxJxJxJxJxJxJxJx.', 'Asha Seller', 'SELLER'),
(3, 'seller2@jsmart.local', '$2a$10$7EqYBGJ9hQA9k5p8p8p8puzxJxJxJxJxJxJxJxJxJxJxJxJxJxJx.', 'Ravi Seller', 'SELLER'),
(4, 'buyer1@jsmart.local', '$2a$10$7EqYBGJ9hQA9k5p8p8p8puzxJxJxJxJxJxJxJxJxJxJxJxJxJxJx.', 'Meera Buyer', 'BUYER'),
(5, 'buyer2@jsmart.local', '$2a$10$7EqYBGJ9hQA9k5p8p8p8puzxJxJxJxJxJxJxJxJxJxJxJxJxJxJx.', 'Arjun Buyer', 'BUYER');
