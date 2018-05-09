FROM perl:5.26.2

RUN echo $(perl --version)

RUN cpanm LWP::UserAgent
RUN cpanm IO::Socket::SSL
# RUN cpanm Net::HTTP


# RUN cpanm --installdeps .
# RUN perl Build.PL
# RUN ./Build build

ADD . /src
# WORKDIR /src

RUN chmod u+x /src/example.pl

CMD ["perl", "/src/example.pl"]