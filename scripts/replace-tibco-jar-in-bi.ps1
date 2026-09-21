# Builds the local tibco-migration-assistant jar and swaps it into the Ballerina
# tool cache slot that WSO2 Integrator (BI) actually runs, so local converter
# changes show up in the BI desktop app without publishing/pulling a new version.
#
# Run scripts\revert-tibco-jar-in-bi.ps1 to restore the original jar.

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot "tibco-bi-jar-common.ps1")

Write-Host "Building tibco-migration-assistant jar..."
Push-Location $repoRoot
try {
    & .\gradlew.bat tibcoJar
    if ($LASTEXITCODE -ne 0) {
        throw "gradlew tibcoJar failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}

$builtJar = Get-ChildItem (Join-Path $repoRoot "cli-tibco\build\libs") -Filter "tibco-migration-assistant-*.jar" |
    Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $builtJar) {
    throw "Could not find built jar under cli-tibco\build\libs"
}
Write-Host "Built jar: $($builtJar.FullName)"

$cachedJarPath = Get-ActiveTibcoToolJarPath
Write-Host "Active BI tool cache jar: $cachedJarPath"

$backupPath = "$cachedJarPath.orig"
if (-not (Test-Path $backupPath)) {
    Copy-Item $cachedJarPath $backupPath
    Write-Host "Backed up original jar to: $backupPath"
} else {
    Write-Host "Backup already exists at: $backupPath (leaving it untouched)"
}

try {
    Copy-Item $builtJar.FullName $cachedJarPath -Force
} catch {
    throw "Failed to overwrite $cachedJarPath - it may be locked. Close WSO2 Integrator and any running 'bal' processes, then retry.`n$_"
}

Write-Host "Replaced BI's migrate-tibco jar with the local build."
Write-Host "Restart WSO2 Integrator (or re-run the migrate-tibco command) to pick up the change."
