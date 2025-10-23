<template>
  <div class="authority-layout">
    <!-- Sidebar -->
    <aside class="dashboard-sidebar">
      <div class="sidebar-header">
        <h2 class="sidebar-title">Panel de administración</h2>
        <p class="sidebar-subtitle">Gestión del sistema</p>
      </div>

      <nav class="sidebar-nav">
        <button
            :class="['sidebar-nav-item', { active: activeTab === 'overview' }]"
            @click="activeTab = 'overview'"
        >
          <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
          </svg>
          <span>General</span>
        </button>

        <button
            :class="['sidebar-nav-item', { active: activeTab === 'finds' }]"
            @click="activeTab = 'finds'"
        >
          <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          <span>Lista de reportes</span>
        </button>

        <button
            :class="['sidebar-nav-item', { active: activeTab === 'users' }]"
            @click="switchToUsers"
        >
          <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
          </svg>
          <span>Usuarios</span>
        </button>
      </nav>

      <div class="sidebar-footer">
        <router-link to="/" class="sidebar-back-link">
          <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          <span>Volver al inicio</span>
        </router-link>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="authority-dashboard">
      <div class="dashboard-header">
        <h1 class="page-title">
          {{ activeTab === 'overview' ? 'Resumen General' : activeTab === 'finds' ? 'Todos los Hallazgos' : 'Gestión de Usuarios' }}
        </h1>
        <p class="page-subtitle">
          {{ activeTab === 'overview' ? 'Estadísticas globales del sistema' : activeTab === 'finds' ? 'Listado completo de hallazgos reportados' : 'Información de todos los usuarios registrados' }}
        </p>
      </div>

      <!-- Overview Tab -->
      <div v-if="activeTab === 'overview'" class="dashboard-content">
        <div v-if="isLoadingStats" class="loading-state">
          <div class="spinner"></div>
          <p>Cargando estadísticas...</p>
        </div>

        <div v-else-if="statsError" class="error-state">
          <p>{{ statsError }}</p>
        </div>

        <template v-else>
          <!-- Stats Grid -->
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon" style="background: #dbeafe;">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
                  <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7z"/>
                </svg>
              </div>
              <div class="stat-content">
                <p class="stat-label">Reportes totales</p>
                <p class="stat-value">{{ stats.totalFinds }}</p>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon" style="background: #fef3c7;">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
              </div>
              <div class="stat-content">
                <p class="stat-label">Reportes a revisar</p>
                <p class="stat-value" style="color: #f59e0b;">{{ stats.pendingFinds }}</p>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon" style="background: #d1fae5;">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
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
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                </svg>
              </div>
              <div class="stat-content">
                <p class="stat-label">Rechazados</p>
                <p class="stat-value" style="color: #dc2626;">{{ stats.rejectedFinds }}</p>
              </div>
            </div>
          </div>

          <!-- Charts Grid -->
          <div class="charts-grid">
            <!-- Top Reporters -->
            <div class="chart-card">
              <h3 class="chart-title">Usuarios más activos</h3>
              <div v-if="stats.topReporters && stats.topReporters.length > 0" class="table-container">
                <table class="data-table">
                  <thead>
                  <tr>
                    <th>Usuario</th>
                    <th>Total</th>
                    <th>Validados</th>
                    <th>Proporción</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="reporter in stats.topReporters" :key="reporter.userId">
                    <td class="user-cell">{{ reporter.userName }}</td>
                    <td>{{ reporter.totalFinds }}</td>
                    <td class="validated-cell">{{ reporter.validatedFinds }}</td>
                    <td>
                      <span :class="['ratio-badge', getRatioClass(reporter)]">
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

            <!-- Finds by Region -->
            <div class="chart-card">
              <h3 class="chart-title">Reportes por Comunidad Autónoma</h3>
              <div v-if="stats.findsByCcaa && stats.findsByCcaa.length > 0" class="region-list">
                <div v-for="region in stats.findsByCcaa" :key="region.ccaa" class="region-item">
                  <div class="region-info">
                    <span class="region-name">{{ region.ccaa }}</span>
                    <span class="region-count">{{ region.count }} hallazgos</span>
                  </div>
                  <div class="region-bar">
                    <div class="region-bar-fill" :style="{ width: getRegionPercentage(region) + '%' }"></div>
                  </div>
                </div>
              </div>
              <div v-else class="empty-chart">
                <p>No hay datos disponibles</p>
              </div>
            </div>
          </div>

          <!-- Monthly Evolution -->
          <div class="chart-card full-width">
            <h3 class="chart-title">Evolución mensual</h3>
            <button class="advanced-search-btn">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8"></circle>
                <path d="m21 21-4.35-4.35"></path>
              </svg>
              Búsqueda avanzada
            </button>

            <div v-if="sortedMonthlyStats.length > 0" class="monthly-chart">
              <div
                  v-for="month in sortedMonthlyStats"
                  :key="`${month.month}-${month.year}`"
                  class="month-bar"
              >
                <div
                    class="month-bar-fill"
                    :style="{ height: getMonthHeight(month) }"
                >
                  <span class="month-value">{{ month.count }}</span>
                </div>
                <span class="month-label">{{ month.month }}</span>
              </div>
            </div>

            <div v-else class="empty-chart">
              📊 No hay datos mensuales disponibles
            </div>
          </div>
        </template>
      </div>

      <!-- Finds Tab -->
      <div v-if="activeTab === 'finds'" class="dashboard-content">
        <div class="filter-buttons-container">
          <div class="filter-buttons">
            <button
                :class="['filter-btn', { active: filterStatus === 'ALL' }]"
                @click="filterStatus = 'ALL'"
            >
              Todos
            </button>
            <button
                :class="['filter-btn', 'filter-pending', { active: filterStatus === 'PENDING' }]"
                @click="filterStatus = 'PENDING'"
            >
              Pendientes
            </button>
            <button
                :class="['filter-btn', 'filter-validated', { active: filterStatus === 'VALIDATED' }]"
                @click="filterStatus = 'VALIDATED'"
            >
              Validados
            </button>
            <button
                :class="['filter-btn', 'filter-rejected', { active: filterStatus === 'REJECTED' }]"
                @click="filterStatus = 'REJECTED'"
            >
              Rechazados
            </button>
          </div>
        </div>

        <div v-if="isLoadingFinds" class="loading-state">
          <div class="spinner"></div>
          <p>Cargando hallazgos...</p>
        </div>

        <div v-else-if="findsError" class="error-state">
          <p>{{ findsError }}</p>
        </div>

        <div v-else class="chart-card">
          <div class="table-container">
            <table class="data-table finds-table">
              <thead>
              <tr>
                <th>ID</th>
                <th>Reportado por</th>
                <th>Descripción</th>
                <th>CCAA</th>
                <th>Estado</th>
                <th>Prioridad</th>
                <th>Fecha</th>
                <th>Acciones</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="find in filteredFinds" :key="find.id">
                <td><strong>#{{ find.id }}</strong></td>
                <td>{{ find.reporterName }}</td>
                <td class="description-cell">{{ truncateText(find.description, 60) }}</td>
                <td>{{ find.ccaa || 'N/A' }}</td>
                <td>
                  <span :class="['status-badge', getStatusClass(find.status)]">
                    {{ getStatusText(find.status) }}
                  </span>
                </td>
                <td>
                  <span :class="['priority-badge', getPriorityClass(find.priority)]">
                    {{ getPriorityText(find.priority) }}
                  </span>
                </td>
                <td>{{ formatDate(find.discoveredAt) }}</td>
                <td>
                  <button @click="viewFindDetails(find.id)" class="view-btn">
                    Ver detalles
                  </button>
                </td>
              </tr>
              </tbody>
            </table>

            <div v-if="filteredFinds.length === 0" class="empty-chart">
              <p>No hay hallazgos con el filtro seleccionado</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Users Tab -->
      <div v-if="activeTab === 'users'" class="dashboard-content">
        <div v-if="isLoadingUsers" class="loading-state">
          <div class="spinner"></div>
          <p>Cargando usuarios...</p>
        </div>

        <div v-else-if="usersError" class="error-state">
          <p>{{ usersError }}</p>
        </div>

        <div v-else class="chart-card">
          <div class="table-container">
            <table class="data-table users-table">
              <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Nivel</th>
                <th>Email</th>
                <th>Total</th>
                <th>Validados</th>
                <th>Rechazados</th>
                <th>Pendientes</th>
                <th>Ratio</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="user in users" :key="user.userId">
                <td><strong>#{{ user.userId }}</strong></td>
                <td class="user-cell">{{ user.userName }}</td>
                <td>
                  <span :class="['role-badge', getRoleClass(user.role)]">
                    {{ getRoleText(user.role) }}
                  </span>
                </td>
                <td class="email-cell">{{ user.email }}</td>
                <td>{{ user.totalFinds }}</td>
                <td class="validated-cell">{{ user.validatedFinds }}</td>
                <td style="color: #dc2626; font-weight: 600;">{{ user.rejectedFinds }}</td>
                <td style="color: #f59e0b; font-weight: 600;">{{ user.pendingFinds }}</td>
                <td>
                  <span :class="['ratio-badge', getUserRatioClass(user)]">
                    {{ calculateUserRatio(user) }}%
                  </span>
                </td>
              </tr>
              </tbody>
            </table>

            <div v-if="users.length === 0" class="empty-chart">
              <p>No hay usuarios registrados</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { apiClient } from '../services/api'
