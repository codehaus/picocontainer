require 'net/http'

# Download docs from Confluence
http = Net::HTTP.new("docs.codehaus.org")
resp, data = http.get("/spaces/doexportspace.action?key=PICO&type=TYPE_HTML")
if(resp.code.to_i != 302)
  raise "Didn't get redirect (302): #{resp.code.to_s}"
end
location = resp["location"]
resp, data = http.get(location)

if(resp.code.to_i != 200)
  raise "Download of zip failed: #{resp.code.to_s}"
end
zip = File.new("target/docs.zip", "w+")
zip.write(data)
zip.close
