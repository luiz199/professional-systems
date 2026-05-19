import { useState, useEffect } from "react"
import { motion, AnimatePresence } from "framer-motion"
import Link from "next/link"
import { useRouter } from "next/router"
import { useAuth } from "@/context/AuthContext"
import { FiGrid, FiUsers, FiCalendar, FiDollarSign, FiPackage, FiBox, FiMenu, FiX, FiLogOut, FiChevronLeft, FiBell, FiSearch, FiMoon, FiSun, FiHeart, FiSettings } from "react-icons/fi"
import { useTheme } from "@/context/ThemeContext"

const navItems = [
  { href: "/dashboard", label: "Dashboard", icon: FiGrid, color: "from-[#10b981] to-[#06b6d4]" },
  { href: "/dashboard/pets", label: "Pets", icon: FiHeart, color: "from-[#f59e0b] to-[#f97316]" },
  { href: "/dashboard/clients", label: "Clientes", icon: FiUsers, color: "from-[#8b5cf6] to-[#6366f1]" },
  { href: "/dashboard/appointments", label: "Agendamentos", icon: FiCalendar, color: "from-[#ec4899] to-[#f43f5e]" },
  { href: "/dashboard/medications", label: "Medicamentos", icon: FiPackage, color: "from-[#14b8a6] to-[#0ea5e9]" },
  { href: "/dashboard/inventory", label: "Estoque", icon: FiBox, color: "from-[#a855f7] to-[#d946ef]" },
  { href: "/dashboard/financial", label: "Financeiro", icon: FiDollarSign, color: "from-[#10b981] to-[#84cc16]" },
  { href: "/dashboard/profile", label: "Configurações", icon: FiSettings, color: "from-[#64748b] to-[#475569]" },
]

