@echo off
if not exist "bin" (
    echo Please compile first: compile.bat
    exit /b 1
)
echo Starting TA Recruitment System...
java -cp "lib\gson-2.10.1.jar;bin" Main
