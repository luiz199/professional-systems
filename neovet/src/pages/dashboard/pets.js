import { useState, useEffect } from "react"
import Layout from "@/components/Layout"
import { motion } from "framer-motion"
import { FiPlus, FiEdit2, FiTrash2, FiSearch, FiCamera, FiDroplet, FiScissors, FiHeart } from "react-icons/fi"
import { getPets, savePet, deletePet, getClients } from "@/data/store"
import toast from "react-hot-toast"

const especies = ["Cachorro", "Gato", "Ave", "Roedor", "Réptil", "Outro"]

export default function Pets() {
  const [pets, setPets] = useState([])
  const [clients, setClients] = useState([])
  const [search, setSearch] = useState("")
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({ nome: "", especie: "Cachorro", raca: "", peso: "", idade: "", tutorId: "", foto: "", vacinas: "", historico: "" })

  useEffect(() => { setPets(getPets()); setClients(getClients()) }, [])

  function load() { setPets(getPets()) }

  function openEdit(pet) {
    if (pet) { setEditing(pet.id); setForm(pet) }
    else { setEditing(null); setForm({ nome: "", especie: "Cachorro", raca: "", peso: "", idade: "", tutorId: clients[0]?.id || "", foto: "", vacinas: "", historico: "" }) }
    setModalOpen(true)
  }

  function handleSave(e) {
    e.preventDefault()
    if (!form.nome.trim() || !form.tutorId) return toast.error("Nome e tutor são obrigatórios")
    savePet(editing ? { id: editing, ...form } : form)
    toast.success(editing ? "Pet atualizado!" : "Pet cadastrado!")
    setModalOpen(false)
    load()
  }

  function handleDelete(id) {
    if (!confirm("Tem certeza?")) return
    deletePet(id)
    toast.success("Pet removido!")
    load()
  }

  const filtered = pets.filter(p =>
    p.nome?.toLowerCase().includes(search.toLowerCase()) ||
    p.raca?.toLowerCase().includes(search.toLowerCase())
  )

  const getClientName = (id) => clients.find(c => c.id === id)?.nome || "—"

  const especieIcons = { Cachorro: "🐶", Gato: "🐱", Ave: "🐦", Roedor: "🐹", Réptil: "🦎", Outro: "🐾" }

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between flex-wrap gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Pets</h1>
            <p className="text-sm text-gray-400">{pets.length} pets cadastrados</p>
          </div>
          <button onClick={() => openEdit(null)}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-[#f59e0b] to-[#f97316] text-white text-sm font-medium hover:shadow-lg hover:shadow-[#f59e0b]/20 transition-all">
            <FiPlus /> Novo Pet
          </button>
        </div>

        <div className="relative">
          <FiSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
          <input type="text" value={search} onChange={e => setSearch(e.target.value)}
            placeholder="Buscar pets..."
            className="w-full pl-11 pr-4 py-3 rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 text-gray-900 dark:text-white placeholder-gray-400 focus:outline-none focus:border-[#f59e0b]/30 transition-all"
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.length === 0 ? (
            <div className="col-span-full text-center py-12 text-gray-400">Nenhum pet encontrado</div>
          ) : filtered.map((pet, i) => (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
              key={pet.id}
              className="group rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-5 hover:shadow-lg hover:shadow-black/5 transition-all"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="w-14 h-14 rounded-xl bg-gradient-to-br from-[#f59e0b] to-[#f97316] flex items-center justify-center text-2xl">
                    {especieIcons[pet.especie] || "🐾"}
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white">{pet.nome}</h3>
                    <p className="text-xs text-gray-400">{pet.especie}{pet.raca ? ` • ${pet.raca}` : ""}</p>
                    <p className="text-xs text-gray-500">Tutor: {getClientName(pet.tutorId)}</p>
                  </div>
                </div>
                <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button onClick={() => openEdit(pet)} className="p-2 rounded-lg hover:bg-white/5 text-gray-400 hover:text-[#f59e0b]"><FiEdit2 size={14} /></button>
                  <button onClick={() => handleDelete(pet.id)} className="p-2 rounded-lg hover:bg-white/5 text-gray-400 hover:text-red-400"><FiTrash2 size={14} /></button>
                </div>
              </div>
              <div className="flex flex-wrap gap-2">
                {pet.peso && <span className="text-[10px] px-2.5 py-1 rounded-full bg-[#f59e0b]/10 text-[#f59e0b] border border-[#f59e0b]/20">🏋️ {pet.peso}kg</span>}
                {pet.idade && <span className="text-[10px] px-2.5 py-1 rounded-full bg-[#06b6d4]/10 text-[#06b6d4] border border-[#06b6d4]/20">🎂 {pet.idade} anos</span>}
                {pet.vacinas && <span className="text-[10px] px-2.5 py-1 rounded-full bg-[#10b981]/10 text-[#10b981] border border-[#10b981]/20">💉 Vacinado</span>}
              </div>
            </motion.div>
          ))}
        </div>
      </div>

      {/* Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={() => setModalOpen(false)} />
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="relative w-full max-w-lg rounded-2xl bg-white dark:bg-[#0f172a] border border-white/10 p-6 max-h-[90vh] overflow-y-auto"
          >
            <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-6">{editing ? "Editar Pet" : "Novo Pet"}</h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="col-span-2">
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Nome do Pet</label>
                  <input type="text" value={form.nome} onChange={e => setForm({ ...form, nome: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#f59e0b]/50 transition-all" required />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Espécie</label>
                  <select value={form.especie} onChange={e => setForm({ ...form, especie: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#f59e0b]/50 transition-all">
                    {especies.map(e => <option key={e} value={e}>{e}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Raça</label>
                  <input type="text" value={form.raca} onChange={e => setForm({ ...form, raca: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#f59e0b]/50 transition-all" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Peso (kg)</label>
                  <input type="number" step="0.1" value={form.peso} onChange={e => setForm({ ...form, peso: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#f59e0b]/50 transition-all" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Idade (anos)</label>
                  <input type="number" step="0.5" value={form.idade} onChange={e => setForm({ ...form, idade: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#f59e0b]/50 transition-all" />
                </div>
                <div className="col-span-2">
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Tutor Responsável</label>
                  <select value={form.tutorId} onChange={e => setForm({ ...form, tutorId: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#f59e0b]/50 transition-all" required>
                    <option value="">Selecione um tutor</option>
                    {clients.map(c => <option key={c.id} value={c.id}>{c.nome}</option>)}
                  </select>
                </div>
                <div className="col-span-2">
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Vacinas</label>
                  <input type="text" value={form.vacinas} onChange={e => setForm({ ...form, vacinas: e.target.value })}
                    placeholder="Ex: V8, V10, Antirrábica"
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#f59e0b]/50 transition-all" />
                </div>
                <div className="col-span-2">
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Histórico Médico</label>
                  <textarea value={form.historico} onChange={e => setForm({ ...form, historico: e.target.value })}
                    rows={3}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#f59e0b]/50 transition-all" />
                </div>
              </div>
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setModalOpen(false)}
                  className="flex-1 py-3 rounded-xl border border-white/10 text-gray-400 hover:text-white transition-all text-sm">Cancelar</button>
                <button type="submit"
                  className="flex-1 py-3 rounded-xl bg-gradient-to-r from-[#f59e0b] to-[#f97316] text-white text-sm font-medium hover:shadow-lg transition-all">
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
