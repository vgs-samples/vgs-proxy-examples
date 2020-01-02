FROM ruby:2

RUN echo $(ruby --version)

ADD . /src
WORKDIR /src

RUN bundle install

RUN sed -i 's/CipherString = DEFAULT@SECLEVEL=2/CipherString = DEFAULT@SECLEVEL=1/g' /etc/ssl/openssl.cnf

CMD ["ruby", "example.rb"]
