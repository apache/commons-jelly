@echo off

%JAVA_HOME%\bin\java -classpath %JELLY_HOME%\lib\forehead.jar -Dforehead.conf.file=%JELLY_HOME%\bin\forehead.conf -Dant.home=%ANT_HOME% -Djelly.home=%JELLY_HOME% -Dtools.jar=%JAVA_HOME%\lib\tools.jar com.werken.forehead.Forehead %*

