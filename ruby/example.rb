require 'faraday_middleware'
require 'faraday'
require 'json'
require 'faker'


@username = 'US6JXwGY219cUNWJ8jotyAMw'
@password = '81d369b2-8c35-4ba4-9748-24315b15abaa'
@forward_proxy = 'tntbeiahp7q.SANDBOX.verygoodproxy.com:8080'
@reverse_proxy = 'tntbeiahp7q.SANDBOX.verygoodproxy.com'

def random_json
  return {
      'secret': Faker::Markdown.random
  }.to_json
end

def tokenize_via_reverse_proxy(original_data)
  connection = Faraday.new(:url => "https://#{@reverse_proxy}",
                           :headers => {'Content-type' => 'application/json', 'VGS-Log-Request' => 'all'},
                           :ssl => {:verify => false}) do |f|
    f.response :json
    f.adapter Faraday.default_adapter
  end
  rsp = connection.post '/post', original_data
  rsp.body['data']
end


def reveal_via_forward_proxy(tokenized_data)
  connection = Faraday.new(
      :url => 'https://httpbin.verygoodsecurity.io',
      :headers => {'Content-type' => 'application/json', 'VGS-Log-Request' => 'all'},
      :ssl => {:verify => false},
      :proxy => "https://#{@username}:#{@password}@#{@forward_proxy}") do |f|
    # f.request :json
    f.response :json
    f.adapter Faraday.default_adapter
  end
  rsp = connection.post '/post', tokenized_data
  rsp.body['data']
end


original_value = random_json
puts original_value

tokenized_value = tokenize_via_reverse_proxy original_value
print(tokenized_value)
raise 'Tokenize failed' if original_value == tokenized_value


revealed_value = reveal_via_forward_proxy tokenized_value
puts revealed_value
raise 'Reveale failed' unless original_value == revealed_value

puts 'Test passed'