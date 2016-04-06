FROM    node:4.2

ADD . /src
RUN cd /src; npm install; npm rebuild

WORKDIR /src

CMD ["npm", "start"]
