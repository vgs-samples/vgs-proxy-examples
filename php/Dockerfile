FROM php:7

RUN echo $(php --version)

ADD . /app
WORKDIR /app

RUN sed -i 's/CipherString = DEFAULT@SECLEVEL=2/CipherString = DEFAULT@SECLEVEL=1/g' /etc/ssl/openssl.cnf

CMD ["php", "example.php"]
