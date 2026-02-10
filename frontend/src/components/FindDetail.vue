<template>
  <div class="find-detail-container">
    <div class="find-detail-content">
      <router-link :to="backLink" class="back-link">⬅ {{ backText }}</router-link>

      <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <p>Cargando hallazgo...</p>
      </div>

      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <router-link to="/my-finds" class="btn btn-primary">Volver a mis hallazgos</router-link>
      </div>

      <div v-else-if="find" class="find-detail-card">
        <div class="find-header">
          <div class="header-left">
            <h1 class="find-title">Hallazgo #{{ find.id }}</h1>
            <div class="find-badges">
              <span class="find-status" :class="getStatusClass(find.status)">
                {{ getStatusText(find.status) }}
              </span>
              <span class="find-priority" :class="getPriorityClass(find.priority)">
                {{ getPriorityText(find.priority) }}
              </span>
            </div>
          </div>
          <div v-if="canValidate" class="header-actions">
            <button @click="showModal = true" class="btn btn-primary">
              Validar hallazgo
            </button>
          </div>
        </div>

        <div class="find-sections">
          <div class="info-section">
            <h2 class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="section-icon">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
              Información general
            </h2>

            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">Fecha de descubrimiento</span>
                <span class="info-value">{{ formatDate(find.discoveredAt) }}</span>
              </div>

              <div class="info-item">
                <span class="info-label">Registrado por</span>
                <span class="info-value">{{ find.reporterName }}</span>
              </div>

              <div class="info-item">
                <span class="info-label">Fecha de registro</span>
                <span class="info-value">{{ formatDate(find.createdAt) }}</span>
              </div>
            </div>
          </div>

          <div class="info-section">
            <h2 class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="section-icon">
                <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
              </svg>
              Ubicación
            </h2>

            <div class="location-card">
              <div v-if="locationName" class="location-name">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="location-icon">
                  <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                </svg>
                <span>{{ locationName }}</span>
              </div>

              <div class="coordinates">
                <div class="coordinate-item">
                  <span class="coordinate-label">Latitud</span>
                  <span class="coordinate-value">{{ find.latitude.toFixed(6) }}</span>
                </div>
                <div class="coordinate-item">
                  <span class="coordinate-label">Longitud</span>
                  <span class="coordinate-value">{{ find.longitude.toFixed(6) }}</span>
                </div>
              </div>

              <button class="btn-map" @click="openInMaps">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="btn-icon">
                  <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                </svg>
                Abrir en Google Maps
              </button>
            </div>
          </div>

          <div class="info-section">
            <h2 class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="section-icon">
                <path d="M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/>
              </svg>
              Descripción del hallazgo
            </h2>

            <div class="description-box">
              <p>{{ find.description }}</p>
            </div>
          </div>

          <div class="info-section" v-if="images.length > 0">
            <h2 class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="section-icon">
                <path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/>
              </svg>
              Imágenes ({{ images.length }})
            </h2>

            <div v-if="isLoadingImages" class="loading-images">
              <div class="spinner-small"></div>
              <p>Cargando imágenes...</p>
            </div>

            <div v-else class="images-gallery">
              <div
                  v-for="(image, index) in images"
                  :key="image.id"
                  class="image-card"
                  :class="{ 'primary': image.isPrimary }"
                  @click="openImageModal(index)"
              >
                <img
                    :src="getImageUrl(image.filePath)"
                    :alt="`Imagen ${index + 1}`"
                    class="gallery-image"
                />
                <div v-if="image.isPrimary" class="primary-badge">Principal</div>
              </div>
            </div>
          </div>

          <!-- Modal para ver imagen en grande -->
          <div v-if="selectedImageIndex !== null" class="image-modal-overlay" @click="closeImageModal">
            <div class="image-modal-content" @click.stop>
              <button @click="closeImageModal" class="image-modal-close">&times;</button>

              <div class="image-modal-body">
                <button
                    v-if="selectedImageIndex > 0"
                    @click="previousImage"
                    class="image-nav-btn prev"
                >
                  &#8249;
                </button>

                <img
                    :src="getImageUrl(images[selectedImageIndex].filePath)"
                    :alt="`Imagen ${selectedImageIndex + 1}`"
                    class="modal-image"
                />

                <button
                    v-if="selectedImageIndex < images.length - 1"
                    @click="nextImage"
                    class="image-nav-btn next"
                >
                  &#8250;
                </button>
              </div>

              <div class="image-modal-footer">
                <span class="image-counter">{{ selectedImageIndex + 1 }} / {{ images.length }}</span>
                <span class="image-info">{{ images[selectedImageIndex].originalFilename }}</span>
              </div>
            </div>
          </div>

          <div class="info-section" v-if="comments.length > 0 || canComment">
            <h2 class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="section-icon">
                <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>
              </svg>
              Comentarios
            </h2>

            <div v-if="isLoadingComments" class="loading-comments">
              <div class="spinner-small"></div>
              <p>Cargando comentarios...</p>
            </div>

            <div v-else class="comments-section">
              <div v-if="comments.length === 0" class="no-comments">
                <p>No hay comentarios aún</p>
              </div>

              <div v-else class="comments-list">
                <div v-for="comment in comments" :key="comment.id" class="comment-item">
                  <div class="comment-header">
                    <div class="comment-author">
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="author-icon">
                        <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                      </svg>
                      <span class="author-name">{{ comment.reviewerName }}</span>
                    </div>
                    <span class="comment-date">{{ formatCommentDate(comment.createdAt) }}</span>
                  </div>
                  <p class="comment-text">{{ comment.comment }}</p>
                </div>
              </div>

              <div v-if="canComment" class="add-comment-form">
                <h3 class="form-subtitle">Añadir comentario</h3>
                <textarea
                    v-model="newComment"
                    class="comment-textarea"
                    placeholder="Escribe tu comentario..."
                    rows="3"
                    maxlength="1000"
                    :disabled="isAddingComment"
                ></textarea>
                <div class="comment-form-footer">
                  <span class="char-counter">{{ newComment.length }} / 1000</span>
                  <button
                      @click="addComment"
                      class="btn btn-primary"
                      :disabled="isAddingComment || !newComment.trim()"
                  >
                    <span v-if="isAddingComment" class="loading"></span>
                    {{ isAddingComment ? 'Enviando...' : 'Añadir Comentario' }}
                  </button>
                </div>
                <div v-if="commentError" class="alert alert-error">
                  {{ commentError }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>Validar hallazgo #{{ find?.id }}</h2>
          <button @click="closeModal" class="modal-close">&times;</button>
        </div>

        <div class="modal-body">
          <form @submit.prevent="showConfirmation">
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
              <button type="button" @click="closeModal" class="btn btn-outline">
                Cancelar
              </button>
              <button type="submit" class="btn btn-primary" :disabled="!validationForm.status">
                Continuar
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <ConfirmationModal
        :isVisible="confirmModal.show"
        :title="confirmModal.title"
        :message="confirmModal.message"
        :details="confirmModal.details"
        :confirmText="confirmModal.confirmText"
        :cancelText="confirmModal.cancelText"
        :variant="confirmModal.variant"
        :loading="confirmModal.loading"
        @confirm="handleModalConfirm"
        @cancel="handleModalCancel"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { apiClient } from '../services/api'
import ConfirmationModal from './common/ConfirmationModal.vue'
import './FindDetail.css'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const find = ref<any>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)
const locationName = ref<string>('')
const isLoadingLocation = ref(false)
const showModal = ref(false)
const isValidating = ref(false)
const validationError = ref<string | null>(null)
const comments = ref<any[]>([])
const isLoadingComments = ref(false)
const isAddingComment = ref(false)
const newComment = ref('')
const commentError = ref<string | null>(null)

