<template>
  <div class="app">
    <!-- Navigation -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="nav-content">
          <div class="logo">
            <img :src="logoDigreport" alt="DIGREPORT" class="logo-img">
            <h1>DIGREPORT</h1>
          </div>

          <div class="nav-desktop">
            <div class="nav-links">
              <a href="#inicio" class="nav-link">Inicio</a>
              <a href="#sobre-proyecto" class="nav-link">Proyecto</a>
              <a href="#como-funciona" class="nav-link">Cómo funciona</a>

              <template v-if="authStore.isAuthenticated">
                <UserMenu />
              </template>
              <template v-else>
                <a href="/login" class="btn btn-primary">Iniciar Sesión</a>
                <a href="/register" class="btn btn-secondary">Registro</a>
              </template>
            </div>
          </div>

          <div class="mobile-menu-btn">
            <button @click="toggleMenu" class="menu-toggle">
              <span class="hamburger"></span>
              <span class="hamburger"></span>
              <span class="hamburger"></span>
            </button>
          </div>
        </div>

        <div v-show="isMenuOpen" class="mobile-menu">
          <a href="#inicio" class="mobile-link" @click="toggleMenu">Inicio</a>
          <a href="#sobre-proyecto" class="mobile-link" @click="toggleMenu">El Proyecto</a>
          <a href="#como-funciona" class="mobile-link" @click="toggleMenu">Cómo Funciona</a>

          <template v-if="authStore.isAuthenticated">
            <router-link to="/profile" class="mobile-link" @click="toggleMenu">Mi Perfil</router-link>
            <button @click="handleLogout" class="mobile-link logout">Cerrar Sesión</button>
          </template>
          <template v-else>
            <router-link to="/login" class="mobile-link primary" @click="toggleMenu">Iniciar Sesión</router-link>
            <router-link to="/register" class="mobile-link secondary" @click="toggleMenu">Registro</router-link>
          </template>
        </div>
      </div>
    </nav>

    <!-- Hero Section - Split Screen Design -->
    <section id="inicio" class="hero">
      <div class="hero-visual">
        <div class="heritage-card">
          <div class="card-icon">
            <img :src="logoDigreport" alt="DIGREPORT" class="card-icon-img">
          </div>
          <h3>DIGREPORT</h3>
          <h4>Tecnología al servicio de nuestra historia</h4>

          <!-- 🆕 Badge de validación -->
          <div class="validation-badge">
            <span class="badge-icon">✓</span>
            Sistema de validación profesional
          </div>
        </div>
      </div>

      <div class="hero-content">
        <h1 class="hero-title">
          Protegiendo el
          <span class="highlight">Patrimonio Histórico</span>
          de forma colaborativa
        </h1>
        <p class="hero-subtitle">
          DIGREPORT conecta ciudadanos, arqueólogos y autoridades para la gestión
          responsable de hallazgos arqueológicos y la protección del patrimonio cultural.
        </p>
        <div class="hero-actions">
          <router-link to="/register-find" class="btn btn-primary btn-large">
            <span class="btn-icon">📍</span>
            Registrar Hallazgo
          </router-link>
          <a href="#sobre-proyecto" class="btn btn-outline btn-large">
            <span class="btn-icon">📖</span>
            Conocer Más
          </a>
        </div>

        <!-- 🆕 ESTADÍSTICAS -->
        <div class="hero-stats" v-if="!statsLoading">
          <div class="stat">
            <span class="stat-number">+{{ stats.totalFinds }}</span>
            <span class="stat-label">Hallazgos Registrados</span>
          </div>
          <div class="stat">
            <span class="stat-number">+{{ stats.totalArchaeologists }}</span>
            <span class="stat-label">Arqueólogos Activos</span>
          </div>
          <div class="stat">
            <span class="stat-number">{{ stats.validationRate.toFixed(0) }}%</span>
            <span class="stat-label">Tasa de Validación</span>
          </div>
          <div class="stat">
            <span class="stat-number">+{{ stats.totalCitizens }}</span>
            <span class="stat-label">personas activas</span>
          </div>
          <div class="stat">
            <span class="stat-number">+{{ stats.pendingFinds }}</span>
            <span class="stat-label">hallazgos en estudio</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Project Section - 3 Column Cards -->
    <section id="sobre-proyecto" class="project-section">
      <div class="container">
        <div class="section-header">
          <h2>¿Qué es DIGREPORT?</h2>
          <p>
            Una plataforma institucional que permite a ciudadanos, profesionales y autoridades
            colaborar en la protección del patrimonio histórico español.
          </p>
        </div>

        <div class="project-grid">
          <div class="project-item">
            <div class="project-icon-wrapper">
              <div class="project-icon">👥</div>
            </div>
            <div class="project-content">
              <h3>Ciudadanos</h3>
              <p>
                Registran hallazgos arqueológicos de forma legal y responsable,
                contribuyendo directamente a la preservación del patrimonio.
              </p>
            </div>
          </div>

          <div class="project-item">
            <div class="project-icon-wrapper">
              <div class="project-icon">🔬</div>
            </div>
            <div class="project-content">
              <h3>Profesionales</h3>
              <p>
                Supervisan los hallazgos reportados por ciudadanos y
                colaboran en la identificación de bienes arqueológicos.
              </p>
            </div>
          </div>

          <div class="project-item">
            <div class="project-icon-wrapper">
              <div class="project-icon">⚖️</div>
            </div>
            <div class="project-content">
              <h3>Autoridades</h3>
              <p>
                Supervisión de la actividad, gestión de zonas protegidas y generación
                de informes para la administración del patrimonio.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- How it Works - Horizontal Timeline -->
    <section id="como-funciona" class="how-it-works">
      <div class="container">
        <div class="section-header">
          <h2>Cómo funciona</h2>
          <p>Un proceso simple y colaborativo para la gestión del patrimonio</p>
        </div>

        <div class="steps">
          <div class="step">
            <div class="step-number">1</div>
            <div class="step-content">
              <h3>Registro del hallazgo</h3>
              <p>
                Los ciudadanos registran hallazgos con información precisa y fotografías del descubrimiento.
              </p>
            </div>
          </div>

          <div class="step">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3>Validación profesional</h3>
              <p>
                Profesionales técnicos revisan, clasifican y validan el hallazgo arqueológico.
              </p>
            </div>
          </div>

          <div class="step">
            <div class="step-number">3</div>
            <div class="step-content">
              <h3>Supervisión oficial</h3>
              <p>
                Las autoridades toman medidas necesarias para la protección según la legalidad vigente.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features - 2 Column Modern Layout -->
    <section class="features">
      <div class="container">
        <div class="section-header">
          <h2>Funcionalidades principales</h2>
        </div>

        <div class="features-grid">
          <div class="feature">
            <div class="feature-icon">📍</div>
            <div class="feature-text">
              <h3>Geolocalización precisa</h3>
              <p>
                Registro exacto de ubicaciones con coordenadas GPS para una
                documentación arqueológica rigurosa y profesional.
              </p>
            </div>
          </div>

          <div class="feature">
            <div class="feature-icon">🗺️</div>
            <div class="feature-text">
              <h3>Zonas protegidas</h3>
              <p>
                Mapa actualizado de áreas de protección patrimonial y
                delimitación de zonas arqueológicas sensibles.
              </p>
            </div>
          </div>

          <div class="feature">
            <div class="feature-icon">📊</div>
            <div class="feature-text">
              <h3>Análisis y estadísticas</h3>
              <p>
                Informes detallados y visualización de datos sobre
                actividad arqueológica en tiempo real.
              </p>
            </div>
          </div>

          <div class="feature">
            <div class="feature-icon">🏆</div>
            <div class="feature-text">
              <h3>Sistema de reputación</h3>
              <p>
                Reconocimiento a ciudadanos por su comportamiento cívico
                responsable en la preservación del patrimonio.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h2>Defiende la historia</h2>
          <p>
            Forma parte de una comunidad comprometida con la preservación
            de nuestro patrimonio histórico y cultural.
          </p>
          <div class="cta-actions">
            <a href="/register" class="btn btn-primary btn-large">
              Únete Ahora
            </a>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>DIGREPORT</h3>
            <p>
              Plataforma colaborativa para la gestión del patrimonio histórico español.
              Conectando ciudadanos, profesionales y autoridades.
            </p>
          </div>

          <div class="footer-section">
            <h4>Marco Legal</h4>
            <ul>
              <li>
                <a href="https://www.boe.es/buscar/act.php?id=BOE-A-1985-12534" target="_blank">
                  Ley 16/1985 del Patrimonio Histórico Español
                </a>
              </li>
              <li>
                <a href="#" target="_blank">Normativas Autonómicas</a>
              </li>
            </ul>
          </div>

          <div class="footer-section">
            <h4>Enlaces</h4>
            <ul>
              <li><a href="#sobre-proyecto">Sobre el Proyecto</a></li>
              <li><a href="#como-funciona">Cómo Funciona</a></li>
              <li><a href="/register">Registro</a></li>
            </ul>
          </div>
        </div>

        <div class="footer-bottom">
          <p>&copy; 2025 DIGREPORT - TFG Marcos Francisco Varela Marcos</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import UserMenu from './UserMenu.vue'
