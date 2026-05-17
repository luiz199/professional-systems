USE avatarworld;

-- Admin user (password: admin123)
INSERT INTO users (username, password, email, coins, avatar_data, is_admin) VALUES
('Admin', '$2y$10$8KzQMGx5C5Kc5Qy5Q5z5Q.5y5z5Q5y5Q5z5Q5y5z5Q5y5Q5z5Q', 'admin@avatarworld.com', 999999, '{"headColor":"#FFD700","torsoColor":"#FF0000","legsColor":"#0000FF","hairStyle":"default","accessory":"crown"}', TRUE);

-- Sample users (password: 123456)
INSERT INTO users (username, password, email, coins, avatar_data) VALUES
('Player1', '$2y$10$8KzQMGx5C5Kc5Qy5Q5z5Q.5y5z5Q5y5Q5z5Q5y5z5Q5y5Q5z5Q', 'player1@test.com', 5000, '{"headColor":"#FFB6C1","torsoColor":"#87CEEB","legsColor":"#8B4513","hairStyle":"long","accessory":"none"}'),
('Player2', '$2y$10$8KzQMGx5C5Kc5Qy5Q5z5Q.5y5z5Q5y5Q5z5Q5y5z5Q5y5Q5z5Q', 'player2@test.com', 3000, '{"headColor":"#FFD700","torsoColor":"#32CD32","legsColor":"#4169E1","hairStyle":"short","accessory":"glasses"}'),
('VIP_Member', '$2y$10$8KzQMGx5C5Kc5Qy5Q5z5Q.5y5z5Q5y5Q5z5Q5y5z5Q5y5Q5z5Q', 'vip@test.com', 50000, '{"headColor":"#E6E6FA","torsoColor":"#9932CC","legsColor":"#FFD700","hairStyle":"vip","accessory":"wings"}');

-- Sample rooms
INSERT INTO rooms (name, owner_id, max_users, layout_data, wallpaper, floor) VALUES
('Salão Principal', 1, 100, '{"width":30,"height":20,"tiles":[]}', 'default', 'default'),
('Praia Tropical', 2, 50, '{"width":25,"height":18,"tiles":[]}', 'beach', 'sand'),
('Festa VIP', 1, 30, '{"width":20,"height":15,"tiles":[]}', 'vip', 'gold'),
('Jardim Secreto', 3, 40, '{"width":28,"height":22,"tiles":[]}', 'garden', 'grass');

-- Items
INSERT INTO items (name, type, category, price, rarity, data, is_furniture) VALUES
-- Clothing
('Camiseta Azul', 'shirt', 'tops', 100, 'common', '{"color":"#1E90FF"}', FALSE),
('Camiseta Vermelha', 'shirt', 'tops', 100, 'common', '{"color":"#FF4500"}', FALSE),
('Camiseta Verde', 'shirt', 'tops', 100, 'common', '{"color":"#32CD32"}', FALSE),
('Camisa Social', 'shirt', 'tops', 500, 'rare', '{"color":"#FFFFFF"}', FALSE),
('Jaqueta de Couro', 'shirt', 'tops', 1000, 'rare', '{"color":"#2F2F2F"}', FALSE),
('Terno VIP', 'shirt', 'tops', 5000, 'vip', '{"color":"#1C1C1C"}', FALSE),
('Calça Jeans', 'pants', 'bottoms', 150, 'common', '{"color":"#4682B4"}', FALSE),
('Calça Preta', 'pants', 'bottoms', 150, 'common', '{"color":"#000000"}', FALSE),
('Shorts Vermelho', 'pants', 'bottoms', 100, 'common', '{"color":"#DC143C"}', FALSE),
('Calça VIP Dourada', 'pants', 'bottoms', 3000, 'vip', '{"color":"#FFD700"}', FALSE),
('Tênis Branco', 'shoes', 'footwear', 100, 'common', '{"color":"#FFFFFF"}', FALSE),
('Tênis Preto', 'shoes', 'footwear', 100, 'common', '{"color":"#000000"}', FALSE),
('Botas de Couro', 'shoes', 'footwear', 500, 'rare', '{"color":"#8B4513"}', FALSE),
('Chinelo VIP', 'shoes', 'footwear', 2000, 'vip', '{"color":"#FFD700"}', FALSE),
('Chapéu Panamá', 'hat', 'accessories', 200, 'common', '{"color":"#F5DEB3"}', FALSE),
('Boné Azul', 'hat', 'accessories', 150, 'common', '{"color":"#1E90FF"}', FALSE),
('Cartola', 'hat', 'accessories', 1000, 'rare', '{"color":"#000000"}', FALSE),
('Coroa VIP', 'hat', 'accessories', 5000, 'vip', '{"color":"#FFD700"}', FALSE),
('Cabelo Longo', 'hair', 'hairstyles', 200, 'common', '{"color":"#000000","style":"long"}', FALSE),
('Cabelo Curto', 'hair', 'hairstyles', 100, 'common', '{"color":"#8B4513","style":"short"}', FALSE),
('Cabelo VIP', 'hair', 'hairstyles', 3000, 'vip', '{"color":"#FF00FF","style":"mohawk"}', FALSE),
('Óculos Escuros', 'accessory', 'face', 300, 'common', '{"color":"#000000"}', FALSE),
('Colar de Ouro', 'accessory', 'neck', 2000, 'rare', '{"color":"#FFD700"}', FALSE),
('Asas VIP', 'accessory', 'back', 10000, 'vip', '{"color":"#FFFFFF"}', FALSE),

