FROM microsoft/dotnet:2-sdk

RUN echo $(dotnet --version)

ADD . /src
WORKDIR /src

COPY cert.pem /usr/local/share/ca-certificates/cert.crt

RUN update-ca-certificates

CMD ["dotnet", "test", "test/test.csproj"]
