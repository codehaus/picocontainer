require 'net/http'

# Download docs from Confluence
http = Net::HTTP.new("docs.codehaus.org")
resp, data = http.get("/spaces/doexportspace.action?key=PICO&type=TYPE_HTML") {|result|
  puts result
}
if(resp.code.to_i != 302)
  raise "Didn't get redirect (302): #{resp.code.to_s}"
end
location = resp["location"]

zip = File.new("target/docs.zip", "w+")
resp, data = http.get(location){|data|
  zip.write(data)
}
zip.flush
zip.close

if(resp.code.to_i != 200)
  raise "Download of zip failed: #{resp.code.to_s}"
end
