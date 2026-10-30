@echo off

echo Compiling Java files...
javac -d . src\src\main\java\org\example\*.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Starting SVFRMS...
echo.

java -cp . org.example.SVFRMS

pause
