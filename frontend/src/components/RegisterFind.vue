<template>
  <div class="register-find-container">
    <router-link to="/" class="back-link">⬅ Volver al inicio</router-link>
    <div class="register-find-card">
      <div class="register-find-header">
        <h1 class="register-find-title">Registrar hallazgo</h1>
        <p class="register-find-subtitle">Ayuda a preservar nuestro patrimonio histórico</p>
      </div>

      <form @submit.prevent="handleSubmit" class="register-find-form">
        <!-- ===== SECCIÓN DE IMÁGENES ===== -->
        <div class="form-section">
          <h3 class="section-title">Imágenes del hallazgo</h3>

          <div class="image-upload-area">
            <input
                ref="fileInput"
                type="file"
                accept="image/*"
                multiple
                @change="handleImageSelect"
                style="display: none"
            />

            <button
                type="button"
                class="btn-upload"
                @click="$refs.fileInput.click()"
                :disabled="uploadedImages.length >= 10"
            >
              Seleccionar imágenes (máx. 10)
            </button>

            <p class="upload-hint">Formatos: JPG, PNG, WebP • Máximo 10MB por imagen</p>
          </div>

          <!-- Preview de imágenes -->
          <div v-if="uploadedImages.length > 0" class="images-preview">
            <div
                v-for="(img, index) in uploadedImages"
                :key="index"
                class="image-preview-item"
            >
              <img :src="img.preview" :alt="`Imagen ${index + 1}`" />
              <button
                  type="button"
                  class="btn-remove-image"
                  @click="removeImage(index)"
                  title="Eliminar imagen"
              >
                ✕
              </button>
              <span class="image-number">{{ index + 1 }}</span>
            </div>
          </div>
        </div>

        <!-- ===== SECCIÓN DE INFORMACIÓN ===== -->
        <div class="form-section">
          <h3 class="section-title">Información del hallazgo</h3>

          <div class="form-group">
            <label for="discoveredAt" class="form-label">Fecha del descubrimiento *</label>
            <input
                id="discoveredAt"
                v-model="form.discoveredAt"
                type="datetime-local"
                class="form-input"
                :class="{ error: errors.discoveredAt }"
                :max="maxDate"
                @input="clearFieldError('discoveredAt')"
                @blur="validateField('discoveredAt')"
            />
            <span v-if="errors.discoveredAt" class="form-error">{{ errors.discoveredAt }}</span>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="latitude" class="form-label">Latitud *</label>
              <input
                  id="latitude"
                  v-model.number="form.latitude"
                  type="number"
                  step="0.000001"
                  class="form-input"
                  :class="{ error: errors.latitude }"
                  placeholder="43.362275"
                  min="-90"
                  max="90"
                  @input="clearFieldError('latitude')"
                  @blur="validateField('latitude')"
              />
              <span v-if="errors.latitude" class="form-error">{{ errors.latitude }}</span>
            </div>

            <div class="form-group">
              <label for="longitude" class="form-label">Longitud *</label>
              <input
                  id="longitude"
                  v-model.number="form.longitude"
                  type="number"
                  step="0.000001"
                  class="form-input"
                  :class="{ error: errors.longitude }"
                  placeholder="-8.411540"
                  min="-180"
                  max="180"
                  @input="clearFieldError('longitude')"
                  @blur="validateField('longitude')"
              />
              <span v-if="errors.longitude" class="form-error">{{ errors.longitude }}</span>
            </div>
          </div>

          <div class="location-helper">
            <button type="button" class="btn-location" @click="getCurrentLocation" :disabled="isGettingLocation">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="icon-location">
                <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
              </svg>
              {{ isGettingLocation ? 'Obteniendo ubicación...' : 'Usar mi ubicación actual' }}
            </button>
          </div>

          <div class="form-group">
            <label for="description" class="form-label">Descripción del hallazgo *</label>

            <!-- Botón generador de IA -->
            <div v-if="canShowIAButton" class="ia-suggestion-box">
              <div class="ia-suggestion-content">
                <img :src="logoDigreport" alt="IA" class="ia-logo" />
                <span class="ia-text">
                  ¿Quieres que la IA analice las imágenes y sugiera una descripción?
                </span>
              </div>
              <button
                  type="button"
                  :disabled="iaState.isGenerating"
                  class="btn-generar-ia"
                  @click="handleGenerateIA"
              >
                <span v-if="!iaState.isGenerating"> Generar con IA</span>
                <span v-else class="generating-content">
                  <span class="spinner"></span>
                  Analizando...
                </span>
              </button>
            </div>

            <!-- Badge de IA generada -->
            <div v-if="iaState.isGenerated" class="ia-badge-generated">
              <div class="ia-badge-content">
                <img :src="logoDigreport" alt="IA" class="ia-badge-logo" />
                <span class="ia-badge-text">
                  Descripción generada por IA - Revisa y edita antes de guardar
                </span>
              </div>
              <button
                  type="button"
                  class="ia-badge-discard"
                  title="Descartar y escribir manualmente"
                  @click="confirmDiscardIA"
              >
                ✕
              </button>
            </div>

            <textarea
                id="description"
                v-model="form.description"
                class="form-textarea"
                :class="{
                error: errors.description,
                'textarea-ia-generated': iaState.isGenerated
              }"
                placeholder="Describe el hallazgo con el mayor detalle posible: tipo de objeto, material, dimensiones aproximadas, contexto del descubrimiento..."
                rows="6"
                maxlength="1000"
                @input="clearFieldError('description')"
                @blur="validateField('description')"
            ></textarea>
            <div class="char-counter">
              {{ form.description.length }} / 1000 caracteres
            </div>
            <span v-if="errors.description" class="form-error">{{ errors.description }}</span>

            <!-- Análisis detallado de IA -->
            <details v-if="iaState.analysis" class="ia-analysis-details">
              <summary>📊 Ver análisis detallado de la IA</summary>
              <div class="ia-analysis-content">
                <div class="analysis-grid">
                  <div class="analysis-item">
                    <strong>Tipo probable:</strong>
                    <span>{{ iaState.analysis.tipo_probable }}</span>
                  </div>
                  <div class="analysis-item">
                    <strong>Material estimado:</strong>
                    <span>{{ iaState.analysis.material_estimado }}</span>
                  </div>
                  <div class="analysis-item">
                    <strong>Período estimado:</strong>
                    <span>{{ iaState.analysis.periodo_estimado }}</span>
                  </div>
                  <div class="analysis-item">
                    <strong>Nivel de confianza:</strong>
                    <div class="confidence-bar">
                      <div
                          class="confidence-fill"
                          :style="{ width: confidencePercentage + '%' }"
                      />
                      <span class="confidence-text">
                        {{ confidencePercentage }}%
                      </span>
                    </div>
                  </div>
                </div>

                <div v-if="iaState.analysis.caracteristicas_clave?.length" class="caracteristicas-box">
                  <strong>Características detectadas:</strong>
                  <ul>
                    <li v-for="(car, idx) in iaState.analysis.caracteristicas_clave" :key="idx">
                      {{ car }}
                    </li>
                  </ul>
                </div>

                <div v-if="iaState.analysis.advertencias" class="advertencias-box">
                  <strong>⚠️ Consideraciones importantes:</strong>
                  <p>{{ iaState.analysis.advertencias }}</p>
                </div>
              </div>
            </details>
          </div>
        </div>

        <div class="info-box">
          <p><strong> Importante:</strong> Asegúrate de proporcionar información precisa y detallada. Esto ayudará a los arqueólogos a evaluar correctamente el hallazgo.</p>
        </div>

        <div v-if="successMessage" class="alert alert-success">
          {{ successMessage }}
        </div>

        <div v-if="error" class="alert alert-error">
          {{ error }}
        </div>

        <div class="form-actions">
          <button
              type="button"
              class="btn btn-outline"
              @click="router.push('/')"
              :disabled="isLoading"
          >
            Cancelar
          </button>
          <button
              type="submit"
              class="btn btn-primary btn-large"
              :disabled="isLoading || uploadedImages.length === 0"
          >
            <span v-if="isLoading" class="loading"></span>
            {{ isLoading ? 'Registrando...' : 'Registrar hallazgo' }}
          </button>
        </div>
      </form>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { apiClient } from '../services/api'
