# Shared helper for replace-tibco-jar-in-bi.ps1 / revert-tibco-jar-in-bi.ps1.
# Resolves the cached jar for the tool version that WSO2 Integrator (BI) currently
# has active for `migrate-tibco`, by reading the same bal-tools.toml the `bal` CLI uses.

function Get-ActiveTibcoToolJarPath {
    $balToolsToml = Join-Path $env:USERPROFILE ".ballerina\.config\bal-tools.toml"
    if (-not (Test-Path $balToolsToml)) {
        throw "Could not find $balToolsToml. Is the Ballerina tool cache initialized? Try running 'bal tool pull migrate-tibco' first."
    }

    $content = Get-Content $balToolsToml -Raw
    $blocks = $content -split '(?=\[\[tool\]\])' | Where-Object { $_ -match '\[\[tool\]\]' }

    $activeEntry = $null
    foreach ($block in $blocks) {
        if ($block -notmatch 'id\s*=\s*"migrate-tibco"') { continue }
        if ($block -notmatch 'active\s*=\s*true') { continue }

        $versionMatch = [regex]::Match($block, 'version\s*=\s*"([^"]+)"')
        $repoMatch = [regex]::Match($block, 'repository\s*=\s*"([^"]+)"')

        $activeEntry = [PSCustomObject]@{
            Version    = $versionMatch.Groups[1].Value
            Repository = if ($repoMatch.Success) { $repoMatch.Groups[1].Value } else { "central.ballerina.io" }
        }
        break
    }

    if (-not $activeEntry) {
        throw "No active 'migrate-tibco' entry found in $balToolsToml. Try running 'bal tool pull migrate-tibco' first."
    }

    $versionDir = Join-Path $env:USERPROFILE ".ballerina\repositories\$($activeEntry.Repository)\bala\wso2\tool_migrate_tibco\$($activeEntry.Version)"
    if (-not (Test-Path $versionDir)) {
        throw "Expected tool cache directory not found: $versionDir"
    }

    $platformDir = Get-ChildItem $versionDir -Directory | Select-Object -First 1
    if (-not $platformDir) {
        throw "No platform subfolder found under $versionDir"
    }

    $toolLibsDir = Join-Path $platformDir.FullName "tool\libs"
    $cachedJar = Get-ChildItem $toolLibsDir -Filter "tibco-migration-assistant-*.jar" -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $cachedJar) {
        throw "No tibco-migration-assistant-*.jar found under $toolLibsDir"
    }

    return $cachedJar.FullName
}
