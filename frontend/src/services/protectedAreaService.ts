import { apiClient } from './api'

export interface ProtectedAreaDto {
    id?: number
    name: string
    description?: string
    type: 'MONUMENT' | 'AREA'
    geometry: string
    ccaa?: string
    createdBy?: number
    createdAt?: string
    updatedAt?: string
    active?: boolean
}

export const protectedAreaService = {
    async getAll(): Promise<ProtectedAreaDto[]> {
        const response = await apiClient.get('/api/protected-areas')
        return response.data
    },

    async getById(id: number): Promise<ProtectedAreaDto> {
        const response = await apiClient.get(`/api/protected-areas/${id}`)
        return response.data
    },

    async getByCcaa(ccaa: string): Promise<ProtectedAreaDto[]> {
        const response = await apiClient.get(`/api/protected-areas/ccaa/${ccaa}`)
        return response.data
    },

    async create(data: ProtectedAreaDto): Promise<ProtectedAreaDto> {
        const response = await apiClient.post('/api/protected-areas', data)
        return response.data
    },

    async update(id: number, data: ProtectedAreaDto): Promise<ProtectedAreaDto> {
        const response = await apiClient.put(`/api/protected-areas/${id}`, data)
        return response.data
    },

    async delete(id: number): Promise<void> {
        await apiClient.delete(`/api/protected-areas/${id}`)
    },

    async checkLocation(latitude: number, longitude: number): Promise<boolean> {
        const response = await apiClient.get('/api/protected-areas/check', {
            params: { latitude, longitude }
        })
        return response.data
    }
}