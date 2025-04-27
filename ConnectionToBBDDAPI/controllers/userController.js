const { client } = require('../config/db');

// Registrar nuevo usuario
const registerUser = async (req, res) => {
    const { nickname, email, password_hash } = req.body;
  
    try {
        const result = await client.query(
            'INSERT INTO public.users (nickname, email, password_hash) VALUES ($1, $2, $3) RETURNING *', 
            [nickname, email, password_hash]
        );
        res.status(201).json(result.rows[0]);
    } catch (err) {
        console.error("Error en registerUser:", err);
        res.status(500).send("Error interno");
    }
};

// Login de usuario
const loginUser = async (req, res) => {
    const { email, password } = req.body;

    try {
        const result = await client.query(
            'SELECT * FROM public.users WHERE email = $1',
            [email]
        );

        if (result.rows.length === 0) {
            return res.status(401).json({ error: 'Usuario no encontrado' });
        }

        const user = result.rows[0];
        if (password === user.password_hash) {
            res.json({
                id: user.id,
                nickname: user.nickname
            });
        } else {
            res.status(401).json({ error: 'Contraseña incorrecta' });
        }
    } catch (err) {
        console.error("Error en loginUser:", err);
        res.status(500).send("Error interno");
    }
};

// Actualizar carta en la colección del usuario
const updateUserCard = async (req, res) => {
    const { user_id, card_id, quantity } = req.body;

    try {
        if (quantity > 0) {
            const result = await client.query(
                `INSERT INTO public.user_cards (user_id, card_id, quantity)
                VALUES ($1, $2, $3)
                ON CONFLICT (user_id, card_id) 
                DO UPDATE SET quantity = EXCLUDED.quantity
                RETURNING *`,
                [user_id, card_id, quantity]
            );
            res.status(200).json(result.rows[0]);
        } else {
            await client.query(
                'DELETE FROM public.user_cards WHERE user_id = $1 AND card_id = $2',
                [user_id, card_id]
            );
            res.status(200).send("Carta eliminada de la colección");
        }
    } catch (err) {
        console.error("Error en updateUserCard:", err);
        res.status(500).send("Error interno");
    }
};

// Obtener colección del usuario
const getUserCollection = async (req, res) => {
    const { user_id } = req.params;

    try {
        const result = await client.query(
            `SELECT * FROM public.user_cards WHERE user_id = $1`,
            [user_id]
        );
        res.status(200).json(result.rows);
    } catch (err) {
        console.error("Error en getUserCollection:", err);
        res.status(500).send("Error interno");
    }
};

module.exports = {
    registerUser,
    loginUser,
    updateUserCard,
    getUserCollection
};