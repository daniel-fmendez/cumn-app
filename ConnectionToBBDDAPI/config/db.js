const { Client } = require('pg');
const fs = require('fs');
require('dotenv').config();

const client = new Client({
    host: process.env.PGHOST,
    user: process.env.PGUSER,
    port: process.env.PGPORT,
    database: process.env.PGDATABASE,
    password: process.env.PGPASSWORD,
    ssl: {
        ca: fs.readFileSync("BaltimoreCyberTrustRoot.crt.pem").toString(),
        rejectUnauthorized: false
    }
});

const connectDB = async () => {
    try {
        await client.connect();
        console.log("Conectado a la base de datos");
    } catch (err) {
        console.error("Error al conectar:", err);
        process.exit(1); // Terminar el proceso con error
    }
};

module.exports = { client, connectDB };