const validationForm = ref({
  status: '',
  priority: 'MEDIUM',
  comment: ''
})

const images = ref<any[]>([])
const isLoadingImages = ref(false)
const selectedImageIndex = ref<number | null>(null)


const getImageUrl = (filePath: string) => {

  return `http://localhost:8080/uploads/${filePath}`
}

const openImageModal = (index: number) => {
  selectedImageIndex.value = index
}

const closeImageModal = () => {
  selectedImageIndex.value = null
}

const nextImage = () => {
  if (selectedImageIndex.value !== null && selectedImageIndex.value < images.value.length - 1) {
    selectedImageIndex.value++
  }
}

const previousImage = () => {
  if (selectedImageIndex.value !== null && selectedImageIndex.value > 0) {
    selectedImageIndex.value--
  }
}

// Función para cargar imágenes
const loadImages = async () => {
  try {
    isLoadingImages.value = true
    const response = await apiClient.get(`/api/finds/${route.params.id}/images`)
    images.value = response.data

    // DEBUGGING: Ver qué contiene la respuesta
    console.log('Imágenes recibidas:', response.data)

  } catch (err) {
    console.error('Error al cargar imágenes:', err)
  } finally {
    isLoadingImages.value = false
  }
}

const confirmModal = ref({
  show: false,
  title: '',
  message: '',
  details: '',
  confirmText: 'Aceptar',
  cancelText: 'Cancelar',
  variant: 'warning' as 'warning' | 'danger' | 'info' | 'success',
  loading: false
})

const canValidate = computed(() => {
  return authStore.isArchaeologist && find.value?.status === 'PENDING'
})

const canComment = computed(() => {
  return authStore.isArchaeologist || authStore.isAuthority
})

const backLink = computed(() => {
  if (authStore.isUser) {
    return '/my-finds'
  } else if (authStore.isArchaeologist) {
    return '/validate-finds'
  } else if (authStore.isAuthority) {
    return '/authority'
  }
  return '/'
})

const backText = computed(() => {
  if (authStore.isUser) {
    return 'Volver a mis hallazgos'
  } else if (authStore.isArchaeologist) {
    return 'Volver a pendientes'
  } else if (authStore.isAuthority) {
    return 'Volver al panel'
  }
  return 'Volver al inicio'
})

