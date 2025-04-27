const { client } = require('../config/db');

// Crear un nuevo mazo
const createDeck = async (req, res) => {
    const { name, user_id, hero_id } = req.body;

    try {
        const result = await client.query(
            'INSERT INTO public.decks (name, user_id, hero_id) VALUES ($1, $2, $3) RETURNING *', 
            [name, user_id, hero_id]
        );
        res.status(201).json(result.rows[0]);
    } catch (err) {
        console.error("Error en createDeck:", err);
        res.status(500).send("Error interno");
    }
};

// Obtener mazos de un usuario
const getUserDecks = async (req, res) => {
    const { user_id } = req.params;

    try {
        const result = await client.query(
            `SELECT * FROM public.decks WHERE user_id = $1 ORDER BY id ASC`,
            [user_id]
        );
        res.status(200).json(result.rows);
    } catch (err) {
        console.error("Error en getUserDecks:", err);
        res.status(500).send("Error interno");
    }
};

// Actualizar carta en un mazo
const updateDeckCard = async (req, res) => {
    const { deck_id, card_id, quantity } = req.body;

    try {
        if (quantity > 0) {
            const result = await client.query(
                `INSERT INTO public.deck_cards (deck_id, card_id, quantity)
                VALUES ($1, $2, $3)
                ON CONFLICT (deck_id, card_id) 
                DO UPDATE SET quantity = EXCLUDED.quantity
                RETURNING *`,
                [deck_id, card_id, quantity]
            );
            res.status(200).json(result.rows[0]);
        } else {
            await client.query(
                'DELETE FROM public.deck_cards WHERE deck_id = $1 AND card_id = $2',
                [deck_id, card_id]
            );
            res.status(200).send("Carta eliminada del mazo");
        }
    } catch (err) {
        console.error("Error en updateDeckCard:", err);
        res.status(500).send("Error interno");
    }
};

// Obtener cartas de un mazo
const getDeckCards = async (req, res) => {
    const { deck_id } = req.params;

    try {
        const result = await client.query(
            `SELECT * FROM public.deck_cards WHERE deck_id = $1 ORDER BY card_id ASC`,
            [deck_id]
        );
        res.status(200).json(result.rows);
    } catch (err) {
        console.error("Error en getDeckCards:", err);
        res.status(500).send("Error interno");
    }
};

module.exports = {
    createDeck,
    getUserDecks,
    updateDeckCard,
    getDeckCards
};