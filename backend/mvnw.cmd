@REM Maven Wrapper — usa C:\apache-maven-3.9.6 si mvn no esta en PATH
@SETLOCAL
@SET MAVEN_HOME=C:\apache-maven-3.9.6
@SET MVN_EXE=mvn.cmd
@WHERE mvn.cmd >NUL 2>&1
@IF %ERRORLEVEL% NEQ 0 (
    @IF EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
        @SET MVN_EXE=%MAVEN_HOME%\bin\mvn.cmd
    ) ELSE (
        @ECHO Maven no encontrado. Ejecuta setup.ps1 o instala Maven manualmente.
        @EXIT /B 1
    )
)
@CALL "%MVN_EXE%" %*
@ENDLOCAL
