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
          <span>Actualizar Perfil</span>
        </router-link>

        <div class="menu-divider"></div>

        <button @click="handleLogout" class="menu-item logout">
          <svg class="menu-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
          </svg>
          <span>Cerrar Sesión</span>
        </button>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import './UserMenu.css'

const router = useRouter()
const authStore = useAuthStore()
const showMenu = ref(false)

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
  authStore.logout()
  showMenu.value = false
  router.push('/')
}
</script>