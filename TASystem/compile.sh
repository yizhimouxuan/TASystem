#!/bin/bash
echo "Compiling TA Recruitment System..."
mkdir -p bin
find src -name "*.java" > sources.txt
javac -cp "lib/gson-2.10.1.jar" -d bin @sources.txt
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    rm sources.txt
else
    echo "Compilation failed!"
    rm sources.txt
    exit 1
fi
