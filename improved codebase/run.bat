
@echo off
setlocal

echo Compiling Java files...

if exist out rmdir /s /q out
mkdir out

powershell -NoProfile -Command "$files = Get-ChildItem -Path 'src\src\main\java' -Filter '*.java' -Recurse | ForEach-Object { $_.FullName }; & javac -Xmaxerrs 300 -d out $files; exit $LASTEXITCODE"

if errorlevel 1 (
    echo.
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.
echo Starting SVFRMS...
echo.

java -cp out org.example.app.SVFRMS

pause
