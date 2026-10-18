//dev database
const path = require('path');
const fs = require('fs');
const EmbeddedPostgres = require('embedded-postgres').default;

const dir = path.join(__dirname, '..', '.pgdata');
const fresh = !fs.existsSync(path.join(dir, 'PG_VERSION'));

const pg = new EmbeddedPostgres({ databaseDir: dir, user: 'postgres', password: 'postgres', port: 5432, persistent: true });

(async () => {
  if (fresh) await pg.initialise();
  await pg.start();
  if (fresh) await pg.createDatabase('bidai');
  console.log('PostgreSQL ready on postgres://postgres:postgres@localhost:5432/bidai  (Ctrl+C to stop)');
})();

process.on('SIGINT', async () => {
  await pg.stop();
  process.exit(0);
});
