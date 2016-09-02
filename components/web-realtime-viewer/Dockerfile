FROM node:4.2
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# Bundle app source
COPY . /usr/src/app
COPY package.json /usr/src/app/
RUN npm install

RUN npm run build
EXPOSE 8080
CMD [ "npm", "start" ]


