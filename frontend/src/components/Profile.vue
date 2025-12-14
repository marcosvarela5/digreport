<template>
  <div class="profile-container">
    <router-link to="/" class="back-link">⬅ Volver al inicio</router-link>
    <div class="profile-card">
      <div class="profile-header">
        <h1 class="profile-title">Mi perfil</h1>
        <p class="profile-subtitle">Actualiza tu información personal</p>
      </div>

      <form @submit.prevent="showConfirmation" class="profile-form">
        <div class="form-section">
          <h3 class="section-title">Datos personales</h3>

          <div class="form-row">
            <div class="form-group">
              <label for="name" class="form-label">Nombre</label>
              <input
                  id="name"
                  v-model="form.name"
                  type="text"
                  class="form-input"
                  :class="{ error: errors.name }"
                  placeholder="Tu nombre"
                  maxlength="50"
                  @input="clearFieldError('name')"
                  @blur="validateField('name')"
              />
              <span v-if="errors.name" class="form-error">{{ errors.name }}</span>
            </div>

            <div class="form-group">
              <label for="surname1" class="form-label">Primer apellido</label>
              <input
                  id="surname1"
                  v-model="form.surname1"
                  type="text"
                  class="form-input"
                  :class="{ error: errors.surname1 }"
                  placeholder="Primer apellido"
                  maxlength="50"
                  @input="clearFieldError('surname1')"
                  @blur="validateField('surname1')"
              />
              <span v-if="errors.surname1" class="form-error">{{ errors.surname1 }}</span>
            </div>
          </div>

          <div class="form-group">
            <label for="surname2" class="form-label">Segundo apellido</label>
            <input
                id="surname2"
                v-model="form.surname2"
                type="text"
                class="form-input"
                placeholder="Segundo apellido (opcional)"
                maxlength="50"
            />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="dni" class="form-label">DNI</label>
              <input
                  id="dni"
                  v-model="form.dni"
                  type="text"
                  class="form-input"
                  :class="{ error: errors.dni }"
                  placeholder="12345678A"
                  maxlength="9"
                  @input="clearFieldError('dni')"
                  @blur="validateField('dni')"
              />
              <span v-if="errors.dni" class="form-error">{{ errors.dni }}</span>
            </div>

            <div class="form-group">
              <label for="mobile" class="form-label">Teléfono móvil</label>
              <input
                  id="mobile"
                  v-model="form.mobile"
                  type="tel"
                  class="form-input"
                  :class="{ error: errors.mobile }"
                  placeholder="666777888"
                  maxlength="15"
                  @input="clearFieldError('mobile')"
                  @blur="validateField('mobile')"
              />
              <span v-if="errors.mobile" class="form-error">{{ errors.mobile }}</span>
            </div>
          </div>

          <div class="form-group">
            <label for="ccaa" class="form-label">Comunidad Autónoma</label>
            <select
                id="ccaa"
                v-model="form.ccaa"
                class="form-input"
                :class="{ error: errors.ccaa }"
                @change="clearFieldError('ccaa')"
            >
              <option value="">Selecciona tu comunidad autónoma</option>
              <option value="Andalucía">Andalucía</option>
              <option value="Aragón">Aragón</option>
              <option value="Asturias">Asturias</option>
              <option value="Baleares">Baleares</option>
              <option value="Canarias">Canarias</option>
              <option value="Cantabria">Cantabria</option>
              <option value="Castilla-La Mancha">Castilla-La Mancha</option>
              <option value="Castilla y León">Castilla y León</option>
              <option value="Cataluña">Cataluña</option>
              <option value="Comunidad Valenciana">Comunidad Valenciana</option>
              <option value="Extremadura">Extremadura</option>
              <option value="Galicia">Galicia</option>
              <option value="La Rioja">La Rioja</option>
              <option value="Madrid">Madrid</option>
              <option value="Murcia">Murcia</option>
              <option value="Navarra">Navarra</option>
              <option value="País Vasco">País Vasco</option>
              <option value="Ceuta">Ceuta</option>
              <option value="Melilla">Melilla</option>
            </select>
            <span v-if="errors.ccaa" class="form-error">{{ errors.ccaa }}</span>
          </div>
        </div>

        <div class="form-section">
          <h3 class="section-title">Datos de acceso</h3>

          <div class="form-group">
            <label for="email" class="form-label">Email</label>
            <input
                id="email"
                v-model="form.email"
                type="email"
                class="form-input"
                :class="{ error: errors.email }"
                placeholder="tu@email.com"
                maxlength="100"
                @input="clearFieldError('email')"
                @blur="validateField('email')"
            />
            <span v-if="errors.email" class="form-error">{{ errors.email }}</span>
          </div>

          <div class="info-box">
            <p>💡 Para cambiar tu contraseña, usa la opción "Olvidé mi contraseña" en el login.</p>
          </div>
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
              :disabled="isLoading || !hasChanges"
          >
            <span v-if="isLoading" class="loading"></span>
            {{ isLoading ? 'Guardando...' : 'Guardar Cambios' }}
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
import './Profile.css'

const router = useRouter()
const authStore = useAuthStore()

const isLoading = ref(false)
const error = ref<string | null>(null)
const successMessage = ref<string | null>(null)

interface ProfileForm {
  name: string
  surname1: string
  surname2: string
  email: string
  dni: string
  mobile: string
  ccaa: string
}

