import { useState, useEffect } from "react"
import { motion, AnimatePresence } from "framer-motion"
import { useAuth } from "@/context/AuthContext"
import { FiMail, FiLock, FiEye, FiEyeOff, FiArrowRight, FiShield } from "react-icons/fi"

export default function Login() {
  const { login, user, loading: authLoading } = useAuth()
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [showPwd, setShowPwd] = useState(false)
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)
  const [recovery, setRecovery] = useState(false)

  useEffect(() => {
    if (!authLoading && user) window.location.href = "/dashboard"
  }, [user, authLoading])

  if (authLoading) return (
    <div className="min-h-screen bg-[#020617] flex items-center justify-center">
      <div className="w-8 h-8 border-2 border-[#10b981]/30 border-t-[#10b981] rounded-full animate-spin" />
    </div>
  )

  async function handleSubmit(e) {
    e.preventDefault()
    setError("")
    setLoading(true)
    await new Promise(r => setTimeout(r, 800))
    const result = login(email, password)
    if (!result.success) setError(result.error)
    setLoading(false)
  }

  return (
    <div className="min-h-screen relative overflow-hidden bg-[#020617] flex">
      {/* Background layers */}
      <div className="absolute inset-0">
        <div className="absolute inset-0 bg-gradient-to-br from-[#020617] via-[#0f172a] to-[#020617]" />
        <div className="absolute inset-0 opacity-20" style={{
          backgroundImage: `radial-gradient(circle at 25% 25%, rgba(16,185,129,0.3) 0%, transparent 50%),
                            radial-gradient(circle at 75% 75%, rgba(6,182,212,0.3) 0%, transparent 50%)`
        }} />
        <div className="absolute inset-0" style={{
          backgroundImage: `linear-gradient(rgba(16,185,129,0.03) 1px, transparent 1px),
                            linear-gradient(90deg, rgba(16,185,129,0.03) 1px, transparent 1px)`,
          backgroundSize: '60px 60px'
        }} />
      </div>

      {/* Floating particles */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        {[...Array(20)].map((_, i) => (
          <motion.div
            key={i}
            className="absolute w-1 h-1 bg-[#10b981]/30 rounded-full"
            style={{ left: `${Math.random() * 100}%`, top: `${Math.random() * 100}%` }}
            animate={{ y: [-20, 20, -20], opacity: [0.2, 0.8, 0.2] }}
            transition={{ duration: 4 + Math.random() * 4, repeat: Infinity, delay: Math.random() * 4 }}
          />
        ))}
      </div>

      {/* Left side - Login form */}
      <div className="relative z-10 w-full lg:w-1/2 min-h-screen flex items-center justify-center p-8">
        <motion.div
          initial={{ opacity: 0, x: -40 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.8, ease: "easeOut" }}
          className="w-full max-w-md"
        >
          {/* Logo */}
          <motion.div
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ delay: 0.2, duration: 0.5 }}
            className="flex items-center gap-3 mb-12"
          >
            <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-[#10b981] to-[#06b6d4] flex items-center justify-center shadow-lg shadow-[#10b981]/20">
              <FiShield className="text-white text-xl" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-white">NeoVet</h1>
              <p className="text-xs text-gray-400">O futuro da veterinária digital</p>
            </div>
          </motion.div>

          {recovery ? (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
              <h2 className="text-2xl font-bold text-white mb-2">Recuperar senha</h2>
              <p className="text-gray-400 text-sm mb-8">Digite seu email para receber instruções</p>
              <div className="space-y-4">
                <div className="relative">
                  <FiMail className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />
                  <input type="email" placeholder="seu@email.com"
                    className="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-white/5 border border-white/10 text-white placeholder-gray-500 focus:outline-none focus:border-[#10b981]/50 focus:ring-1 focus:ring-[#10b981]/20 transition-all"
                  />
                </div>
                <button className="w-full py-3.5 rounded-2xl bg-gradient-to-r from-[#10b981] to-[#06b6d4] text-white font-semibold hover:shadow-lg hover:shadow-[#10b981]/20 transition-all">
                  Enviar instruções
                </button>
                <button onClick={() => setRecovery(false)}
                  className="w-full text-sm text-gray-400 hover:text-white transition-colors">
                  Voltar ao login
                </button>
              </div>
            </motion.div>
          ) : (
            <>
              <motion.div
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.3 }}
              >
                <h2 className="text-3xl font-bold text-white mb-2">Bem-vindo</h2>
                <p className="text-gray-400 text-sm mb-8">Faça login no NeoVet para continuar</p>
              </motion.div>

              <form onSubmit={handleSubmit} className="space-y-5">
                <motion.div
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.4 }}
                  className="relative"
                >
                  <FiMail className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 z-10" />
                  <input type="email" value={email} onChange={e => setEmail(e.target.value)}
                    placeholder="Email"
                    className="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-white/5 border border-white/10 text-white placeholder-gray-500 focus:outline-none focus:border-[#10b981]/50 focus:ring-1 focus:ring-[#10b981]/20 transition-all"
                    required
                  />
                </motion.div>

                <motion.div
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.5 }}
                  className="relative"
                >
                  <FiLock className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 z-10" />
                  <input type={showPwd ? "text" : "password"} value={password}
                    onChange={e => setPassword(e.target.value)}
                    placeholder="Senha"
                    className="w-full pl-12 pr-12 py-3.5 rounded-2xl bg-white/5 border border-white/10 text-white placeholder-gray-500 focus:outline-none focus:border-[#10b981]/50 focus:ring-1 focus:ring-[#10b981]/20 transition-all"
                    required
                  />
                  <button type="button" onClick={() => setShowPwd(!showPwd)}
                    className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-white transition-colors">
                    {showPwd ? <FiEyeOff /> : <FiEye />}
                  </button>
                </motion.div>

                {error && (
                  <motion.p initial={{ opacity: 0, y: -5 }} animate={{ opacity: 1, y: 0 }}
                    className="text-red-400 text-sm bg-red-400/10 border border-red-400/20 rounded-xl px-4 py-2">
                    {error}
                  </motion.p>
                )}

                <motion.div
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.6 }}
                  className="flex items-center justify-between"
                >
                  <label className="flex items-center gap-2 text-sm text-gray-400 cursor-pointer">
                    <input type="checkbox" defaultChecked className="rounded border-gray-600 bg-white/5 text-[#10b981] focus:ring-[#10b981]/20" />
                    Lembrar-me
                  </label>
                  <button type="button" onClick={() => setRecovery(true)}
                    className="text-sm text-[#10b981] hover:text-[#34d399] transition-colors">
                    Esqueceu a senha?
                  </button>
                </motion.div>

                <motion.button
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.7 }}
                  type="submit" disabled={loading}
                  className="relative w-full py-3.5 rounded-2xl bg-gradient-to-r from-[#10b981] to-[#06b6d4] text-white font-semibold flex items-center justify-center gap-2 hover:shadow-lg hover:shadow-[#10b981]/20 transition-all disabled:opacity-70 group"
                >
                  {loading ? (
                    <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  ) : (
                    <>
                      Entrar <FiArrowRight className="group-hover:translate-x-1 transition-transform" />
                    </>
                  )}
                </motion.button>
              </form>

              <motion.p
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.8 }}
                className="text-center text-xs text-gray-500 mt-8"
              >
                Sistema NeoVet v1.0 &copy; 2026
              </motion.p>
            </>
          )}
        </motion.div>
      </div>

      {/* Right side - Realistic image */}
      <div className="hidden lg:block relative w-1/2 min-h-screen overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-[#020617]/80 via-[#020617]/40 to-[#020617]/80 z-10" />
        <div className="absolute inset-0 bg-gradient-to-t from-[#020617] via-transparent to-transparent z-10" />
        <img
          src="https://images.unsplash.com/photo-1629909613654-28e377c37b09?q=80&w=2068&auto=format&fit=crop"
          alt="Veterinária cuidando de um cachorro"
          className="absolute inset-0 w-full h-full object-cover"
          style={{ filter: 'saturate(1.1) contrast(1.05)' }}
        />
        <div className="absolute inset-0 z-20 flex flex-col items-center justify-end pb-20 px-12 text-center">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5, duration: 0.8 }}
          >
            <h2 className="text-3xl font-bold text-white mb-3">O futuro da veterinária digital</h2>
            <p className="text-gray-300 text-sm max-w-md leading-relaxed">
              Sistema completo de gestão clínica com cadastro de pets, agendamentos, 
              controle financeiro e muito mais.
            </p>
            <div className="flex items-center justify-center gap-6 mt-8">
              <span className="text-xs text-gray-400">+ de 500 clínicas confiam</span>
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  )
}
