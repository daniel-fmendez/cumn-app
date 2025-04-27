const express = require('express');
const router = express.Router();
const cardController = require('../controllers/cardController');

// Rutas para cartas
router.get('/heroes', cardController.getHeroes);
router.get('/cards', cardController.searchCards);
router.get('/card/:card_id', cardController.getCardById);
router.get('/editions', cardController.getEditions);

module.exports = router;