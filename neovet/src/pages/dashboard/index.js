import { useState, useEffect } from "react"
import Layout from "@/components/Layout"
import { motion } from "framer-motion"
import { FiUsers, FiCalendar, FiDollarSign, FiActivity, FiTrendingUp, FiArrowUp, FiArrowDown } from "react-icons/fi"
import { getDashboardStats, getActivity, getAppointments, getFinancial } from "@/data/store"
import { LineChart, Line, AreaChart, Area, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from "recharts"

const statsCards = [
  { label: "Total de Pets", key: "totalPets", icon: FiActivity, color: "from-[#10b981] to-[#06b6d4]", bg: "bg-[#10b981]/10" },
  { label: "Clientes", key: "totalClientes", icon: FiUsers, color: "from-[#8b5cf6] to-[#6366f1]", bg: "bg-[#8b5cf6]/10" },
  { label: "Consultas", key: "totalConsultas", icon: FiCalendar, color: "from-[#ec4899] to-[#f43f5e]", bg: "bg-[#ec4899]/10" },
  { label: "Receita do Mês", key: "receitaMes", icon: FiDollarSign, color: "from-[#f59e0b] to-[#f97316]", bg: "bg-[#f59e0b]/10", prefix: "R$" },
]

const COLORS = ["#10b981", "#8b5cf6", "#ec4899", "#f59e0b", "#06b6d4"]

export default function Dashboard() {
  const [stats, setStats] = useState({ totalPets: 0, totalClientes: 0, totalConsultas: 0, receitaMes: 0 })
  const [activities, setActivities] = useState([])
  const [appointments, setAppointments] = useState([])

  useEffect(() => {
    setStats(getDashboardStats())
    setActivities(getActivity().slice(0, 8))
    setAppointments(getAppointments().filter(a => a.status !== "cancelado"))
  }, [])

  const chartData = [
    { mes: "Jan", consultas: 45, receita: 5200 },
    { mes: "Fev", consultas: 52, receita: 6100 },
    { mes: "Mar", consultas: 48, receita: 5800 },
    { mes: "Abr", consultas: 61, receita: 7200 },
    { mes: "Mai", consultas: 55, receita: 6500 },
    { mes: "Jun", consultas: stats.totalConsultas || 67, receita: stats.receitaMes || 7800 },
  ]

  const petTypes = [
    { name: "Cães", value: 65 },
    { name: "Gatos", value: 25 },
    { name: "Outros", value: 10 },
  ]

  const recentAppointments = appointments.slice(0, 5)

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
        {/* Header */}
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Dashboard</h1>
          <p className="text-sm text-gray-400">Visão geral do NeoVet</p>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          {statsCards.map((card, i) => {
            const Icon = card.icon
            const value = stats[card.key]
            return (
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.1 }}
                key={card.key}
                className="relative overflow-hidden rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-5 group hover:shadow-lg hover:shadow-black/5 transition-all"
              >
                <div className="flex items-start justify-between">
                  <div className="space-y-2">
                    <p className="text-xs font-medium text-gray-400">{card.label}</p>
                    <p className="text-2xl font-bold text-gray-900 dark:text-white">
                      {card.prefix ? `${card.prefix} ${typeof value === 'number' ? value.toLocaleString() : value}` : typeof value === 'number' ? value.toLocaleString() : value}
                    </p>
                  </div>
                  <div className={`p-3 rounded-xl ${card.bg}`}>
                    <Icon className={`text-lg bg-gradient-to-r ${card.color} bg-clip-text text-transparent`} />
                  </div>
                </div>
                <div className={`absolute bottom-0 left-0 right-0 h-0.5 bg-gradient-to-r ${card.color} opacity-50`} />
              </motion.div>
            )
          })}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Chart */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="lg:col-span-2 rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-6"
          >
            <h3 className="text-sm font-semibold text-gray-900 dark:text-white mb-6">Receita × Consultas</h3>
            <div className="h-72">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData}>
                  <defs>
                    <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#10b981" stopOpacity={0.3} />
                      <stop offset="95%" stopColor="#10b981" stopOpacity={0} />
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" />
                  <XAxis dataKey="mes" stroke="#64748b" fontSize={12} />
                  <YAxis stroke="#64748b" fontSize={12} />
                  <Tooltip
                    contentStyle={{
                      background: "#1e293b",
                      border: "1px solid rgba(16,185,129,0.2)",
                      borderRadius: "12px",
                      color: "#fff",
                      fontSize: "12px",
                    }}
                  />
                  <Area type="monotone" dataKey="receita" stroke="#10b981" strokeWidth={2} fill="url(#colorRevenue)" />
                  <Line type="monotone" dataKey="consultas" stroke="#8b5cf6" strokeWidth={2} dot={false} />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </motion.div>

          {/* Pet distribution */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5 }}
            className="rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-6"
          >
            <h3 className="text-sm font-semibold text-gray-900 dark:text-white mb-6">Distribuição de Pets</h3>
            <div className="h-60">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie data={petTypes} cx="50%" cy="50%" innerRadius={50} outerRadius={80} paddingAngle={5} dataKey="value">
                    {petTypes.map((_, i) => (
                      <Cell key={i} fill={COLORS[i]} />
                    ))}
                  </Pie>
                  <Tooltip
                    contentStyle={{ background: "#1e293b", border: "1px solid rgba(16,185,129,0.2)", borderRadius: "12px", color: "#fff", fontSize: "12px" }}
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="flex justify-center gap-4 mt-2">
              {petTypes.map((item, i) => (
                <div key={item.name} className="flex items-center gap-1.5">
                  <div className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: COLORS[i] }} />
                  <span className="text-xs text-gray-400">{item.name} {item.value}%</span>
                </div>
              ))}
            </div>
          </motion.div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Recent appointments */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.6 }}
            className="rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-6"
          >
            <h3 className="text-sm font-semibold text-gray-900 dark:text-white mb-4">Agendamentos Recentes</h3>
            <div className="space-y-3">
              {recentAppointments.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-6">Nenhum agendamento ainda</p>
              ) : recentAppointments.map((apt, i) => (
                <div key={apt.id} className="flex items-center justify-between p-3 rounded-xl bg-gray-50 dark:bg-white/5">
                  <div className="flex items-center gap-3">
                    <div className="w-9 h-9 rounded-lg bg-gradient-to-br from-[#10b981] to-[#06b6d4] flex items-center justify-center text-white text-xs font-bold">
                      {apt.petNome?.charAt(0) || "P"}
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-900 dark:text-white">{apt.petNome}</p>
                      <p className="text-xs text-gray-400">{apt.tipo} • {apt.data}</p>
                    </div>
                  </div>
                  <span className={`text-[10px] font-medium px-2.5 py-1 rounded-full border ${statusColors[apt.status] || statusColors.agendado}`}>
                    {apt.status}
                  </span>
                </div>
              ))}
            </div>
          </motion.div>

          {/* Activity */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.7 }}
            className="rounded-2xl bg-white dark:bg-[#0f172a] border border-gray-200 dark:border-white/5 p-6"
          >
            <h3 className="text-sm font-semibold text-gray-900 dark:text-white mb-4">Atividade Recente</h3>
            <div className="space-y-3">
              {activities.length === 0 ? (
                <p className="text-sm text-gray-400 text-center py-6">Nenhuma atividade ainda</p>
              ) : activities.map((act, i) => (
                <div key={act.id} className="flex items-start gap-3 p-3 rounded-xl bg-gray-50 dark:bg-white/5">
                  <div className="w-2 h-2 rounded-full bg-[#10b981] mt-1.5 flex-shrink-0" />
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900 dark:text-white truncate">{act.titulo}</p>
                    <p className="text-xs text-gray-400 truncate">{act.desc}</p>
                  </div>
                  <span className="text-[10px] text-gray-500 whitespace-nowrap">
                    {act.data ? new Date(act.data).toLocaleDateString("pt-BR") : ""}
                  </span>
                </div>
              ))}
            </div>
          </motion.div>
        </div>
      </div>
    </Layout>
  )
}
