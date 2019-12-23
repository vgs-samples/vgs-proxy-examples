ARG BASE_IMAGE_TAG=2

FROM python:$BASE_IMAGE_TAG

RUN echo $(python --version)

ADD . /src
WORKDIR /src

RUN sed -i 's/CipherString = DEFAULT@SECLEVEL=2/CipherString = DEFAULT@SECLEVEL=1/g' /etc/ssl/openssl.cnf

RUN pip install -r requirements.txt

CMD ["python", "example.py"]
