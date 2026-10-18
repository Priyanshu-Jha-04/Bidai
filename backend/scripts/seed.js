//demo data
require('dotenv').config();
const { createPool, migrate } = require('../src/db');

const SELLER_UID = 'seed-demo-seller';

//row
const LISTINGS = [
  ['Cars', 'Black Tata punch', 'Tata Motors', 215000, 226000],
  ['Cars', 'Tata I20', 'Tata Motors', 215000, 235000],
  ['Cars', 'Hyundai Creta', 'Hyundai', 980000, 1050000],
  ['Cars', 'Honda City', 'Honda', 640000, 700000],
  ['Bikes', 'MT 16', 'Yamaha Motor Company', 85000, 85000],
  ['Bikes', 'Himalayan 650', 'Royal Enfield', 95000, 98000],
  ['Bikes', 'Pulsar NS200', 'Bajaj Auto', 72000, 80000],
  ['Real Estate', 'Open house', '3 BHK', 215000, 215000],
  ['Mobiles', 'Vivo 20', 'Vivo', 25000, 28000],
  ['Clothing', 'Black Slim fit jeans', 'Red Tap jeans', 500, 700],
];

(async () => {
  const pool = createPool();
  await migrate(pool);
  const existing = await pool.query('SELECT id FROM users WHERE firebase_uid = $1', [SELLER_UID]);
  if (existing.rows.length) {
    console.log('Demo data already present.');
    return pool.end();
  }
  const { rows } = await pool.query(
    `INSERT INTO users (firebase_uid, name, email, phone) VALUES ($1, 'Bid.ai Demo', 'demo-seller@bid.ai', '9999999999') RETURNING id`,
    [SELLER_UID],
  );
  for (const [category, title, brand, price, original] of LISTINGS) {
    await pool.query(
      `INSERT INTO products (seller_id, title, description, price, original_price, category, condition, brand,
                             seller_type, location, eco_score, verified)
       VALUES ($1, $2, '', $3, $4, $5, 'used', $6, 'vendor', 'Bengaluru', 82, TRUE)`,
      [rows[0].id, title, price, original, category, brand],
    );
  }
  console.log(`Seeded ${LISTINGS.length} demo listings.`);
  await pool.end();
})().catch((e) => { console.error(e); process.exit(1); });
