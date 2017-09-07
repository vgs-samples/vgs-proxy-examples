FROM maven:3.5.0

RUN echo $(java --version)
RUN echo $(mvn --version)

ADD . /src
WORKDIR /src

#no run test for now since there is no env vars put in
RUN mvn clean install -DskipTests

CMD ["mvn", "verify"]
