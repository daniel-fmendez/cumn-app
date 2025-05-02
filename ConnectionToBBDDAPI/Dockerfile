# Imagen base de Node.js
FROM node:18-alpine

# Creamos el directorio de trabajo
WORKDIR /app

# Archivo de dependencias de NPM
COPY package*.json ./

# Instalamos las dependicias
RUN npm install

# Restos de archivo fuente
COPY . .

# Copiamos el certificado d
COPY BaltimoreCyberTrustRoot.crt.pem ./

RUN chmod 644 BaltimoreCyberTrustRoot.crt.pem

# Exponemos el puerto que usamos en app.js
EXPOSE 3000

# Ejecutamos el programa
CMD ["node", "app.js"]