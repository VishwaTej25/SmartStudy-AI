#!/usr/bin/env powershell
# SmartStudy API DAST Test Runner

param(
    [string]$BaseUrl = "http://localhost:5000",
    [string]$ConfigFile = "$PSScriptRoot/input.json"
)

$ErrorActionPreference = "Continue"
$ProgressPreference = "SilentlyContinue"

$ScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$TestDir = $ScriptRoot
$ReportFile = Join-Path $TestDir "report.json"
$SummaryFile = Join-Path $TestDir "report_summary.json"
$TestResults = @()
$TestStartTime = Get-Date

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "   SmartStudy API - DAST Security Test Suite" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

Write-Host "`n[*] Loading configuration..." -ForegroundColor Yellow

if (-Not (Test-Path $ConfigFile)) {
    Write-Host "ERROR: Config file not found: $ConfigFile" -ForegroundColor Red
    exit 1
}

$config = Get-Content $ConfigFile | ConvertFrom-Json
$BaseUrl = $config.baseUrl
$TokenUser = $config.user
$TokenAdmin = $config.admin

Write-Host "[OK] Base URL: $BaseUrl" -ForegroundColor Green
Write-Host "[OK] Loaded config with 2 roles" -ForegroundColor Green

function Test-APIConnectivity {
    param([string]$Url)
    
    Write-Host "`n[*] Testing API connectivity..." -ForegroundColor Yellow
    
    try {
        $response = Invoke-WebRequest -Uri "$Url/api/v1/health" -Method Get -TimeoutSec 5 -ErrorAction Stop
        Write-Host "[OK] API is reachable" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "[FAIL] Cannot reach API at $Url" -ForegroundColor Red
        return $false
    }
}

function Invoke-APIRequest {
    param(
        [string]$Method = "GET",
        [string]$Endpoint,
        [hashtable]$Headers = @{},
        [object]$Body = $null
    )
    
    $url = "$BaseUrl$Endpoint"
    $startTime = Get-Date
    
    $requestParams = @{
        Uri = $url
        Method = $Method
        TimeoutSec = 10
        ErrorAction = "Continue"
    }
    
    if ($Headers.Count -gt 0) {
        $requestParams['Headers'] = $Headers
    }
    
    if ($Body) {
        $requestParams['Body'] = ($Body | ConvertTo-Json -Compress)
        $requestParams['ContentType'] = 'application/json'
    }
    
    try {
        $response = Invoke-WebRequest @requestParams
        $endTime = Get-Date
        $duration = ($endTime - $startTime).TotalMilliseconds
        
        return @{
            StatusCode = $response.StatusCode
            Duration = $duration
            Body = $response.Content
            Success = $true
        }
    } catch {
        $endTime = Get-Date
        $duration = ($endTime - $startTime).TotalMilliseconds
        
        $statusCode = $_.Exception.Response.StatusCode.Value__
        $body = $null
        
        try {
            $body = $_.Exception.Response.Content.ReadAsString()
        } catch {
            $body = $_.Exception.Message
        }
        
        return @{
            StatusCode = if ($statusCode) { $statusCode } else { 0 }
            Duration = $duration
            Body = $body
            Success = $false
        }
    }
}

function Record-Test {
    param(
        [string]$Endpoint,
        [string]$Method,
        [string]$Role,
        [int]$StatusCode,
        [int]$ExpectedStatus,
        [string]$Category,
        [string]$Note = "",
        [double]$Duration = 0
    )
    
    $isVulnerable = $false
    $severity = "INFO"
    
    if ($StatusCode -ne $ExpectedStatus) {
        $isVulnerable = $true
        
        if ($Category -in @("AuthN Bypass", "AuthZ Bypass", "Privilege Escalation", "IDOR")) {
            $severity = "CRITICAL"
        } elseif ($Category -in @("Token Tampering", "Injection")) {
            $severity = "HIGH"
        } else {
            $severity = "MEDIUM"
        }
    }
    
    $result = @{
        timestamp = (Get-Date -Format 'o')
        endpoint = $Endpoint
        method = $Method
        role = $Role
        status = $StatusCode
        expected_status = $ExpectedStatus
        finding = $isVulnerable
        severity = $severity
        response_time_ms = [Math]::Round($Duration, 2)
        test_category = $Category
        note = $Note
    }
    
    $script:TestResults += $result
    
    $indicator = if ($isVulnerable) { "[FAIL]" } else { "[OK]" }
    $color = if ($isVulnerable) { "Red" } else { "Green" }
    
    Write-Host "$indicator $Method $Endpoint (Role: $Role) Status: $StatusCode" -ForegroundColor $color
    if ($Note) {
        Write-Host "      Note: $Note" -ForegroundColor Yellow
    }
}

