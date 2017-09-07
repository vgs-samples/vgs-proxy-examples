# vault-examples
Examples of using vault proxy and APIs in different languages

[![CircleCI](https://circleci.com/gh/verygoodsecurity/vgs-proxy-examples.svg?style=svg&circle-token=49745d4329fdfd9f294f29430350eed19d3ff49b)](https://circleci.com/gh/verygoodsecurity/vgs-proxy-examples)

## To run for your language

```
$ export language=python
$ docker build -t $language-example $language/ 
$ docker run -t $language-example
```

## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Write your code **and tests**
4. Ensure all tests still pass (`python setup.py test`)
5. Commit your changes (`git commit -am 'Add some feature'`)
6. Push to the branch (`git push origin my-new-feature`)
7. Create new pull request
