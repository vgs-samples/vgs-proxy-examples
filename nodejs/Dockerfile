FROM node:8

RUN echo $(node --version)

ADD . /src
WORKDIR /src

RUN npm install

CMD ["npm", "run", "test"]
