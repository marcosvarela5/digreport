<template>
  <div class="user-menu" @mouseenter="showMenu = true" @mouseleave="showMenu = false">
    <button class="user-button">
      <svg class="user-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
      </svg>
      <span class="user-name">{{ userName }}</span>
      <svg class="dropdown-arrow" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
        <path d="M7 10l5 5 5-5z"/>
      </svg>
    </button>

    <transition name="dropdown">
      <div v-if="showMenu" class="dropdown-menu">
        <div class="menu-header">
          <p class="user-email">{{ userEmail }}</p>
          <span class="user-role" :class="userRoleClass">{{ userRoleText }}</span>
        </div>

        <div class="menu-divider"></div>

        <router-link to="/profile" class="menu-item">
          <svg class="menu-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 3c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3zm0 14.2c-2.5 0-4.71-1.28-6-3.22.03-1.99 4-3.08 6-3.08 1.99 0 5.97 1.09 6 3.08-1.29 1.94-3.5 3.22-6 3.22z"/>
          </svg>
          <span>Configuración</span>
        </router-link>

        <router-link to="/my-finds" class="menu-item" v-if="authStore.isUser">
          <svg class="menu-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
          </svg>
          <span>Mis reportes</span>
        </router-link>

        <router-link to="/validate-finds" class="menu-item" v-if="authStore.isArchaeologist">
          <svg class="menu-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
          </svg>
          <span>Validación</span>
          <span v-if="pendingCount > 0" class="badge-count">{{ pendingCount }}</span>
        </router-link>

        <router-link to="/authority" class="menu-item menu-item-authority" v-if="authStore.isAuthority">
          <svg class="menu-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>
          </svg>
          <span>Administración de autoridades</span>
        </router-link>

        <div class="menu-divider"></div>

        <button @click="handleLogout" class="menu-item logout">
          <svg class="menu-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
          </svg>
          <span>Cerrar sesión</span>
        </button>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { apiClient } from '../services/api'
import './UserMenu.css'

const emit = defineEmits(['logout'])

const authStore = useAuthStore()
const showMenu = ref(false)
const pendingCount = ref(0)

const userName = computed(() => {
  if (!authStore.user) return 'Usuario'
  return `${authStore.user.name} ${authStore.user.surname1}`
})

const userEmail = computed(() => authStore.user?.email || '')

const userRoleText = computed(() => {
  switch (authStore.user?.role) {
    case 'USER':
      return 'Ciudadano'
    case 'ARCHAEOLOGIST':
      return 'Arqueólogo'
    case 'AUTHORITY':
      return 'Autoridad'
    default:
      return 'Usuario'
  }
})

const userRoleClass = computed(() => {
  switch (authStore.user?.role) {
    case 'ARCHAEOLOGIST':
      return 'archaeologist'
    case 'AUTHORITY':
      return 'authority'
    default:
      return ''
  }
})

const handleLogout = () => {
  showMenu.value = false
  emit('logout')
}

const loadPendingCount = async () => {
  if (authStore.isArchaeologist || authStore.isAuthority) {
    try {
      const response = await apiClient.get('/api/finds/pending/count')
      pendingCount.value = response.data
    } catch (err) {
      console.error('Error al cargar contador de pendientes:', err)
    }
  }
}

onMounted(() => {
  loadPendingCount()
})
</script>