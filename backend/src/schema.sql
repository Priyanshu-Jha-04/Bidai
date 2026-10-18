CREATE TABLE IF NOT EXISTS users (
  id            SERIAL PRIMARY KEY,
  name          VARCHAR(100)  NOT NULL,
  email         VARCHAR(255)  NOT NULL UNIQUE,
  phone         VARCHAR(15),
  firebase_uid  VARCHAR(128)  UNIQUE,
  created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

--migrations
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(15);
ALTER TABLE users ADD COLUMN IF NOT EXISTS firebase_uid VARCHAR(128) UNIQUE;
ALTER TABLE users DROP COLUMN IF EXISTS password_hash;

CREATE TABLE IF NOT EXISTS products (
  id          SERIAL PRIMARY KEY,
  seller_id   INTEGER       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  title       VARCHAR(120)  NOT NULL,
  description TEXT          NOT NULL DEFAULT '',
  price       NUMERIC(12,2) NOT NULL CHECK (price > 0),
  category    VARCHAR(40)   NOT NULL,
  condition   VARCHAR(20)   NOT NULL DEFAULT 'used',
  image_url   TEXT,
  status      VARCHAR(10)   NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'sold')),
  created_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_products_seller   ON products(seller_id);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_created  ON products(created_at DESC);

--listing details
ALTER TABLE products ADD COLUMN IF NOT EXISTS brand          VARCHAR(80);
ALTER TABLE products ADD COLUMN IF NOT EXISTS product_type   VARCHAR(80);
ALTER TABLE products ADD COLUMN IF NOT EXISTS seller_type    VARCHAR(10) NOT NULL DEFAULT 'individual';
ALTER TABLE products ADD COLUMN IF NOT EXISTS location       VARCHAR(120);
--inspection fields
ALTER TABLE products ADD COLUMN IF NOT EXISTS original_price NUMERIC(12,2);
ALTER TABLE products ADD COLUMN IF NOT EXISTS eco_score      SMALLINT CHECK (eco_score BETWEEN 0 AND 100);
ALTER TABLE products ADD COLUMN IF NOT EXISTS verified       BOOLEAN NOT NULL DEFAULT FALSE;
--photos
ALTER TABLE products ADD COLUMN IF NOT EXISTS images         TEXT[] NOT NULL DEFAULT '{}';
UPDATE products SET images = ARRAY[image_url] WHERE image_url IS NOT NULL AND cardinality(images) = 0;

CREATE INDEX IF NOT EXISTS idx_products_brand ON products(brand);
