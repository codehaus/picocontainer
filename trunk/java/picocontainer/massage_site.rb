# Ruby script that removes google ads and modifies links to 
# point to local reports in static html docs scraped from 
# http://www.picocontainer.org/
#
# Author: Aslak Hellesoy

require 'ftools'

Dir.entries(".").each do |orig|
  if(orig=~/.*\.htm/)
    puts "Massaging #{orig}"
    temp="___tmp___.htm"
    f=File.new(orig)
    tmp = File.new(temp, "w")

    skip=false
    f.each_line do |line|
      # Remove Google ads
      if(line=~/.*id="extraColumn".*/)
        skip=true
        puts " Removing Google ads"
      elsif(skip && line=~/.*<\/td>.*/)
        skip=false
      end

      # Link to local reports (this is slooooow!?!?!?)
      if(line=~/(.*)http:\/\/www.picocontainer.org\/reports(.*)/)
        puts " Linking to local reports"
        line = "#{$1}reports#{$2}"
      end
      
      tmp.puts(line) unless skip
    end
    f.close
    tmp.close
    File.mv(temp,orig)
  end
end