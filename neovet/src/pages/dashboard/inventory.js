import { useState, useEffect } from "react"
import Layout from "@/components/Layout"
import { motion } from "framer-motion"
import { FiPlus, FiEdit2, FiTrash2, FiSearch, FiBox, FiAlertTriangle } from "react-icons/fi"
import { getInventory, saveInventoryItem, deleteInventoryItem } from "@/data/store"
import toast from "react-hot-toast"

export default function Inventory() {
  const [items, setItems] = useState([])
  const [search, setSearch] = useState("")
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({ nome: "", categoria: "Medicamento", quantidade: "", minimo: "", fornecedor: "", observacoes: "" })

  useEffect(() => { setItems(getInventory()) }, [])

  function load() { setItems(getInventory()) }

  function openEdit(item) {
    if (item) { setEditing(item.id); setForm(item) }
    else { setEditing(null); setForm({ nome: "", categoria: "Medicamento", quantidade: "", minimo: "", fornecedor: "", observacoes: "" }) }
    setModalOpen(true)
  }

  function handleSave(e) {
    e.preventDefault()
    if (!form.nome.trim()) return toast.error("Nome é obrigatório")
    saveInventoryItem(editing ? { id: editing, ...form } : form)
    toast.success(editing ? "Item atualizado!" : "Item cadastrado!")
    setModalOpen(false)
    load()
  }

  function handleDelete(id) {
    if (!confirm("Tem certeza?")) return
    deleteInventoryItem(id)
    toast.success("Item removido!")
    load()
  }

  const filtered = items.filter(i =>
    i.nome?.toLowerCase().includes(search.toLowerCase()) ||
    i.categoria?.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between flex-wrap gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Estoque</h1>
            <p className="text-sm text-gray-400">{items.length} itens</p>
          </div>
          <button onClick={() => openEdit(null)}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-[#a855f7] to-[#d946ef] text-white text-sm font-medium hover:shadow-lg hover:shadow-[#a855f7]/20 transition-all">
            <FiPlus /> Novo Item
          </button>
        </div>

        <div className="relative">
          <FiSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
          <input type="text" value={search} onChange={e => setSearch(e.target.value)}
            placeholder="Buscar no estoque..."
            className="w-full pl-11 pr-4 py-3 rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 text-gray-900 dark:text-white placeholder-gray-400 focus:outline-none focus:border-[#a855f7]/30 transition-all"
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.length === 0 ? (
            <div className="col-span-full text-center py-12 text-gray-400">Nenhum item encontrado</div>
          ) : filtered.map((item, i) => {
            const baixo = item.quantidade && item.minimo && Number(item.quantidade) <= Number(item.minimo)
            return (
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.05 }}
                key={item.id}
                className={`group rounded-2xl bg-white dark:bg-[#0f172a] border p-5 hover:shadow-lg transition-all ${baixo ? 'border-red-400/30' : 'border-gray-200 dark:border-white/5'}`}
              >
                <div className="flex items-start justify-between mb-3">
                  <div className="flex items-center gap-3">
                    <div className={`w-12 h-12 rounded-xl bg-gradient-to-br flex items-center justify-center text-white text-lg ${baixo ? 'from-red-400 to-red-500' : 'from-[#a855f7] to-[#d946ef]'}`}>
                      <FiBox />
                    </div>
                    <div>
                      <h3 className="font-semibold text-gray-900 dark:text-white flex items-center gap-2">
                        {item.nome}
                        {baixo && <FiAlertTriangle className="text-red-400 text-sm" />}
                      </h3>
                      <p className="text-xs text-gray-400">{item.categoria}</p>
                    </div>
                  </div>
                  <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                    <button onClick={() => openEdit(item)} className="p-2 rounded-lg hover:bg-white/5 text-gray-400 hover:text-[#a855f7]"><FiEdit2 size={14} /></button>
                    <button onClick={() => handleDelete(item.id)} className="p-2 rounded-lg hover:bg-white/5 text-gray-400 hover:text-red-400"><FiTrash2 size={14} /></button>
                  </div>
                </div>
                <div className="flex items-center gap-4 text-sm">
                  <div>
                    <p className="text-xs text-gray-400">Quantidade</p>
                    <p className={`font-semibold ${baixo ? 'text-red-400' : 'text-gray-900 dark:text-white'}`}>{item.quantidade || 0}</p>
                  </div>
                  {item.minimo && (
                    <div>
                      <p className="text-xs text-gray-400">Mínimo</p>
                      <p className="font-semibold text-gray-900 dark:text-white">{item.minimo}</p>
                    </div>
                  )}
                  {item.fornecedor && (
                    <div>
                      <p className="text-xs text-gray-400">Fornecedor</p>
                      <p className="text-gray-900 dark:text-white truncate max-w-[100px]">{item.fornecedor}</p>
                    </div>
                  )}
                </div>
              </motion.div>
            )
          })}
        </div>
      </div>

      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={() => setModalOpen(false)} />
          <motion.div initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }}
            className="relative w-full max-w-lg rounded-2xl bg-white dark:bg-[#0f172a] border border-white/10 p-6">
            <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-6">{editing ? "Editar" : "Novo"} Item</h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="col-span-2">
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Nome</label>
                  <input type="text" value={form.nome} onChange={e => setForm({ ...form, nome: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#a855f7]/50 transition-all" required />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Categoria</label>
                  <select value={form.categoria} onChange={e => setForm({ ...form, categoria: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#a855f7]/50 transition-all">
                    <option>Medicamento</option>
                    <option>Vacina</option>
                    <option>Material</option>
                    <option>Equipamento</option>
                    <option>Higiene</option>
                    <option>Outro</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Fornecedor</label>
                  <input type="text" value={form.fornecedor} onChange={e => setForm({ ...form, fornecedor: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#a855f7]/50 transition-all" />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Quantidade</label>
                  <input type="number" value={form.quantidade} onChange={e => setForm({ ...form, quantidade: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#a855f7]/50 transition-all" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Estoque Mínimo</label>
                  <input type="number" value={form.minimo} onChange={e => setForm({ ...form, minimo: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#a855f7]/50 transition-all" />
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Observações</label>
                <textarea value={form.observacoes} onChange={e => setForm({ ...form, observacoes: e.target.value })}
                  rows={2} className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#a855f7]/50 transition-all" />
              </div>
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setModalOpen(false)}
                  className="flex-1 py-3 rounded-xl border border-white/10 text-gray-400 hover:text-white text-sm">Cancelar</button>
                <button type="submit"
                  className="flex-1 py-3 rounded-xl bg-gradient-to-r from-[#a855f7] to-[#d946ef] text-white text-sm font-medium hover:shadow-lg transition-all">
                  {editing ? "Atualizar" : "Cadastrar"}
                </button>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </Layout>
  )
}
