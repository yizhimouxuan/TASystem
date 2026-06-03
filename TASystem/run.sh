#!/bin/bash
if [ ! -d "bin" ]; then
    echo "Please compile first: ./compile.sh"
    exit 1
fi
echo "Starting TA Recruitment System..."
java -cp "lib/gson-2.10.1.jar:bin" Main
