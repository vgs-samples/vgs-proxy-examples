ARG BASE_IMAGE_TAG=3

FROM python:$BASE_IMAGE_TAG

RUN echo $(python --version)

ADD . /src
WORKDIR /src

# https://stackoverflow.com/a/42334357
RUN sed -i 's/CipherString = DEFAULT@SECLEVEL=2/CipherString = DEFAULT@SECLEVEL=1/g' /etc/ssl/openssl.cnf

RUN pip install -r requirements.txt
RUN chmod u+x /src/example.py

CMD ["python", "example.py"]
