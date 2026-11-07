Add-Type -AssemblyName System.Drawing

function New-Placeholder {
    param([string]$Path, [string]$Title, [string]$Sub, [string]$Accent, [int]$W=1280, [int]$H=720)

    $bmp = New-Object System.Drawing.Bitmap($W, $H)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = 'HighQuality'
    $g.TextRenderingHint = 'AntiAliasGridFit'

    $bg1 = [System.Drawing.Color]::FromArgb(255,12,12,20)
    $bg2 = [System.Drawing.Color]::FromArgb(255,20,20,40)

    $ar = [System.Int32]::Parse($Accent.Substring(1,2), 'AllowHexSpecifier')
    $ag = [System.Int32]::Parse($Accent.Substring(3,2), 'AllowHexSpecifier')
    $ab = [System.Int32]::Parse($Accent.Substring(5,2), 'AllowHexSpecifier')
    $ac = [System.Drawing.Color]::FromArgb(255,$ar,$ag,$ab)

    $rect = New-Object System.Drawing.Rectangle(0,0,$W,$H)
    $brush = New-Object System.Drawing.Drawing2D.LinearGradientBrush($rect, $bg1, $bg2, 45)
    $g.FillRectangle($brush, $rect)

    $barBrush = New-Object System.Drawing.SolidBrush($ac)
    $g.FillRectangle($barBrush, 0, 0, $W, 4)

    $gridPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(15,255,255,255))
    for ($x=0; $x -le $W; $x+=60) { $g.DrawLine($gridPen, $x, 0, $x, $H) }
    for ($y=0; $y -le $H; $y+=60) { $g.DrawLine($gridPen, 0, $y, $W, $y) }

    $bottomPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(30,255,255,255))
    $g.DrawLine($bottomPen, 0, $H-60, $W, $H-60)

    $fontT = New-Object System.Drawing.Font('Segoe UI', 48, [System.Drawing.FontStyle]::Bold)
    $fontS = New-Object System.Drawing.Font('Segoe UI', 22)
    $fontB = New-Object System.Drawing.Font('Segoe UI', 11)

    $fmt = New-Object System.Drawing.StringFormat
    $fmt.Alignment = 'Center'
    $fmt.LineAlignment = 'Center'

    $white = [System.Drawing.Color]::White
    $gray = [System.Drawing.Color]::FromArgb(180,180,200)
    $dim = [System.Drawing.Color]::FromArgb(100,255,255,255)

    $g.DrawString($Title, $fontT, (New-Object System.Drawing.SolidBrush($white)), ($W/2), ($H/2 - 50), $fmt)
    $g.DrawString($Sub, $fontS, (New-Object System.Drawing.SolidBrush($gray)), ($W/2), ($H/2 + 50), $fmt)

    $lp = New-Object System.Drawing.Pen($ac, 3)
    $g.DrawLine($lp, ($W/2 - 80), ($H/2 + 20), ($W/2 + 80), ($H/2 + 20))

    $g.DrawString("Placeholder - substitua por screenshot real", $fontB, (New-Object System.Drawing.SolidBrush($dim)), ($W/2), ($H-30), $fmt)

    $cp = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(60,$ac.R,$ac.G,$ac.B), 2)
    $g.DrawLine($cp, 20, 30, 20, 80)
    $g.DrawLine($cp, 20, 30, 80, 30)

    $bmp.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose()
    $bmp.Dispose()
}

function Gen-Restau {
    $d = "C:\Users\luizw\AppData\Local\Temp\opencode\restau-master-pro\screenshots"
    $list = @(
        @{t="RESTAU MASTER PRO"; s="Tela de Login"; a="#f39c12"; f="login.png"}
        @{t="Dashboard"; s="Metricas e Graficos"; a="#f39c12"; f="dashboard.png"}
        @{t="Mapa de Mesas"; s="Layout Visual Interativo"; a="#f39c12"; f="mesas.png"}
        @{t="Cardapio Digital"; s="Produtos com Fotos e Categorias"; a="#f39c12"; f="cardapio.png"}
        @{t="Sistema de Pedidos"; s="Comandas e Adicao de Itens"; a="#f39c12"; f="pedidos.png"}
        @{t="Painel Administrativo"; s="Usuarios, Configuracoes e Backup"; a="#f39c12"; f="admin.png"}
        @{t="Tema Escuro"; s="Modo Noturno Padrao"; a="#f39c12"; f="dark-mode.png"}
        @{t="Tema Claro"; s="Modo Diurno"; a="#f39c12"; f="light-mode.png"}
    )
    foreach ($x in $list) {
        New-Placeholder -Path (Join-Path $d $x.f) -Title $x.t -Sub $x.s -Accent $x.a
        Write-Host "  [OK] $($x.f)" -ForegroundColor Green
    }
}

