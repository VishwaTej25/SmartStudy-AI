#!/usr/bin/env powershell
<#
.SYNOPSIS
    Start the SmartStudy Mock Backend API
    
.DESCRIPTION
    Starts the Flask-based mock backend on http://localhost:5000
#>

param(
    [int]$Port = 5000
)

Write-Host "╔════════════════════════════════════════════════════════════╗"
Write-Host "║   Starting SmartStudy Mock Backend...                     ║"
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan

$ScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

# Check if Python is installed
Write-Host "`n[*] Checking Python installation..." -ForegroundColor Yellow

$pythonCmd = $null
$pythonVersion = $null

# Try different Python commands
foreach ($cmd in @("python", "python3", "py")) {
    try {
        $version = & $cmd --version 2>&1
        if ($LASTEXITCODE -eq 0) {
            $pythonCmd = $cmd
            $pythonVersion = $version
            break
        }
    } catch {
        continue
    }
}

if (-Not $pythonCmd) {
    Write-Host "[✗] Python not found. Please install Python 3.7+ and add it to PATH." -ForegroundColor Red
    Write-Host "`nTroubleshooting:" -ForegroundColor Yellow
    Write-Host "  1. Download Python from https://www.python.org/downloads/" -ForegroundColor Yellow
    Write-Host "  2. During installation, check 'Add Python to PATH'" -ForegroundColor Yellow
    Write-Host "  3. Restart PowerShell and try again" -ForegroundColor Yellow
    exit 1
}

Write-Host "[✓] Found: $pythonVersion" -ForegroundColor Green

# Check if required packages are installed
Write-Host "`n[*] Checking Python dependencies..." -ForegroundColor Yellow

$dependencies = @("flask", "flask_cors", "jwt")

foreach ($dep in $dependencies) {
    try {
        & $pythonCmd -c "import $dep" 2>&1 | Out-Null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[✓] $dep installed" -ForegroundColor Green
        }
    } catch {
        Write-Host "[!] $dep not found. Installing..." -ForegroundColor Yellow
        & $pythonCmd -m pip install $dep --quiet
    }
}

# Start the backend
Write-Host "`n[*] Starting Mock Backend on http://localhost:$Port..." -ForegroundColor Yellow
Write-Host "`n" -ForegroundColor Yellow

$backendPath = Join-Path $ScriptRoot "mock_backend.py"

if (-Not (Test-Path $backendPath)) {
    Write-Host "[✗] mock_backend.py not found at: $backendPath" -ForegroundColor Red
    exit 1
}

try {
    & $pythonCmd $backendPath
} catch {
    Write-Host "[✗] Failed to start backend: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
