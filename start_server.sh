# Run this from root
cd frontend
# Needs to install node and enable pnpm in docker
npm install --global corepack@latest
corepack enable pnpm
pnpm install
pnpm build
cd ..
cd backendserver
DB_NAME=prod ./mvnw clean spring-boot:run