import ConfirmationModal from './common/ConfirmationModal.vue'
import logoDigreport from '../assets/logodigreport.png'
import './RegisterFind.css'

const router = useRouter()
const authStore = useAuthStore()

const isLoading = ref(false)
const isGettingLocation = ref(false)
const error = ref<string | null>(null)
const successMessage = ref<string | null>(null)

// ===== ESTADO DE IMÁGENES =====
interface UploadedImage {
  file: File
  preview: string
}

const uploadedImages = ref<UploadedImage[]>([])
const fileInput = ref<HTMLInputElement | null>(null)

// ===== ESTADO DE IA =====
interface IAAnalysis {
  tipo_probable: string
  material_estimado: string
  periodo_estimado: string
  confianza: number
  caracteristicas_clave: string[]
  descripcion: string
  advertencias?: string
}

interface IAState {
  isGenerating: boolean
  isGenerated: boolean
  analysis: IAAnalysis | null
}

const iaState = reactive<IAState>({
  isGenerating: false,
  isGenerated: false,
  analysis: null
})

// ===== ESTADO DEL MODAL =====
const confirmModal = ref({
  show: false,
  title: '',
  message: '',
  details: '',
  confirmText: 'Aceptar',
  cancelText: 'Cancelar',
  variant: 'warning' as 'warning' | 'danger' | 'info' | 'success',
  loading: false,
  action: null as string | null
})

