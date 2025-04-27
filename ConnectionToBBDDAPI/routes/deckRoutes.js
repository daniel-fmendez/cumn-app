const express = require('express');
const router = express.Router();
const deckController = require('../controllers/deckController');

// Rutas para mazos
router.post('/add_deck', deckController.createDeck);
router.get('/decks/:user_id', deckController.getUserDecks);
router.post('/update_deck_card', deckController.updateDeckCard);
router.get('/deck_cards/:deck_id', deckController.getDeckCards);

module.exports = router;