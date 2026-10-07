-- Product seed (runs after users)
MERGE INTO products (id, seller_id, name, description, price, stock_qty, category, image_url) KEY(id) VALUES
(1, 2, 'JS Bluetooth Speaker', 'Portable speaker with deep bass and 12h battery.', 2499.00, 50, 'Electronics', 'https://via.placeholder.com/400x300?text=Speaker'),
(2, 2, 'Cotton T-Shirt', 'Premium cotton round-neck t-shirt.', 499.00, 200, 'Fashion', 'https://via.placeholder.com/400x300?text=TShirt'),
(3, 3, 'Stainless Steel Bottle', '1L vacuum insulated flask.', 899.00, 120, 'Home', 'https://via.placeholder.com/400x300?text=Bottle'),
(4, 3, 'Java Programming Book', 'Learn Java 17 with examples.', 650.00, 80, 'Books', 'https://via.placeholder.com/400x300?text=JavaBook'),
(5, 2, 'Wireless Mouse', 'Ergonomic 2.4GHz wireless mouse.', 799.00, 150, 'Electronics', 'https://via.placeholder.com/400x300?text=Mouse'),
(6, 3, 'Running Shoes', 'Lightweight running shoes, all sizes.', 1999.00, 60, 'Fashion', 'https://via.placeholder.com/400x300?text=Shoes');
