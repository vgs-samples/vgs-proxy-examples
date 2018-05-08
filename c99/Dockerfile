FROM gjyoung1974/cmake:latest

ADD . /src
WORKDIR /src

#no run test for now since there is no env vars put in
RUN cmake /src 
RUN cmake --build /src
RUN /src/vgs_proxy_c_example

# do something?
# CMD ["/bin/bash -c", "tail -f /dev/null"]
