@echo off
if not exist %cd%/BlazeLoader/instlflg (
    echo BlazeLoader environment not found!  Do you want to download it?
    choice
    if errorlevel 2 (
        echo MCP setup canceled.
        exit /b -1
    ) else (
        REM echo DEBUG: %cd%/temp/setup/bl_downloader.vbs
        mkdir %cd%\temp\setup\
        echo Extracting downloader script...
REM begin embedded VBScript file
        echo strURL = WScript.Arguments.Item(0^)>%cd%/temp/setup/bl_downloader.vbs
        echo strPath = WScript.Arguments.Item(1^)>>%cd%/temp/setup/bl_downloader.vbs
        echo dim xHttp: Set xHttp = createobject("MSXML2.ServerXMLHTTP.6.0"^)>>%cd%/temp/setup/bl_downloader.vbs
        echo dim bStrm: Set bStrm = createobject("Adodb.Stream"^)>>%cd%/temp/setup/bl_downloader.vbs
        echo xHttp.Open "GET", strURL, False>>%cd%/temp/setup/bl_downloader.vbs
        echo xHttp.Send>>%cd%/temp/setup/bl_downloader.vbs
        echo with bStrm>>%cd%/temp/setup/bl_downloader.vbs
        echo     .type = 1 1>>%cd%/temp/setup/bl_downloader.vbs
        echo     .open>>%cd%/temp/setup/bl_downloader.vbs
        echo     .write xHttp.responseBody>>%cd%/temp/setup/bl_downloader.vbs
        echo     .savetofile strPath, 2 1>>%cd%/temp/setup/bl_downloader.vbs
        echo end with>>%cd%/temp/setup/bl_downloader.vbs
        echo WScript.Quit>>%cd%/temp/setup/bl_downloader.vbs
REM end embedded VBScript file
        if exist %cd%/temp/setup/bl_downloader.vbs (
            echo Done.
        ) else (
            echo Unable to extract downloader script, aborting install!
            exit /b 3
        )
        echo Downloading BlazeLoader from "http://github.com/warriordog/BlazeLoader/archive/master.zip"...
        cscript.exe %cd%/temp/setup/bl_downloader.vbs http://github.com/warriordog/BlazeLoader/archive/master.zip %cd%/temp/setup/BlazeLoader_repo.zip
        echo Done.
        echo Downloading 7-Zip from "http://www.acomputerdog.net/BL_STORE/7-zip_package.exe"...
        cscript.exe %cd%/temp/setup/bl_downloader.vbs http://www.acomputerdog.net/BL_STORE/7-zip_package.exe %cd%/temp/setup/7-zip_package.exe
        echo Done.
        echo Extracting 7-Zip...
        %cd%/temp/setup/7-zip_package.exe /q:a /t:%cd%\temp\setup\7-zip\ /c /r:n
        if exist %cd%/temp/setup/7-zip/7z.exe (
            echo Done.
        ) else (
            echo 7-zip was not extracted! (you may need to disable your antivirus if the self-extracting archive was blocked^)
            echo Unable to extract BlazeLoader, aborting install!
            exit /b 1
        )
        echo Extracting BlazeLoader...
        %cd%/temp/setup/7-zip/7z.exe x -o%cd%/temp/setup/ -y %cd%/temp/setup/BlazeLoader_repo.zip
        xcopy /I /Y /E /C /Q %cd%\temp\setup\BlazeLoader-master\* %cd%\BlazeLoader
        rmdir /S /Q %cd%\temp\setup\BlazeLoader-master
        echo Done.
        echo Downloading MCP...
        set /p mcpver=<%cd%/BlazeLoader/mcp.ver
        cscript.exe %cd%/temp/setup/bl_downloader.vbs http://mcp.ocean-labs.de/files/archive/mcp%mcpver%.zip %cd%/temp/setup/mcp%mcpver%.zip
        echo Done.
        echo Extracting MCP...
        %cd%/temp/setup/7-zip/7z.exe x -o%cd% -y %cd%/temp/setup/mcp%mcpver%.zip
        if exist %cd%/decompile.bat (
            echo Done.
        ) else (
            echo MCP version %mcpver% was not found, provide it manually to %cd%/temp/setup/mcp%mcpver%.zip!  Aborting install!
            exit /b 2
        )
        echo Applying AccessTransformer...
        %cd%/BlazeLoader/apply_at.bat
        echo Done.
        echo Decompiling...
        decompile.bat
        echo Done.
        echo %date%:%time%>%cd%/BlazeLoader/instlflg
        echo MCP setup complete!
    )
) else (
    echo MCP has already been set up!
    exit /b -2
)