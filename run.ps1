param([ValidateSet('test', 'experiment')][string]$Task = 'test')
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (-not $env:JAVA_HOME) {
    $jdk = Get-ChildItem "$env:USERPROFILE/.jdks" -Directory -ErrorAction SilentlyContinue |
        Where-Object { Test-Path (Join-Path $_.FullName 'bin/javac.exe') } |
        Sort-Object Name -Descending | Select-Object -First 1
    if ($jdk) { $env:JAVA_HOME = $jdk.FullName }
}
if (-not $env:JAVA_HOME) { throw 'Set JAVA_HOME to a JDK 17 or newer.' }
$java = Join-Path $env:JAVA_HOME 'bin/java.exe'

$maven = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if ($maven) {
    $mavenPath = $maven.Source
} else {
    $bundled = Get-ChildItem "$env:LOCALAPPDATA/Programs/IntelliJ IDEA*/plugins/maven-plugin/lib/maven3/bin/mvn.cmd" -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if (-not $bundled) { throw 'Install Maven 3.9+ and add its bin directory to PATH.' }
    $mavenPath = $bundled.FullName
}

& $mavenPath -B -ntp clean verify
if ($LASTEXITCODE -ne 0) { throw 'Build or tests failed.' }
if ($Task -eq 'experiment') {
    & $java '-Djava.awt.headless=true' -cp target/classes org.example.Experiment
    if ($LASTEXITCODE -ne 0) { throw 'Experiment failed.' }
}