export default function Layout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const [mobileOpen, setMobileOpen] = useState(false)
  const [scrolled, setScrolled] = useState(false)
  const router = useRouter()
  const { user, logout, loading } = useAuth()
  const { dark, toggleTheme } = useTheme()

  useEffect(() => { document.documentElement.classList.toggle("dark", dark) }, [dark])

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 10)
    window.addEventListener("scroll", handleScroll)
    return () => window.removeEventListener("scroll", handleScroll)
  }, [])

  useEffect(() => {
    if (!loading && !user && typeof window !== "undefined") router.push("/")
  }, [user, loading])

  if (loading) return (
    <div className="min-h-screen bg-[#020617] flex items-center justify-center">
      <div className="w-8 h-8 border-2 border-[#10b981]/30 border-t-[#10b981] rounded-full animate-spin" />
    </div>
  )
  if (!user) return null

  return (
    <div className="min-h-screen bg-[#f8fafc] dark:bg-[#020617] transition-colors duration-300">
      {/* Sidebar */}
      <AnimatePresence>
        {mobileOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setMobileOpen(false)}
            className="fixed inset-0 z-40 bg-black/60 lg:hidden"
          />
        )}
      </AnimatePresence>

      <motion.aside
        initial={{ width: 0 }}
        animate={{ width: mobileOpen ? 280 : sidebarOpen ? 260 : 72 }}
        className="fixed left-0 top-0 h-full z-50 bg-white dark:bg-[#0f172a] border-r border-gray-200 dark:border-white/5 overflow-hidden transition-all duration-300 hidden lg:block"
      >
        <div className="flex flex-col h-full">
          {/* Logo */}
          <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-white/5">
            <Link href="/dashboard" className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-[#10b981] to-[#06b6d4] flex items-center justify-center shadow-lg shadow-[#10b981]/20 flex-shrink-0">
                <span className="text-white text-sm font-bold">NV</span>
              </div>
              <motion.span
                initial={{ opacity: 0 }}
                animate={{ opacity: sidebarOpen ? 1 : 0 }}
                className="font-bold text-gray-900 dark:text-white text-lg whitespace-nowrap"
              >
                NeoVet
              </motion.span>
            </Link>
            <button onClick={() => setSidebarOpen(!sidebarOpen)}
              className="p-1.5 rounded-lg hover:bg-gray-100 dark:hover:bg-white/5 text-gray-400 transition-colors">
              <FiChevronLeft className={`transition-transform duration-300 ${!sidebarOpen ? 'rotate-180' : ''}`} />
            </button>
          </div>

          {/* Nav items */}
          <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
            {navItems.map((item, i) => {
              const Icon = item.icon
              const active = router.pathname === item.href
              return (
                <Link key={item.href} href={item.href}
                  className={`flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all duration-200 group relative ${
                    active
                      ? "bg-gradient-to-r " + item.color + " text-white shadow-lg shadow-black/5"
                      : "text-gray-400 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-white/5"
                  }`}
                >
                  <Icon className="text-lg flex-shrink-0" />
                  <motion.span
                    initial={{ opacity: 0 }}
                    animate={{ opacity: sidebarOpen ? 1 : 0 }}
                    className="text-sm font-medium whitespace-nowrap"
                  >
                    {item.label}
                  </motion.span>
                </Link>
              )
            })}
          </nav>

          {/* User */}
          <div className="p-3 border-t border-gray-200 dark:border-white/5">
            <div className="flex items-center gap-3 px-3 py-2">
              <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-[#10b981] to-[#06b6d4] flex items-center justify-center flex-shrink-0 text-white text-sm font-bold">
                {user.nome?.charAt(0) || "A"}
              </div>
              <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: sidebarOpen ? 1 : 0 }}
                className="flex-1 min-w-0"
              >
                <p className="text-sm font-medium text-gray-900 dark:text-white truncate">{user.nome}</p>
                <p className="text-xs text-gray-400 truncate">{user.email}</p>
              </motion.div>
            </div>
          </div>
        </div>
      </motion.aside>

      {/* Mobile sidebar */}
      <AnimatePresence>
        {mobileOpen && (
          <motion.aside
            initial={{ x: -300 }}
            animate={{ x: 0 }}
            exit={{ x: -300 }}
            transition={{ type: "spring", damping: 25 }}
            className="fixed left-0 top-0 h-full w-[280px] z-50 bg-white dark:bg-[#0f172a] border-r border-gray-200 dark:border-white/10 lg:hidden"
          >
            <div className="flex flex-col h-full">
              <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-white/5">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-[#10b981] to-[#06b6d4] flex items-center justify-center">
                    <span className="text-white font-bold">NV</span>
                  </div>
                  <span className="font-bold text-gray-900 dark:text-white">NeoVet</span>
                </div>
                <button onClick={() => setMobileOpen(false)} className="p-1.5 text-gray-400">
                  <FiX />
                </button>
              </div>
              <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
                {navItems.map((item) => {
                  const Icon = item.icon
                  const active = router.pathname === item.href
                  return (
                    <Link key={item.href} href={item.href} onClick={() => setMobileOpen(false)}
                      className={`flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all ${
                        active ? "bg-gradient-to-r " + item.color + " text-white" : "text-gray-400 hover:text-white hover:bg-white/5"
                      }`}
                    >
                      <Icon className="text-lg" />
                      <span className="text-sm font-medium">{item.label}</span>
                    </Link>
                  )
                })}
              </nav>
              <div className="p-3 border-t border-white/5">
                <button onClick={logout} className="flex items-center gap-3 px-3 py-2.5 w-full rounded-xl text-gray-400 hover:text-red-400 hover:bg-red-400/5 transition-all">
                  <FiLogOut /> <span className="text-sm">Sair</span>
                </button>
              </div>
            </div>
          </motion.aside>
        )}
      </AnimatePresence>

      {/* Main content */}
      <div className={`transition-all duration-300 ${sidebarOpen ? 'lg:ml-[260px]' : 'lg:ml-[72px]'}`}>
        {/* Top navbar */}
        <header className={`sticky top-0 z-30 bg-white/80 dark:bg-[#0f172a]/80 backdrop-blur-xl border-b border-gray-200 dark:border-white/5 transition-all ${scrolled ? 'shadow-sm' : ''}`}>
          <div className="flex items-center justify-between px-4 lg:px-6 h-16">
            <div className="flex items-center gap-3">
              <button onClick={() => setMobileOpen(true)} className="lg:hidden p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-white/5 text-gray-400">
                <FiMenu className="text-xl" />
              </button>
              <div className="relative hidden sm:block">
                <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm" />
                <input type="text" placeholder="Pesquisar..."
                  className="w-64 pl-9 pr-4 py-2 rounded-xl bg-gray-100 dark:bg-white/5 border border-transparent focus:border-[#10b981]/30 text-sm text-gray-900 dark:text-white placeholder-gray-400 focus:outline-none transition-all"
                />
              </div>
            </div>

            <div className="flex items-center gap-2">
              <button onClick={toggleTheme} className="p-2.5 rounded-xl hover:bg-gray-100 dark:hover:bg-white/5 text-gray-400 transition-colors">
                {dark ? <FiSun className="text-lg" /> : <FiMoon className="text-lg" />}
              </button>
              <button className="p-2.5 rounded-xl hover:bg-gray-100 dark:hover:bg-white/5 text-gray-400 transition-colors relative">
                <FiBell className="text-lg" />
                <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-[#10b981] rounded-full" />
              </button>
              <div className="hidden sm:flex items-center gap-3 ml-2 pl-4 border-l border-gray-200 dark:border-white/10">
                <div className="text-right">
                  <p className="text-sm font-medium text-gray-900 dark:text-white">{user.nome}</p>
                  <p className="text-xs text-gray-400">{user.email}</p>
                </div>
                <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-[#10b981] to-[#06b6d4] flex items-center justify-center text-white text-sm font-bold">
                  {user.nome?.charAt(0) || "A"}
                </div>
                <button onClick={logout} className="p-2 rounded-xl hover:bg-red-400/10 text-gray-400 hover:text-red-400 transition-all">
                  <FiLogOut />
                </button>
              </div>
            </div>
          </div>
        </header>

        {/* Page content */}
        <main className="p-4 lg:p-6">
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4 }}
          >
            {children}
          </motion.div>
        </main>
      </div>
    </div>
  )
}
