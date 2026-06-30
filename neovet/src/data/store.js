const STORE_KEYS = {
  CLIENTS: "neovet_clients",
  PETS: "neovet_pets",
  VETS: "neovet_vets",
  APPOINTMENTS: "neovet_appointments",
  MEDICATIONS: "neovet_medications",
  FINANCIAL: "neovet_financial",
  INVENTORY: "neovet_inventory",
  ACTIVITY: "neovet_activity",
  SETTINGS: "neovet_settings",
}

const DEFAULT_SETTINGS = {
  clinicaNome: "NeoVet",
  clinicaEndereco: "",
  clinicaTelefone: "",
  clinicaEmail: "",
  clinicaCnpj: "",
  clinicaLogo: "",
  medicoNome: "Dr. Admin",
  medicoCrmv: "",
  prazoPagamento: "30",
  notasFiscais: true,
  impostos: 0,
}

export function getSettings() {
  const data = get(STORE_KEYS.SETTINGS)
  return { ...DEFAULT_SETTINGS, ...data }
}

export function saveSettings(data) {
  set(STORE_KEYS.SETTINGS, { ...getSettings(), ...data })
  addActivity("Configurações atualizadas", "Dados da clínica foram alterados")
}

function get(key) {
  if (typeof window === "undefined") return null
  try {
    const data = localStorage.getItem(key)
    return data ? JSON.parse(data) : null
  } catch { return null }
}

function set(key, value) {
  if (typeof window === "undefined") return
  try { localStorage.setItem(key, JSON.stringify(value)) } catch {}
}

function generateId() {
  return Date.now().toString(36) + Math.random().toString(36).substring(2, 8)
}

function today() {
  return new Date().toISOString().split("T")[0]
}

export function getClients() { return get(STORE_KEYS.CLIENTS) || [] }
export function getClient(id) { return getClients().find(c => c.id === id) }
export function saveClient(data) {
  const list = getClients()
  if (data.id) {
    const idx = list.findIndex(c => c.id === data.id)
    if (idx >= 0) list[idx] = { ...list[idx], ...data, updatedAt: new Date().toISOString() }
  } else {
    data.id = generateId()
    data.createdAt = new Date().toISOString()
    data.updatedAt = data.createdAt
    list.push(data)
  }
  set(STORE_KEYS.CLIENTS, list)
  addActivity(data.id ? "Cliente atualizado" : "Novo cliente cadastrado", data.nome)
  return data
}
export function deleteClient(id) {
  set(STORE_KEYS.CLIENTS, getClients().filter(c => c.id !== id))
  set(STORE_KEYS.PETS, getPets().filter(p => p.tutorId !== id))
}

export function getPets() { return get(STORE_KEYS.PETS) || [] }
export function getPet(id) { return getPets().find(p => p.id === id) }
export function getPetsByTutor(tutorId) { return getPets().filter(p => p.tutorId === tutorId) }
export function savePet(data) {
  const list = getPets()
  if (data.id) {
    const idx = list.findIndex(p => p.id === data.id)
    if (idx >= 0) list[idx] = { ...list[idx], ...data, updatedAt: new Date().toISOString() }
  } else {
    data.id = generateId()
    data.createdAt = new Date().toISOString()
    data.updatedAt = data.createdAt
    list.push(data)
  }
  set(STORE_KEYS.PETS, list)
  addActivity(data.id ? "Pet atualizado" : "Novo pet cadastrado", data.nome)
  return data
}
export function deletePet(id) { set(STORE_KEYS.PETS, getPets().filter(p => p.id !== id)) }

export function getVets() { return get(STORE_KEYS.VETS) || [] }
export function saveVet(data) {
  const list = getVets()
  if (data.id) {
    const idx = list.findIndex(v => v.id === data.id)
    if (idx >= 0) list[idx] = { ...list[idx], ...data }
  } else {
    data.id = generateId()
    data.createdAt = new Date().toISOString()
    list.push(data)
  }
  set(STORE_KEYS.VETS, list)
  return data
}
export function deleteVet(id) { set(STORE_KEYS.VETS, getVets().filter(v => v.id !== id)) }

