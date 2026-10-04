@echo off
rem Runs the tests, then builds the Allure HTML report whether or not the tests passed.
rem Arguments go straight to Maven, e.g.:
rem   run-tests-and-report.cmd -DsuiteXmlFile=src/test/resources/suite/ui_smoke.xml -Dheadless=true
rem Exits with the test run's exit code, so a failing run still reports failure.

call mvn clean test %*
set TEST_EXIT_CODE=%ERRORLEVEL%

call mvn allure:report

echo.
echo Allure report: %~dp0allure-report\index.html
exit /b %TEST_EXIT_CODE%
