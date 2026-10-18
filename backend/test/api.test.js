//api tests
const { test, before, after } = require('node:test');
const assert = require('node:assert/strict');
const os = require('os');
const fs = require('fs');
const path = require('path');
const EmbeddedPostgres = require('embedded-postgres').default;
const { generateKeyPair, SignJWT } = require('jose');
const { createFirebaseVerifier } = require('../src/middleware/auth');
const { createPool, migrate } = require('../src/db');
const { createApp } = require('../src/app');

const PG_PORT = 54329;
let pg, pool, server, base, tmp;

const PROJECT_ID = 'test-project';
let privateKey;

//sign token
const idToken = (uid, email, over = {}) =>
  new SignJWT({ email, ...over.claims })
    .setProtectedHeader({ alg: 'RS256', kid: 'test' })
    .setSubject(uid)
    .setIssuer(over.iss ?? `https://securetoken.google.com/${PROJECT_ID}`)
    .setAudience(over.aud ?? PROJECT_ID)
    .setIssuedAt()
    .setExpirationTime(over.exp ?? '1h')
    .sign(privateKey);
const bearer = async (uid, email) => 'Bearer ' + (await idToken(uid, email));

const json = (method, url, body, auth) =>
  fetch(base + url, {
    method,
    headers: { 'Content-Type': 'application/json', ...(auth ? { Authorization: auth } : {}) },
    body: body ? JSON.stringify(body) : undefined,
  });

before(async () => {
  tmp = fs.mkdtempSync(path.join(os.tmpdir(), 'bidai-test-'));
  pg = new EmbeddedPostgres({ databaseDir: path.join(tmp, 'pg'), user: 'postgres', password: 'postgres', port: PG_PORT, persistent: false });
  await pg.initialise();
  await pg.start();
  await pg.createDatabase('bidai');
  pool = createPool(`postgres://postgres:postgres@localhost:${PG_PORT}/bidai`);
  await migrate(pool);
  const pair = await generateKeyPair('RS256');
  privateKey = pair.privateKey;
  const verifier = createFirebaseVerifier(PROJECT_ID, async () => pair.publicKey);
  server = createApp(pool, path.join(tmp, 'uploads'), verifier).listen(0);
  base = `http://localhost:${server.address().port}`;
});

after(async () => {
  server?.close();
  await pool?.end();
  await pg?.stop();
  fs.rmSync(tmp, { recursive: true, force: true });
});

test('signup validates input', async () => {
  const res = await json('POST', '/api/auth/signup', { name: 'A', phone: '12' }, await bearer('u-bad', 'bad@example.com'));
  assert.equal(res.status, 400);
  const body = await res.json();
  assert.deepEqual(Object.keys(body.fields).sort(), ['name', 'phone']);
});

test('signup, repeat, duplicate email, login', async () => {
  const asha = await bearer('u-asha', 'Asha@Example.com');
  const ok = await json('POST', '/api/auth/signup', { name: 'Asha', phone: '9876543210' }, asha);
  assert.equal(ok.status, 201);
  const created = (await ok.json()).user;
  assert.equal(created.email, 'asha@example.com');
  assert.equal(created.phone, '9876543210');

  //repeat signup
  const again = await json('POST', '/api/auth/signup', { name: 'Other', phone: '1111111111' }, asha);
  assert.equal(again.status, 200);
  assert.equal((await again.json()).user.id, created.id);

  //email clash
  const dup = await json('POST', '/api/auth/signup', { name: 'Asha 2', phone: '9876543210' }, await bearer('u-asha-2', 'asha@example.com'));
  assert.equal(dup.status, 409);

  const login = await json('POST', '/api/auth/login', null, asha);
  assert.equal(login.status, 200);
  assert.equal((await login.json()).user.name, 'Asha');

  //no profile
  assert.equal((await json('POST', '/api/auth/login', null, await bearer('u-nobody', 'nobody@example.com'))).status, 401);
});

test('rejects missing, malformed, expired and foreign tokens', async () => {
  assert.equal((await json('POST', '/api/auth/login')).status, 401);
  assert.equal((await json('POST', '/api/auth/login', null, 'Bearer garbage')).status, 401);
  assert.equal((await json('POST', '/api/auth/login', null, 'Basic YTpi')).status, 401);
  const expired = 'Bearer ' + (await idToken('u-asha', 'asha@example.com', { exp: Math.floor(Date.now() / 1000) - 60 }));
  assert.equal((await json('POST', '/api/auth/login', null, expired)).status, 401);
  const wrongAud = 'Bearer ' + (await idToken('u-asha', 'asha@example.com', { aud: 'other-project' }));
  assert.equal((await json('POST', '/api/auth/login', null, wrongAud)).status, 401);
  const wrongIss = 'Bearer ' + (await idToken('u-asha', 'asha@example.com', { iss: 'https://securetoken.google.com/other-project' }));
  assert.equal((await json('POST', '/api/auth/login', null, wrongIss)).status, 401);
});

