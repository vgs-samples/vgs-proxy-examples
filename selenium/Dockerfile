ARG BASE_IMAGE_TAG=2

FROM python:$BASE_IMAGE_TAG

RUN echo $(python --version)

ADD . /src
WORKDIR /src

RUN pip install -r requirements.txt

CMD ["python", "example.py"]
