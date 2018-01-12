ARG BASE_IMAGE_TAG=3

FROM rust:1.19.0

ADD . /src
WORKDIR /src

RUN cargo install

CMD ["cargo run"]