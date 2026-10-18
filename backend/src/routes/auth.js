const express = require('express');

const PHONE_RE = /^\d{10}$/;

//profile routes
module.exports = function authRoutes(pool, verifyToken, requireUser) {
  const router = express.Router();

  //signup
  router.post('/signup', verifyToken, async (req, res, next) => {
    try {
      const body = req.body || {};
      const name = String(body.name || '').trim();
      const phone = String(body.phone || '').replace(/[\s-]/g, '');
      const { uid, email } = req.firebase;

      const errors = {};
      if (name.length < 2 || name.length > 100) errors.name = 'Name must be 2-100 characters';
      if (!PHONE_RE.test(phone)) errors.phone = 'Enter a valid 10-digit mobile number';
      if (Object.keys(errors).length) return res.status(400).json({ error: 'Validation failed', fields: errors });
      if (!email) return res.status(400).json({ error: 'Account has no email address' });

      const existing = await pool.query('SELECT id, name, email, phone FROM users WHERE firebase_uid = $1', [uid]);
      if (existing.rows.length) return res.status(200).json({ user: existing.rows[0] });

      const { rows } = await pool.query(
        `INSERT INTO users (firebase_uid, name, email, phone) VALUES ($1, $2, $3, $4)
         ON CONFLICT DO NOTHING
         RETURNING id, name, email, phone`,
        [uid, name, email, phone],
      );
      if (!rows.length) {
        return res.status(409).json({ error: 'Email already registered', fields: { email: 'Email already registered' } });
      }
      res.status(201).json({ user: rows[0] });
    } catch (err) {
      next(err);
    }
  });

  //login
  router.post('/login', requireUser, (req, res) => {
    res.json({ user: req.user });
  });

  return router;
};
