@echo off
set version=1.7.2
if not exist .\jars\versions\%version%\%version%.json (
    echo Copying %version%.json...
    if exist %appdata%\.minecraft\versions\%version%\%version%.json (
        if not exist ".\jars\versions\%version%/" mkdir ".\jars\versions\%version%/"
        xcopy /I /Y "%appdata%\.minecraft\versions\%version%\%version%.json" ".\jars\versions\%version%\"
        echo done.
        echo.
    ) else (
        echo %version%.json not found!
    )
)
if not exist .\jars\versions\%version%\%version%.jar (
    echo Copying %version%.jar...
    if exist %appdata%\.minecraft\versions\%version%\%version%.jar (
        if not exist ".\jars\versions\%version%/" mkdir ".\jars\versions\%version%/"
        xcopy /I /Y "%appdata%\.minecraft\versions\%version%\%version%.jar" ".\jars\versions\%version%\"
        echo done.
        echo.
    ) else (
        echo %version%.jar not found!
    )
)
if not exist .\jars\libraries\net\minecraft\launchwrapper\1.9\launchwrapper-1.9.jar (
    echo Copying launchwrapper-1.9.jar...
    if exist %appdata%\.minecraft\libraries\net\minecraft\launchwrapper\1.9\launchwrapper-1.9.jar (
        if not exist ".\jars\libraries\net\minecraft\launchwrapper\1.9/" mkdir ".\jars\libraries\net\minecraft\launchwrapper\1.9/"
        xcopy /I /Y "%appdata%\.minecraft\libraries\net\minecraft\launchwrapper\1.9\launchwrapper-1.9.jar" ".\jars\libraries\net\minecraft\launchwrapper\1.9\"
        echo done.
        echo.
    ) else (
        echo launchwrapper-1.9.jar not found!
    )
)
if not exist .\jars\libraries\org\ow2\asm\asm-debug-all\4.1\asm-debug-all-4.1.jar (
    echo Copying asm-debug-all-4.1.jar...
    if exist %appdata%\.minecraft\libraries\org\ow2\asm\asm-debug-all\4.1\asm-debug-all-4.1.jar (
        if not exist ".\jars\libraries\org\ow2\asm\asm-debug-all\4.1/" mkdir ".\jars\libraries\org\ow2\asm\asm-debug-all\4.1/"
        xcopy /I /Y "%appdata%\.minecraft\libraries\org\ow2\asm\asm-debug-all\4.1\asm-debug-all-4.1.jar" ".\jars\libraries\org\ow2\asm\asm-debug-all\4.1\"
        echo done.
        echo.
    ) else (
        echo asm-debug-all-4.1.jar not found!
    )
)
cd BlazeLoader\
..\runtime\bin\python\python_mcp \util\apply_at.py %*
cd ..