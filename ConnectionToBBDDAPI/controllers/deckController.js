const { client } = require('../config/db');

// Crear un nuevo mazo
const createDeck = async (req, res) => {
    const { name, user_id, hero_id, type } = req.body;

    try {
        const result = await client.query(
            'INSERT INTO public.decks (name, user_id, hero_id, type) VALUES ($1, $2, $3, $4) RETURNING *', 
            [name, user_id, hero_id, type]
        );
        res.status(201).json(result.rows[0]);
    } catch (err) {
        console.error("Error en createDeck:", err);
        res.status(500).send("Error interno");
    }
};

//Delete deck 
const deleteDeck = async (req, res) => {
    const { id } = req.body;

    try {
        const result = await client.query(
            'DELETE FROM public.decks WHERE id = $1 RETURNING *',
            [id]
        );

        if (result.rowCount === 0) {
            return res.status(404).json({ message: "Deck no encontrado" });
        }

        res.status(200).json({ message: "Deck eliminado", deck: result.rows[0] });
    } catch (err) {
        console.error("Error en deleteDeck:", err);
        res.status(500).send("Error interno");
    }
};
//Actualizar deck
const updateDeck = async (req, res) => {
    const { id, name, hero_id, type } = req.body;

    try {
        const result = await client.query(
            'UPDATE public.decks SET name = $1, hero_id = $2, type = $3 WHERE id = $4 RETURNING *',
            [name, hero_id, type, id]
        );

        if (result.rowCount === 0) {
            return res.status(404).send("Deck no encontrado");
        }

        res.status(200).json(result.rows[0]);
    } catch (err) {
        console.error("Error en updateDeck:", err);
        res.status(500).send("Error interno");
    }
};

//Elimina las cartas de un deck
const cleanDeck = async (req, res) => {
    const { deck_id } = req.body;

    try {
        const result = await client.query(
            'DELETE FROM deck_cards WHERE deck_id = $1 RETURNING *',
            [deck_id]
        );

        if (result.rowCount === 0) {
            return res.status(404).send("Deck no encontrado");
        }

        res.status(200).json(result.rows[0]);
    } catch (err) {
        console.error("Error en updateDeck:", err);
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
            res.status(201).json(result.rows[0]);
        } else {
            await client.query(
                'DELETE FROM public.deck_cards WHERE deck_id = $1 AND card_id = $2 RETURNING *',
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
    getDeckCards,
    deleteDeck,
    updateDeck,
    cleanDeck
};