function Gen-Fit {
    $d = "C:\Users\luizw\AppData\Local\Temp\opencode\fitmanager-pro\screenshots"
    $list = @(
        @{t="FitManager Pro"; s="Tela de Login"; a="#6366f1"; f="login.png"}
        @{t="Dashboard"; s="Metricas da Academia"; a="#6366f1"; f="dashboard.png"}
        @{t="Gerenciamento de Alunos"; s="CRUD com Busca e Status"; a="#6366f1"; f="alunos.png"}
        @{t="Fichas de Treino"; s="Splits A/B/C"; a="#6366f1"; f="treinos.png"}
        @{t="Catraca Virtual"; s="Acesso por CPF"; a="#6366f1"; f="catraca.png"}
        @{t="Controle Financeiro"; s="Mensalidades e Inadimplencia"; a="#6366f1"; f="financeiro.png"}
        @{t="Painel de Alertas"; s="Notificacoes do Sistema"; a="#6366f1"; f="alertas.png"}
    )
    foreach ($x in $list) {
        New-Placeholder -Path (Join-Path $d $x.f) -Title $x.t -Sub $x.s -Accent $x.a
        Write-Host "  [OK] $($x.f)" -ForegroundColor Green
    }
}

function Gen-Data {
    $d = "C:\Users\luizw\AppData\Local\Temp\opencode\datamind-ai\screenshots"
    $list = @(
        @{t="DataMind AI"; s="Hero Page com Particulas"; a="#00ff41"; f="hero.png"}
        @{t="Nossos Servicos"; s="IA, Automacao, Analise e Solucoes"; a="#00ff41"; f="servicos.png"}
        @{t="Planos e Precos"; s="Starter - Professional - Enterprise"; a="#00ff41"; f="planos.png"}
        @{t="Chat IA"; s="Ollama Local + Fallback NLP"; a="#00ff41"; f="chat.png"}
        @{t="Previsao do Tempo"; s="Open-Meteo API - 5 Dias"; a="#00ff41"; f="clima.png"}
        @{t="Painel Admin"; s="Gerenciamento da Plataforma"; a="#00ff41"; f="admin.png"}
    )
    foreach ($x in $list) {
        New-Placeholder -Path (Join-Path $d $x.f) -Title $x.t -Sub $x.s -Accent $x.a
        Write-Host "  [OK] $($x.f)" -ForegroundColor Green
    }
}

function Gen-Port {
    $d = "C:\Users\luizw\AppData\Local\Temp\opencode\portfolio\screenshots"
    $list = @(
        @{t="Portfolio CS"; s="Hero com Rede de Particulas"; a="#00ff41"; f="hero.png"}
        @{t="Sobre Mim"; s="Formacao e Trajetoria"; a="#00ff41"; f="sobre.png"}
        @{t="Linha do Tempo"; s="Carreira Profissional"; a="#00ff41"; f="timeline.png"}
        @{t="Projetos"; s="Portfolio de Trabalhos"; a="#00ff41"; f="projetos.png"}
        @{t="Blog"; s="Artigos Tecnicos"; a="#00ff41"; f="blog.png"}
        @{t="Contato"; s="Formulario e Redes Sociais"; a="#00ff41"; f="contato.png"}
        @{t="Tema Escuro"; s="Modo Computer Science"; a="#00ff41"; f="dark-mode.png"}
        @{t="Tema Claro"; s="Modo Diurno"; a="#00ff41"; f="light-mode.png"}
    )
    foreach ($x in $list) {
        New-Placeholder -Path (Join-Path $d $x.f) -Title $x.t -Sub $x.s -Accent $x.a
        Write-Host "  [OK] $($x.f)" -ForegroundColor Green
    }
}

# --- Main ---
Write-Host "=== Gerador de Screenshots Placeholders ===" -ForegroundColor Cyan

Write-Host "[RESTAU MASTER PRO]" -ForegroundColor Yellow; Gen-Restau
Write-Host "`n[FitManager Pro]" -ForegroundColor Yellow; Gen-Fit
Write-Host "`n[DataMind AI]" -ForegroundColor Yellow; Gen-Data
Write-Host "`n[Portfolio CS]" -ForegroundColor Yellow; Gen-Port

Write-Host "`nConcluido!" -ForegroundColor Cyan