# TEST EXECUTION

Write-Host "`n======================================================================" -ForegroundColor Cyan
Write-Host "STEP 1: API CONNECTIVITY TEST" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

if (-Not (Test-APIConnectivity $BaseUrl)) {
    Write-Host "`n[CRITICAL] API not reachable" -ForegroundColor Red
    exit 1
}

Write-Host "`n======================================================================" -ForegroundColor Cyan
Write-Host "STEP 2: PUBLIC ENDPOINTS" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

$response = Invoke-APIRequest -Method "GET" -Endpoint "/api/v1/health"
Record-Test -Endpoint "/api/v1/health" -Method "GET" -Role "public" -StatusCode $response.StatusCode -ExpectedStatus 200 -Category "Public Access" -Duration $response.Duration

$response = Invoke-APIRequest -Method "POST" -Endpoint "/api/v1/auth/login" -Body @{email="test@test.com"; password="test"}
Record-Test -Endpoint "/api/v1/auth/login" -Method "POST" -Role "public" -StatusCode $response.StatusCode -ExpectedStatus 401 -Category "Public Access" -Note "Wrong credentials" -Duration $response.Duration

$response = Invoke-APIRequest -Method "GET" -Endpoint "/api/v1/courses"
Record-Test -Endpoint "/api/v1/courses" -Method "GET" -Role "public" -StatusCode $response.StatusCode -ExpectedStatus 200 -Category "Public Access" -Duration $response.Duration

Write-Host "`n======================================================================" -ForegroundColor Cyan
Write-Host "STEP 3: AUTHENTICATION BYPASS (Missing Token)" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

$protectedEndpoints = @(
    @{path="/api/v1/auth/logout"; method="POST"}
    @{path="/api/v1/profile"; method="GET"}
    @{path="/api/v1/settings"; method="GET"}
    @{path="/api/v1/enrollments"; method="GET"}
    @{path="/api/v1/users"; method="GET"}
)

foreach ($endpoint in $protectedEndpoints) {
    $response = Invoke-APIRequest -Method $endpoint.method -Endpoint $endpoint.path
    Record-Test -Endpoint $endpoint.path -Method $endpoint.method -Role "none" -StatusCode $response.StatusCode -ExpectedStatus 401 -Category "AuthN Bypass" -Note "No token" -Duration $response.Duration
}

Write-Host "`n======================================================================" -ForegroundColor Cyan
Write-Host "STEP 4: AUTHORIZATION / PRIVILEGE ESCALATION" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

$adminEndpoints = @(
    @{path="/api/v1/courses"; method="POST"}
    @{path="/api/v1/admin/users"; method="GET"}
)

$userHeaders = @{
    "Authorization" = "Bearer $TokenUser"
}

foreach ($endpoint in $adminEndpoints) {
    $response = Invoke-APIRequest -Method $endpoint.method -Endpoint $endpoint.path -Headers $userHeaders
    Record-Test -Endpoint $endpoint.path -Method $endpoint.method -Role "user" -StatusCode $response.StatusCode -ExpectedStatus 403 -Category "AuthZ Bypass" -Note "User attempting admin action" -Duration $response.Duration
}

Write-Host "`n======================================================================" -ForegroundColor Cyan
Write-Host "STEP 5: IDOR (Insecure Direct Object References)" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

$otherUserId = "admin456"

$response = Invoke-APIRequest -Method "GET" -Endpoint "/api/v1/users/$otherUserId" -Headers $userHeaders
Record-Test -Endpoint "/api/v1/users/$otherUserId" -Method "GET" -Role "user" -StatusCode $response.StatusCode -ExpectedStatus 403 -Category "IDOR" -Note "User accessing other user profile" -Duration $response.Duration

