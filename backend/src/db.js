const fs = require('fs');
const path = require('path');
const { Pool } = require('pg');

function createPool(connectionString = process.env.DATABASE_URL) {
  return new Pool({
    connectionString,
    ssl: process.env.DATABASE_SSL === 'true' ? { rejectUnauthorized: false } : undefined,
  });
}

async function migrate(pool) {
  const sql = fs.readFileSync(path.join(__dirname, 'schema.sql'), 'utf8');
  await pool.query(sql);
}

module.exports = { createPool, migrate };
