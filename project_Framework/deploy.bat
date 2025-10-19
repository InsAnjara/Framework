@echo off
:: Variables
set "JAVA_HOME=E:\JAVA"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set APP_NAME=ProjetFramework
set SRC_DIR=src\main\java
set WEB_DIR=src\main\webapp
set BUILD_DIR=build
set LIB_DIR=lib
set TOMCAT_WEBAPPS=D:\Tomcat\webapps
set SERVLET_API_JAR=%LIB_DIR%\servlet-api.jar
set FRONT_SERVLET_JAR=%LIB_DIR%\Framework.jar

:: Nettoyage
if exist %BUILD_DIR% (
    rmdir /s /q %BUILD_DIR%
)
mkdir %BUILD_DIR%\WEB-INF
mkdir %BUILD_DIR%\WEB-INF\classes
mkdir %BUILD_DIR%\WEB-INF\lib

:: Compilation avec les deux JARs
echo Compilation de l'application Test...
dir /b /s %SRC_DIR%\*.java > sources.txt
javac -cp "%SERVLET_API_JAR%;%FRONT_SERVLET_JAR%" -source 17 -target 17 -d %BUILD_DIR%\WEB-INF\classes @sources.txt
if errorlevel 1 (
    echo Erreur de compilation!
    del sources.txt
    pause
    exit /b 1
)
del sources.txt

:: Copier les librairies
copy /Y %LIB_DIR%\*.jar %BUILD_DIR%\WEB-INF\lib\

:: Copier TOUS les fichiers web (y compris le web.xml existant)
if exist %WEB_DIR% (
    echo Copie des fichiers web...
    xcopy /E /I /Y %WEB_DIR%\* %BUILD_DIR%\
)

:: Création du WAR
echo Creation du WAR %APP_NAME%.war...
cd %BUILD_DIR%
jar -cvf %APP_NAME%.war *
cd ..

:: Déploiement vers Tomcat
echo Deploiement vers Tomcat...
copy /Y %BUILD_DIR%\%APP_NAME%.war %TOMCAT_WEBAPPS%\

echo.
echo Déploiement terminé avec succes!
echo Application accessible sur: http://localhost:8080/%APP_NAME%/
echo.
pause