# ============================================================
# Setup inicial del proyecto — Sistema de Horarios UEB
# Ejecutar desde la raiz del proyecto: .\setup.ps1
# ============================================================

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot

Write-Host "`n=== Setup: Sistema de Horarios Academicos ===" -ForegroundColor Cyan

# ---- Maven ----
$MavenVersion = "3.9.6"
$MavenDir = "C:\apache-maven-$MavenVersion"
$MavenBin = "$MavenDir\bin\mvn.cmd"

if (-not (Test-Path $MavenBin)) {
    Write-Host "`n[1/4] Descargando Apache Maven $MavenVersion..." -ForegroundColor Yellow
    $MavenZip = "$env:TEMP\apache-maven-$MavenVersion-bin.zip"
    $MavenUrl = "https://downloads.apache.org/maven/maven-3/$MavenVersion/binaries/apache-maven-$MavenVersion-bin.zip"
    Invoke-WebRequest -Uri $MavenUrl -OutFile $MavenZip -UseBasicParsing
    Write-Host "    Extrayendo en C:\..." -ForegroundColor Gray
    Expand-Archive -Path $MavenZip -DestinationPath "C:\" -Force
    Remove-Item $MavenZip
    Write-Host "    Maven instalado en $MavenDir" -ForegroundColor Green
} else {
    Write-Host "`n[1/4] Maven ya instalado: $MavenBin" -ForegroundColor Green
}

# Agregar Maven al PATH de la sesion actual
$env:PATH = "$MavenDir\bin;$env:PATH"
$env:JAVA_HOME = (Get-Command java -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source | Split-Path | Split-Path)
Write-Host "    JAVA_HOME = $env:JAVA_HOME"

# ---- Verificar PostgreSQL ----
Write-Host "`n[2/4] Verificando PostgreSQL..." -ForegroundColor Yellow
$PsqlPath = Get-Command psql -ErrorAction SilentlyContinue
if (-not $PsqlPath) {
    # Buscar en rutas comunes
    $candidates = @(
        "C:\Program Files\PostgreSQL\16\bin\psql.exe",
        "C:\Program Files\PostgreSQL\15\bin\psql.exe",
        "C:\Program Files\PostgreSQL\14\bin\psql.exe"
    )
    foreach ($c in $candidates) {
        if (Test-Path $c) { $PsqlPath = $c; break }
    }
}
if ($PsqlPath) {
    Write-Host "    psql encontrado: $PsqlPath" -ForegroundColor Green
} else {
    Write-Host "    ADVERTENCIA: psql no encontrado. Instala PostgreSQL 14+ y ejecuta db\init.sql manualmente." -ForegroundColor Red
}

# ---- Crear Base de Datos ----
Write-Host "`n[3/4] Creando base de datos 'horarios_db'..." -ForegroundColor Yellow
if ($PsqlPath) {
    $env:PGPASSWORD = "postgres"
    $psqlExe = if ($PsqlPath -is [string]) { $PsqlPath } else { $PsqlPath.Source }

    # Crear DB si no existe
    & $psqlExe -U postgres -c "CREATE DATABASE horarios_db;" 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "    Base de datos creada" -ForegroundColor Green
    } else {
        Write-Host "    La base de datos ya existe o hubo un error (puede ignorarse si ya existe)" -ForegroundColor Yellow
    }

    # Ejecutar DDL
    Write-Host "    Ejecutando DDL..." -ForegroundColor Gray
    & $psqlExe -U postgres -d horarios_db -f "$ProjectRoot\db\init.sql"
    Write-Host "    Ejecutando datos iniciales..." -ForegroundColor Gray
    & $psqlExe -U postgres -d horarios_db -f "$ProjectRoot\db\seed.sql"
    Write-Host "    Base de datos lista" -ForegroundColor Green
} else {
    Write-Host "    Skipping DB setup (psql no disponible)" -ForegroundColor Yellow
    Write-Host "    Ejecuta manualmente:`n      psql -U postgres -c 'CREATE DATABASE horarios_db;'" -ForegroundColor Gray
    Write-Host "      psql -U postgres -d horarios_db -f db\init.sql" -ForegroundColor Gray
    Write-Host "      psql -U postgres -d horarios_db -f db\seed.sql" -ForegroundColor Gray
}

# ---- Compilar Backend ----
Write-Host "`n[4/4] Compilando backend..." -ForegroundColor Yellow
Set-Location "$ProjectRoot\backend"
& $MavenBin clean compile -q
if ($LASTEXITCODE -eq 0) {
    Write-Host "    Backend compila correctamente" -ForegroundColor Green
} else {
    Write-Host "    Error al compilar el backend" -ForegroundColor Red
}

Set-Location $ProjectRoot

Write-Host "`n=== Setup completado ===" -ForegroundColor Cyan
Write-Host @"

Proximos pasos:
  Backend:   cd backend && $MavenBin spring-boot:run -Dspring-boot.run.profiles=dev
  Frontend:  cd frontend && npm run dev

  Backend:   http://localhost:8080
  Frontend:  http://localhost:5173

Variables de entorno requeridas (opcionales si usas defaults):
  DB_URL      = jdbc:postgresql://localhost:5432/horarios_db
  DB_USER     = postgres
  DB_PASSWORD = postgres
  JWT_SECRET  = (minimo 32 caracteres)
"@ -ForegroundColor White
