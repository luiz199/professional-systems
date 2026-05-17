# Professional Systems — Portfólio de Projetos

---

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
3. Login:
4. Pronto! Tudo funciona localmente, sem necessidade de servidor

---

# FITMANAGER PRO

**Sistema profissional de gestão de academia.**

Single-file HTML5 com Tailwind CSS, Alpine.js e Lucide Icons. Dashboard, controle de alunos, módulo financeiro, fichas de treino e catraca virtual.

## Funcionalidades

| Módulo | Descrição |
|--------|-----------|
| **Dashboard** | Métricas em tempo real: total de alunos, ativos, inadimplentes, faturamento mensal |
| **Alunos** | CRUD completo com tabela, busca por nome/CPF, modal de cadastro |
| **Financeiro** | Controle de inadimplência, histórico de caixa, recebimento com atualização automática |
| **Treinos** | Lista geral e ficha individual com abas A/B/C, gerenciamento completo de exercícios |
| **Catraca** | Simulador de acesso por CPF com liberação/bloqueio automático |
| **Alertas** | Painel de cobrança destacando alunos em atraso |
| **Fluxo** | Registro de entrada com histórico de acessos |

## Características Técnicas

- 100% **single-file** (único arquivo `.html`)
- **Tailwind CSS** via CDN para estilização
- **Alpine.js** para estado e interatividade em memória
- **Lucide Icons** para iconografia profissional
- Design **glassmorphism** com gradientes e animações
- **Toast notifications** para feedback de ações
- **Impressão profissional** de fichas de treino
- **Split login** com branding DataMind AI
- **5 alunos fictícios** pré-carregados

## Como usar

1. Baixe o arquivo `fitmanager-pro.html`
2. Abra no navegador
3. Login:
4. Pronto! Tudo funciona localmente, sem necessidade de servidor

---

# DATAMIND AI

**Site institucional SaaS com inteligência artificial integrada.**

Single-file HTML5 com Tailwind CSS, Alpine.js e Lucide Icons. Design futurista preto e neon verde, estilo NVIDIA. Chat IA inteligente, seção de clima e painel administrativo.

## Funcionalidades

| Módulo | Descrição |
|--------|-----------|
| **Hero** | Apresentação animada com partículas flutuantes e grid background |
| **Navbar** | Navegação responsiva com menu mobile |
| **Serviços** | 4 cards: IA, Automação, Análise de Dados, Soluções Digitais |
| **Planos** | 3 planos (Starter Grátis, Professional R$ 97, Enterprise R$ 297) |
| **Chat IA** | Assistente virtual com duas modalidades de resposta |
| **Clima** | Previsão do tempo com busca por cidade e 5 dias de forecast |
| **Painel** | Login e dashboard administrativo |
| **Footer** | Rodapé com branding |

## Chat IA — Duas Modalidades

| Modo | Descrição |
|------|-----------|
| **Ollama Local** | Conecta-se ao Ollama rodando em `localhost:11434`. Usa modelo local (ex: `llama3.2`) para respostas inteligentes e naturais |
| **Fallback Local** | Se o Ollama não estiver disponível, usa sistema próprio de NLP em português com respostas pré-programadas |

Status indicado no header do chat (verde = Ollama conectado, cinza = modo local).

## Clima

- API **Open-Meteo** (gratuita, sem necessidade de chave)
- Geocoding automático por nome de cidade
- Temperatura atual, sensação térmica, umidade, vento, pressão, UV
- Previsão para 5 dias com máximas/mínimas e chance de chuva
- Sugestões de cidades populares

## Características Técnicas

- 100% **single-file** (único arquivo `.html`)
- **Tailwind CSS** via CDN para estilização
- **Alpine.js** para estado e interatividade em memória
- **Lucide Icons** para iconografia profissional
- Design **futurista** dark com neon green (#00ff41)
- **Animações** CSS: fadeIn, slideUp, scaleIn, pulse, float, glowPulse
- **Glassmorphism** com backdrop-filter e bordas translúcidas
- **Responsivo** e mobile-first
- **Chat com contexto** — mantém histórico da conversa
- **Clima com dados reais** via Open-Meteo API
- **Ollama integration** — respostas via modelo local de IA

## Como usar

1. Baixe o arquivo `datamind-ai.html`
2. Abra no navegador
3. Chat IA funciona imediatamente (modo fallback)
4. Para modo Ollama: instale o [Ollama](https://ollama.ai), rode `ollama pull llama3.2`, e mantenha o serviço ativo
5. Login painel:

---

## Licença

Projetos pessoais — uso livre.
