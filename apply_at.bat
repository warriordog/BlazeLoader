@echo off
set version=1.7.2
if not exist .\jars\versions\%version%\%version%.json (
    echo Copying %version%.json...
    if exist %appdata%\.minecraft\versions\%version%\%version%.json (
        copy /Y /B "%appdata%\.minecraft\versions\%version%\%version%.json" ".\jars\versions\%version%\%version%.json"
        echo done.
        echo ---
    ) else (
        echo %version%.json not found!  Aborting!
        exit /b 1
    )
)
if not exist .\jars\versions\%version%\%version%.jar (
    echo Copying %version%.jar...
    if exist %appdata%\.minecraft\versions\%version%\%version%.jar (
        copy /Y /B "%appdata%\.minecraft\versions\%version%\%version%.jar" ".\jars\versions\%version%\%version%.jar"
        echo done.
        echo ---
    ) else (
        echo %version%.jar not found!  Aborting!
        exit /b 1
    )
)
if not exist .\jars\libraries\net\minecraft\launchwrapper\1.9\launchwrapper-1.9.jar (
    echo Copying launchwrapper-1.9.jar...
    if exist %appdata%\.minecraft\libraries\net\minecraft\launchwrapper\1.9\launchwrapper-1.9.jar (
        xcopy /Y /B "%appdata%\.minecraft\libraries\net\minecraft\launchwrapper\1.9\launchwrapper-1.9.jar" ".\jars\libraries\net\minecraft\launchwrapper\1.9\launchwrapper-1.9.jar"
        echo done.
        echo ---
    ) else (
        echo launchwrapper-1.9.jar not found!  Aborting!
        exit /b 1
    )
)
if not exist .\jars\libraries\org\ow2\asm\asm-debug-all\4.1\asm-debug-all-4.1.jar (
    echo Copying asm-debug-all-4.1.jar...
    if exist %appdata%\.minecraft\libraries\org\ow2\asm\asm-debug-all\4.1\asm-debug-all-4.1.jar (
        xcopy /Y /B "%appdata%\.minecraft\libraries\org\ow2\asm\asm-debug-all\4.1\asm-debug-all-4.1.jar" ".\jars\libraries\org\ow2\asm\asm-debug-all\4.1\asm-debug-all-4.1.jar"
        echo done.
        echo ---
    ) else (
        echo asm-debug-all-4.1.jar not found!  Aborting!
        exit /b 1
    )
)
cd BlazeLoader
..\runtime\bin\python\python_mcp apply_at.py %*
cd ..