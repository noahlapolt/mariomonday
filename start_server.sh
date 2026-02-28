# Run this from root
cd frontend
# Needs to install node and enable pnpm in docker
npm install --global corepack@latest
corepack enable pnpm
pnpm install
pnpm build
cd ..
cd backendserver
DB_NAME=prod DB_HOST=database_server ./mvnw clean spring-boot:run