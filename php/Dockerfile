FROM php:7

RUN echo $(php --version)

ADD . /src
WORKDIR /src

CMD ["php", "example.php"]
