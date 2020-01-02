FROM hseeberger/scala-sbt:11.0.4_1.3.5_2.13.1

ADD ./scala-example /src
WORKDIR /src

RUN sbt compile

CMD ["sbt", "run"]
