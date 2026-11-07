<div align="center">
  <br/>
  <a href="https://github.com/luiz199/professional-systems/tree/master/datamind-ai">
    <img src="https://img.shields.io/badge/DataMind-AI-00FF41?style=for-the-badge&logo=openai&logoColor=white&labelColor=0a0a0a" alt="DataMind AI"/>
  </a>
  <br/>
  <br/>

  <p align="center">
    <strong>🧠 AI-Powered SaaS Platform</strong>
    <br/>
    Intelligent chatbot · Real-time weather · Premium design
  </p>

  <br/>

  <div>
    <img src="https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white" alt="HTML5"/>
    <img src="https://img.shields.io/badge/TailwindCSS-3.4-06B6D4?style=flat-square&logo=tailwindcss" alt="TailwindCSS"/>
    <img src="https://img.shields.io/badge/Alpine.js-3.x-8BC0D0?style=flat-square&logo=alpinedotjs&logoColor=black" alt="Alpine.js"/>
    <img src="https://img.shields.io/badge/Ollama-000000?style=flat-square&logo=ollama&logoColor=white" alt="Ollama"/>
    <img src="https://img.shields.io/badge/Open--Meteo-FF6F00?style=flat-square&logo=openweathermap&logoColor=white" alt="Open-Meteo"/>
    <img src="https://img.shields.io/badge/Lucide_Icons-F56565?style=flat-square&logo=lucide&logoColor=white" alt="Lucide"/>
  </div>

  <div>
    <img src="https://img.shields.io/github/last-commit/luiz199/professional-systems/master?style=flat-square&logo=git&logoColor=white&labelColor=0a0a0a&color=00FF41" alt="Last Commit"/>
    <img src="https://img.shields.io/github/repo-size/luiz199/professional-systems?style=flat-square&logo=files&logoColor=white&labelColor=0a0a0a&color=00FF41" alt="Repo Size"/>
    <img src="https://img.shields.io/github/license/luiz199/professional-systems?style=flat-square&logo=opensourceinitiative&logoColor=white&labelColor=0a0a0a&color=00FF41" alt="License"/>
  </div>

  <br/>
  <br/>
</div>

---

## 📋 Overview

**DataMind AI** is a premium SaaS landing page with integrated artificial intelligence chatbot and real-time weather forecast. Built as a single HTML file with Tailwind CSS, Alpine.js, and Lucide Icons. Features a futuristic black/neon green design inspired by NVIDIA's aesthetic.

### ✨ Key Features

| Module | Description |
|--------|-------------|
| 🏠 **Animated Hero** | Floating particles, grid background, typing effects |
| 🤖 **AI Chat** | Two modes: Ollama local (LLM) or built-in NLP fallback |
| 🌤️ **Weather** | 5-day forecast via Open-Meteo API (free, no key needed) |
| 💲 **Pricing** | 3 plans: Free, Professional R$97/mo, Enterprise R$297/mo |
| 🛠️ **Services** | 4 cards: AI, Automation, Data Analysis, Digital Solutions |
| 🔐 **Admin Panel** | Protected dashboard with login (any email/password) |
| 📱 **Responsive** | Mobile-first design with hamburger menu |

---

## 🖼️ Screenshot

<p align="center">
  <img src="../portfolio/screenshots/datamind-ai.png" alt="DataMind AI Screenshot" width="90%" />
</p>

---

## 🤖 AI Chat — Two Modes

| Mode | How it Works |
|------|-------------|
| **Ollama Local** | Connects to `localhost:11434`. Requires Ollama installed + model pulled |
| **NLP Fallback** | Portuguese keyword-based NLP. Works offline, no dependencies |

> Status indicator in chat header: 🟢 green = Ollama connected, ⚫ gray = local fallback

---

## 🌤️ Weather

- **API:** [Open-Meteo](https://open-meteo.com/) — completely free, no API key
- **Geocoding:** Automatic city name search
- **Forecast:** 5 days with temperature, humidity, wind, UV index
- **Suggestions:** Quick-select for popular cities

---

## 📂 Structure

```
datamind-ai/
├── index.html       # Complete application (single-file)
├── README.md        # This file
└── screenshots/     # Project screenshots
```

---

## 🚀 Quick Start

```bash
# Open directly in browser
start datamind-ai/index.html

# For Ollama integration:
# 1. Install Ollama: https://ollama.ai
# 2. Pull a model: ollama pull llama3.2
# 3. Keep Ollama running in background
# 4. Refresh the page — chat connects automatically
```

> **Note:** All features work immediately in fallback mode without any setup.

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| HTML5 | Structure & semantics |
| Tailwind CSS 3.4 | Styling via CDN |
| Alpine.js 3.x | State management & interactivity |
| Lucide Icons | Professional icon set |
| Open-Meteo API | Free weather data |
| Ollama API | Local LLM integration |

---

## 📄 License

MIT License — see [LICENSE](../LICENSE).

---

<div align="center">
  <sub>Built with ❤️ by <a href="https://github.com/luiz199">Luiz Henrique</a></sub>
  <br/>
  <sub>© 2026 DataMind — Inteligência • Inovação • Impacto</sub>
</div>
