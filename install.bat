for /r %%a in (*.java) do ( javac -cp .;chatter-bot-api.jar;. "%%a" )
java -Xmx512m Install.Start
pause