@echo off
echo Compiling TA Recruitment System...
if not exist "bin" mkdir bin
javac -cp "lib\gson-2.10.1.jar" -d bin src\model\enums\*.java src\model\*.java src\data\*.java src\service\*.java src\util\*.java src\gui\*.java src\Main.java
if %errorlevel% equ 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
)
