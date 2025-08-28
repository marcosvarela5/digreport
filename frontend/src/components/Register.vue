<template>
  <div class="register-container">
    <router-link to="/" class="back-link">⬅ Volver</router-link>
    <div class="register-card">
      <div class="register-header">
        <h1 class="register-title">DIGREPORT</h1>
        <p class="register-subtitle">Registro</p>
      </div>

      <form @submit.prevent="handleSubmit" class="register-form">
        <!-- Datos personales -->
        <div class="form-section">
          <h3 class="section-title">Datos Personales</h3>

          <div class="form-row">
            <div class="form-group">
              <label for="name" class="form-label">Nombre *</label>
              <input
                  id="name"
                  v-model="form.name"
                  type="text"
                  class="form-input"
                  :class="{ error: errors.name }"
                  placeholder="Tu nombre"
                  maxlength="100"
                  required
                  @input="clearFieldError('name')"
              />
              <span v-if="errors.name" class="form-error">{{ errors.name }}</span>
            </div>

            <div class="form-group">
              <label for="surname1" class="form-label">Primer Apellido *</label>
              <input
                  id="surname1"
                  v-model="form.surname1"
                  type="text"
                  class="form-input"
                  :class="{ error: errors.surname1 }"
                  placeholder="Primer apellido"
                  maxlength="100"
                  required
                  @input="clearFieldError('surname1')"
              />
              <span v-if="errors.surname1" class="form-error">{{ errors.surname1 }}</span>
            </div>
          </div>

          <div class="form-group">
            <label for="surname2" class="form-label">Segundo Apellido</label>
            <input
                id="surname2"
                v-model="form.surname2"
                type="text"
                class="form-input"
                placeholder="Segundo apellido (opcional)"
                maxlength="100"
            />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="dni" class="form-label">DNI *</label>
              <input
                  id="dni"
                  v-model="form.dni"
                  type="text"
                  class="form-input"
                  :class="{ error: errors.dni }"
                  placeholder="12345678A"
                  maxlength="20"
                  required
                  @input="clearFieldError('dni')"
              />
              <span v-if="errors.dni" class="form-error">{{ errors.dni }}</span>
            </div>

            <div class="form-group">
              <label for="mobile" class="form-label">Teléfono Móvil *</label>
              <input
                  id="mobile"
                  v-model="form.mobile"
                  type="tel"
                  class="form-input"
                  :class="{ error: errors.mobile }"
                  placeholder="666777888"
                  maxlength="20"
                  required
                  @input="clearFieldError('mobile')"
              />
              <span v-if="errors.mobile" class="form-error">{{ errors.mobile }}</span>
            </div>
          </div>

          <div class="form-group">
            <label for="ccaa" class="form-label">Comunidad Autónoma *</label>
            <select
                id="ccaa"
                v-model="form.ccaa"
                class="form-input"
                :class="{ error: errors.ccaa }"
                required
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

        <!-- Datos de acceso -->
        <div class="form-section">
          <h3 class="section-title">Datos de Acceso</h3>

          <div class="form-row">
            <div class="form-group">
              <label for="email" class="form-label">Email *</label>
              <input
                  id="email"
                  v-model="form.email"
                  type="email"
                  class="form-input"
                  :class="{ error: errors.email }"
                  placeholder="tu@email.com"
                  maxlength="100"
                  required
                  @input="clearFieldError('email')"
              />
              <span v-if="errors.email" class="form-error">{{ errors.email }}</span>
            </div>

            <div class="form-group">
              <label for="confirmEmail" class="form-label">Confirmar Email *</label>
              <input
                  id="confirmEmail"
                  v-model="form.confirmEmail"
                  type="email"
                  class="form-input"
                  :class="{ error: errors.confirmEmail }"
                  placeholder="Repite tu email"
                  maxlength="100"
                  required
                  @input="clearFieldError('confirmEmail')"
              />
              <span v-if="errors.confirmEmail" class="form-error">{{ errors.confirmEmail }}</span>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="password" class="form-label">Contraseña *</label>
              <div class="password-input-wrapper">
                <input
                    id="password"
                    v-model="form.password"
                    :type="showPassword ? 'text' : 'password'"
                    class="form-input"
                    :class="{ error: errors.password }"
                    placeholder="Mínimo 6 caracteres"
                    maxlength="50"
                    required
                    @input="clearFieldError('password')"
                />
                <button
                    type="button"
                    class="password-toggle"
                    @click="showPassword = !showPassword"
                >
                  {{ showPassword ? '🙈' : '👁️' }}
                </button>
              </div>
              <span v-if="errors.password" class="form-error">{{ errors.password }}</span>
            </div>

            <div class="form-group">
              <label for="confirmPassword" class="form-label">Confirmar Contraseña *</label>
              <div class="password-input-wrapper">
                <input
                    id="confirmPassword"
                    v-model="form.confirmPassword"
                    :type="showConfirmPassword ? 'text' : 'password'"
                    class="form-input"
                    :class="{ error: errors.confirmPassword }"
                    placeholder="Repite tu contraseña"
                    maxlength="50"
                    required
                    @input="clearFieldError('confirmPassword')"
                />
                <button
                    type="button"
                    class="password-toggle"
                    @click="showConfirmPassword = !showConfirmPassword"
                >
                  {{ showConfirmPassword ? '🙈' : '👁️' }}
                </button>
              </div>
              <span v-if="errors.confirmPassword" class="form-error">{{ errors.confirmPassword }}</span>
            </div>
          </div>
        </div>

        <!-- Términos y condiciones -->
        <div class="form-section">
          <div class="form-group">
            <label class="checkbox-wrapper">
              <input
                  v-model="form.acceptTerms"
                  type="checkbox"
                  class="checkbox-input"
                  :class="{ error: errors.acceptTerms }"
                  required
              />
              <span class="checkbox-label">
                Acepto los
                <a href="#" class="link">términos y condiciones</a>
                y la
                <a href="#" class="link">política de privacidad</a> *
              </span>
            </label>
            <span v-if="errors.acceptTerms" class="form-error">{{ errors.acceptTerms }}</span>
          </div>

          <div class="form-group">
            <label class="checkbox-wrapper">
              <input
                  v-model="form.receiveNewsletter"
                  type="checkbox"
                  class="checkbox-input"
              />
              <span class="checkbox-label">
                Deseo recibir información sobre actualizaciones del sistema
              </span>
            </label>
          </div>
        </div>

        <div v-if="authStore.error" class="alert alert-error">
          {{ authStore.error }}
        </div>

        <button
            type="submit"
            class="btn btn-primary btn-large register-submit"
            :disabled="authStore.isLoading"
        >
          <span v-if="authStore.isLoading" class="loading"></span>
          {{ authStore.isLoading ? 'Registrando...' : 'Crear Cuenta' }}
        </button>
      </form>

      <div class="register-footer">
        <p class="login-link">
          ¿Ya tienes cuenta?
          <router-link to="/login" class="link">Inicia sesión aquí</router-link>
        </p>
      </div>
    </div>

    <div class="register-info">
      <div class="info-card">
        <h3>Únete a DIGREPORT</h3>
        <p>
          Forma parte de quienes protegen el patrimonio histórico español.
          Tu participación es clave para preservar nuestra herencia cultural.
        </p>
        <div class="benefits">
          <div class="benefit">
            <span class="benefit-icon">📍</span>
            <span>Registra hallazgos arqueológicos</span>
          </div>
          <div class="benefit">
            <span class="benefit-icon">🏆</span>
            <span>Obtén reconocimiento por tu contribución</span>
          </div>
          <div class="benefit">
            <span class="benefit-icon">🤝</span>
            <span>Colabora con profesionales</span>
          </div>
          <div class="benefit">
            <span class="benefit-icon">📚</span>
            <span>Aprende sobre patrimonio histórico</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import type { RegisterRequest } from '../stores/auth'
