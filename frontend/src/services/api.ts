import axios from 'axios'
import type { AxiosError, AxiosResponse } from 'axios'

// Configuración base de axios
export const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
})

// Interceptor JWT
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('digreport-token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// Interceptor PROVISIONAL
apiClient.interceptors.response.use(
    (response: AxiosResponse) => {
        return response
    },
    (error: AxiosError) => {
        // Manejar errores comunes - verificar que response existe
        if (error.response) {
            if (error.response.status === 401) {
                // Token expirado o inválido
                localStorage.removeItem('digreport-token')
                window.location.href = '/login'
            }

            if (error.response.status === 403) {
                // Sin permisos
                console.error('Sin permisos para acceder a este recurso')
            }

            if (error.response.status >= 500) {
                // Error del servidor
                console.error('Error del servidor:', error.response.data)
            }
        } else if (error.request) {
            // Error de red (sin respuesta del servidor)
            console.error('Error de red:', error.message)
        } else {
            // Error en la configuración de la petición
            console.error('Error en la petición:', error.message)
        }

        return Promise.reject(error)
    }
)