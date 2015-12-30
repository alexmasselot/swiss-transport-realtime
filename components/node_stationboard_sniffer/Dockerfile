FROM    node:4.2

ADD . /src
RUN cd /src; rm -r node_modules; npm install

WORKDIR /src
#CMD ["npm", "start"]

CMD ["npm", "start"]
