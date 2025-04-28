@echo off
REM Employee Management System Build Script for Windows
REM A simple batch script to compile and run the Java application

REM Project directories
set SRC_DIR=src
set BIN_DIR=bin
set LIB_DIR=lib
set RESOURCES_DIR=src\resources

REM Class paths
set JUNIT_PATH=%LIB_DIR%\junit-4.13.2.jar;%LIB_DIR%\hamcrest-core-1.3.jar
set MYSQL_PATH=%LIB_DIR%\mysql-connector-j-8.0.33.jar
set CLASS_PATH=%BIN_DIR%;%JUNIT_PATH%;%MYSQL_PATH%

REM Package structure
set PACKAGE_PATH=com\group02
set MAIN_CLASS=com.group02.App

REM Function to display usage information
:show_usage
if "%1"=="" (
    echo Usage:
    echo   build.bat clean    - Remove build directory
    echo   build.bat compile  - Compile the source code
    echo   build.bat test     - Run tests
    echo   build.bat run      - Run the application
    echo   build.bat all      - Clean, compile, test, and run
    goto :eof
)

REM Function to clean the build directory
:clean
if "%1"=="clean" (
    echo Cleaning build directory...
    if exist %BIN_DIR% rmdir /s /q %BIN_DIR%
    echo Clean completed.
    goto :eof
)

REM Function to create necessary directories
:init
if not exist %BIN_DIR% (
    echo Creating directories...
    mkdir %BIN_DIR%
    echo Directories created.
)
goto :eof

REM Function to compile source code
:compile
if "%1"=="compile" (
    echo Compiling source code...
    
    REM Create build directory if it doesn't exist
    call :init
    
    REM Compile source files (excluding test files)
    for /r %SRC_DIR% %%f in (*.java) do (
        echo %%f | findstr /v "Test.java" > nul
        if not errorlevel 1 (
            javac -d %BIN_DIR% -cp %CLASS_PATH% "%%f"
            if errorlevel 1 goto :compile_error
        )
    )
    
    echo Compilation successful.
    
    REM Copy resource files
    echo Copying resources...
    if exist %RESOURCES_DIR% (
        xcopy /E /Y %RESOURCES_DIR%\* %BIN_DIR%\
        echo Resources copied.
    )
    goto :eof
)
goto :next_command

:compile_error
echo Compilation failed.
exit /b 1

REM Function to compile and run tests
:test
if "%1"=="test" (
    echo Compiling tests...
    
    REM Make sure source is compiled first
    if not exist %BIN_DIR% call :compile compile
    
    REM Compile test files
    for /r %SRC_DIR% %%f in (*Test.java) do (
        javac -d %BIN_DIR% -cp %CLASS_PATH% "%%f"
        if errorlevel 1 goto :test_compile_error
    )
    
    echo Test compilation successful.
    
    REM Run tests using JUnit
    echo Running tests...
    java -cp %CLASS_PATH% org.junit.runner.JUnitCore com.group02.AppTest
    if errorlevel 1 goto :test_run_error
    
    echo All tests passed.
    goto :eof
)
goto :next_command

:test_compile_error
echo Test compilation failed.
exit /b 1

:test_run_error
echo Tests failed.
exit /b 1

REM Function to run the application
:run
if "%1"=="run" (
    echo Running application...
    
    REM Make sure source is compiled first
    if not exist %BIN_DIR% call :compile compile
    
    REM Run the main class
    java -cp %CLASS_PATH% %MAIN_CLASS%
    goto :eof
)
goto :next_command

REM Run all tasks
:all
if "%1"=="all" (
    call :clean clean
    call :compile compile
    call :test test
    call :run run
    goto :eof
)

:next_command
if "%1"=="clean" goto clean
if "%1"=="compile" goto compile
if "%1"=="test" goto test
if "%1"=="run" goto run
if "%1"=="all" goto all
goto show_usage