test('products require auth', async () => {
  assert.equal((await json('GET', '/api/products')).status, 401);
});

test('sell flow: create (multipart + image), list, filter, detail, sold, delete', async () => {
  const auth = await bearer('u-ravi', 'ravi@example.com');
  await json('POST', '/api/auth/signup', { name: 'Ravi', phone: '9876543210' }, auth);

  const bad = new FormData();
  bad.append('title', 'x');
  const badRes = await fetch(base + '/api/products', { method: 'POST', headers: { Authorization: auth }, body: bad });
  assert.equal(badRes.status, 400);
  assert.ok((await badRes.json()).fields.title);

  const form = new FormData();
  form.append('title', 'Vintage Camera');
  form.append('description', 'Works great');
  form.append('price', '4999.50');
  form.append('category', 'Electronics');
  form.append('condition', 'like_new');
  form.append('brand', 'Canon');
  form.append('productType', 'Camera');
  form.append('sellerType', 'vendor');
  form.append('location', 'Bengaluru');
  const jpeg = () => new Blob([Buffer.from([0xff, 0xd8, 0xff, 0xd9])], { type: 'image/jpeg' });
  form.append('images', jpeg(), 'cam1.jpg');
  form.append('images', jpeg(), 'cam2.jpg');
  const created = await fetch(base + '/api/products', { method: 'POST', headers: { Authorization: auth }, body: form });
  assert.equal(created.status, 201);
  const { product } = await created.json();
  assert.equal(product.price, 4999.5);
  assert.equal(product.seller.name, 'Ravi');
  assert.match(product.imageUrl, /\/uploads\/.+\.jpg$/);
  assert.equal((await fetch(product.imageUrl)).status, 200);
  assert.equal(product.images.length, 2);
  assert.equal(product.images[0], product.imageUrl);
  assert.deepEqual([product.brand, product.productType, product.sellerType, product.location], ['Canon', 'Camera', 'vendor', 'Bengaluru']);
  assert.equal(product.verified, false);
  assert.equal(product.ecoScore, null);

  const brands = await (await json('GET', '/api/products/brands?category=Electronics', null, auth)).json();
  assert.deepEqual(brands.brands, ['Canon']);
  assert.equal((await (await json('GET', '/api/products?brand=Canon', null, auth)).json()).products.length, 1);
  assert.equal((await (await json('GET', '/api/products?brand=Nikon', null, auth)).json()).products.length, 0);

  const badSeller = new FormData();
  badSeller.append('title', 'Some bike');
  badSeller.append('price', '10');
  badSeller.append('category', 'Bikes');
  badSeller.append('sellerType', 'martian');
  const badSellerRes = await fetch(base + '/api/products', { method: 'POST', headers: { Authorization: auth }, body: badSeller });
  assert.equal(badSellerRes.status, 400);
  assert.ok((await badSellerRes.json()).fields.sellerType);

  const list = await (await json('GET', '/api/products?category=Electronics&search=camera', null, auth)).json();
  assert.equal(list.products.length, 1);
  const none = await (await json('GET', '/api/products?category=Books', null, auth)).json();
  assert.equal(none.products.length, 0);

  assert.equal((await json('GET', `/api/products/${product.id}`, null, auth)).status, 200);
  assert.equal((await json('GET', '/api/products/9999', null, auth)).status, 404);

  //not owner
  const other = await bearer('u-mallory', 'm@example.com');
  await json('POST', '/api/auth/signup', { name: 'Mallory', phone: '9876543210' }, other);
  assert.equal((await json('PATCH', `/api/products/${product.id}/sold`, null, other)).status, 404);
  assert.equal((await json('DELETE', `/api/products/${product.id}`, null, other)).status, 404);

  const sold = await (await json('PATCH', `/api/products/${product.id}/sold`, null, auth)).json();
  assert.equal(sold.product.status, 'sold');
  assert.equal((await (await json('GET', '/api/products', null, auth)).json()).products.length, 0);
  assert.equal((await (await json('GET', '/api/products/mine', null, auth)).json()).products.length, 1);

  assert.equal((await json('DELETE', `/api/products/${product.id}`, null, auth)).status, 204);
});
