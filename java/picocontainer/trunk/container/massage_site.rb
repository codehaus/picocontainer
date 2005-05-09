# Ruby script that removes google ads and modifies links to 
# point to local reports in static html docs scraped from 
# http://www.picocontainer.org/
#
# Author: Aslak Hellesoy
# 
require 'ftools'

temp="___tmp___.htm"
Dir.entries(".").each do |orig|
  if(orig=~/.*\.htm/ && orig != temp)
    puts "Massaging #{orig}"
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

      # Link to local reports
      reports="http://www.picocontainer.org/reports/"
      reports_index=line.index(reports)
      if(reports_index)
        puts " Linking to local reports"
        before=line[0..reports_index-1]
        after=line[reports_index+reports.length..-1]
        line = "#{before}#{after}"
      end
      
      tmp.puts(line) unless skip
    end
    f.close
    tmp.close
    File.mv(temp,orig)
  end
end