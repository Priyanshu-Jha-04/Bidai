const express = require('express');
const multer = require('multer');
const path = require('path');
const crypto = require('crypto');
const { CATEGORIES, CONDITIONS, SELLER_TYPES, MAX_IMAGES } = require('../constants');

module.exports = function productRoutes(pool, uploadDir) {
  const router = express.Router();

  const upload = multer({
    storage: multer.diskStorage({
      destination: uploadDir,
      filename: (_req, file, cb) =>
        cb(null, `${Date.now()}-${crypto.randomBytes(6).toString('hex')}${path.extname(file.originalname).toLowerCase() || '.jpg'}`),
    }),
    limits: { fileSize: 5 * 1024 * 1024 },
    fileFilter: (_req, file, cb) => cb(null, /^image\//.test(file.mimetype)),
  });

  //photo fields
  const uploadPhotos = upload.fields([
    { name: 'images', maxCount: MAX_IMAGES },
    { name: 'image', maxCount: 1 },
  ]);

  const SELECT = `
    SELECT p.id, p.title, p.description, p.price::float8 AS price, p.category, p.condition,
           p.brand, p.product_type, p.seller_type, p.location,
           p.original_price::float8 AS original_price, p.eco_score, p.verified,
           p.image_url, p.images, p.status, p.created_at, p.seller_id, u.name AS seller_name
    FROM products p JOIN users u ON u.id = p.seller_id`;

  const toProduct = (req) => (row) => ({
    id: row.id,
    title: row.title,
    description: row.description,
    price: row.price,
    category: row.category,
    condition: row.condition,
    brand: row.brand,
    productType: row.product_type,
    sellerType: row.seller_type,
    location: row.location,
    originalPrice: row.original_price,
    ecoScore: row.eco_score,
    verified: row.verified,
    imageUrl: row.image_url ? `${req.protocol}://${req.get('host')}${row.image_url}` : null,
    images: (row.images || []).map((path) => `${req.protocol}://${req.get('host')}${path}`),
    status: row.status,
    createdAt: row.created_at,
    seller: { id: row.seller_id, name: row.seller_name },
  });

  //categories
  router.get('/categories', (_req, res) => res.json({ categories: CATEGORIES, conditions: CONDITIONS }));

  //list
  router.get('/', async (req, res, next) => {
    try {
      const page = Math.max(parseInt(req.query.page, 10) || 1, 1);
      const limit = Math.min(Math.max(parseInt(req.query.limit, 10) || 20, 1), 50);
      const where = [`p.status = 'active'`];
      const params = [];
      if (req.query.category && req.query.category !== 'All') {
        params.push(String(req.query.category));
        where.push(`p.category = $${params.length}`);
      }
      if (req.query.brand) {
        params.push(String(req.query.brand));
        where.push(`p.brand = $${params.length}`);
      }
      if (req.query.search) {
        params.push(`%${String(req.query.search).trim()}%`);
        where.push(`(p.title ILIKE $${params.length} OR p.description ILIKE $${params.length} OR p.brand ILIKE $${params.length})`);
      }
      params.push(limit, (page - 1) * limit);
      const { rows } = await pool.query(
        `${SELECT} WHERE ${where.join(' AND ')} ORDER BY p.created_at DESC, p.id DESC
         LIMIT $${params.length - 1} OFFSET $${params.length}`,
        params,
      );
      res.json({ page, limit, products: rows.map(toProduct(req)) });
    } catch (err) {
      next(err);
    }
  });

  //mine
  router.get('/mine', async (req, res, next) => {
    try {
      const { rows } = await pool.query(`${SELECT} WHERE p.seller_id = $1 ORDER BY p.created_at DESC, p.id DESC`, [req.user.id]);
      res.json({ products: rows.map(toProduct(req)) });
    } catch (err) {
      next(err);
    }
  });

  //one
  router.get('/:id(\\d+)', async (req, res, next) => {
    try {
      const { rows } = await pool.query(`${SELECT} WHERE p.id = $1`, [req.params.id]);
      if (!rows.length) return res.status(404).json({ error: 'Product not found' });
      res.json({ product: toProduct(req)(rows[0]) });
    } catch (err) {
      next(err);
    }
  });

  //brands
  router.get('/brands', async (req, res, next) => {
    try {
      const params = [];
      let extra = '';
      if (req.query.category) {
        params.push(String(req.query.category));
        extra = ' AND category = $1';
      }
      const { rows } = await pool.query(
        `SELECT brand FROM products WHERE status = 'active' AND brand IS NOT NULL${extra}
         GROUP BY brand ORDER BY MAX(created_at) DESC LIMIT 12`,
        params,
      );
      res.json({ brands: rows.map((r) => r.brand) });
    } catch (err) {
      next(err);
    }
  });

  //create
  router.post('/', uploadPhotos, async (req, res, next) => {
    try {
      const body = req.body || {};
      const title = String(body.title || '').trim();
      const description = String(body.description || '').trim();
      const price = Number(body.price);
      const category = String(body.category || '').trim();
      const condition = String(body.condition || 'used').trim();
      const brand = String(body.brand || '').trim();
      const productType = String(body.productType || '').trim();
      const sellerType = String(body.sellerType || 'individual').trim();
      const location = String(body.location || '').trim();

      const errors = {};
      if (title.length < 3 || title.length > 120) errors.title = 'Title must be 3-120 characters';
      if (!Number.isFinite(price) || price <= 0 || price > 9999999999) errors.price = 'Enter a valid price greater than 0';
      if (!CATEGORIES.includes(category)) errors.category = 'Choose a valid category';
      if (!CONDITIONS.includes(condition)) errors.condition = 'Choose a valid condition';
      if (description.length > 2000) errors.description = 'Description is too long';
      if (brand.length > 80) errors.brand = 'Brand is too long';
      if (productType.length > 80) errors.productType = 'Product type is too long';
      if (location.length > 120) errors.location = 'Location is too long';
      if (!SELLER_TYPES.includes(sellerType)) errors.sellerType = 'Choose vendor / business or individual';
      if (Object.keys(errors).length) return res.status(400).json({ error: 'Validation failed', fields: errors });

      const files = [...(req.files?.images || []), ...(req.files?.image || [])].slice(0, MAX_IMAGES);
      const images = files.map((f) => `/uploads/${f.filename}`);
      const { rows } = await pool.query(
        `INSERT INTO products (seller_id, title, description, price, category, condition, brand, product_type,
                               seller_type, location, image_url, images)
         VALUES ($1, $2, $3, $4, $5, $6, NULLIF($7, ''), NULLIF($8, ''), $9, NULLIF($10, ''), $11, $12) RETURNING id`,
        [req.user.id, title, description, price.toFixed(2), category, condition, brand, productType, sellerType, location, images[0] || null, images],
      );
      const created = await pool.query(`${SELECT} WHERE p.id = $1`, [rows[0].id]);
      res.status(201).json({ product: toProduct(req)(created.rows[0]) });
    } catch (err) {
      next(err);
    }
  });

  //mark sold
  router.patch('/:id(\\d+)/sold', async (req, res, next) => {
    try {
      const { rowCount } = await pool.query(
        `UPDATE products SET status = 'sold' WHERE id = $1 AND seller_id = $2`,
        [req.params.id, req.user.id],
      );
      if (!rowCount) return res.status(404).json({ error: 'Product not found' });
      const { rows } = await pool.query(`${SELECT} WHERE p.id = $1`, [req.params.id]);
      res.json({ product: toProduct(req)(rows[0]) });
    } catch (err) {
      next(err);
    }
  });

  //delete
  router.delete('/:id(\\d+)', async (req, res, next) => {
    try {
      const { rowCount } = await pool.query('DELETE FROM products WHERE id = $1 AND seller_id = $2', [req.params.id, req.user.id]);
      if (!rowCount) return res.status(404).json({ error: 'Product not found' });
      res.status(204).end();
    } catch (err) {
      next(err);
    }
  });

  return router;
};
