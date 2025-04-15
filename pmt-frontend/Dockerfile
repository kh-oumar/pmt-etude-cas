FROM node:22 as build
WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

# Construire l'application Angular en mode production en précisant le projet
RUN npm run build -- --project pmt-frontend --configuration production

# Étape finale : utiliser Nginx pour servir l'application
FROM nginx:1.25-alpine

# Copier les fichiers générés dans le dossier de build Angular vers le dossier par défaut de Nginx
COPY --from=build /app/dist/pmt-frontend/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
