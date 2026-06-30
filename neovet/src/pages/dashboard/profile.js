import { useState, useEffect } from "react"
import Layout from "@/components/Layout"
import { motion } from "framer-motion"
import { FiSave, FiUser, FiClipboard, FiMapPin, FiPhone, FiMail, FiFileText, FiShield } from "react-icons/fi"
import { getSettings, saveSettings } from "@/data/store"
import toast from "react-hot-toast"

export default function Profile() {
  const [form, setForm] = useState({
    clinicaNome: "", clinicaEndereco: "", clinicaTelefone: "", clinicaEmail: "",
    clinicaCnpj: "", clinicaLogo: "", medicoNome: "", medicoCrmv: "",
    prazoPagamento: "30", notasFiscais: true, impostos: 0,
  })

  useEffect(() => { setForm(getSettings()) }, [])

  function handleSave(e) {
    e.preventDefault()
    saveSettings(form)
    toast.success("Configurações salvas!")
  }

  return (
    <Layout>
      <div className="max-w-3xl mx-auto space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Perfil da Clínica</h1>
          <p className="text-sm text-gray-400">Configure os dados da sua clínica para emissão de notas fiscais</p>
        </div>

        <form onSubmit={handleSave} className="space-y-6">
          {/* Clinic info */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-6 space-y-5"
          >
            <div className="flex items-center gap-3 pb-3 border-b border-gray-200 dark:border-white/5">
              <div className="p-2 rounded-xl bg-[#10b981]/10">
                <FiClipboard className="text-[#10b981]" />
              </div>
              <div>
                <h2 className="text-sm font-semibold text-gray-900 dark:text-white">Dados da Clínica</h2>
                <p className="text-xs text-gray-400">Informações para nota fiscal</p>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="md:col-span-2">
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Nome da Clínica</label>
                <input type="text" value={form.clinicaNome} onChange={e => setForm({ ...form, clinicaNome: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all" />
              </div>
              <div className="md:col-span-2">
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Endereço</label>
                <input type="text" value={form.clinicaEndereco} onChange={e => setForm({ ...form, clinicaEndereco: e.target.value })}
                  placeholder="Rua, número, bairro, cidade - UF"
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all" />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Telefone</label>
                <input type="text" value={form.clinicaTelefone} onChange={e => setForm({ ...form, clinicaTelefone: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all" />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Email</label>
                <input type="email" value={form.clinicaEmail} onChange={e => setForm({ ...form, clinicaEmail: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all" />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">CNPJ</label>
                <input type="text" value={form.clinicaCnpj} onChange={e => setForm({ ...form, clinicaCnpj: e.target.value })}
                  placeholder="00.000.000/0000-00"
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white font-mono focus:outline-none focus:border-[#10b981]/50 transition-all" />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Prazo p/ Pagamento</label>
                <select value={form.prazoPagamento} onChange={e => setForm({ ...form, prazoPagamento: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#10b981]/50 transition-all">
                  <option value="15">15 dias</option>
                  <option value="30">30 dias</option>
                  <option value="45">45 dias</option>
                  <option value="60">60 dias</option>
                </select>
              </div>
            </div>
          </motion.div>

          {/* Doctor info */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-6 space-y-5"
          >
            <div className="flex items-center gap-3 pb-3 border-b border-gray-200 dark:border-white/5">
              <div className="p-2 rounded-xl bg-[#06b6d4]/10">
                <FiUser className="text-[#06b6d4]" />
              </div>
              <div>
                <h2 className="text-sm font-semibold text-gray-900 dark:text-white">Médico Responsável</h2>
                <p className="text-xs text-gray-400">Dados do profissional</p>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Nome do Médico</label>
                <input type="text" value={form.medicoNome} onChange={e => setForm({ ...form, medicoNome: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#06b6d4]/50 transition-all" />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">CRMV</label>
                <input type="text" value={form.medicoCrmv} onChange={e => setForm({ ...form, medicoCrmv: e.target.value })}
                  placeholder="CRMV/UF 00000"
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white font-mono focus:outline-none focus:border-[#06b6d4]/50 transition-all" />
              </div>
            </div>
          </motion.div>

          {/* Invoice settings */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-6 space-y-5"
          >
            <div className="flex items-center gap-3 pb-3 border-b border-gray-200 dark:border-white/5">
              <div className="p-2 rounded-xl bg-[#f59e0b]/10">
                <FiFileText className="text-[#f59e0b]" />
              </div>
              <div>
                <h2 className="text-sm font-semibold text-gray-900 dark:text-white">Nota Fiscal</h2>
                <p className="text-xs text-gray-400">Configurações de faturamento</p>
              </div>
            </div>

            <div className="space-y-4">
              <div className="flex items-center justify-between p-4 rounded-xl bg-white/5">
                <div>
                  <p className="text-sm font-medium text-gray-900 dark:text-white">Emitir Nota Fiscal</p>
                  <p className="text-xs text-gray-400">Gerar nota fiscal ao finalizar transações</p>
                </div>
                <label className="relative inline-flex items-center cursor-pointer">
                  <input type="checkbox" checked={form.notasFiscais} onChange={e => setForm({ ...form, notasFiscais: e.target.checked })}
                    className="sr-only peer" />
                  <div className="w-11 h-6 bg-gray-700 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:bg-[#10b981] after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:rounded-full after:h-5 after:w-5 after:transition-all" />
                </label>
              </div>

              <div>
                <label className="block text-xs font-medium text-gray-400 mb-1.5">Alíquota de Impostos (%)</label>
                <input type="number" step="0.1" value={form.impostos} onChange={e => setForm({ ...form, impostos: e.target.value })}
                  className="w-full px-4 py-3 rounded-xl bg-white/5 border border-white/10 text-white focus:outline-none focus:border-[#f59e0b]/50 transition-all" />
              </div>
            </div>
          </motion.div>

          {/* Actions */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.3 }}
            className="flex justify-end"
          >
            <button type="submit"
              className="flex items-center gap-2 px-8 py-3 rounded-xl bg-gradient-to-r from-[#10b981] to-[#06b6d4] text-white font-medium hover:shadow-lg hover:shadow-[#10b981]/20 transition-all">
              <FiSave /> Salvar Configurações
            </button>
          </motion.div>
        </form>
      </div>
    </Layout>
  )
}