const confidencePercentage = computed(() =>
    iaState.analysis ? Math.round(iaState.analysis.confianza * 100) : 0
)

const canShowIAButton = computed(() =>
    uploadedImages.value.length > 0 && !form.description && !iaState.isGenerated
)

// ===== FORMULARIO =====
interface FindForm {
  discoveredAt: string
  latitude: number | null
  longitude: number | null
  description: string
  ccaa: string
}

const form = reactive<FindForm>({
  discoveredAt: '',
  latitude: null,
  longitude: null,
  description: '',
  ccaa: ''
})

const errors = reactive({
  discoveredAt: '',
  latitude: '',
  longitude: '',
  description: ''
})

const maxDate = computed(() => {
  const now = new Date()
  return now.toISOString().slice(0, 16)
})

// ===== MANEJO DE IMÁGENES =====
const handleImageSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files

  if (!files) return

  const newImages = Array.from(files).slice(0, 10 - uploadedImages.value.length)

  newImages.forEach(file => {
    if (file.size > 10 * 1024 * 1024) {
      error.value = `La imagen ${file.name} excede el tamaño máximo de 10MB`
      return
    }

    if (!file.type.startsWith('image/')) {
      error.value = `${file.name} no es una imagen válida`
      return
    }

    const preview = URL.createObjectURL(file)
    uploadedImages.value.push({ file, preview })
  })

  if (newImages.length > 0 && !error.value) {
    error.value = null
  }

  if (target) target.value = ''
}

const removeImage = (index: number) => {
  const img = uploadedImages.value[index]
  URL.revokeObjectURL(img.preview)
  uploadedImages.value.splice(index, 1)

  if (uploadedImages.value.length === 0) {
    iaState.isGenerated = false
    iaState.analysis = null
    form.description = ''
  }
}

