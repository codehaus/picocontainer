#! /bin/bash
# This is a *very* simple continuous integration script.
# It gets the sources from CVS, builds and uploads the site and binaries.
#
# In order to configure a machine to run this once a day,
# run "crontab -e" from a shell and add the following line:
#
# 49 1 * * * $HOME/cvs/pico/continuous-integration.sh
#
# That will run this script every day at 1:49 AM. (Off peek, likely to be a quiet period).
#
# In order to make this work, you should also set the following in your ~/build.properties:
# (Don't set maven.test.failure.ignore to true on your developer machine)
#
# maven.username = <yourlogin_on_deploy_machine>
# maven.test.failure.ignore = true
#
# Finally, to get the full picture, see maven.xml
#

JAVA_HOME=/usr/local/j2sdk1.4.1
MAVEN_HOME=~bwalding/maven
ANT_HOME=$HOME/cvs/ant/dist
PATH=$HOME/bin:$JAVA_HOME/bin:$MAVEN_HOME/bin:$ANT_HOME/bin:$PATH
CVS_RSH=$HOME/ssh1.sh

export JAVA_HOME
export MAVEN_HOME
export ANT_HOME
export PATH
export CVS_RSH

cd $HOME/cvs/pico

cvs -d:ext:$USER@cvs.codehaus.org:/cvsroot/picocontainer update -d -P

# Clean old builds and make the target folder. Logs go here too.
rm -Rf target
mkdir target

# clover requiring it to run twice to get proper coverage.
# The 2nd time happens during site:deploy
maven clover &> target/cleanbuild.log

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

    # We'll deploy the site even if the tests fail. Log currently not used.
    maven site:deploy &> target/sitedeploy.log
  else
    # Deploy site only if compile and tests pass. Logs currently not used.
    # Must be run separately to get the files uploaded in the proper dir
    # on the server
    maven jar:deploy &> target/jardeploy.log
    maven dist:deploy &> target/distdeploy.log
  fi
  # We'll deploy the site even if the tests fail. Log currently not used.
  maven site:deploy &> target/sitedeploy.log
fi