$response = Invoke-APIRequest -Method "GET" -Endpoint "/api/v1/users/user123" -Headers $userHeaders
Record-Test -Endpoint "/api/v1/users/user123" -Method "GET" -Role "user" -StatusCode $response.StatusCode -ExpectedStatus 200 -Category "IDOR" -Note "User accessing own profile" -Duration $response.Duration

Write-Host "`n======================================================================" -ForegroundColor Cyan
Write-Host "STEP 6: RBAC MATRIX TEST" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

$protectedMatrix = @(
    @{path="/api/v1/profile"; method="GET"; expectedUser=200; expectedAdmin=200}
    @{path="/api/v1/settings"; method="GET"; expectedUser=200; expectedAdmin=200}
    @{path="/api/v1/users"; method="GET"; expectedUser=200; expectedAdmin=200}
    @{path="/api/v1/enrollments"; method="GET"; expectedUser=200; expectedAdmin=200}
    @{path="/api/v1/payments/history"; method="GET"; expectedUser=200; expectedAdmin=200}
)

$adminHeaders = @{
    "Authorization" = "Bearer $TokenAdmin"
}

foreach ($endpoint in $protectedMatrix) {
    $response = Invoke-APIRequest -Method $endpoint.method -Endpoint $endpoint.path -Headers $userHeaders
    Record-Test -Endpoint $endpoint.path -Method $endpoint.method -Role "user" -StatusCode $response.StatusCode -ExpectedStatus $endpoint.expectedUser -Category "RBAC Matrix" -Duration $response.Duration
    
    $response = Invoke-APIRequest -Method $endpoint.method -Endpoint $endpoint.path -Headers $adminHeaders
    Record-Test -Endpoint $endpoint.path -Method $endpoint.method -Role "admin" -StatusCode $response.StatusCode -ExpectedStatus $endpoint.expectedAdmin -Category "RBAC Matrix" -Duration $response.Duration
}

Write-Host "`n======================================================================" -ForegroundColor Cyan
Write-Host "STEP 7: TOKEN TAMPERING" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

$tamperedHeaders = @{
    "Authorization" = "Bearer malformed_xyz"
}

$response = Invoke-APIRequest -Method "GET" -Endpoint "/api/v1/profile" -Headers $tamperedHeaders
Record-Test -Endpoint "/api/v1/profile" -Method "GET" -Role "tampering" -StatusCode $response.StatusCode -ExpectedStatus 401 -Category "Token Tampering" -Note "Invalid token" -Duration $response.Duration

$modifiedToken = $TokenUser.Substring(0, $TokenUser.Length - 5) + "XXXXX"
$tamperedHeaders = @{
    "Authorization" = "Bearer $modifiedToken"
}

$response = Invoke-APIRequest -Method "GET" -Endpoint "/api/v1/profile" -Headers $tamperedHeaders
Record-Test -Endpoint "/api/v1/profile" -Method "GET" -Role "tampering" -StatusCode $response.StatusCode -ExpectedStatus 401 -Category "Token Tampering" -Note "Token modified" -Duration $response.Duration

Write-Host "`n======================================================================" -ForegroundColor Cyan
Write-Host "STEP 8: INJECTION DETECTION" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

$injectionPayloads = @(
    @{payload="admin' OR 1=1--"; name="SQLi"}
    @{payload="../../../etc/passwd"; name="Path Traversal"}
)

foreach ($payload in $injectionPayloads) {
    $response = Invoke-APIRequest -Method "GET" -Endpoint "/api/v1/users/$($payload.payload)" -Headers $userHeaders
    Record-Test -Endpoint "/api/v1/users/<$($payload.name)>" -Method "GET" -Role "user" -StatusCode $response.StatusCode -ExpectedStatus 404 -Category "Injection" -Note "$($payload.name) payload" -Duration $response.Duration
}

Write-Host "`n======================================================================" -ForegroundColor Cyan
Write-Host "STEP 9: RATE LIMITING" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

Write-Host "Sending 30 burst requests..."

