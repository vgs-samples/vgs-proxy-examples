FROM frolvlad/alpine-python3

RUN pip install --upgrade pip

ADD . /src
WORKDIR /src
# https://stackoverflow.com/a/42334357
RUN pip3 install -r requirements.txt

CMD ["python", "/src/example.py"]