import './Register.css'

const router = useRouter()
const authStore = useAuthStore()

const showPassword = ref(false)
const showConfirmPassword = ref(false)

const form = reactive<RegisterRequest & { acceptTerms: boolean; receiveNewsletter: boolean }>({
  name: '',
  surname1: '',
  surname2: '',
  email: '',
  dni: '',
  mobile: '',
  password: '',
  ccaa: '',
  confirmEmail: '',
  confirmPassword: '',
  acceptTerms: false,
  receiveNewsletter: false
})

const errors = reactive({
  name: '',
  surname1: '',
  email: '',
  confirmEmail: '',
  dni: '',
  mobile: '',
  password: '',
  confirmPassword: '',
  ccaa: '',
  acceptTerms: ''
})

const validateForm = () => {
  // Reset errors
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = ''
  })

  let isValid = true

  // Validaciones básicas
  if (!form.name.trim()) {
    errors.name = 'El nombre es obligatorio'
    isValid = false
  } else if (form.name.trim().length < 2) {
    errors.name = 'El nombre debe tener al menos 2 caracteres'
    isValid = false
  }

  if (!form.surname1.trim()) {
    errors.surname1 = 'El primer apellido es obligatorio'
    isValid = false
  } else if (form.surname1.trim().length < 2) {
    errors.surname1 = 'El primer apellido debe tener al menos 2 caracteres'
    isValid = false
  }

  if (!form.email) {
    errors.email = 'El email es obligatorio'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'El formato del email no es válido'
    isValid = false
  }

  if (!form.confirmEmail) {
    errors.confirmEmail = 'Confirma tu email'
    isValid = false
  } else if (form.email !== form.confirmEmail) {
    errors.confirmEmail = 'Los emails no coinciden'
    isValid = false
  }

  if (!form.dni) {
    errors.dni = 'El DNI es obligatorio'
    isValid = false
  } else if (!/^\d{8}[A-Za-z]$/.test(form.dni)) {
    errors.dni = 'El formato del DNI no es válido (8 dígitos + letra)'
    isValid = false
  }

  if (!form.mobile) {
    errors.mobile = 'El teléfono móvil es obligatorio'
    isValid = false
  } else if (!/^[679]\d{8}$/.test(form.mobile)) {
    errors.mobile = 'El formato del móvil no es válido'
    isValid = false
  }

  if (!form.password) {
    errors.password = 'La contraseña es obligatoria'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = 'La contraseña debe tener al menos 6 caracteres'
    isValid = false
  }

  if (!form.confirmPassword) {
    errors.confirmPassword = 'Confirma tu contraseña'
    isValid = false
  } else if (form.password !== form.confirmPassword) {
    errors.confirmPassword = 'Las contraseñas no coinciden'
    isValid = false
  }

  if (!form.ccaa) {
    errors.ccaa = 'Selecciona tu comunidad autónoma'
    isValid = false
  }

  if (!form.acceptTerms) {
    errors.acceptTerms = 'Debes aceptar los términos y condiciones'
    isValid = false
  }

  return isValid
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  const success = await authStore.register({
    name: form.name.trim(),
    surname1: form.surname1.trim(),
    surname2: form.surname2?.trim() || '',
    email: form.email.trim().toLowerCase(),
    dni: form.dni.trim().toUpperCase(),
    mobile: form.mobile.trim(),
    password: form.password,
    ccaa: form.ccaa,
    confirmEmail: form.confirmEmail.trim().toLowerCase(),
    confirmPassword: form.confirmPassword
  })

  if (success) {
    await router.push('/login?registered=true')
  }
}

const clearFieldError = (field: keyof typeof errors) => {
  errors[field] = ''
  authStore.clearError()
}

// Redirigir si ya está autenticado
onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push('/dashboard')
  }
})
</script>