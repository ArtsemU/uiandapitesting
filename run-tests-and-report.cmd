@echo off
rem Runs the tests, then builds the Allure HTML report whether or not the tests passed.
rem Each run gets its own folder: allure-report\report-yyyyMMdd-HHmmss (sorts chronologically).
rem Arguments go straight to Maven, e.g.:
rem   run-tests-and-report.cmd -DsuiteXmlFile=src/test/resources/suite/ui_smoke.xml -Dheadless=true
rem Exits with the test run's exit code, so a failing run still reports failure.

call mvn clean test %*
set TEST_EXIT_CODE=%ERRORLEVEL%

rem %DATE% and %TIME% depend on the Windows locale; PowerShell gives a fixed format.
for /f %%t in ('powershell -NoProfile -Command "Get-Date -Format yyyyMMdd-HHmmss"') do set REPORT_TIMESTAMP=%%t
set REPORT_DIR=allure-report/report-%REPORT_TIMESTAMP%

call mvn allure:report -Dallure.report.directory=%REPORT_DIR%

echo.
echo Allure report: %~dp0allure-report\report-%REPORT_TIMESTAMP%\index.html
exit /b %TEST_EXIT_CODE%
