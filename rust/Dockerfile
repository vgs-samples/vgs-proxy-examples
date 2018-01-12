ARG BASE_IMAGE_TAG=3

FROM rust:1.19.0

ADD ./rust_proxy_example
WORKDIR /rust_proxy_example

RUN cargo install

CMD ["cargo", "run"]