import logoDigreport from '../assets/logodigreport.png'
import {apiClient} from "@/services/api.ts";
import './Home.css'

const stats = ref({
  totalFinds: 0,
  totalArchaeologists: 0,
  validationRate: 0,
  totalCitizens: 0,      // 🆕
  pendingFinds: 0        // 🆕
})

const statsLoading = ref(true)

const loadStats = async () => {
  try {
    statsLoading.value = true
    const response = await apiClient.get('/api/stats/public')

    stats.value = {
      totalFinds: response.data.totalFinds || 0,
      totalArchaeologists: response.data.totalArchaeologists || 0,
      validationRate: response.data.validationRate || 0,
      totalCitizens: response.data.totalCitizens || 0,      // 🆕
      pendingFinds: response.data.pendingFinds || 0         // 🆕
    }
  } catch (error) {
    console.error('Error cargando estadísticas:', error)
    stats.value = {
      totalFinds: 0,
      totalArchaeologists: 0,
      validationRate: 0,
      totalCitizens: 0,
      pendingFinds: 0
    }
  } finally {
    statsLoading.value = false
  }
}

const router = useRouter()
const authStore = useAuthStore()
const isMenuOpen = ref(false)

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const handleLogout = () => {
  authStore.logout()
  isMenuOpen.value = false
  router.push('/')
}

onMounted(() => {
  authStore.checkAuthStatus()
  loadStats()
})
</script>