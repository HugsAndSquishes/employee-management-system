#!/bin/bash
# Employee Management System Build Script
# A simple shell script to compile and run the Java application

# Project directories
SRC_DIR="src"
BIN_DIR="bin"
LIB_DIR="lib"
RESOURCES_DIR="src/resources"

# Class paths
JUNIT_PATH="$LIB_DIR/junit-4.13.2.jar:$LIB_DIR/hamcrest-core-1.3.jar"
MYSQL_PATH="$LIB_DIR/mysql-connector-j-8.0.33.jar"
CLASS_PATH="$BIN_DIR:$JUNIT_PATH:$MYSQL_PATH"

# Package structure
PACKAGE_PATH="com/group02"
MAIN_CLASS="com.group02.App"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to display usage information
show_usage() {
  echo -e "${BLUE}Usage:${NC}"
  echo "  ./build.sh clean    - Remove build directory"
  echo "  ./build.sh compile  - Compile the source code"
  echo "  ./build.sh test     - Run tests"
  echo "  ./build.sh run      - Run the application"
  echo "  ./build.sh all      - Clean, compile, test, and run"
}

# Function to clean the build directory
clean() {
  echo -e "${BLUE}Cleaning build directory...${NC}"
  rm -rf $BIN_DIR
  echo -e "${GREEN}Clean completed.${NC}"
}

# Function to create necessary directories
init() {
  echo -e "${BLUE}Creating directories...${NC}"
  mkdir -p $BIN_DIR
  echo -e "${GREEN}Directories created.${NC}"
}

# Function to compile source code
compile() {
  echo -e "${BLUE}Compiling source code...${NC}"
  
  # Create build directory if it doesn't exist
  init
  
  # Find all .java files (excluding test files)
  JAVA_FILES=$(find $SRC_DIR -name "*.java" -not -name "*Test.java")
  
  # Compile source files
  javac -d $BIN_DIR -cp $CLASS_PATH $JAVA_FILES
  
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}Compilation successful.${NC}"
    
    # Copy resource files
    echo -e "${BLUE}Copying resources...${NC}"
    cp -r $RESOURCES_DIR/* $BIN_DIR
    echo -e "${GREEN}Resources copied.${NC}"
  else
    echo -e "${RED}Compilation failed.${NC}"
    exit 1
  fi
}

# Function to compile and run tests
test() {
  echo -e "${BLUE}Compiling tests...${NC}"
  
  # Make sure source is compiled first
  if [ ! -d "$BIN_DIR" ]; then
    compile
  fi
  
  # Find all test files
  TEST_FILES=$(find $SRC_DIR -name "*Test.java")
  
  # Compile test files
  javac -d $BIN_DIR -cp $CLASS_PATH $TEST_FILES
  
  if [ $? -eq 0 ]; then
    echo -e "${GREEN}Test compilation successful.${NC}"
    
    # Run tests using JUnit
    echo -e "${BLUE}Running tests...${NC}"
    java -cp $CLASS_PATH org.junit.runner.JUnitCore com.group02.AppTest
    
    if [ $? -eq 0 ]; then
      echo -e "${GREEN}All tests passed.${NC}"
    else
      echo -e "${RED}Tests failed.${NC}"
      exit 1
    fi
  else
    echo -e "${RED}Test compilation failed.${NC}"
    exit 1
  fi
}

# Function to run the application
run() {
  echo -e "${BLUE}Running application...${NC}"
  
  # Make sure source is compiled first
  if [ ! -d "$BIN_DIR" ]; then
    compile
  fi
  
  # Run the main class
  java -cp $CLASS_PATH $MAIN_CLASS
}

# Main script execution
case "$1" in
  clean)
    clean
    ;;
  compile)
    compile
    ;;
  test)
    test
    ;;
  run)
    run
    ;;
  all)
    clean
    compile
    test
    run
    ;;
  *)
    show_usage
    ;;
esac