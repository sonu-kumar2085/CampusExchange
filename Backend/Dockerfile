# Use the official Node.js 20 LTS Alpine image for a lightweight runtime environment
FROM node:20-alpine

# Set the working directory inside the container
WORKDIR /campus_exchange/Backend


COPY package*.json ./
RUN npm install


COPY . .
EXPOSE 4000
CMD ["npm", "run", "dev"]
