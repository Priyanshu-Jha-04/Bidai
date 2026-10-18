const express = require('express');
const cors = require('cors');
const path = require('path');
const fs = require('fs');
const multer = require('multer');
const { createFirebaseVerifier, verifyToken, requireUser } = require('./middleware/auth');
const authRoutes = require('./routes/auth');
const productRoutes = require('./routes/products');

const DEFAULT_PROJECT_ID = 'bidai-e8512bf5';

function createApp(
  pool,
  uploadDir = path.join(__dirname, '..', 'uploads'),
  verifyIdToken = createFirebaseVerifier(process.env.FIREBASE_PROJECT_ID || DEFAULT_PROJECT_ID),
) {
  fs.mkdirSync(uploadDir, { recursive: true });
  const app = express();
  const needToken = verifyToken(verifyIdToken);
  const needUser = requireUser(pool, verifyIdToken);

  app.set('trust proxy', true);
  app.use(cors());
  app.use(express.json());
  app.use('/uploads', express.static(uploadDir));

  app.get('/health', (_req, res) => res.json({ status: 'ok' }));
  app.use('/api/auth', authRoutes(pool, needToken, needUser));
  app.use('/api/products', needUser, productRoutes(pool, uploadDir));

  app.use((_req, res) => res.status(404).json({ error: 'Not found' }));
  // eslint-disable-next-line no-unused-vars
  app.use((err, _req, res, _next) => {
    if (err instanceof multer.MulterError) return res.status(400).json({ error: err.message });
    if (err.type === 'entity.parse.failed') return res.status(400).json({ error: 'Malformed JSON body' });
    console.error(err);
    res.status(500).json({ error: 'Internal server error' });
  });
  return app;
}

module.exports = { createApp };
