FROM ruby:2

RUN echo $(ruby --version)

ADD . /src
WORKDIR /src

RUN bundle install
RUN gem install faraday_middleware
RUN gem install faker
RUN sed -i 's/CipherString = DEFAULT@SECLEVEL=2/CipherString = DEFAULT@SECLEVEL=1/g' /etc/ssl/openssl.cnf

CMD ["ruby", "example.rb"]
