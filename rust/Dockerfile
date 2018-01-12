ARG BASE_IMAGE_TAG=3

FROM rust:1.19.0

ADD ./rust_proxy_example /src
WORKDIR /rc

RUN cargo install

CMD ["cargo", "run"]
