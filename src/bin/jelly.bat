@echo off

if "%FOREHEAD_CONF%"=="" set FOREHEAD_CONF=%JELLY_HOME%\bin\forehead.conf

%JAVA_HOME%\bin\java -classpath %CLASSPATH%;%JELLY_HOME%\lib\forehead.jar -Dforehead.conf.file=%FOREHEAD_CONF% -Dant.home=%ANT_HOME% -Djelly.home=%JELLY_HOME% -Dtools.jar=%JAVA_HOME%\lib\tools.jar %JELLY_OPTS% com.werken.forehead.Forehead %*

