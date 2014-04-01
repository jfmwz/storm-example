require 'rubygems'
require 'json/pure'
require 'rest_client'
require 'uri'


url="http://localhost:7272/druid/v2/?pretty=true"
response = RestClient.post url, File.read("realtime_query"), :accept => :json, :content_type => 'appplication/json'
#puts(response)
result = JSON.parse(response.to_s)

word_relevance = {}
result.each do |slice|  
  event = slice['event']
  word_relevance[event['word']]=event['relevance']
end

count = 0
word_relevance.sort_by {|k,v| v}.reverse.each do |word, relevance|
  puts("#{word}->#{relevance}")
  count=count+1
  if(count == 20) then
    break
  end
end
