# Étape 1 : build Angular
FROM node:22 AS build
WORKDIR /app

# Copier package.json et installer
COPY package*.json ./
RUN npm install

# Copier tout le code et builder
COPY . .
RUN npm run build -- --project pmt-frontend --configuration production
# => génère /app/dist/pmt-frontend/browser

# Étape 2 : servir via Nginx avec fallback pour les routes SPA
FROM nginx:1.25-alpine

# 1. Remplacer la conf Nginx par défaut
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 2. Copier le build Angular
COPY --from=build /app/dist/pmt-frontend/browser /usr/share/nginx/html

# Exposer le port HTTP
EXPOSE 80

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
