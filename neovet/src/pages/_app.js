import "@/styles/globals.css"
import { AuthProvider } from "@/context/AuthContext"
import { Toaster } from "react-hot-toast"
import { motion, AnimatePresence } from "framer-motion"
import { useRouter } from "next/router"
import { ThemeProvider } from "@/context/ThemeContext"

export default function App({ Component, pageProps }) {
  const router = useRouter()
  return (
    <ThemeProvider>
      <AuthProvider>
        <AnimatePresence mode="wait">
          <motion.div
            key={router.route}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.3 }}
          >
            <Component {...pageProps} />
          </motion.div>
        </AnimatePresence>
        <Toaster
          position="top-right"
          toastOptions={{
            style: {
              background: "#1e293b",
              color: "#f8fafc",
              border: "1px solid rgba(16,185,129,0.2)",
              borderRadius: "12px",
              fontSize: "14px",
            },
            success: { iconTheme: { primary: "#10b981", secondary: "#fff" } },
            error: { iconTheme: { primary: "#ef4444", secondary: "#fff" } },
          }}
        />
      </AuthProvider>
    </ThemeProvider>
  )
}