export function getAppointments() { return get(STORE_KEYS.APPOINTMENTS) || [] }
export function saveAppointment(data) {
  const list = getAppointments()
  if (data.id) {
    const idx = list.findIndex(a => a.id === data.id)
    if (idx >= 0) list[idx] = { ...list[idx], ...data }
  } else {
    data.id = generateId()
    data.createdAt = new Date().toISOString()
    data.status = data.status || "agendado"
    list.push(data)
  }
  set(STORE_KEYS.APPOINTMENTS, list)
  addActivity(data.id ? "Agendamento atualizado" : "Novo agendamento", `${data.tipo} - ${data.petNome}`)
  return data
}
export function deleteAppointment(id) { set(STORE_KEYS.APPOINTMENTS, getAppointments().filter(a => a.id !== id)) }

export function getMedications() { return get(STORE_KEYS.MEDICATIONS) || [] }
export function saveMedication(data) {
  const list = getMedications()
  if (data.id) {
    const idx = list.findIndex(m => m.id === data.id)
    if (idx >= 0) list[idx] = { ...list[idx], ...data }
  } else {
    data.id = generateId()
    data.createdAt = new Date().toISOString()
    list.push(data)
  }
  set(STORE_KEYS.MEDICATIONS, list)
  return data
}
export function deleteMedication(id) { set(STORE_KEYS.MEDICATIONS, getMedications().filter(m => m.id !== id)) }

export function getInventory() { return get(STORE_KEYS.INVENTORY) || [] }
export function saveInventoryItem(data) {
  const list = getInventory()
  if (data.id) {
    const idx = list.findIndex(i => i.id === data.id)
    if (idx >= 0) list[idx] = { ...list[idx], ...data }
  } else {
    data.id = generateId()
    data.createdAt = new Date().toISOString()
    list.push(data)
  }
  set(STORE_KEYS.INVENTORY, list)
  return data
}
export function deleteInventoryItem(id) { set(STORE_KEYS.INVENTORY, getInventory().filter(i => i.id !== id)) }

export function getFinancial() { return get(STORE_KEYS.FINANCIAL) || [] }
export function saveTransaction(data) {
  const list = getFinancial()
  if (data.id) {
    const idx = list.findIndex(t => t.id === data.id)
    if (idx >= 0) list[idx] = { ...list[idx], ...data }
  } else {
    data.id = generateId()
    data.createdAt = new Date().toISOString()
    list.push(data)
  }
  set(STORE_KEYS.FINANCIAL, list)
  addActivity("Nova transação financeira", `${data.tipo === "entrada" ? "+" : "-"} R$ ${data.valor}`)
  return data
}
export function deleteTransaction(id) { set(STORE_KEYS.FINANCIAL, getFinancial().filter(t => t.id !== id)) }

export function getActivity() { return get(STORE_KEYS.ACTIVITY) || [] }
export function addActivity(titulo, desc) {
  const list = getActivity()
  list.unshift({ id: generateId(), titulo, desc, data: new Date().toISOString() })
  if (list.length > 50) list.length = 50
  set(STORE_KEYS.ACTIVITY, list)
}

export function getDashboardStats() {
  const clients = getClients()
  const pets = getPets()
  const appointments = getAppointments()
  const financial = getFinancial()
  return {
    totalClientes: clients.length,
    totalPets: pets.length,
    totalConsultas: appointments.length,
    receitaMes: financial
      .filter(t => t.tipo === "entrada" && t.data?.startsWith(today().substring(0, 7)))
      .reduce((s, t) => s + Number(t.valor || 0), 0),
    agendamentosHoje: appointments.filter(a => a.data === today()).length,
  }
}
