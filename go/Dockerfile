FROM golang:1.17.2

RUN go get -u github.com/Masterminds/glide
RUN mkdir -p $GOPATH/src/example

ADD . $GOPATH/src/example
WORKDIR $GOPATH/src/example

RUN glide install

CMD ["go", "test", "test/example_test.go", "-v"]
