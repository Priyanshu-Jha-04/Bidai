//dev server
const path = require('path');
const fs = require('fs');
const EmbeddedPostgres = require('embedded-postgres').default;
const { createPool, migrate } = require('../src/db');
const { createApp } = require('../src/app');

const dir = path.join(__dirname, '..', '.pgdata');
const fresh = !fs.existsSync(path.join(dir, 'PG_VERSION'));
const pg = new EmbeddedPostgres({ databaseDir: dir, user: 'postgres', password: 'postgres', port: 5432, persistent: true });

(async () => {
  if (fresh) await pg.initialise();
  await pg.start();
  if (fresh) await pg.createDatabase('bidai');
  const pool = createPool('postgres://postgres:postgres@localhost:5432/bidai');
  await migrate(pool);
  const port = process.env.PORT || 3000;
  createApp(pool).listen(port, '0.0.0.0', () => console.log(`Bid.ai API on http://localhost:${port}  (emulator: http://10.0.2.2:${port})`));
})().catch((e) => { console.error(e); process.exit(1); });

process.on('SIGINT', async () => { await pg.stop(); process.exit(0); });
