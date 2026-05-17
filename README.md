# RESTAU MASTER PRO

**Sistema profissional de gestão de pedidos para restaurantes e lanchonetes.**

Single-file HTML5 com dashboard, mapa visual de mesas, comanda digital, cardápio com fotos e painel admin completo. Zero dependências externas.

## Funcionalidades

| Módulo | Descrição |
|--------|-----------|
| **Dashboard** | Estatísticas em tempo real com gráficos de faturamento diário/semanal/mensal |
| **Mapa de Mesas** | Planta baixa visual com status coloridos (livre, ocupada, reservada) |
| **Comanda Digital** | Adição de itens por categoria com cálculo automático de total |
| **Cardápio** | Cadastro com foto, descrição, categoria e preços |
| **Pagamento** | Modal com suporte a dinheiro, cartão, PIX e débito + cálculo de troco |
| **Histórico** | Vendas com filtros por período, visualização detalhada e reimpressão |
| **Painel Admin** | Usuários, configurações, manutenção (backup/restore/limpar), auditoria |
| **Tema** | Alternância entre modo claro e escuro com persistência |

## Características Técnicas

- 100% **single-file** (único arquivo `.html`)
- **Zero dependências externas** — sem CDN, sem frameworks, sem bibliotecas
- Persistência via **LocalStorage** do navegador
- Design **responsivo** e profissional
- Interface **premium** com animações e transições suaves
- **Multi-usuário** com sistema de autenticação
- **Backup e restore** completo em JSON
- **Auditoria** com registro de todas as ações importantes

## Como usar

1. Baixe o arquivo `restau-master-pro.html`
2. Abra no navegador
3. Pronto! Tudo funciona localmente, sem necessidade de servidor

## Preview

```
┌─────────────────────────────────────────────────┐
│  RESTAU MASTER PRO                              │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐           │
│  │  Mesa 1 │ │  Mesa 2 │ │  Mesa 3 │           │
│  │  LIVRE  │ │ OCUPADA │ │RESERVADA│           │
│  └─────────┘ └─────────┘ └─────────┘           │
│  Dashboard │ Pedidos │ Cardápio │ Admin         │
└─────────────────────────────────────────────────┘
```

## Licença

Projeto pessoal — uso livre.
