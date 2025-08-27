import { defineStore } from 'pinia'
import { ref, computed, readonly } from 'vue'
import { apiClient } from '../services/api'

export interface User {
    id: number
    name: string
    surname1: string
    surname2?: string
    email: string
    dni: string
    mobile: string
    role: 'USER' | 'ARCHAEOLOGIST' | 'AUTHORITY'
    registerDate: string
    ccaa: string
}

export interface LoginRequest {
    email: string
    password: string
}

export interface RegisterRequest {
    name: string
    surname1: string
    surname2?: string
    email: string
    dni: string
    mobile: string
    password: string
    ccaa: string
    confirmEmail: string
    confirmPassword: string
}

export interface LoginResponse {
    token: string
    name: string
    role: string
}

export const useAuthStore = defineStore('auth', () => {
    // State
    const user = ref<User | null>(null)
    const token = ref<string | null>(localStorage.getItem('digreport-token'))
    const isLoading = ref(false)
    const error = ref<string | null>(null)

    // Getters
    const isAuthenticated = computed(() => !!token.value && !!user.value)
    const isUser = computed(() => user.value?.role === 'USER')
    const isArchaeologist = computed(() => user.value?.role === 'ARCHAEOLOGIST')
    const isAuthority = computed(() => user.value?.role === 'AUTHORITY')

    // Actions
    async function login(credentials: LoginRequest) {
        try {
            isLoading.value = true
            error.value = null

            const response = await apiClient.post<LoginResponse>('/api/auth/login', credentials)

            token.value = response.data.token
            localStorage.setItem('digreport-token', response.data.token)

            // Obtener datos completos del usuario
            await fetchUserProfile()

            return true
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Error al iniciar sesión'
            return false
        } finally {
            isLoading.value = false
        }
    }

    async function register(userData: RegisterRequest) {
        try {
            isLoading.value = true
            error.value = null

            await apiClient.post('/api/auth/register', userData)
            return true
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Error al registrarse'
            return false
        } finally {
            isLoading.value = false
        }
    }

    async function fetchUserProfile() {
        try {
            const response = await apiClient.get<User>('/api/auth/me')
            user.value = response.data
        } catch (err) {
            logout()
            throw err
        }
    }

    function logout() {
        user.value = null
        token.value = null
        localStorage.removeItem('digreport-token')
        error.value = null
    }

    // Verificar token al inicializar
    async function checkAuthStatus() {
        if (token.value) {
            try {
                await fetchUserProfile()
            } catch (err) {
                logout()
            }
        }
    }

    function clearError() {
        error.value = null
    }

    return {
        // State
        user: readonly(user),
        token: readonly(token),
        isLoading: readonly(isLoading),
        error: readonly(error),

        // Getters
        isAuthenticated,
        isUser,
        isArchaeologist,
        isAuthority,

        // Actions
        login,
        register,
        logout,
        checkAuthStatus,
        clearError
    }
})