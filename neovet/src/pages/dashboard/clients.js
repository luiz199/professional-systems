import { useState, useEffect } from "react"
import Layout from "@/components/Layout"
import { motion } from "framer-motion"
import { FiPlus, FiEdit2, FiTrash2, FiSearch, FiMail, FiPhone, FiMapPin, FiUser } from "react-icons/fi"
import { getClients, saveClient, deleteClient, getPetsByTutor } from "@/data/store"
import toast from "react-hot-toast"

export default function Clients() {
  const [clients, setClients] = useState([])
  const [search, setSearch] = useState("")
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState({ nome: "", email: "", telefone: "", endereco: "" })

  useEffect(() => { setClients(getClients()) }, [])

  function load() { setClients(getClients()) }

  function openEdit(client) {
    if (client) { setEditing(client.id); setForm(client) }
    else { setEditing(null); setForm({ nome: "", email: "", telefone: "", endereco: "" }) }
    setModalOpen(true)
  }

  function handleSave(e) {
    e.preventDefault()
    if (!form.nome.trim()) return toast.error("Nome é obrigatório")
    saveClient(editing ? { id: editing, ...form } : form)
    toast.success(editing ? "Cliente atualizado!" : "Cliente cadastrado!")
    setModalOpen(false)
    load()
  }

  function handleDelete(id) {
    if (!confirm("Tem certeza?")) return
    deleteClient(id)
    toast.success("Cliente removido!")
    load()
  }

  const filtered = clients.filter(c =>
    c.nome?.toLowerCase().includes(search.toLowerCase()) ||
    c.email?.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between flex-wrap gap-4">
          <div>
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Clientes</h1>
            <p className="text-sm text-gray-400">{clients.length} clientes cadastrados</p>
          </div>
          <button onClick={() => openEdit(null)}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-gradient-to-r from-[#8b5cf6] to-[#6366f1] text-white text-sm font-medium hover:shadow-lg hover:shadow-[#8b5cf6]/20 transition-all">
            <FiPlus /> Novo Cliente
          </button>
        </div>

        <div className="relative">
          <FiSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
          <input type="text" value={search} onChange={e => setSearch(e.target.value)}
            placeholder="Buscar clientes..."
            className="w-full pl-11 pr-4 py-3 rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 text-gray-900 dark:text-white placeholder-gray-400 focus:outline-none focus:border-[#8b5cf6]/30 transition-all"
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.length === 0 ? (
            <div className="col-span-full text-center py-12 text-gray-400">Nenhum cliente encontrado</div>
          ) : filtered.map((client, i) => (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
              key={client.id}
              className="group rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-5 hover:shadow-lg hover:shadow-black/5 transition-all"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#8b5cf6] to-[#6366f1] flex items-center justify-center text-white font-bold text-lg">
                    {client.nome?.charAt(0) || "?"}
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white">{client.nome}</h3>
                    <p className="text-xs text-gray-400">{getPetsByTutor(client.id).length} pet(s)</p>
                  </div>
                </div>
                <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button onClick={() => openEdit(client)} className="p-2 rounded-lg hover:bg-white/5 text-gray-400 hover:text-[#8b5cf6]"><FiEdit2 size={14} /></button>
                  <button onClick={() => handleDelete(client.id)} className="p-2 rounded-lg hover:bg-white/5 text-gray-400 hover:text-red-400"><FiTrash2 size={14} /></button>
                </div>
              </div>
              <div className="space-y-2 text-sm">
                {client.email && (
                  <div className="flex items-center gap-2 text-gray-400"><FiMail size={12} /><span>{client.email}</span></div>
                )}
                {client.telefone && (
                  <div className="flex items-center gap-2 text-gray-400"><FiPhone size={12} /><span>{client.telefone}</span></div>
                )}
                {client.endereco && (
                  <div className="flex items-center gap-2 text-gray-400"><FiMapPin size={12} /><span className="truncate">{client.endereco}</span></div>
                )}
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
            className="relative w-full max-w-lg rounded-2xl bg-white dark:bg-[#0f172a] border border-white/10 p-6"
          >
            <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-6">{editing ? "Editar Cliente" : "Novo Cliente"}</h2>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Nome completo</label>
                <input type="text" value={form.nome} onChange={e => setForm({ ...form, nome: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#8b5cf6]/50 transition-all" required />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Email</label>
                  <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#8b5cf6]/50 transition-all" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-400 mb-1.5">Telefone</label>
                  <input type="text" value={form.telefone} onChange={e => setForm({ ...form, telefone: e.target.value })}
                    className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#8b5cf6]/50 transition-all" />
                </div>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Endereço</label>
                <input type="text" value={form.endereco} onChange={e => setForm({ ...form, endereco: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#8b5cf6]/50 transition-all" />
              </div>
              <div className="flex gap-3 pt-2">
                <button type="button" onClick={() => setModalOpen(false)}
                  className="flex-1 py-3 rounded-xl border border-white/10 text-gray-400 hover:text-white transition-all text-sm">
                  Cancelar
                </button>
                <button type="submit"
                  className="flex-1 py-3 rounded-xl bg-gradient-to-r from-[#8b5cf6] to-[#6366f1] text-white text-sm font-medium hover:shadow-lg transition-all">
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