$rateLimitResults = @()
for ($i = 1; $i -le 30; $i++) {
    $response = Invoke-APIRequest -Method "GET" -Endpoint "/api/v1/health"
    $rateLimitResults += $response.StatusCode
    Start-Sleep -Milliseconds 50
    if ($i % 10 -eq 0) { Write-Host "  $i requests sent" -ForegroundColor Gray }
}

$violations = @($rateLimitResults | Where-Object { $_ -eq 429 }).Count

if ($violations -gt 0) {
    Write-Host "[WARN] Rate limit detected: $violations/30 requests blocked" -ForegroundColor Yellow
} else {
    Write-Host "[FAIL] No rate limiting - API allows unlimited burst" -ForegroundColor Red
}

Record-Test -Endpoint "/api/v1/health" -Method "GET (30x)" -Role "all" -StatusCode (if ($violations -gt 0) { 429 } else { 200 }) -ExpectedStatus 429 -Category "Rate Limiting" -Note "Violations: $violations/30" -Duration 0

# REPORT GENERATION

Write-Host "`n======================================================================" -ForegroundColor Cyan
Write-Host "GENERATING REPORTS" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

$TestResults | ConvertTo-Json -Depth 10 | Out-File -FilePath $ReportFile -Encoding UTF8
Write-Host "[OK] Detailed report: $(Split-Path -Leaf $ReportFile)" -ForegroundColor Green

$findings = @($TestResults | Where-Object { $_.finding -eq $true })
$criticalFindings = @($findings | Where-Object { $_.severity -eq "CRITICAL" })
$highFindings = @($findings | Where-Object { $_.severity -eq "HIGH" })
$mediumFindings = @($findings | Where-Object { $_.severity -eq "MEDIUM" })

$summary = @{
    test_run_date = (Get-Date -Format 'o')
    total_endpoints = 14
    total_tests_run = $TestResults.Count
    total_findings = $findings.Count
    critical_severity = $criticalFindings.Count
    high_severity = $highFindings.Count
    medium_severity = $mediumFindings.Count
    average_response_time_ms = [Math]::Round(($TestResults | Measure-Object -Property response_time_ms -Average).Average, 2)
    pass_rate = [Math]::Round((($TestResults.Count - $findings.Count) / $TestResults.Count * 100), 2)
    test_duration_seconds = [Math]::Round(((Get-Date) - $TestStartTime).TotalSeconds, 2)
}

$summary | ConvertTo-Json -Depth 10 | Out-File -FilePath $SummaryFile -Encoding UTF8
Write-Host "[OK] Summary: $(Split-Path -Leaf $SummaryFile)" -ForegroundColor Green

# CONSOLE SUMMARY

Write-Host "`n"
Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host "                      TEST SUMMARY" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

Write-Host ""
Write-Host "Execution Summary:" -ForegroundColor Cyan
Write-Host "  Total Tests:              $($TestResults.Count)" -ForegroundColor White
Write-Host "  Test Duration:            $($summary.test_duration_seconds) sec" -ForegroundColor White
Write-Host "  Avg Response Time:        $($summary.average_response_time_ms) ms" -ForegroundColor White

Write-Host ""
Write-Host "Security Findings:" -ForegroundColor Cyan
Write-Host "  Pass Rate:                $($summary.pass_rate) percent" -ForegroundColor Green
Write-Host "  Total Findings:           $($findings.Count)" -ForegroundColor Yellow
Write-Host "    CRITICAL:               $($criticalFindings.Count)" -ForegroundColor Red
Write-Host "    HIGH:                   $($highFindings.Count)" -ForegroundColor Red
Write-Host "    MEDIUM:                 $($mediumFindings.Count)" -ForegroundColor Yellow

if ($criticalFindings.Count -gt 0) {
    Write-Host ""
    Write-Host "Critical Issues:" -ForegroundColor Red
    foreach ($f in $criticalFindings) {
        Write-Host "  FAIL $($f.method) $($f.endpoint)" -ForegroundColor Red
        Write-Host "    Category: $($f.test_category)" -ForegroundColor Red
        Write-Host "    Issue: $($f.note)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Reports created in: $TestDir" -ForegroundColor Cyan

exit 0