// ===== GENERAR DESCRIPCIÓN CON IA =====
const handleGenerateIA = async () => {
  try {
    iaState.isGenerating = true
    error.value = null

    const formData = new FormData()
    uploadedImages.value.forEach(img => {
      formData.append('imagenes', img.file)
    })

    const response = await apiClient.post('/api/finds/analyze-with-ai', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    iaState.analysis = response.data
    form.description = response.data.descripcion
    iaState.isGenerated = true

  } catch (err: any) {
    error.value = err.response?.data?.message || 'Error al generar descripción con IA'
    console.error('Error IA:', err)
  } finally {
    iaState.isGenerating = false
  }
}

const confirmDiscardIA = () => {
  confirmModal.value = {
    show: true,
    title: 'Descartar descripción generada por IA',
    message: '¿Estás seguro de que deseas descartar la descripción generada por la IA?',
    details: 'Tendrás que escribir la descripción manualmente desde cero.',
    confirmText: 'Descartar',
    cancelText: 'Cancelar',
    variant: 'warning',
    loading: false,
    action: 'discard-ia'
  }
}

const handleModalConfirm = () => {
  if (confirmModal.value.action === 'discard-ia') {
    form.description = ''
    iaState.isGenerated = false
    iaState.analysis = null
  }
  confirmModal.value.show = false
}

const handleModalCancel = () => {
  confirmModal.value.show = false
  confirmModal.value.action = null
}

// ===== GEOLOCALIZACIÓN =====
const getCurrentLocation = () => {
  if (!navigator.geolocation) {
    error.value = 'Tu navegador no soporta geolocalización'
    return
  }

  isGettingLocation.value = true
  error.value = null

  navigator.geolocation.getCurrentPosition(
      async (position) => {
        form.latitude = parseFloat(position.coords.latitude.toFixed(6))
        form.longitude = parseFloat(position.coords.longitude.toFixed(6))
        clearFieldError('latitude')
        clearFieldError('longitude')

        await fetchCcaaFromCoordinates(form.latitude, form.longitude)
        isGettingLocation.value = false
      },
      (err) => {
        error.value = 'No se pudo obtener la ubicación. Verifica los permisos del navegador.'
        isGettingLocation.value = false
      }
  )
}

const fetchCcaaFromCoordinates = async (lat: number, lon: number) => {
  try {
    const response = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&zoom=5&addressdetails=1`,
        { headers: { 'Accept-Language': 'es' } }
    )

    const data = await response.json()

    if (data.address && data.address.state) {
      form.ccaa = data.address.state
    } else if (data.address && data.address.province) {
      form.ccaa = data.address.province
    }
  } catch (err) {
    console.error('Error al obtener CCAA:', err)
  }
}

// ===== VALIDACIONES =====
const validateField = (field: keyof typeof errors) => {
  switch(field) {
    case 'discoveredAt':
      if (!form.discoveredAt) {
        errors.discoveredAt = 'La fecha de descubrimiento es obligatoria'
      } else if (new Date(form.discoveredAt) > new Date()) {
        errors.discoveredAt = 'La fecha no puede ser futura'
      }
      break

    case 'latitude':
      if (form.latitude === null) {
        errors.latitude = 'La latitud es obligatoria'
      } else if (form.latitude < -90 || form.latitude > 90) {
        errors.latitude = 'La latitud debe estar entre -90 y 90'
      }
      break

    case 'longitude':
      if (form.longitude === null) {
        errors.longitude = 'La longitud es obligatoria'
      } else if (form.longitude < -180 || form.longitude > 180) {
        errors.longitude = 'La longitud debe estar entre -180 y 180'
      }
      break

    case 'description':
      if (!form.description.trim()) {
        errors.description = 'La descripción es obligatoria'
      } else if (form.description.trim().length < 10) {
        errors.description = 'La descripción debe tener al menos 10 caracteres'
      } else if (form.description.length > 1000) {
        errors.description = 'La descripción no puede exceder 1000 caracteres'
      }
      break
  }
}

const validateForm = () => {
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = ''
  })

  let isValid = true

  if (!form.discoveredAt) {
    errors.discoveredAt = 'La fecha de descubrimiento es obligatoria'
    isValid = false
  } else if (new Date(form.discoveredAt) > new Date()) {
    errors.discoveredAt = 'La fecha no puede ser futura'
    isValid = false
  }

  if (form.latitude === null) {
    errors.latitude = 'La latitud es obligatoria'
    isValid = false
  } else if (form.latitude < -90 || form.latitude > 90) {
    errors.latitude = 'La latitud debe estar entre -90 y 90'
    isValid = false
  }

  if (form.longitude === null) {
    errors.longitude = 'La longitud es obligatoria'
    isValid = false
  } else if (form.longitude < -180 || form.longitude > 180) {
    errors.longitude = 'La longitud debe estar entre -180 y 180'
    isValid = false
  }

  if (!form.description.trim()) {
    errors.description = 'La descripción es obligatoria'
    isValid = false
  } else if (form.description.trim().length < 10) {
    errors.description = 'La descripción debe tener al menos 10 caracteres'
    isValid = false
  }

  if (uploadedImages.value.length === 0) {
    error.value = 'Debes subir al menos una imagen del hallazgo'
    isValid = false
  }

  return isValid
}

// ===== SUBMIT =====
const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  try {
    isLoading.value = true
    error.value = null
    successMessage.value = null

    if (!form.ccaa && form.latitude && form.longitude) {
      await fetchCcaaFromCoordinates(form.latitude, form.longitude)
    }

    const formData = new FormData()

    const findData = {
      discoveredAt: form.discoveredAt,
      latitude: form.latitude,
      longitude: form.longitude,
      description: form.description.trim(),
      ccaa: form.ccaa || 'Desconocida',
      descriptionGeneratedByAi: iaState.isGenerated,
      aiAnalysisJson: iaState.isGenerated ? JSON.stringify(iaState.analysis) : null
    }

    formData.append('data', new Blob([JSON.stringify(findData)], { type: 'application/json' }))

    uploadedImages.value.forEach(img => {
      formData.append('images', img.file)
    })

    await apiClient.post('/api/finds', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    successMessage.value = '✓ Hallazgo registrado correctamente'

    setTimeout(() => {
      router.push('/my-finds')
    }, 2000)

  } catch (err: any) {
    error.value = err.response?.data?.message || 'Error al registrar el hallazgo'
  } finally {
    isLoading.value = false
  }
}

const clearFieldError = (field: keyof typeof errors) => {
  errors[field] = ''
  error.value = null
}

onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  if (!authStore.isUser) {
    error.value = 'Solo los ciudadanos pueden registrar hallazgos'
    setTimeout(() => router.push('/'), 3000)
  }
})
</script>