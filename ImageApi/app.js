const fs = require("fs").promises;
const express = require("express");
const path = require("path");

const app = express();
const imagesDir = path.join(__dirname, "images");

app.use(express.static(imagesDir));

// Función recursiva para listar archivos y subdirectorios
async function listFilesRecursive(dir, baseDir = imagesDir) {
    let results = { folder: path.relative(baseDir, dir).replace(/\\/g, "/"), contents: [] };

    try {
        const files = await fs.readdir(dir, { withFileTypes: true });

        for (const file of files) {
            const filePath = path.join(dir, file.name);
            const relativePath = path.relative(baseDir, filePath).replace(/\\/g, "/");

            if (file.isDirectory()) {
                const subResults = await listFilesRecursive(filePath, baseDir);
                results.contents.push(subResults);
            } else {
                results.contents.push(relativePath);
            }
        }
    } catch (err) {
        console.error("Error leyendo el directorio:", err);
    }

    return results;
}

// Endpoint para listar archivos y carpetas recursivamente
app.get("/list-images", async (req, res) => {
    const data = await listFilesRecursive(imagesDir);
    res.json(data);
});

console.log("Listening on port 4000");
app.listen(4000, () => {
    console.log("Server is running");
});
