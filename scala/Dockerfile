FROM hseeberger/scala-sbt

ADD ./scala-example /src
WORKDIR /src

RUN sbt compile

CMD ["sbt", "run"]