const loadFind = async () => {
  try {
    isLoading.value = true
    error.value = null

    const findId = route.params.id
    const response = await apiClient.get(`/api/finds/${findId}`)
    find.value = response.data

    await loadLocationName(find.value.latitude, find.value.longitude)
    await loadComments()
    await loadImages()

  } catch (err: any) {
    if (err.response?.status === 404) {
      error.value = 'Hallazgo no encontrado'
    } else if (err.response?.status === 403) {
      error.value = 'No tienes permiso para ver este hallazgo'
    } else {
      error.value = err.response?.data?.message || 'Error al cargar el hallazgo'
    }
  } finally {
    isLoading.value = false
  }
}

const loadComments = async () => {
  try {
    isLoadingComments.value = true
    const response = await apiClient.get(`/api/finds/${route.params.id}/notes`)
    comments.value = response.data
  } catch (err) {
    console.error('Error al cargar comentarios:', err)
  } finally {
    isLoadingComments.value = false
  }
}

const addComment = async () => {
  if (!newComment.value.trim()) return

  try {
    isAddingComment.value = true
    commentError.value = null

    const response = await apiClient.post(`/api/finds/${find.value.id}/notes`, {
      comment: newComment.value.trim()
    })

    comments.value.unshift(response.data)
    newComment.value = ''

  } catch (err: any) {
    commentError.value = err.response?.data?.message || 'Error al añadir el comentario'
  } finally {
    isAddingComment.value = false
  }
}

const formatCommentDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadLocationName = async (lat: number, lon: number) => {
  try {
    isLoadingLocation.value = true
    const response = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&zoom=10&addressdetails=1`,
        {
          headers: {
            'Accept-Language': 'es'
          }
        }
    )

    const data = await response.json()

    if (data.address) {
      const parts = []

      if (data.address.village || data.address.town || data.address.city) {
        parts.push(data.address.village || data.address.town || data.address.city)
      }

      if (data.address.municipality && data.address.municipality !== parts[0]) {
        parts.push(data.address.municipality)
      }

      if (data.address.province || data.address.state) {
        parts.push(data.address.province || data.address.state)
      }

      if (data.address.country) {
        parts.push(data.address.country)
      }

      locationName.value = parts.length > 0 ? `Cerca de ${parts.join(', ')}` : ''
    } else if (data.display_name) {
      locationName.value = data.display_name
    }
  } catch (err) {
    console.error('Error al obtener nombre de ubicación:', err)
  } finally {
    isLoadingLocation.value = false
  }
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusClass = (status: string) => {
  return status.toLowerCase()
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': 'Pendiente de Validación',
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
    'LOW': 'Prioridad Baja',
    'MEDIUM': 'Prioridad Media',
    'HIGH': 'Prioridad Alta',
    'CRITICAL': 'Prioridad Crítica'
  }
  return priorityMap[priority] || priority
}

const openInMaps = () => {
  if (find.value) {
    const url = `https://www.google.com/maps?q=${find.value.latitude},${find.value.longitude}`
    window.open(url, '_blank')
  }
}

const closeModal = () => {
  showModal.value = false
  validationForm.value = {
    status: '',
    priority: find.value?.priority || 'MEDIUM',
    comment: ''
  }
  validationError.value = null
}

const showConfirmation = () => {
  if (!validationForm.value.status) {
    validationError.value = 'Debes seleccionar una decisión'
    return
  }

  const isValidation = validationForm.value.status === 'VALIDATED'

  confirmModal.value = {
    show: true,
    title: isValidation ? 'Confirmar validación' : 'Confirmar rechazo',
    message: isValidation
        ? `¿Estás seguro de validar el hallazgo #${find.value.id}?`
        : `¿Estás seguro de rechazar el hallazgo #${find.value.id}?`,
    details: isValidation
        ? `El ciudadano ${find.value.reporterName} será notificado de la validación.`
        : `El ciudadano ${find.value.reporterName} será notificado del rechazo.`,
    confirmText: isValidation ? 'Validar hallazgo' : 'Rechazar hallazgo',
    cancelText: 'Cancelar',
    variant: isValidation ? 'success' : 'danger',
    loading: false
  }

  showModal.value = false
}

const handleModalConfirm = async () => {
  confirmModal.value.loading = true

  try {
    const payload: any = {
      status: validationForm.value.status,
      priority: validationForm.value.priority
    }

    if (validationForm.value.comment.trim()) {
      payload.reviewComment = validationForm.value.comment.trim()
    }

    await apiClient.put(`/api/finds/${find.value.id}/validate`, payload)

    confirmModal.value.show = false
    router.push('/validate-finds')

  } catch (err: any) {
    confirmModal.value.show = false
    validationError.value = err.response?.data?.message || 'Error al validar el hallazgo'
    showModal.value = true
  } finally {
    confirmModal.value.loading = false
  }
}

const handleModalCancel = () => {
  confirmModal.value.show = false
  showModal.value = true
}

onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  loadFind()
})
</script>