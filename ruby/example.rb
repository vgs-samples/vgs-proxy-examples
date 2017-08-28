require 'faraday_middleware'
require 'faraday'
require 'json'
require 'faker'

@username = ENV['FORWARD_HTTP_PROXY_USERNAME']
@password = ENV['FORWARD_HTTP_PROXY_PASSWORD']
@forward_proxy = ENV['FORWARD_HTTP_PROXY_HOST']
@reverse_proxy = ENV['REVERSE_HTTP_PROXY_HOST']


def random_json
  return {
      'secret': Faker::Markdown.random
  }.to_json
end

def tokenize_via_reverse_proxy(original_data)
  connection = Faraday.new(:url => "https://#{@reverse_proxy}",
                           :headers => {'Content-type' => 'application/json', 'VGS-Log-Request' => 'all'}) do |f|
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
      :ssl => {:ca_file => './cert.pem'},
      :proxy => "https://#{@username}:#{@password}@#{@forward_proxy}") do |f|
    f.response :json
    f.adapter Faraday.default_adapter
  end
  rsp = connection.post '/post', tokenized_data
  rsp.body['data']
end


original_value = random_json
puts original_value

tokenized_value = tokenize_via_reverse_proxy original_value
puts tokenized_value
raise 'Tokenize failed' if original_value == tokenized_value


revealed_value = reveal_via_forward_proxy tokenized_value
puts revealed_value
raise 'Reveale failed' unless original_value == revealed_value

puts 'Test passed'