const form = reactive<ProfileForm>({
  name: '',
  surname1: '',
  surname2: '',
  email: '',
  dni: '',
  mobile: '',
  ccaa: ''
})

const originalForm = reactive<ProfileForm>({
  name: '',
  surname1: '',
  surname2: '',
  email: '',
  dni: '',
  mobile: '',
  ccaa: ''
})

const errors = reactive({
  name: '',
  surname1: '',
  email: '',
  dni: '',
  mobile: '',
  ccaa: ''
})

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

const hasChanges = computed(() => {
  return Object.keys(form).some(key => {
    const formKey = key as keyof ProfileForm
    return form[formKey] !== originalForm[formKey]
  })
})

const validateField = (field: keyof typeof errors) => {
  if (form[field] === originalForm[field]) {
    errors[field] = ''
    return
  }

  switch(field) {
    case 'name':
      if (form.name && form.name.trim().length < 2) {
        errors.name = 'El nombre debe tener al menos 2 caracteres'
      }
      break

    case 'surname1':
      if (form.surname1 && form.surname1.trim().length < 2) {
        errors.surname1 = 'El primer apellido debe tener al menos 2 caracteres'
      }
      break

    case 'email':
      if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
        errors.email = 'El formato del email no es válido'
      }
      break

    case 'dni':
      if (form.dni && !/^\d{8}[A-Za-z]$/.test(form.dni)) {
        errors.dni = 'El formato del DNI no es válido (8 dígitos + letra)'
      }
      break

    case 'mobile':
      if (form.mobile && !/^[679]\d{8}$/.test(form.mobile)) {
        errors.mobile = 'El formato del móvil no es válido'
      }
      break
  }
}

const validateForm = () => {
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = ''
  })

  let isValid = true

  if (form.name !== originalForm.name && form.name.trim().length < 2) {
    errors.name = 'El nombre debe tener al menos 2 caracteres'
    isValid = false
  }

  if (form.surname1 !== originalForm.surname1 && form.surname1.trim().length < 2) {
    errors.surname1 = 'El primer apellido debe tener al menos 2 caracteres'
    isValid = false
  }

  if (form.email !== originalForm.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'El formato del email no es válido'
    isValid = false
  }

  if (form.dni !== originalForm.dni && !/^\d{8}[A-Za-z]$/.test(form.dni)) {
    errors.dni = 'El formato del DNI no es válido (8 dígitos + letra)'
    isValid = false
  }

  if (form.mobile !== originalForm.mobile && !/^[679]\d{8}$/.test(form.mobile)) {
    errors.mobile = 'El formato del móvil no es válido'
    isValid = false
  }

  return isValid
}

const showConfirmation = () => {
  if (!validateForm()) {
    return
  }

  if (!hasChanges.value) {
    successMessage.value = 'No hay cambios para guardar'
    return
  }

  const changedFields: string[] = []
  Object.keys(form).forEach(key => {
    const formKey = key as keyof ProfileForm
    if (form[formKey] !== originalForm[formKey]) {
      const fieldNames: Record<string, string> = {
        name: 'Nombre',
        surname1: 'Primer apellido',
        surname2: 'Segundo apellido',
        email: 'Email',
        dni: 'DNI',
        mobile: 'Teléfono móvil',
        ccaa: 'Comunidad Autónoma'
      }
      changedFields.push(fieldNames[formKey])
    }
  })

  confirmModal.value = {
    show: true,
    title: 'Confirmar cambios',
    message: '¿Deseas guardar los cambios realizados en tu perfil?',
    details: `Campos modificados: ${changedFields.join(', ')}`,
    confirmText: 'Guardar cambios',
    cancelText: 'Cancelar',
    variant: 'info',
    loading: false
  }
}

const handleModalConfirm = async () => {
  confirmModal.value.loading = true

  try {
    error.value = null
    successMessage.value = null

    const updateData: Partial<ProfileForm> = {}

    Object.keys(form).forEach(key => {
      const formKey = key as keyof ProfileForm
      if (form[formKey] !== originalForm[formKey]) {
        updateData[formKey] = form[formKey]
      }
    })

    if (updateData.email) {
      updateData.email = updateData.email.trim().toLowerCase()
    }
    if (updateData.dni) {
      updateData.dni = updateData.dni.trim().toUpperCase()
    }

    await apiClient.put('/api/profile', updateData)

    await authStore.checkAuthStatus()

    Object.assign(originalForm, form)

    confirmModal.value.show = false
    successMessage.value = '✓ Perfil actualizado correctamente'

    setTimeout(() => {
      successMessage.value = null
    }, 3000)

  } catch (err: any) {
    confirmModal.value.show = false
    error.value = err.response?.data?.message || 'Error al actualizar el perfil'
  } finally {
    confirmModal.value.loading = false
  }
}

const handleModalCancel = () => {
  confirmModal.value.show = false
}

const clearFieldError = (field: keyof typeof errors) => {
  errors[field] = ''
}

const loadUserData = () => {
  if (authStore.user) {
    form.name = authStore.user.name
    form.surname1 = authStore.user.surname1
    form.surname2 = authStore.user.surname2 || ''
    form.email = authStore.user.email
    form.dni = authStore.user.dni
    form.mobile = authStore.user.mobile
    form.ccaa = authStore.user.ccaa

    Object.assign(originalForm, form)
  }
}

onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  loadUserData()
})
</script>