const { client } = require('../config/db');

// Obtener todos los héroes
const getHeroes = async (req, res) => {
    try {
        const result = await client.query("SELECT * FROM cards WHERE 'Hero' = ANY(types)");
        res.json(result.rows);
    } catch (err) {
        console.error("Error en getHeroes:", err);
        res.status(500).send("Error interno");
    }
};

// Buscar cartas por nombre
const searchCards = async (req, res) => {
    const searchTerm = req.query.name || '';
    try {
        const result = await client.query(
            'SELECT * FROM cards WHERE name ILIKE $1', 
            [`%${searchTerm}%`]
        );
        res.json(result.rows);
    } catch (err) {
        console.error("Error en searchCards:", err);
        res.status(500).send("Error interno");
    }
};

// Obtener carta por ID
const getCardById = async (req, res) => {
    const { card_id } = req.params;
    try {
        const result = await client.query(
            'SELECT * FROM cards WHERE unique_id = $1',
            [card_id]
        );

        if (result.rows.length > 0) {
            res.json(result.rows[0]);
        } else {
            res.status(404).send("Carta no encontrada");
        }
    } catch (err) {
        console.error("Error en getCardById:", err);
        res.status(500).send("Error interno");
    }
};

// Obtener ediciones de una carta
const getEditions = async (req, res) => {
    const searchTerm = req.query.id || '';
    try {
        const result = await client.query(
            'SELECT * FROM card_printings WHERE card_unique_id = $1', 
            [searchTerm]
        );
        res.json(result.rows);
    } catch (err) {
        console.error("Error en getEditions:", err);
        res.status(500).send("Error interno");
    }
};

module.exports = {
    getHeroes,
    searchCards,
    getCardById,
    getEditions
};