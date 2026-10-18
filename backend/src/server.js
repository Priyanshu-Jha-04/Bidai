require('dotenv').config();
const { createPool, migrate } = require('./db');
const { createApp } = require('./app');

async function main() {
  const pool = createPool();
  await migrate(pool);
  const port = process.env.PORT || 3000;
  createApp(pool).listen(port, '0.0.0.0', () => console.log(`Bid.ai API listening on :${port}`));
}

main().catch((err) => {
  console.error('Failed to start', err);
  process.exit(1);
});
