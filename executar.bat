@echo off
rem Reduza - compila e executa sem Maven (precisa do JDK 17+ e do driver em lib\)
cd /d "%~dp0"
if not exist lib\mysql-connector-j-*.jar (
  echo Falta o driver do MySQL na pasta lib. Veja lib\LEIA-ME.txt
  pause
  exit /b 1
)
if not exist reduza.properties copy reduza.properties.exemplo reduza.properties >nul
if exist out rmdir /s /q out
rem -sourcepath compila a App e todas as classes que ela usa (evita problemas com espaços no caminho)
javac --release 17 -encoding UTF-8 -d out -sourcepath src\main\java src\main\java\br\com\devnexus\reduza\App.java
if errorlevel 1 (
  echo Erro de compilacao.
  pause
  exit /b 1
)
java -cp "out;lib\*" br.com.devnexus.reduza.App
