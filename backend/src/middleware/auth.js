const { createRemoteJWKSet, jwtVerify } = require('jose');

const FIREBASE_JWKS_URL = 'https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com';

//verifier
function createFirebaseVerifier(projectId, keys = createRemoteJWKSet(new URL(FIREBASE_JWKS_URL))) {
  return async function verifyIdToken(token) {
    const { payload } = await jwtVerify(token, keys, {
      issuer: `https://securetoken.google.com/${projectId}`,
      audience: projectId,
      algorithms: ['RS256'],
    });
    if (!payload.sub) throw new Error('Token has no subject');
    return { uid: payload.sub, email: payload.email ? String(payload.email).toLowerCase() : null };
  };
}

function bearerToken(req) {
  const [scheme, token] = (req.headers.authorization || '').split(' ');
  return scheme === 'Bearer' && token ? token : null;
}

function unauthorized(res, message = 'Please sign in again') {
  //no header
  return res.status(401).json({ error: message });
}

//token only
function verifyToken(verifyIdToken) {
  return async (req, res, next) => {
    const token = bearerToken(req);
    if (!token) return unauthorized(res);
    try {
      req.firebase = await verifyIdToken(token);
      next();
    } catch {
      unauthorized(res);
    }
  };
}

//token and profile
function requireUser(pool, verifyIdToken) {
  const verified = verifyToken(verifyIdToken);
  return (req, res, next) =>
    verified(req, res, async (err) => {
      if (err) return next(err);
      try {
        const { rows } = await pool.query('SELECT id, name, email, phone FROM users WHERE firebase_uid = $1', [req.firebase.uid]);
        if (!rows.length) return unauthorized(res, 'Account profile not found. Please sign up again.');
        req.user = rows[0];
        next();
      } catch (e) {
        next(e);
      }
    });
}

module.exports = { createFirebaseVerifier, verifyToken, requireUser };