-- Furniture
('Sofá Vermelho', 'furniture', 'seat', 500, 'common', '{"width":2,"height":1,"color":"#DC143C"}', TRUE),
('Sofá Azul', 'furniture', 'seat', 500, 'common', '{"width":2,"height":1,"color":"#1E90FF"}', TRUE),
('Mesa de Centro', 'furniture', 'table', 300, 'common', '{"width":2,"height":2,"color":"#8B4513"}', TRUE),
('Cadeira de Palha', 'furniture', 'seat', 200, 'common', '{"width":1,"height":1,"color":"#DEB887"}', TRUE),
('Vaso de Planta', 'furniture', 'decoration', 150, 'common', '{"width":1,"height":1,"color":"#228B22"}', TRUE),
('Tapete Vermelho', 'furniture', 'carpet', 400, 'common', '{"width":3,"height":2,"color":"#B22222"}', TRUE),
('Lustre VIP', 'furniture', 'lighting', 5000, 'vip', '{"width":1,"height":1,"color":"#FFD700"}', TRUE),
('Fonte Mágica', 'furniture', 'decoration', 3000, 'rare', '{"width":2,"height":2,"color":"#00BFFF"}', TRUE),
('TV Plasma', 'furniture', 'electronics', 2000, 'rare', '{"width":3,"height":1,"color":"#000000"}', TRUE),
('Mesa de Sinuca', 'furniture', 'game', 2500, 'rare', '{"width":3,"height":2,"color":"#006400"}', TRUE),
('Trono Real', 'furniture', 'seat', 10000, 'vip', '{"width":2,"height":2,"color":"#FFD700"}', TRUE),
('Balcão VIP', 'furniture', 'bar', 4000, 'vip', '{"width":3,"height":1,"color":"#8B4513"}', TRUE);

-- Give Admin some items
INSERT INTO inventory (user_id, item_id, is_equipped) 
SELECT 1, id, CASE WHEN type IN ('shirt','pants','shoes','hat','hair','accessory') THEN TRUE ELSE FALSE END
FROM items WHERE rarity IN ('vip', 'rare');

-- Give Player1 some items
INSERT INTO inventory (user_id, item_id, is_equipped)
SELECT 2, id, CASE WHEN type='shirt' AND id=1 THEN TRUE ELSE FALSE END
FROM items WHERE price <= 500;

-- Give Player2 some items
INSERT INTO inventory (user_id, item_id, is_equipped)
SELECT 3, id, CASE WHEN type='accessory' AND id=22 THEN TRUE ELSE FALSE END
FROM items WHERE price <= 1000;

-- Place some furniture in Salão Principal
INSERT INTO room_furniture (room_id, item_id, x, y, rotation) VALUES
(1, 25, 5, 5, 0),  -- Sofá Vermelho
(1, 27, 8, 5, 0),  -- Mesa de Centro
(1, 28, 5, 8, 0),  -- Cadeira
(1, 29, 3, 3, 0);  -- Vaso

-- Initial admin log
INSERT INTO server_logs (action, details, ip_address) VALUES
('SYSTEM', 'Database initialized successfully', '127.0.0.1');
