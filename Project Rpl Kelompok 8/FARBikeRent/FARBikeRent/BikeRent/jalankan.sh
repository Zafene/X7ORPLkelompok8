#!/bin/bash
# Menjalankan BikeRent tanpa NetBeans (Linux / macOS)
cd "$(dirname "$0")"

echo "[1/2] Mengkompilasi..."
mkdir -p build/classes
javac -encoding UTF-8 -cp lib/sqlite-jdbc-3.46.1.3.jar -d build/classes $(find src -name "*.java") || exit 1

echo "[2/2] Menjalankan aplikasi..."
java -cp build/classes:lib/sqlite-jdbc-3.46.1.3.jar com.bikerent.Main
