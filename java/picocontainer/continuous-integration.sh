#! /bin/bash
# This is a *very* simple continuous integration script.
# It gets the sources from CVS, builds and uploads the site and binaries.
#
# In order to configure a machine to run this once a day,
# run "crontab -e" from a shell and add the following line:
#
# 0 2 * * * /usr/local/builds/pico/continuous-integration.sh
#
# That will run this script every day at 2:00 AM. (See http://wiki.codehaus.org/general/CodehausBuildmeister).
#
# To get the full picture, see maven.xml
#

cd /usr/local/builds/pico

cvs -d:ext:$USER@cvs.codehaus.org:/cvsroot/picocontainer update -d -P

# Clean old builds and make the target folder. Logs go here too.
rm -Rf target
mkdir target

# Compile and test
maven -e -Dmaven.test.failure.ignore=true test:test &> target/cleanbuild.log

# See if the "compiling" file is there. If it is, compilation
# failed.
if [ -e "target/compiling" ] ; then
  # Mail Maven's output to the dev list.
  cat target/cleanbuild.log | mutt -s "[BUILD] Clean build failed" picocontainer-dev@lists.codehaus.org
else
  # See if the "testfailure" file is there. If it is, tests failed.
  if [ -e "target/testfailure" ] ; then
    # Mail Maven's output to the dev list.
    cat target/cleanbuild.log | mutt -s "[BUILD] Test failure - see http://www.picocontainer.org/junit-report.html" picocontainer-dev@lists.codehaus.org
  else
    # Deploy site only if compile and tests pass. Logs currently not used.
    # Must be run separately to get the files uploaded in the proper dir
    # on the server.
    maven -e -Dmaven.username=$USER jar:deploy &> target/jardeploy.log
    maven -e -Dmaven.username=$USER dist:deploy &> target/distdeploy.log
  fi
  # We'll deploy the site even if the tests fail. Log currently not used.
  maven -e -Dmaven.username=$USER site:deploy &> target/sitedeploy.log
fi
