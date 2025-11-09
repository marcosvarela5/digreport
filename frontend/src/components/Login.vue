<template>
  <div class="login-container">
    <router-link to="/" class="back-link">⬅ Volver</router-link>
    <div class="login-card">
      <div class="login-header">
        <h1 class="login-title">DIGREPORT</h1>
        <p class="login-subtitle">Iniciar sesión</p>
      </div>

      <form @submit.prevent="handleSubmit" class="login-form">
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
              @input="clearFieldError('email')"
              @blur="validateField('email')"
          />
          <span v-if="errors.email" class="form-error">{{ errors.email }}</span>
        </div>

        <div class="form-group">
          <label for="password" class="form-label">Contraseña *</label>
          <div class="password-input-wrapper">
            <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                :class="{ error: errors.password }"
                placeholder="Tu contraseña"
                maxlength="50"
                @input="clearFieldError('password')"
                @blur="validateField('password')"
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

        <div class="form-options">
          <label class="checkbox-wrapper">
            <input
                v-model="form.rememberMe"
                type="checkbox"
                class="checkbox-input"
            />
            <span class="checkbox-label">Recordarme</span>
          </label>
          <router-link to="/forgot-password" class="link">
            ¿Olvidaste tu contraseña?
          </router-link>
        </div>

        <div v-if="successMessage" class="alert alert-success">
          {{ successMessage }}
        </div>

        <div v-if="authStore.error" class="alert alert-error">
          {{ authStore.error }}
        </div>

        <button
            type="submit"
            class="btn btn-primary btn-large login-submit"
            :disabled="authStore.isLoading"
        >
          <span v-if="authStore.isLoading" class="loading"></span>
          {{ authStore.isLoading ? 'Iniciando sesión...' : 'Iniciar Sesión' }}
        </button>
      </form>

      <div class="login-footer">
        <p class="register-link">
          ¿No tienes cuenta?
          <router-link to="/register" class="link">Regístrate aquí</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import type { LoginRequest } from '../stores/auth'
import './Login.css'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const showPassword = ref(false)
const successMessage = ref('')

const form = reactive<LoginRequest & { rememberMe: boolean }>({
  email: '',
  password: '',
  rememberMe: false
})

const errors = reactive({
  email: '',
  password: ''
})

const validateField = (field: keyof typeof errors) => {
  if (field === 'email') {
    if (!form.email) {
      errors.email = 'El email es obligatorio'
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      errors.email = 'El formato del email no es válido'
    }
  } else if (field === 'password') {
    if (!form.password) {
      errors.password = 'La contraseña es obligatoria'
    } else if (form.password.length < 6) {
      errors.password = 'La contraseña debe tener al menos 6 caracteres'
    }
  }
}

const validateForm = () => {
  // Reset errors
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = ''
  })

  let isValid = true

  if (!form.email) {
    errors.email = 'El email es obligatorio'
    isValid = false
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'El formato del email no es válido'
    isValid = false
  }

  if (!form.password) {
    errors.password = 'La contraseña es obligatoria'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = 'La contraseña debe tener al menos 6 caracteres'
    isValid = false
  }

  return isValid
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }

  const success = await authStore.login({
    email: form.email.trim().toLowerCase(),
    password: form.password
  })

  if (success) {
    const redirect = route.query.redirect as string
    await router.push(redirect || '/')
  }
}

const clearFieldError = (field: keyof typeof errors) => {
  errors[field] = ''
  authStore.clearError()
}

// Mostrar mensaje si viene desde registro
onMounted(() => {
  if (authStore.isAuthenticated) {
    router.push('/')
  }

  if (route.query.registered === 'true') {
    successMessage.value = '¡Registro completado! Por favor, inicia sesión con tus credenciales.'
  }
})
</script>