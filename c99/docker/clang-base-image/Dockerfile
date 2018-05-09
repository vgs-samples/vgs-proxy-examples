FROM debian:stretch
MAINTAINER Gordon Young <gjyoung1974@gmail.com>

ENV DEBIAN_FRONTEND noninteractive

# install a build environment for clang along with libcurl and libssl 
RUN echo "deb http://httpredir.debian.org/debian stretch main contrib" > /etc/apt/sources.list.d/sources.list && \
echo "deb-src http://httpredir.debian.org/debian stretch  main contrib" > /etc/apt/sources.list.d/sources.list && \
echo "deb http://httpredir.debian.org/debian stretch-updates main contrib" > /etc/apt/sources.list.d/sources.list && \
echo "deb-src http://httpredir.debian.org/debian stretch-updates main contrib" > /etc/apt/sources.list.d/sources.list && \
echo "deb http://security.debian.org/ stretch/updates main contrib" > /etc/apt/sources.list.d/sources.list && \
echo "deb-src http://security.debian.org/ stretch/updates main contrib" > /etc/apt/sources.list.d/sources.list 

RUN echo "deb http://httpredir.debian.org/debian stretch contrib non-free" > /etc/apt/sources.list.d/nonfree.list && \
    echo "deb http://httpredir.debian.org/debian stretch-updates contrib non-free" >> /etc/apt/sources.list.d/nonfree.list && \
    echo "deb http://security.debian.org stretch/updates contrib non-free" >> /etc/apt/sources.list.d/nonfree.list && \
    apt-get -y update && apt-get -y upgrade && \
    apt-get -y install devscripts build-essential debhelper git cmake bison flex libssl1.0-dev libpcap-dev libcurl4-openssl-dev


