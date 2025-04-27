const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');

// Rutas para usuarios
router.post('/add_user', userController.registerUser);
router.post('/login', userController.loginUser);
router.post('/update_user_card', userController.updateUserCard);
router.get('/user_collection/:user_id', userController.getUserCollection);

module.exports = router;