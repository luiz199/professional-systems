import { createContext, useContext, useState, useEffect } from "react"
import { useRouter } from "next/router"

const AuthContext = createContext()

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const router = useRouter()

  useEffect(() => {
    const stored = localStorage.getItem("neovet_user")
    if (stored) {
      try { setUser(JSON.parse(stored)) } catch {}
    }
    setLoading(false)
  }, [])

  function login(email, password) {
    if (email === "admin@datamindvet.com" && password === "2025") {
      const u = { nome: "Dr. Admin", email, role: "admin", avatar: "A" }
      localStorage.setItem("neovet_user", JSON.stringify(u))
      setUser(u)
      router.push("/dashboard")
      return { success: true }
    }
    return { success: false, error: "Email ou senha inválidos" }
  }

  function logout() {
    localStorage.removeItem("neovet_user")
    setUser(null)
    router.push("/")
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
