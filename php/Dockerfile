FROM php:7

RUN echo $(php --version)

ADD . /app
WORKDIR /app

CMD ["php", "example.php"]
