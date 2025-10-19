<template>
  <div class="validate-finds-container">
    <div class="validate-finds-header">
      <div class="header-content">
        <router-link to="/" class="back-link">⬅ Volver al inicio</router-link>
        <h1 class="page-title">Panel de Validación</h1>
        <p class="page-subtitle">Revisa y valida los hallazgos reportados por los ciudadanos</p>
      </div>

      <div class="tabs">
        <button
            class="tab"
            :class="{ active: activeTab === 'pending' }"
            @click="switchToPending"
        >
          Pendientes
          <span v-if="activeTab === 'pending' && finds.length > 0" class="tab-badge">{{ finds.length }}</span>
        </button>
        <button
            class="tab"
            :class="{ active: activeTab === 'validated' }"
            @click="switchToValidated"
        >
          Mis Validaciones
          <span v-if="activeTab === 'validated' && finds.length > 0" class="tab-badge">{{ finds.length }}</span>
        </button>
      </div>
    </div>

    <div class="finds-content">
      <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <p>Cargando hallazgos pendientes...</p>
      </div>

      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
      </div>

      <div v-else-if="finds.length === 0" class="empty-state">
        <div class="empty-icon">{{ activeTab === 'pending' ? '✅' : '📋' }}</div>
        <h3>{{ activeTab === 'pending' ? 'No hay hallazgos pendientes' : 'No has validado ningún hallazgo' }}</h3>
        <p>{{ activeTab === 'pending' ? 'Todos los hallazgos han sido revisados.' : 'Los hallazgos que valides aparecerán aquí.' }}</p>
      </div>

      <div v-else class="finds-grid">
        <div
            v-for="find in finds"
            :key="find.id"
            class="find-card"
        >
          <div class="find-card-header">
            <span class="find-id">Hallazgo #{{ find.id }}</span>
            <span class="find-priority" :class="getPriorityClass(find.priority)">
              {{ getPriorityText(find.priority) }}
            </span>
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

    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>Validar Hallazgo #{{ selectedFind?.id }}</h2>
          <button @click="closeModal" class="modal-close">&times;</button>
        </div>

        <div class="modal-body">
          <div class="find-summary">
            <p><strong>Reportado por:</strong> {{ selectedFind?.reporterName }}</p>
            <p><strong>Fecha:</strong> {{ formatDate(selectedFind?.discoveredAt) }}</p>
            <p><strong>Ubicación:</strong> {{ selectedFind?.latitude }}, {{ selectedFind?.longitude }}</p>
          </div>

          <form @submit.prevent="handleValidate">
            <div class="form-group">
              <label class="form-label">Decisión *</label>
              <div class="radio-group">
                <label class="radio-label">
                  <input type="radio" v-model="validationForm.status" value="VALIDATED" />
                  <span>✅ Validar</span>
                </label>
                <label class="radio-label">
                  <input type="radio" v-model="validationForm.status" value="REJECTED" />
                  <span>❌ Rechazar</span>
                </label>
              </div>
            </div>

            <div class="form-group">
              <label for="priority" class="form-label">Prioridad</label>
              <select id="priority" v-model="validationForm.priority" class="form-input">
                <option value="LOW">Baja</option>
                <option value="MEDIUM">Media</option>
                <option value="HIGH">Alta</option>
                <option value="CRITICAL">Crítica</option>
              </select>
            </div>

            <div class="form-group">
              <label for="comment" class="form-label">Comentario</label>
              <textarea
                  id="comment"
                  v-model="validationForm.comment"
                  class="form-textarea"
                  placeholder="Añade un comentario para el ciudadano (opcional)"
                  rows="4"
                  maxlength="500"
              ></textarea>
              <div class="char-counter">{{ validationForm.comment.length }} / 500</div>
            </div>

            <div v-if="validationError" class="alert alert-error">
              {{ validationError }}
            </div>

            <div class="modal-footer">
              <button type="button" @click="closeModal" class="btn btn-outline" :disabled="isValidating">
                Cancelar
              </button>
              <button type="submit" class="btn btn-primary" :disabled="isValidating || !validationForm.status">
                <span v-if="isValidating" class="loading"></span>
                {{ isValidating ? 'Procesando...' : 'Confirmar' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
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
const showModal = ref(false)
const selectedFind = ref<any>(null)
const isValidating = ref(false)
const validationError = ref<string | null>(null)
const activeTab = ref('pending')

const validationForm = reactive({
  status: '',
  priority: 'MEDIUM',
  comment: ''
})

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

const openValidationModal = (find: any) => {
  selectedFind.value = find
  validationForm.status = ''
  validationForm.priority = find.priority
  validationForm.comment = ''
  validationError.value = null
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  selectedFind.value = null
}

const handleValidate = async () => {
  if (!validationForm.status) {
    validationError.value = 'Debes seleccionar una decisión'
    return
  }

  try {
    isValidating.value = true
    validationError.value = null

    const payload: any = {
      status: validationForm.status,
      priority: validationForm.priority
    }

    if (validationForm.comment.trim()) {
      payload.reviewComment = validationForm.comment.trim()
    }

    await apiClient.put(`/api/finds/${selectedFind.value.id}/validate`, payload)

    finds.value = finds.value.filter(f => f.id !== selectedFind.value.id)

    closeModal()

  } catch (err: any) {
    validationError.value = err.response?.data?.message || 'Error al validar el hallazgo'
  } finally {
    isValidating.value = false
  }
}

const viewDetails = (id: number) => {
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

const getPriorityClass = (priority: string) => {
  return `priority-${priority.toLowerCase()}`
}

const getPriorityText = (priority: string) => {
  const priorityMap: Record<string, string> = {
    'LOW': 'Prioridad Baja',
    'MEDIUM': 'Prioridad Media',
    'HIGH': 'Prioridad Alta',
    'CRITICAL': 'Prioridad Crítica'
  }
  return priorityMap[priority] || priority
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