import './AuthorityDashboard.css'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('overview')
const filterStatus = ref('ALL')

const stats = ref<any>({})
const isLoadingStats = ref(false)
const statsError = ref<string | null>(null)

const allFinds = ref<any[]>([])
const isLoadingFinds = ref(false)
const findsError = ref<string | null>(null)

const users = ref<any[]>([])
const isLoadingUsers = ref(false)
const usersError = ref<string | null>(null)

const filteredFinds = computed(() => {
  if (filterStatus.value === 'ALL') return allFinds.value
  return allFinds.value.filter(find => find.status === filterStatus.value)
})


const sortedMonthlyStats = computed(() => {

  const last12Months = []
  const now = new Date()

  for (let i = 11; i >= 0; i--) {
    const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
    const monthNames = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic']
    const monthStr = `${monthNames[date.getMonth()]} ${date.getFullYear()}`


    const existingData = stats.value.monthlyStats?.find((m: any) => {
      const parts = m.month.toLowerCase().split(' ')
      const dataMonth = parts[0]
      const dataYear = parseInt(parts[1])

      return dataMonth === monthNames[date.getMonth()] && dataYear === date.getFullYear()
    })

    last12Months.push({
      month: monthStr,
      count: existingData?.count || 0
    })
  }

  console.log('📊 Últimos 12 meses:', last12Months)
  return last12Months
})

