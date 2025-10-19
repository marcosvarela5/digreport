<template>
  <div class="authority-dashboard">
    <div class="dashboard-header">
      <router-link to="/" class="back-link">⬅ Volver al inicio</router-link>
      <h1 class="page-title">Panel de Autoridad</h1>
      <p class="page-subtitle">Estadísticas y gestión del sistema</p>
    </div>

    <div v-if="isLoading" class="loading-state">
      <div class="spinner"></div>
      <p>Cargando estadísticas...</p>
    </div>

    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
    </div>

    <div v-else class="dashboard-content">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon" style="background: #dbeafe;">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/>
            </svg>
          </div>
          <div class="stat-content">
            <p class="stat-label">Total Hallazgos</p>
            <p class="stat-value">{{ stats.totalFinds }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" style="background: #fef3c7;">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
          </div>
          <div class="stat-content">
            <p class="stat-label">Pendientes</p>
            <p class="stat-value" style="color: #f59e0b;">{{ stats.pendingFinds }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" style="background: #d1fae5;">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
              <path d="M9 16.2L4.8 12l-1.4 1.4L9 19 21 7l-1.4-1.4L9 16.2z"/>
            </svg>
          </div>
          <div class="stat-content">
            <p class="stat-label">Validados</p>
            <p class="stat-value" style="color: #059669;">{{ stats.validatedFinds }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" style="background: #fee2e2;">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
          </div>
          <div class="stat-content">
            <p class="stat-label">Rechazados</p>
            <p class="stat-value" style="color: #dc2626;">{{ stats.rejectedFinds }}</p>
          </div>
        </div>
      </div>

      <div class="charts-grid">
        <div class="chart-card">
          <h3 class="chart-title">Top 10 Ciudadanos</h3>
          <div v-if="stats.topReporters && stats.topReporters.length > 0" class="table-container">
            <table class="data-table">
              <thead>
              <tr>
                <th>Usuario</th>
                <th>Total</th>
                <th>Validados</th>
                <th>Ratio</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="reporter in stats.topReporters" :key="reporter.userId">
                <td class="user-cell">{{ reporter.userName }}</td>
                <td>{{ reporter.totalFinds }}</td>
                <td class="validated-cell">{{ reporter.validatedFinds }}</td>
                <td>
                    <span class="ratio-badge" :class="getRatioClass(reporter)">
                      {{ calculateRatio(reporter) }}%
                    </span>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
          <div v-else class="empty-chart">
            <p>No hay datos disponibles</p>
          </div>
        </div>

        <div class="chart-card">
          <h3 class="chart-title">Hallazgos por Comunidad Autónoma</h3>
          <div v-if="stats.findsByRegion && stats.findsByRegion.length > 0" class="region-list">
            <div v-for="region in stats.findsByRegion" :key="region.ccaa" class="region-item">
              <div class="region-info">
                <span class="region-name">{{ region.ccaa }}</span>
                <span class="region-count">{{ region.count }}</span>
              </div>
              <div class="region-bar">
                <div
                    class="region-bar-fill"
                    :style="{ width: getRegionPercentage(region) + '%' }"
                ></div>
              </div>
            </div>
          </div>
          <div v-else class="empty-chart">
            <p>No hay datos disponibles</p>
          </div>
        </div>
      </div>

      <div class="chart-card full-width">
        <h3 class="chart-title">Evolución Mensual</h3>
        <div v-if="stats.monthlyStats && stats.monthlyStats.length > 0" class="monthly-chart">
          <div
              v-for="month in stats.monthlyStats.slice(-12)"
              :key="month.month"
              class="month-bar"
          >
            <div
                class="month-bar-fill"
                :style="{ height: getMonthPercentage(month) + '%' }"
            >
              <span class="month-value">{{ month.count }}</span>
            </div>
            <span class="month-label">{{ month.month }}</span>
          </div>
        </div>
        <div v-else class="empty-chart">
          <p>No hay datos disponibles</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { apiClient } from '../services/api'
import './AuthorityDashboard.css'

const router = useRouter()
const authStore = useAuthStore()

const stats = ref<any>({})
const isLoading = ref(false)
const error = ref<string | null>(null)

const loadStats = async () => {
  try {
    isLoading.value = true
    error.value = null

    const response = await apiClient.get('/api/authority/dashboard/stats')
    stats.value = response.data

  } catch (err: any) {
    error.value = err.response?.data?.message || 'Error al cargar estadísticas'
  } finally {
    isLoading.value = false
  }
}

const calculateRatio = (reporter: any) => {
  if (reporter.totalFinds === 0) return 0
  return Math.round((reporter.validatedFinds / reporter.totalFinds) * 100)
}

const getRatioClass = (reporter: any) => {
  const ratio = calculateRatio(reporter)
  if (ratio >= 80) return 'ratio-high'
  if (ratio >= 50) return 'ratio-medium'
  return 'ratio-low'
}

const getRegionPercentage = (region: any) => {
  if (!stats.value.findsByRegion) return 0
  const max = Math.max(...stats.value.findsByRegion.map((r: any) => r.count))
  return (region.count / max) * 100
}

const getMonthPercentage = (month: any) => {
  if (!stats.value.monthlyStats) return 0
  const max = Math.max(...stats.value.monthlyStats.map((m: any) => m.count))
  return (month.count / max) * 100
}

onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  if (!authStore.isAuthority) {
    error.value = 'Acceso no autorizado'
    setTimeout(() => router.push('/'), 3000)
    return
  }

  loadStats()
})
</script>