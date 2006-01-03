REM NanoContainer Booter script v @booter-version@
REM http://www.nanocontainer.org/booter

set _EXEC=%JAVA_HOME%\bin\java -Djava.security.manager -Djava.security.policy=file:lib\booter.policy -jar lib\nanocontainer-booter-@booter-version@.jar $@
echo %_EXEC%
%_EXEC%


