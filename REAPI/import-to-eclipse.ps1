<#
.SYNOPSIS
  Set up a Maven project for Eclipse and launch the IDE pointing at a workspace.

.DESCRIPTION
  Eclipse has no first-class headless "import" command for Java projects —
  the recommended import path is via the m2e wizard. This script does the
  rest of the work around it:

    1. Verifies the target directory is a Maven project (has pom.xml).
    2. Runs 'mvn install -DskipTests' so all transitive dependencies are
       in the local repo before Eclipse / m2e starts indexing.
    3. Finds eclipse.exe (or accepts an -EclipsePath override).
    4. Creates the workspace dir if missing.
    5. Launches Eclipse with that workspace.
    6. Prints the 3-click Import step that finishes the job.

  Re-runnable: rerun on the same project to refresh the build / reopen
  the IDE.

.PARAMETER ProjectPath
  Project root (default: current directory). Must contain pom.xml.

.PARAMETER Workspace
  Eclipse workspace folder (default: $env:USERPROFILE\eclipse-workspace).
  Created on demand.

.PARAMETER EclipsePath
  Full path to eclipse.exe. If omitted, common install locations are tried.

.PARAMETER SkipBuild
  Skip the 'mvn install -DskipTests' step. Useful when you just want to
  reopen Eclipse on a project you've already built recently.

.EXAMPLE
  # From the project root, with defaults:
  .\import-to-eclipse.ps1

.EXAMPLE
  # From anywhere, explicit paths:
  .\import-to-eclipse.ps1 -ProjectPath C:\git\REAPI `
                          -Workspace C:\workspaces\reapi-ws `
                          -EclipsePath 'C:\eclipse\eclipse.exe'
#>

[CmdletBinding()]
param(
  [string]$ProjectPath = (Get-Location).Path,
  [string]$Workspace   = (Join-Path $env:USERPROFILE 'eclipse-workspace'),
  [string]$EclipsePath,
  [switch]$SkipBuild
)

$ErrorActionPreference = 'Stop'

function Info($m) { Write-Host "[info]  $m" -ForegroundColor Cyan }
function Ok($m)   { Write-Host "[ok]    $m" -ForegroundColor Green }
function Fail($m) { Write-Host "[error] $m" -ForegroundColor Red; exit 1 }

# ── 1. Validate project ─────────────────────────────────────────────────────
if (-not (Test-Path $ProjectPath -PathType Container)) {
  Fail "ProjectPath not found: $ProjectPath"
}
$ProjectPath = (Resolve-Path $ProjectPath).Path

$pom = Join-Path $ProjectPath 'pom.xml'
if (-not (Test-Path $pom)) {
  Fail "Not a Maven project — pom.xml missing in $ProjectPath"
}
Info "Project   : $ProjectPath"

# ── 2. Pre-build (optional) ─────────────────────────────────────────────────
if (-not $SkipBuild) {
  if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Fail "mvn not on PATH. Install Maven, or rerun with -SkipBuild."
  }
  Info "Building dependencies (mvn install -DskipTests)…"
  Push-Location $ProjectPath
  try {
    & mvn install -DskipTests
    if ($LASTEXITCODE -ne 0) { Fail "mvn build failed (exit $LASTEXITCODE)" }
  } finally { Pop-Location }
  Ok   "Build complete — local Maven repo is warm"
} else {
  Info "Build skipped (-SkipBuild)"
}

# ── 3. Locate Eclipse ───────────────────────────────────────────────────────
if (-not $EclipsePath) {
  $candidates = @(
    'C:\eclipse\eclipse.exe',
    'C:\Program Files\eclipse\eclipse.exe',
    'C:\Program Files (x86)\eclipse\eclipse.exe',
    "$env:USERPROFILE\eclipse\eclipse.exe",
    "$env:LOCALAPPDATA\eclipse\eclipse.exe",
    "$env:LOCALAPPDATA\Programs\Eclipse Foundation\eclipse.exe"
  )
  # Also try anything under "Eclipse Adoptium"/"Eclipse Foundation"/"Eclipse Java" folders.
  $glob = @(
    'C:\eclipse*\eclipse.exe',
    'C:\Program Files\Eclipse*\eclipse.exe',
    "$env:USERPROFILE\eclipse*\eclipse.exe"
  ) | ForEach-Object { Get-ChildItem -Path $_ -ErrorAction SilentlyContinue } |
       Select-Object -ExpandProperty FullName

  $EclipsePath = ($candidates + $glob) |
                 Where-Object { $_ -and (Test-Path $_) } |
                 Select-Object -First 1
}

if (-not $EclipsePath -or -not (Test-Path $EclipsePath)) {
  Fail "Eclipse not found. Pass -EclipsePath 'C:\path\to\eclipse.exe' explicitly."
}
Info "Eclipse   : $EclipsePath"

# ── 4. Ensure workspace exists ──────────────────────────────────────────────
if (-not (Test-Path $Workspace)) {
  New-Item -ItemType Directory -Path $Workspace -Force | Out-Null
  Ok   "Created workspace: $Workspace"
} else {
  Info "Workspace : $Workspace"
}

# ── 5. Launch Eclipse ───────────────────────────────────────────────────────
Info "Launching Eclipse…"
Start-Process -FilePath $EclipsePath -ArgumentList @('-data', $Workspace) | Out-Null

# ── 6. Final instructions ───────────────────────────────────────────────────
Write-Host ""
Write-Host "Once Eclipse opens, finish the import:" -ForegroundColor Yellow
Write-Host "  1. File -> Import -> Maven -> Existing Maven Projects" -ForegroundColor Yellow
Write-Host "  2. Root Directory: $ProjectPath" -ForegroundColor Yellow
Write-Host "  3. Tick the pom.xml, click Finish." -ForegroundColor Yellow
Write-Host ""
Write-Host "After the first import, m2e remembers it — relaunching is just:" -ForegroundColor DarkGray
Write-Host "  .\import-to-eclipse.ps1 -SkipBuild" -ForegroundColor DarkGray
