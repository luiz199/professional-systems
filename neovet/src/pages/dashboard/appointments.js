import { useState, useEffect } from "react"
import Layout from "@/components/Layout"
import { motion } from "framer-motion"
import { FiPlus, FiEdit2, FiTrash2, FiSearch, FiCalendar, FiClock, FiUser } from "react-icons/fi"
import { getAppointments, saveAppointment, deleteAppointment, getPets, getVets } from "@/data/store"
import toast from "react-hot-toast"

const tipos = ["Consulta", "Vacina", "Cirurgia", "Banho e Tosa", "Exame"]
const statusList = ["agendado", "confirmado", "em andamento", "concluido", "cancelado"]

export default function Appointments() {
  const [appointments, setAppointments] = useState([])
  const [pets, setPets] = useState([])
  const [search, setSearch] = useState("")
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({ petId: "", petNome: "", tipo: "Consulta", data: "", hora: "", status: "agendado", observacoes: "" })

  useEffect(() => {
    setAppointments(getAppointments())
    setPets(getPets())
  }, [])

  function load() { setAppointments(getAppointments()) }

  function openEdit(apt) {
    if (apt) { setEditing(apt.id); setForm(apt) }
    else { setEditing(null); setForm({ petId: "", petNome: "", tipo: "Consulta", data: "", hora: "", status: "agendado", observacoes: "" }) }
    setModalOpen(true)
  }

  function handlePetSelect(petId) {
    const pet = pets.find(p => p.id === petId)
    setForm({ ...form, petId, petNome: pet?.nome || "" })
  }

  function handleSave(e) {
    e.preventDefault()
    if (!form.petId || !form.data) return toast.error("Pet e data são obrigatórios")
    saveAppointment(editing ? { id: editing, ...form } : form)
    toast.success(editing ? "Agendamento atualizado!" : "Agendamento criado!")
    setModalOpen(false)
    load()
  }

  function handleDelete(id) {
    if (!confirm("Tem certeza?")) return
    deleteAppointment(id)
    toast.success("Agendamento removido!")
    load()
  }

  const filtered = appointments.filter(a =>
    a.petNome?.toLowerCase().includes(search.toLowerCase()) ||
    a.tipo?.toLowerCase().includes(search.toLowerCase())
  ).sort((a, b) => (a.data || "").localeCompare(b.data || ""))

  const statusColors = {
    agendado: "bg-[#f59e0b]/10 text-[#f59e0b] border-[#f59e0b]/20",
    confirmado: "bg-[#10b981]/10 text-[#10b981] border-[#10b981]/20",
    "em andamento": "bg-[#06b6d4]/10 text-[#06b6d4] border-[#06b6d4]/20",
    concluido: "bg-[#8b5cf6]/10 text-[#8b5cf6] border-[#8b5cf6]/20",
    cancelado: "bg-red-400/10 text-red-400 border-red-400/20",
  }

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between flex-wrap gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Agendamentos</h1>
            <p className="text-sm text-gray-400">{appointments.length} agendamentos</p>
          </div>
          <button onClick={() => openEdit(null)}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-[#ec4899] to-[#f43f5e] text-white text-sm font-medium hover:shadow-lg hover:shadow-[#ec4899]/20 transition-all">
            <FiPlus /> Novo Agendamento
          </button>
        </div>

        <div className="relative">
          <FiSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
          <input type="text" value={search} onChange={e => setSearch(e.target.value)}
            placeholder="Buscar agendamentos..."
            className="w-full pl-11 pr-4 py-3 rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 text-gray-900 dark:text-white placeholder-gray-400 focus:outline-none focus:border-[#ec4899]/30 transition-all"
          />
        </div>

        <div className="space-y-3">
          {filtered.length === 0 ? (
            <div className="text-center py-12 text-gray-400">Nenhum agendamento encontrado</div>
          ) : filtered.map((apt, i) => (
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.03 }}
              key={apt.id}
              className="flex items-center justify-between p-4 rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 hover:shadow-lg hover:shadow-black/5 transition-all group"
            >
              <div className="flex items-center gap-4">
                <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#ec4899] to-[#f43f5e] flex items-center justify-center text-white text-lg">
                  {apt.tipo?.charAt(0) || "?"}
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 dark:text-white">{apt.petNome || "—"}</h3>
                  <div className="flex items-center gap-3 text-xs text-gray-400 mt-0.5">
                    <span className="flex items-center gap-1"><FiCalendar size={11} />{apt.data || "—"}</span>
                    <span className="flex items-center gap-1"><FiClock size={11} />{apt.hora || "—"}</span>
                    <span>{apt.tipo}</span>
                  </div>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <span className={`text-[10px] font-medium px-2.5 py-1 rounded-full border ${statusColors[apt.status] || statusColors.agendado}`}>
                  {apt.status}
                </span>
                <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button onClick={() => openEdit(apt)} className="p-2 rounded-lg hover:bg-white/5 text-gray-400 hover:text-[#ec4899]"><FiEdit2 size={14} /></button>
                  <button onClick={() => handleDelete(apt.id)} className="p-2 rounded-lg hover:bg-white/5 text-gray-400 hover:text-red-400"><FiTrash2 size={14} /></button>
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
            <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-6">{editing ? "Editar" : "Novo"} Agendamento</h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Pet</label>
                <select value={form.petId} onChange={e => handlePetSelect(e.target.value)}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#ec4899]/50 transition-all" required>
                  <option value="">Selecione um pet</option>
                  {pets.map(p => <option key={p.id} value={p.id}>{p.nome} ({p.especie})</option>)}
                </select>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Tipo</label>
                  <select value={form.tipo} onChange={e => setForm({ ...form, tipo: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#ec4899]/50 transition-all">
                    {tipos.map(t => <option key={t} value={t}>{t}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Status</label>
                  <select value={form.status} onChange={e => setForm({ ...form, status: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#ec4899]/50 transition-all">
                    {statusList.map(s => <option key={s} value={s}>{s}</option>)}
                  </select>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Data</label>
                  <input type="date" value={form.data} onChange={e => setForm({ ...form, data: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#ec4899]/50 transition-all" required />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Hora</label>
                  <input type="time" value={form.hora} onChange={e => setForm({ ...form, hora: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#ec4899]/50 transition-all" />
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Observações</label>
                <textarea value={form.observacoes} onChange={e => setForm({ ...form, observacoes: e.target.value })}
                  rows={2} className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#ec4899]/50 transition-all" />
              </div>
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setModalOpen(false)}
                  className="flex-1 py-3 rounded-xl border border-white/10 text-gray-400 hover:text-white transition-all text-sm">Cancelar</button>
                <button type="submit"
                  className="flex-1 py-3 rounded-xl bg-gradient-to-r from-[#ec4899] to-[#f43f5e] text-white text-sm font-medium hover:shadow-lg transition-all">
                  {editing ? "Atualizar" : "Criar"}
                </button>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </Layout>
  )
}
