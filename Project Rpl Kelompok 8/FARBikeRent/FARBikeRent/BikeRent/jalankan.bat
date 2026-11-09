@echo off
REM ============================================================
REM  Menjalankan BikeRent TANPA NetBeans (klik dua kali file ini)
REM  Syarat: JDK sudah terpasang dan perintah "javac" dikenali.
REM ============================================================
cd /d "%~dp0"

echo [1/2] Mengkompilasi...
if not exist build\classes mkdir build\classes
dir /s /b src\*.java > sumber.txt
javac -encoding UTF-8 -cp lib\sqlite-jdbc-3.46.1.3.jar -d build\classes @sumber.txt
del sumber.txt
if errorlevel 1 (
    echo Kompilasi GAGAL.
    pause
    exit /b 1
)

echo [2/2] Menjalankan aplikasi...
java -cp build\classes;lib\sqlite-jdbc-3.46.1.3.jar com.bikerent.Main
pause
