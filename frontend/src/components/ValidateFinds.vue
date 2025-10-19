<template>
  <div class="validate-finds-container">
    <div class="validate-finds-header">
      <div class="header-content">
        <router-link to="/" class="back-link">⬅ Volver al inicio</router-link>
        <h1 class="page-title">Validación</h1>
        <p class="page-subtitle">Revisa y valida los hallazgos reportados por los ciudadanos</p>
      </div>

      <div class="tabs">
        <button
            class="tab"
            :class="{ active: activeTab === 'pending' }"
            @click="switchToPending"
        >
          Validaciones pendientes
          <span v-if="activeTab === 'pending' && finds.length > 0" class="tab-badge">{{ finds.length }}</span>
        </button>
        <button
            class="tab"
            :class="{ active: activeTab === 'validated' }"
            @click="switchToValidated"
        >
          Mis validaciones
          <span v-if="activeTab === 'validated' && finds.length > 0" class="tab-badge">{{ finds.length }}</span>
        </button>
      </div>
    </div>

    <LoadingState v-if="isLoading" message="Cargando hallazgos pendientes..." />

    <ErrorState v-else-if="error" :message="error" />

    <EmptyState
        v-else-if="finds.length === 0"
        :icon="activeTab === 'pending' ? '✅' : '📋'"
        :title="activeTab === 'pending' ? 'No hay hallazgos pendientes' : 'No has validado ningún hallazgo'"
        :message="activeTab === 'pending' ? 'Todos los hallazgos han sido revisados.' : 'Los hallazgos que valides aparecerán aquí.'"
    />

      <div v-else class="finds-grid">
        <div
            v-for="find in finds"
            :key="find.id"
            class="find-card"
        >
          <div class="find-card-header">
            <span class="find-id">#{{ find.id }}</span>
            <PriorityBadge :priority="find.priority" />
          </div>

          <div class="find-card-body">
            <div class="find-info">
              <div class="info-row">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="icon">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
                <span>{{ find.reporterName }}</span>
              </div>

              <div class="info-row">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="icon">
                  <path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z"/>
                </svg>
                <span>{{ formatDate(find.discoveredAt) }}</span>
              </div>

              <div class="info-row">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="icon">
                  <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                </svg>
                <span>{{ find.latitude.toFixed(4) }}, {{ find.longitude.toFixed(4) }}</span>
              </div>
            </div>

            <p class="find-description">{{ truncateText(find.description, 100) }}</p>
          </div>

          <div class="find-card-footer">
            <button @click="viewDetails(find.id)" class="btn btn-primary">
              Ver →
            </button>
          </div>
        </div>
      </div>
    </div>
</template>

<script setup lang="ts">
import { LoadingState, ErrorState, EmptyState, PriorityBadge } from './common'
import { useFormatters } from '@/composables/useFormatters'
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { apiClient } from '../services/api'
import './ValidateFinds.css'

const router = useRouter()
const authStore = useAuthStore()

const finds = ref<any[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)
const activeTab = ref('pending')
const { formatDate, truncateText } = useFormatters()

const loadPendingFinds = async () => {
  try {
    isLoading.value = true
    error.value = null

    const response = await apiClient.get('/api/finds/pending')
    finds.value = response.data

  } catch (err: any) {
    error.value = err.response?.data?.message || 'Error al cargar los hallazgos'
  } finally {
    isLoading.value = false
  }
}

const loadMyValidatedFinds = async () => {
  try {
    isLoading.value = true
    error.value = null

    const response = await apiClient.get('/api/finds/my-validated')
    finds.value = response.data

  } catch (err: any) {
    error.value = err.response?.data?.message || 'Error al cargar los hallazgos'
  } finally {
    isLoading.value = false
  }
}

const switchToValidated = () => {
  activeTab.value = 'validated'
  loadMyValidatedFinds()
}

const switchToPending = () => {
  activeTab.value = 'pending'
  loadPendingFinds()
}

const viewDetails = (id: number) => {
  router.push(`/finds/${id}`)
}

onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  if (!authStore.isArchaeologist) {
    error.value = 'Acceso no autorizado'
    setTimeout(() => router.push('/'), 3000)
    return
  }

  loadPendingFinds()
})
</script>