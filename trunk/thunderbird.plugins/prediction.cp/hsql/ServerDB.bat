@ECHO OFF

REM Copyright 2004 The Apache Software Foundation
REM
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM 
REM     http://www.apache.org/licenses/LICENSE-2.0
REM 
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.


REM ***
REM Usage: java Server [-options]
REM where options include:
REM    -port <nr>            port where the server is listening
REM    -database <name>      name of the database
REM    -silent <true/false>  false means display all queries
REM    -trace <true/false>   display JDBC trace messages
REM    -no_system_exit <true/false>  do not issue System.exit()
REM The command line arguments override the values in the server.properties file.
REM
@java -classpath "%CLASSPATH%;hsqldb.jar" -Xms256m -Xmx512m org.hsqldb.Server -trace true -port 9500 -database words

REM Usage: java WebServer [-options]
REM where options include:
REM     -port <nr>            port where the server is listening
REM     -database <name>      name of the database
REM     -root <path>          root path for sending files
REM     -default_page <file>  default page when page name is missing
REM     -silent <true/false>  false means display all queries
REM     -trace <true/false>   display JDBC trace messages
REM The command line arguments override the values in the webserver.properties file.
REM
REM @java -classpath "%CLASSPATH%;hsqldb.jar" org.hsqldb.WebServer -port 8090 -database sua2db -trace true

pause