const maxMonthlyValue = computed(() => {
  if (!sortedMonthlyStats.value.length) return 1
  const max = Math.max(...sortedMonthlyStats.value.map((m: any) => m.count), 1)
  console.log('📈 Valor máximo:', max)
  return max
})

const getMonthHeight = (month: any) => {
  if (month.count === 0) return '5%'
  const percentage = (month.count / maxMonthlyValue.value) * 100
  console.log(`📏 ${month.month}: ${percentage}%`)
  return `${percentage}%`
}

const loadStats = async () => {
  try {
    isLoadingStats.value = true
    statsError.value = null
    const response = await apiClient.get('/api/authority/dashboard/stats')
    stats.value = response.data
  } catch (err: any) {
    statsError.value = err.response?.data?.message || 'Error al cargar estadísticas'
  } finally {
    isLoadingStats.value = false
  }
}

const loadAllFinds = async () => {
  try {
    isLoadingFinds.value = true
    findsError.value = null
    const response = await apiClient.get('/api/finds')
    allFinds.value = response.data
  } catch (err: any) {
    findsError.value = err.response?.data?.message || 'Error al cargar hallazgos'
  } finally {
    isLoadingFinds.value = false
  }
}

const loadUsers = async () => {
  try {
    isLoadingUsers.value = true
    usersError.value = null
    const response = await apiClient.get('/api/members/stats')

    users.value = response.data.filter((u: any) => u.role === 'USER' || u.role === 'ARCHAEOLOGIST')
  } catch (err: any) {
    usersError.value = err.response?.data?.message || 'Error al cargar usuarios'
  } finally {
    isLoadingUsers.value = false
  }
}

const switchToUsers = () => {
  activeTab.value = 'users'
  if (users.value.length === 0) {
    loadUsers()
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

const calculateUserRatio = (user: any) => {
  if (user.totalFinds === 0) return 0
  return Math.round((user.validatedFinds / user.totalFinds) * 100)
}

const getUserRatioClass = (user: any) => {
  const ratio = calculateUserRatio(user)
  if (ratio >= 80) return 'ratio-high'
  if (ratio >= 50) return 'ratio-medium'
  return 'ratio-low'
}

const getRegionPercentage = (region: any) => {
  if (!stats.value.findsByCcaa) return 0
  const max = Math.max(...stats.value.findsByCcaa.map((r: any) => r.count))
  return (region.count / max) * 100
}

const getMonthPercentage = (month: any) => {
  if (!stats.value.monthlyStats || stats.value.monthlyStats.length === 0) return 0
  const max = Math.max(...stats.value.monthlyStats.map((m: any) => m.count))
  return max === 0 ? 0 : (month.count / max) * 100
}

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    'PENDING': 'status-pending',
    'VALIDATED': 'status-validated',
    'REJECTED': 'status-rejected'
  }
  return classes[status] || 'status-pending'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    'PENDING': 'Pendiente',
    'VALIDATED': 'Validado',
    'REJECTED': 'Rechazado'
  }
  return texts[status] || status
}

const getPriorityClass = (priority: string) => {
  return `priority-${priority.toLowerCase()}`
}

const getPriorityText = (priority: string) => {
  const texts: Record<string, string> = {
    'LOW': 'Baja',
    'MEDIUM': 'Media',
    'HIGH': 'Alta',
    'CRITICAL': 'Crítica'
  }
  return texts[priority] || priority
}

const getRoleClass = (role: string) => {
  const classes: Record<string, string> = {
    'USER': 'role-user',
    'ARCHAEOLOGIST': 'role-archaeologist',
    'AUTHORITY': 'role-authority'
  }
  return classes[role] || 'role-user'
}

const getRoleText = (role: string) => {
  const texts: Record<string, string> = {
    'USER': 'Ciudadano',
    'ARCHAEOLOGIST': 'Profesional',
    'AUTHORITY': 'Administrador'
  }
  return texts[role] || role
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    day: '2-digit',
    month: 'short',
    year: 'numeric'
  })
}

const truncateText = (text: string, maxLength: number) => {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

const viewFindDetails = (id: number) => {
  router.push(`/finds/${id}`)
}

onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }

  if (!authStore.isAuthority) {
    router.push('/')
    return
  }

  loadStats()
  loadAllFinds()
})
</script>