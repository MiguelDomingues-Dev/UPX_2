#!/usr/bin/env bash
# Reduza - compila e executa sem Maven (precisa do JDK 17+ e do driver em lib/)
set -e
cd "$(dirname "$0")"
ls lib/mysql-connector-j-*.jar >/dev/null 2>&1 || { echo "Falta o driver do MySQL em lib/ (veja lib/LEIA-ME.txt)"; exit 1; }
[ -f reduza.properties ] || cp reduza.properties.exemplo reduza.properties
rm -rf out
javac --release 17 -encoding UTF-8 -d out -sourcepath src/main/java src/main/java/br/com/devnexus/reduza/App.java
java -cp "out:lib/*" br.com.devnexus.reduza.App
