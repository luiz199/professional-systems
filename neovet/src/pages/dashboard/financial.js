import { useState, useEffect } from "react"
import Layout from "@/components/Layout"
import { motion } from "framer-motion"
import { FiPlus, FiTrash2, FiSearch, FiDollarSign, FiTrendingUp, FiTrendingDown, FiDownload, FiFileText } from "react-icons/fi"
import { getFinancial, saveTransaction, deleteTransaction, getSettings } from "@/data/store"
import toast from "react-hot-toast"

const categorias = ["Consulta", "Vacina", "Cirurgia", "Banho/Tosa", "Medicamento", "Produto", "Exame", "Outro"]

export default function Financial() {
  const [transactions, setTransactions] = useState([])
  const [search, setSearch] = useState("")
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState({ descricao: "", valor: "", tipo: "entrada", categoria: "Consulta", data: new Date().toISOString().split("T")[0], pagamento: "dinheiro" })

  useEffect(() => { setTransactions(getFinancial()) }, [])

  function load() { setTransactions(getFinancial()) }

  function handleSave(e) {
    e.preventDefault()
    if (!form.descricao || !form.valor) return toast.error("Descrição e valor são obrigatórios")
    saveTransaction({ ...form })
    toast.success("Transação registrada!")
    setModalOpen(false)
    setForm({ descricao: "", valor: "", tipo: "entrada", categoria: "Consulta", data: new Date().toISOString().split("T")[0], pagamento: "dinheiro" })
    load()
  }

  function handleDelete(id) {
    if (!confirm("Tem certeza?")) return
    deleteTransaction(id)
    toast.success("Transação removida!")
    load()
  }

  const filtered = transactions.filter(t =>
    t.descricao?.toLowerCase().includes(search.toLowerCase()) ||
    t.categoria?.toLowerCase().includes(search.toLowerCase())
  )

  const totalEntradas = transactions.filter(t => t.tipo === "entrada").reduce((s, t) => s + Number(t.valor || 0), 0)
  const totalSaidas = transactions.filter(t => t.tipo === "saida").reduce((s, t) => s + Number(t.valor || 0), 0)
  const saldo = totalEntradas - totalSaidas

  function emitirNota(transaction) {
    const settings = getSettings()
    const win = window.open("", "_blank")
    const valor = Number(transaction.valor || 0)
    const impostos = valor * (Number(settings.impostos || 0) / 100)
    const total = valor + impostos
    win.document.write(`
      <html><head><title>Nota Fiscal - ${settings.clinicaNome}</title>
      <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Courier New', monospace; background: #fff; color: #000; padding: 40px; font-size: 12px; }
        .header { text-align: center; border-bottom: 2px solid #000; padding-bottom: 20px; margin-bottom: 20px; }
        .header h1 { font-size: 18px; margin-bottom: 4px; }
        .header p { font-size: 11px; color: #444; }
        .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px; }
        .info-box { border: 1px solid #ccc; padding: 12px; }
        .info-box h3 { font-size: 10px; text-transform: uppercase; margin-bottom: 8px; color: #666; }
        .info-box p { font-size: 12px; line-height: 1.6; }
        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
        th { background: #f0f0f0; padding: 8px; text-align: left; font-size: 10px; text-transform: uppercase; }
        td { padding: 8px; border-bottom: 1px solid #eee; font-size: 12px; }
        .total-box { text-align: right; padding: 16px; border-top: 2px solid #000; margin-top: 10px; }
        .total-box .linha { margin: 4px 0; }
        .total-box .grand-total { font-size: 18px; font-weight: bold; margin-top: 8px; }
        .footer { text-align: center; margin-top: 40px; font-size: 10px; color: #888; border-top: 1px solid #ccc; padding-top: 16px; }
        .status { display: inline-block; padding: 4px 12px; border: 1px solid #000; font-size: 10px; letter-spacing: 1px; margin-top: 8px; }
        .cnpj { font-size: 10px; color: #666; }
      </style></head><body>
        <div class="header">
          <h1>${settings.clinicaNome}</h1>
          <p>${settings.clinicaEndereco || ""}</p>
          <p>${settings.clinicaTelefone ? "Tel: " + settings.clinicaTelefone : ""} ${settings.clinicaEmail ? "| Email: " + settings.clinicaEmail : ""}</p>
          <p class="cnpj">CNPJ: ${settings.clinicaCnpj || "—"}</p>
          <div class="status">NOTA FISCAL</div>
        </div>
        <div class="info-grid">
          <div class="info-box">
            <h3>Prestador</h3>
            <p>${settings.medicoNome}<br>CRMV: ${settings.medicoCrmv || "—"}</p>
          </div>
          <div class="info-box">
            <h3>Dados da Nota</h3>
            <p>Nº: ${String(transaction.id).toUpperCase()}<br>Data: ${new Date(transaction.data).toLocaleDateString("pt-BR")}<br>Pagamento: ${transaction.pagamento}</p>
          </div>
        </div>
        <table>
          <tr><th>Descrição</th><th>Categoria</th><th style="text-align:right">Valor</th></tr>
          <tr><td>${transaction.descricao}</td><td>${transaction.categoria}</td><td style="text-align:right">R$ ${valor.toFixed(2)}</td></tr>
        </table>
        <div class="total-box">
          <div class="linha">Subtotal: R$ ${valor.toFixed(2)}</div>
          <div class="linha">Impostos (${settings.impostos || 0}%): R$ ${impostos.toFixed(2)}</div>
          <div class="grand-total">Total: R$ ${total.toFixed(2)}</div>
        </div>
        <div class="footer">
          <p>NeoVet — O futuro da veterinária digital</p>
          <p>Documento gerado em ${new Date().toLocaleString("pt-BR")}</p>
        </div>
      </body></html>
    `)
    win.document.close()
  }

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between flex-wrap gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Financeiro</h1>
            <p className="text-sm text-gray-400">{transactions.length} transações</p>
          </div>
          <button onClick={() => setModalOpen(true)}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-[#10b981] to-[#84cc16] text-white text-sm font-medium hover:shadow-lg hover:shadow-[#10b981]/20 transition-all">
            <FiPlus /> Nova Transação
          </button>
        </div>

        {/* Summary cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-5">
            <div className="flex items-center gap-3 mb-2">
              <div className="p-2.5 rounded-xl bg-[#10b981]/10"><FiTrendingUp className="text-[#10b981]" /></div>
              <span className="text-sm text-gray-400">Entradas</span>
            </div>
            <p className="text-2xl font-bold text-[#10b981]">R$ {totalEntradas.toLocaleString()}</p>
          </div>
          <div className="rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-5">
            <div className="flex items-center gap-3 mb-2">
              <div className="p-2.5 rounded-xl bg-red-400/10"><FiTrendingDown className="text-red-400" /></div>
              <span className="text-sm text-gray-400">Saídas</span>
            </div>
            <p className="text-2xl font-bold text-red-400">R$ {totalSaidas.toLocaleString()}</p>
          </div>
          <div className="rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-5">
            <div className="flex items-center gap-3 mb-2">
              <div className="p-2.5 rounded-xl bg-[#06b6d4]/10"><FiDollarSign className="text-[#06b6d4]" /></div>
              <span className="text-sm text-gray-400">Saldo</span>
            </div>
            <p className={`text-2xl font-bold ${saldo >= 0 ? 'text-[#10b981]' : 'text-red-400'}`}>R$ {saldo.toLocaleString()}</p>
          </div>
        </div>

        {/* Search and filter */}
        <div className="relative">
          <FiSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
          <input type="text" value={search} onChange={e => setSearch(e.target.value)}
            placeholder="Buscar transações..."
            className="w-full pl-11 pr-4 py-3 rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 text-gray-900 dark:text-white placeholder-gray-400 focus:outline-none focus:border-[#10b981]/30 transition-all"
          />
        </div>

        {/* Transactions list */}
        <div className="space-y-2">
          {filtered.length === 0 ? (
            <div className="text-center py-12 text-gray-400">Nenhuma transação encontrada</div>
          ) : filtered.sort((a, b) => (b.data || "").localeCompare(a.data || "")).map((t, i) => (
            <motion.div
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: i * 0.02 }}
              key={t.id}
              className="flex items-center justify-between p-4 rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 hover:shadow-lg transition-all group"
            >
              <div className="flex items-center gap-4">
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center text-lg ${t.tipo === "entrada" ? 'bg-[#10b981]/10' : 'bg-red-400/10'}`}>
                  {t.tipo === "entrada" ? <FiTrendingUp className="text-[#10b981]" /> : <FiTrendingDown className="text-red-400" />}
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-900 dark:text-white">{t.descricao}</p>
                  <div className="flex items-center gap-2 text-xs text-gray-400">
                    <span>{t.categoria}</span>
                    <span>•</span>
                    <span>{t.pagamento}</span>
                    <span>•</span>
                    <span>{t.data}</span>
                  </div>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <span className={`text-sm font-bold ${t.tipo === "entrada" ? 'text-[#10b981]' : 'text-red-400'}`}>
                  {t.tipo === "entrada" ? "+" : "-"} R$ {Number(t.valor || 0).toLocaleString()}
                </span>
                {t.tipo === "entrada" && (
                  <button onClick={() => emitirNota(t)} className="p-2 rounded-lg opacity-0 group-hover:opacity-100 text-gray-400 hover:text-[#10b981] transition-all" title="Emitir Nota Fiscal">
                    <FiFileText size={14} />
                  </button>
                )}
                <button onClick={() => handleDelete(t.id)} className="p-2 rounded-lg opacity-0 group-hover:opacity-100 text-gray-400 hover:text-red-400 transition-all">
                  <FiTrash2 size={14} />
                </button>
              </div>
            </motion.div>
          ))}
        </div>
      </div>

      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={() => setModalOpen(false)} />
          <motion.div initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }}
            className="relative w-full max-w-lg rounded-2xl bg-white dark:bg-[#0f172a] border border-white/10 p-6">
            <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-6">Nova Transação</h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Tipo</label>
                  <select value={form.tipo} onChange={e => setForm({ ...form, tipo: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all">
                    <option value="entrada">Entrada</option>
                    <option value="saida">Saída</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Categoria</label>
                  <select value={form.categoria} onChange={e => setForm({ ...form, categoria: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all">
                    {categorias.map(c => <option key={c} value={c}>{c}</option>)}
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Descrição</label>
                <input type="text" value={form.descricao} onChange={e => setForm({ ...form, descricao: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all" required />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Valor (R$)</label>
                  <input type="number" step="0.01" value={form.valor} onChange={e => setForm({ ...form, valor: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all" required />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Pagamento</label>
                  <select value={form.pagamento} onChange={e => setForm({ ...form, pagamento: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all">
                    <option value="dinheiro">Dinheiro</option>
                    <option value="cartao">Cartão</option>
                    <option value="pix">Pix</option>
                    <option value="boleto">Boleto</option>
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Data</label>
                <input type="date" value={form.data} onChange={e => setForm({ ...form, data: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all" />
              </div>
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setModalOpen(false)}
                  className="flex-1 py-3 rounded-xl border border-white/10 text-gray-400 hover:text-white text-sm">Cancelar</button>
                <button type="submit"
                  className="flex-1 py-3 rounded-xl bg-gradient-to-r from-[#10b981] to-[#84cc16] text-white text-sm font-medium hover:shadow-lg transition-all">
                  Registrar
                </button>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </Layout>
  )
}
