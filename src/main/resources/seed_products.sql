-- Product seed (runs after users)
MERGE INTO products (id, seller_id, name, description, price, stock_qty, category, image_url) KEY(id) VALUES
(1, 2, 'Nova X5 Smartphone', '6.5-inch display, 128GB storage, 5000mAh battery.', 14999.00, 40, 'Mobiles', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=640&q=70'),
(2, 3, 'Pixel Pro 12 Smartphone', 'Triple camera, 256GB storage, fast charging.', 32999.00, 25, 'Mobiles', 'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?auto=format&fit=crop&w=640&q=70'),
(3, 2, 'UltraBook Air 14 Laptop', '14-inch laptop, 16GB RAM, 512GB SSD for work and study.', 58990.00, 15, 'Laptops', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=640&q=70'),
(4, 3, 'ProBook 15 Laptop', '15.6-inch laptop, dedicated graphics, backlit keyboard.', 72990.00, 10, 'Laptops', 'https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?auto=format&fit=crop&w=640&q=70'),
(5, 2, 'Sonic Bass Headphones', 'Over-ear headphones with deep bass and 30h battery.', 2499.00, 60, 'Headphones', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=640&q=70'),
(6, 3, 'Aero Wireless Headphones', 'Lightweight wireless headphones with noise isolation.', 3999.00, 45, 'Headphones', 'https://images.unsplash.com/photo-1583394838336-acd977736f90?auto=format&fit=crop&w=640&q=70'),
(7, 2, 'Slate Tab 10 Tablet', '10-inch tablet for reading, video and classes.', 12999.00, 30, 'Tablets', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?auto=format&fit=crop&w=640&q=70'),
(8, 3, 'Slate Tab Pro 11 Tablet', '11-inch tablet with stylus support and vivid display.', 21999.00, 20, 'Tablets', 'https://images.unsplash.com/photo-1561154464-82e9adf32764?auto=format&fit=crop&w=640&q=70'),
(9, 2, 'Pulse Smartwatch', 'Fitness tracking, heart-rate monitor, 7-day battery.', 4999.00, 50, 'Accessories', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=640&q=70'),
(10, 3, 'Boom Mini Speaker', 'Portable Bluetooth speaker with 12h playtime.', 1999.00, 70, 'Accessories', 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=640&q=70');
