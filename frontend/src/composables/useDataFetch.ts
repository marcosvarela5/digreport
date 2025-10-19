import { ref } from 'vue'
import { apiClient } from '../services/api'

export function useDataFetch<T = any>() {
    const data = ref<T | null>(null)
    const isLoading = ref(false)
    const error = ref<string | null>(null)

    const fetchData = async (url: string) => {
        try {
            isLoading.value = true
            error.value = null
            const response = await apiClient.get(url)
            data.value = response.data
            return response.data
        } catch (err: any) {
            error.value = err.response?.data?.message || 'Error al cargar datos'
            throw err
        } finally {
            isLoading.value = false
        }
    }

    const reset = () => {
        data.value = null
        error.value = null
        isLoading.value = false
    }

    return {
        data,
        isLoading,
        error,
        fetchData,
        reset
    }
}