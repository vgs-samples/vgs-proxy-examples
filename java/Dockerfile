FROM maven:3.5.0

RUN echo $(java --version)
RUN echo $(mvn --version)

ADD . /src
WORKDIR /src

RUN mvn clean install

CMD ["mvn", "verify"]
