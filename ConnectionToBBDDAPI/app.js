const express = require('express');
const app = express();
require('dotenv').config();

// Middleware
app.use(express.json());

// Importar rutas (verifica las rutas de importación)
const cardRoutes = require('./routes/cardRoutes');
const userRoutes = require('./routes/userRoutes');
const deckRoutes = require('./routes/deckRoutes');

// Conexión a la BD (modifiqué para que uses la configuración existente)
const { client, connectDB } = require('./config/db');
connectDB();

// Usar rutas - asegúrate de que estas líneas sean correctas
app.use('/', cardRoutes);
app.use('/', userRoutes);
app.use('/', deckRoutes);

// Puerto y arranque del servidor
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`API en http://localhost:${PORT}`);
});