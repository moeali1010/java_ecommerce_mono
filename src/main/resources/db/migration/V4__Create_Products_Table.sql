-- Create products table
CREATE TABLE IF NOT EXISTS products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_description TEXT,
    price DECIMAL(15,2) NOT NULL,
    stock_quantity INT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_by VARCHAR(255),
    created_at DATETIME,
    updated_by VARCHAR(255),
    updated_at DATETIME,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES product_category(category_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uk_product_name UNIQUE (product_name),
    INDEX idx_product_name (product_name),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
