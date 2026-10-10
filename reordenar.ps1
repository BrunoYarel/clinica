# ------------------------------------------------------------------
# Reordena SOLO los 13 archivos cuyo package no coincide con su carpeta.
# No modifica el contenido de ningun archivo: unicamente los mueve.
#
# USO: guarda este archivo junto al pom.xml (carpeta del proyecto) y ejecuta:
#   powershell -ExecutionPolicy Bypass -File .\reordenar.ps1
# ------------------------------------------------------------------

param([string]$Proyecto = $PSScriptRoot)

if (-not $Proyecto) { $Proyecto = (Get-Location).Path }

$java = Join-Path $Proyecto 'src/main/java'
if (-not (Test-Path (Join-Path $Proyecto 'pom.xml')) -or -not (Test-Path $java)) {
    Write-Host "ERROR: no encuentro pom.xml y src/main/java en: $Proyecto" -ForegroundColor Red
    Write-Host "Copia este script a la carpeta que contiene pom.xml y vuelve a ejecutarlo." -ForegroundColor Red
    exit 1
}

$vista    = 'com/mycompany/clinica/vista'
$servicio = 'com/mycompany/clinica/servicio'

# Origen (relativo a src/main/java)  ->  Carpeta destino (relativa a src/main/java)
$movimientos = @(
    # Bruno: view/ -> vista
    @('view/Login.java',                        $vista),
    @('view/MenuPrincipal.java',                $vista),
    @('view/NuevoUsuario.java',                 $vista),
    @('view/CambiarPassword.java',              $vista),
    @('view/Estilo.java',                       $vista),
    # Oscar Ticona: raiz com/mycompany/clinica -> vista
    @('com/mycompany/clinica/Especialidad.java',    $vista),
    @('com/mycompany/clinica/FrmEspecialidad.java', $vista),
    @('com/mycompany/clinica/FrmEspecialidad.form', $vista),
    @('com/mycompany/clinica/FrmPaciente.java',     $vista),
    @('com/mycompany/clinica/FrmPaciente.form',     $vista),
    # Bruno: service/ -> servicio
    @('service/Sesion.java',                    $servicio),
    @('service/UsuarioService.java',            $servicio),
    @('service/PasswordUtil.java',              $servicio)
)

$movidos = 0; $omitidos = 0

foreach ($m in $movimientos) {
    $origen  = Join-Path $java $m[0]
    $carpeta = Join-Path $java $m[1]
    $destino = Join-Path $carpeta (Split-Path $m[0] -Leaf)

    if (-not (Test-Path -LiteralPath $origen)) {
        Write-Host "[omitido] $($m[0]) -> no existe en la ruta vieja (ya movido?)" -ForegroundColor Yellow
        $omitidos++
        continue
    }
    if (Test-Path -LiteralPath $destino) {
        Write-Host "[omitido] $($m[0]) -> ya existe en $($m[1]); borra la copia vieja a mano" -ForegroundColor Yellow
        $omitidos++
        continue
    }

    New-Item -ItemType Directory -Path $carpeta -Force | Out-Null
    Move-Item -LiteralPath $origen -Destination $destino
    Write-Host "[movido]  $($m[0])  ->  $($m[1])/" -ForegroundColor Green
    $movidos++
}

Write-Host ""
Write-Host "Listo: $movidos movidos, $omitidos omitidos." -ForegroundColor Cyan

$viejaView = Join-Path $java 'view'
if ((Test-Path $viejaView) -and -not (Get-ChildItem $viejaView -Force)) {
    Write-Host "La carpeta 'view' quedo vacia: ya puedes borrarla." -ForegroundColor Cyan
}