import { useState, useEffect } from "react"
import Layout from "@/components/Layout"
import { motion } from "framer-motion"
import { FiPlus, FiEdit2, FiTrash2, FiSearch, FiPackage } from "react-icons/fi"
import { getMedications, saveMedication, deleteMedication } from "@/data/store"
import toast from "react-hot-toast"

export default function Medications() {
  const [meds, setMeds] = useState([])
  const [search, setSearch] = useState("")
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({ nome: "", descricao: "", dosagem: "", fabricante: "", quantidade: "", validade: "" })

  useEffect(() => { setMeds(getMedications()) }, [])

  function load() { setMeds(getMedications()) }

  function openEdit(med) {
    if (med) { setEditing(med.id); setForm(med) }
    else { setEditing(null); setForm({ nome: "", descricao: "", dosagem: "", fabricante: "", quantidade: "", validade: "" }) }
    setModalOpen(true)
  }

  function handleSave(e) {
    e.preventDefault()
    if (!form.nome.trim()) return toast.error("Nome é obrigatório")
    saveMedication(editing ? { id: editing, ...form } : form)
    toast.success(editing ? "Medicamento atualizado!" : "Medicamento cadastrado!")
    setModalOpen(false)
    load()
  }

  function handleDelete(id) {
    if (!confirm("Tem certeza?")) return
    deleteMedication(id)
    toast.success("Medicamento removido!")
    load()
  }

  const filtered = meds.filter(m =>
    m.nome?.toLowerCase().includes(search.toLowerCase()) ||
    m.fabricante?.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between flex-wrap gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Medicamentos</h1>
            <p className="text-sm text-gray-400">{meds.length} medicamentos</p>
          </div>
          <button onClick={() => openEdit(null)}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-[#14b8a6] to-[#0ea5e9] text-white text-sm font-medium hover:shadow-lg hover:shadow-[#14b8a6]/20 transition-all">
            <FiPlus /> Novo Medicamento
          </button>
        </div>

        <div className="relative">
          <FiSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
          <input type="text" value={search} onChange={e => setSearch(e.target.value)}
            placeholder="Buscar medicamentos..."
            className="w-full pl-11 pr-4 py-3 rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 text-gray-900 dark:text-white placeholder-gray-400 focus:outline-none focus:border-[#14b8a6]/30 transition-all"
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.length === 0 ? (
            <div className="col-span-full text-center py-12 text-gray-400">Nenhum medicamento encontrado</div>
          ) : filtered.map((med, i) => (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
              key={med.id}
              className="group rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-5 hover:shadow-lg transition-all"
            >
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#14b8a6] to-[#0ea5e9] flex items-center justify-center text-white text-lg">
                    <FiPackage />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white">{med.nome}</h3>
                    <p className="text-xs text-gray-400">{med.fabricante || "—"}</p>
                  </div>
                </div>
                <div className="flex gap-1 opacity-0 group-hover:opacity-100">
                  <button onClick={() => openEdit(med)} className="p-2 rounded-lg hover:bg-white/5 text-gray-400 hover:text-[#14b8a6]"><FiEdit2 size={14} /></button>
                  <button onClick={() => handleDelete(med.id)} className="p-2 rounded-lg hover:bg-white/5 text-gray-400 hover:text-red-400"><FiTrash2 size={14} /></button>
                </div>
              </div>
              <div className="space-y-1 text-sm">
                {med.descricao && <p className="text-gray-400 text-xs">{med.descricao}</p>}
                <div className="flex gap-2 mt-2 flex-wrap">
                  {med.dosagem && <span className="text-[10px] px-2 py-1 rounded-full bg-[#14b8a6]/10 text-[#14b8a6] border border-[#14b8a6]/20">{med.dosagem}</span>}
                  {med.quantidade && <span className="text-[10px] px-2 py-1 rounded-full bg-[#0ea5e9]/10 text-[#0ea5e9] border border-[#0ea5e9]/20">Qtd: {med.quantidade}</span>}
                  {med.validade && <span className="text-[10px] px-2 py-1 rounded-full bg-[#f59e0b]/10 text-[#f59e0b] border border-[#f59e0b]/20">Val: {med.validade}</span>}
                </div>
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
            <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-6">{editing ? "Editar" : "Novo"} Medicamento</h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Nome</label>
                <input type="text" value={form.nome} onChange={e => setForm({ ...form, nome: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#14b8a6]/50 transition-all" required />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Dosagem</label>
                  <input type="text" value={form.dosagem} onChange={e => setForm({ ...form, dosagem: e.target.value })} placeholder="Ex: 500mg"
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#14b8a6]/50 transition-all" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Fabricante</label>
                  <input type="text" value={form.fabricante} onChange={e => setForm({ ...form, fabricante: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#14b8a6]/50 transition-all" />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Quantidade</label>
                  <input type="number" value={form.quantidade} onChange={e => setForm({ ...form, quantidade: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#14b8a6]/50 transition-all" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Validade</label>
                  <input type="date" value={form.validade} onChange={e => setForm({ ...form, validade: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#14b8a6]/50 transition-all" />
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Descrição</label>
                <textarea value={form.descricao} onChange={e => setForm({ ...form, descricao: e.target.value })}
                  rows={2} className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#14b8a6]/50 transition-all" />
              </div>
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setModalOpen(false)}
                  className="flex-1 py-3 rounded-xl border border-white/10 text-gray-400 hover:text-white transition-all text-sm">Cancelar</button>
                <button type="submit"
                  className="flex-1 py-3 rounded-xl bg-gradient-to-r from-[#14b8a6] to-[#0ea5e9] text-white text-sm font-medium hover:shadow-lg transition-all">
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
