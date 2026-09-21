# Restores the original migrate-tibco tool jar previously backed up by
# scripts\replace-tibco-jar-in-bi.ps1, undoing the local-dev swap.

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "tibco-bi-jar-common.ps1")

$cachedJarPath = Get-ActiveTibcoToolJarPath
$backupPath = "$cachedJarPath.orig"

if (-not (Test-Path $backupPath)) {
    throw "No backup found at $backupPath - nothing to revert. Did you run scripts\replace-tibco-jar-in-bi.ps1?"
}

try {
    Copy-Item $backupPath $cachedJarPath -Force
} catch {
    throw "Failed to restore $cachedJarPath - it may be locked. Close WSO2 Integrator and any running 'bal' processes, then retry.`n$_"
}

Write-Host "Restored original jar at: $cachedJarPath"
Write-Host "Restart WSO2 Integrator (or re-run the migrate-tibco command) to pick up the change."
