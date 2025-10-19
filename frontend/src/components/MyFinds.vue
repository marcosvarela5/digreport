<template>
  <div class="my-finds-container">
    <div class="my-finds-header">
      <div class="header-content">
        <router-link to="/" class="back-link">⬅ Volver al inicio</router-link>
        <h1 class="page-title">Mis Hallazgos</h1>
        <p class="page-subtitle">Gestiona y consulta tus hallazgos registrados</p>
      </div>
      <router-link to="/register-find" class="btn btn-primary">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="btn-icon">
          <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
        </svg>
        Nuevo Hallazgo
      </router-link>
    </div>

    <div class="finds-content">
      <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <p>Cargando hallazgos...</p>
      </div>

      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
      </div>

      <div v-else-if="finds.length === 0" class="empty-state">
        <div class="empty-icon">📍</div>
        <h3>No tienes hallazgos registrados</h3>
        <p>Comienza a registrar hallazgos arqueológicos para ayudar a preservar nuestro patrimonio.</p>
        <router-link to="/register-find" class="btn btn-primary btn-large">
          Registrar Primer Hallazgo
        </router-link>
      </div>

      <div v-else class="finds-grid">
        <div
            v-for="find in finds"
            :key="find.id"
            class="find-card"
            @click="goToDetail(find.id)"
        >
          <div class="find-card-header">
            <span class="find-status" :class="getStatusClass(find.status)">
              {{ getStatusText(find.status) }}
            </span>
            <span class="find-priority" :class="getPriorityClass(find.priority)">
              {{ getPriorityText(find.priority) }}
            </span>
          </div>

          <div class="find-card-body">
            <div class="find-date">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="icon">
                <path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z"/>
              </svg>
              Descubierto: {{ formatDate(find.discoveredAt) }}
            </div>

            <div class="find-location">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="icon">
                <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
              </svg>
              {{ find.latitude.toFixed(6) }}, {{ find.longitude.toFixed(6) }}
            </div>

            <p class="find-description">{{ truncateText(find.description, 120) }}</p>
          </div>

          <div class="find-card-footer">
            <span class="find-id">Hallazgo #{{ find.id }}</span>
            <span class="find-arrow">→</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { apiClient } from '../services/api'
import './MyFinds.css'

const router = useRouter()
const authStore = useAuthStore()

const finds = ref<any[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

interface Find {
  id: number
  discoveredAt: string
  latitude: number
  longitude: number
  description: string
  status: string
  priority: string
  reporterId: number
  reporterName: string
  createdAt: string
}

const loadFinds = async () => {
  try {
    isLoading.value = true
    error.value = null

    const response = await apiClient.get('/api/finds/my')
    finds.value = response.data

  } catch (err: any) {
    error.value = err.response?.data?.message || 'Error al cargar los hallazgos'
  } finally {
    isLoading.value = false
  }
}

const goToDetail = (id: number) => {
  router.push(`/finds/${id}`)
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    day: '2-digit',
    month: 'long',
    year: 'numeric'
  })
}

const truncateText = (text: string, maxLength: number) => {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

const getStatusClass = (status: string) => {
  return status.toLowerCase()
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': 'Pendiente',
    'VALIDATED': 'Validado',
    'REJECTED': 'Rechazado',
    'REVOKED': 'Revocado'
  }
  return statusMap[status] || status
}

const getPriorityClass = (priority: string) => {
  return `priority-${priority.toLowerCase()}`
}

const getPriorityText = (priority: string) => {
  const priorityMap: Record<string, string> = {
    'LOW': 'Baja',
    'MEDIUM': 'Media',
    'HIGH': 'Alta',
    'CRITICAL': 'Crítica'
  }
  return priorityMap[priority] || priority
}

onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  if (!authStore.isUser) {
    error.value = 'Acceso no autorizado'
    setTimeout(() => router.push('/'), 3000)
    return
  }

  loadFinds()
})
</script>