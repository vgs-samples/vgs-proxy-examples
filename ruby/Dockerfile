FROM ruby:2

RUN echo $(ruby --version)

ADD . /src
WORKDIR /src

RUN bundle install

CMD ["ruby", "example.rb"]
