@echo off
title jiyuan
rem java -classpath run.jar com.hanlin.fadp.base.start.Start
java  -jar fadp.jar -shutdown
ping -n 5 127.0>nul
java   -Dfile.encoding=utf8  -jar fadp.jar
