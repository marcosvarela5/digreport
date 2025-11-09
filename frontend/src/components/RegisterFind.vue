<template>
  <div class="register-find-container">
    <router-link to="/" class="back-link">⬅ Volver al inicio</router-link>
    <div class="register-find-card">
      <div class="register-find-header">
        <h1 class="register-find-title">Registrar Hallazgo</h1>
        <p class="register-find-subtitle">Ayuda a preservar nuestro patrimonio histórico</p>
      </div>

      <form @submit.prevent="handleSubmit" class="register-find-form">
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
            <textarea
                id="description"
                v-model="form.description"
                class="form-textarea"
                :class="{ error: errors.description }"
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
          </div>
        </div>

        <div class="info-box">
          <p><strong>📌 Importante:</strong> Asegúrate de proporcionar información precisa y detallada. Esto ayudará a los arqueólogos a evaluar correctamente el hallazgo.</p>
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
              :disabled="isLoading"
          >
            <span v-if="isLoading" class="loading"></span>
            {{ isLoading ? 'Registrando...' : 'Registrar Hallazgo' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { apiClient } from '../services/api'
import './RegisterFind.css'

const router = useRouter()
const authStore = useAuthStore()

const isLoading = ref(false)
const isGettingLocation = ref(false)
const error = ref<string | null>(null)
const successMessage = ref<string | null>(null)

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
        {
          headers: {
            'Accept-Language': 'es'
          }
        }
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

  return isValid
}

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

    const payload = {
      discoveredAt: form.discoveredAt,
      latitude: form.latitude,
      longitude: form.longitude,
      description: form.description.trim(),
      ccaa: form.ccaa || 'Desconocida'
    }

    await apiClient.post('/api/finds', payload)

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