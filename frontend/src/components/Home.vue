<template>
  <div class="app">
    <!-- Navigation -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="nav-content">
          <!-- Logo -->
          <div class="logo">
            <h1>DIGREPORT</h1>
          </div>

          <!-- Desktop Navigation -->
          <div class="nav-desktop">
            <div class="nav-links">
              <a href="#inicio" class="nav-link">Inicio</a>
              <a href="#sobre-proyecto" class="nav-link">Proyecto</a>
              <a href="#como-funciona" class="nav-link">Cómo funciona</a>
              <a href="/login" class="btn btn-primary">Iniciar Sesión</a>
              <a href="/register" class="btn btn-secondary">Registro</a>
            </div>
          </div>

          <!-- Mobile menu button -->
          <div class="mobile-menu-btn">
            <button @click="toggleMenu" class="menu-toggle">
              <span class="hamburger"></span>
              <span class="hamburger"></span>
              <span class="hamburger"></span>
            </button>
          </div>
        </div>

        <!-- Mobile Navigation Menu -->
        <div v-show="isMenuOpen" class="mobile-menu">
          <a href="#inicio" class="mobile-link" @click="toggleMenu">Inicio</a>
          <a href="#sobre-proyecto" class="mobile-link" @click="toggleMenu">El Proyecto</a>
          <a href="#como-funciona" class="mobile-link" @click="toggleMenu">Cómo Funciona</a>
          <a href="/login" class="mobile-link primary" @click="toggleMenu">Iniciar Sesión</a>
          <a href="/register" class="mobile-link secondary" @click="toggleMenu">Registro</a>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section id="inicio" class="hero">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">
            Protegiendo el
            <span class="highlight">Patrimonio Histórico</span>
            <br>de forma colaborativa
          </h1>
          <p class="hero-subtitle">
            DIGREPORT conecta ciudadanos, arqueólogos y autoridades para la gestión
            responsable de hallazgos arqueológicos y la protección del patrimonio cultural.
          </p>
          <div class="hero-actions">
            <a href="/registerFind" class="btn btn-primary btn-large">
              Registrar Hallazgo
            </a>
            <a href="#sobre-proyecto" class="btn btn-outline btn-large">
              Conocer Más
            </a>
          </div>
        </div>

        <div class="hero-visual">
          <div class="heritage-card">
            <div class="card-icon">🏛️</div>
            <h3>DIGREPORT</h3>
            <h4>Trabajamos juntos para preservar nuestro legado</h4>
          </div>
        </div>
      </div>
    </section>

    <!-- Project  -->
    <section id="sobre-proyecto" class="project-section">
      <div class="container">
        <div class="section-header">
          <h2>Qué es DIGREPORT?</h2>
          <p>
            Una plataforma para uso institucional que permite a ciudadanos, profesionales y autoridades
            colaborar en la protección del patrimonio histórico español.
          </p>
        </div>

        <div class="project-grid">
          <div class="project-item">
            <div class="project-icon">👥</div>
            <h3>Ciudadanos</h3>
            <p>
              Registran hallazgos arqueológicos de forma legal y responsable.
              Contribuyen directamente a la preservación del patrimonio.
            </p>
          </div>
          <div class="project-item">
            <div class="project-icon">🔬</div>
            <h3>Arqueólogos</h3>
            <p>
              Supervisar los hallazgos reportados por ciudadanos.
              Colaborar en la identificación de bienes arqueológicos.
            </p>
          </div>
          <div class="project-item">
            <div class="project-icon">⚖️</div>
            <h3>Autoridades</h3>
            <p>
              Supervisión de la actividad, gestión de zonas protegidas y generación
              de informes para la administración del patrimonio.
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- How it Works Section -->
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
                Los ciudadanos registran hallazgos con la información más precisa posible.
              </p>
            </div>
          </div>
          <div class="step">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3>Validación profesional</h3>
              <p>
                Uno o varios profesionales técnicos revisan, clasifican y validan el mismo.
              </p>
            </div>
          </div>
          <div class="step">
            <div class="step-number">3</div>
            <div class="step-content">
              <h3>Supervisión oficial</h3>
              <p>
                Las autoridades supervisan el proceso y toman las medidas
                necesarias para la protección del patrimonio de acuerdo a la legalidad vigente.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <div class="container">
        <div class="section-header">
          <h2>Funcionalidades</h2>
        </div>

        <div class="features-grid">
          <div class="feature">
            <div class="feature-icon">📍</div>
            <h3>Geolocalización</h3>
            <p>Registro preciso de ubicaciones de hallazgos</p>
          </div>
          <div class="feature">
            <div class="feature-icon">🗺️</div>
            <h3>Zonas protegidas</h3>
            <p>Mapa actualizado de áreas de protección</p>
          </div>
          <div class="feature">
            <div class="feature-icon">📊</div>
            <h3>Estadísticas</h3>
            <p>Informes y análisis de actividad arqueológica</p>
          </div>
          <div class="feature">
            <div class="feature-icon">🏆</div>
            <h3>Reputación</h3>
            <p>Reconocimiento al ciudadano por comportamiento cívico responsable</p>
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
            Forma parte de una nación comprometida con la preservación
            de nuestro patrimonio histórico y cultural.
          </p>
          <div class="cta-actions">
            <a href="/register" class="btn btn-primary btn-large">
              Registrarme
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
            </p>
          </div>
          <div class="footer-section">
            <h4>Marco Legal</h4>
            <ul>
              <li><a href="https://www.boe.es/buscar/act.php?id=BOE-A-1985-12534">Ley 16/1985 del Patrimonio Histórico Español</a></li>
              <p>Normativas Autonómicas</p>
            </ul>
          </div>
          <div class="footer-section">
            <h4>Contacto</h4>
            <ul>
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
import { ref } from 'vue'
import './Home.css'

const isMenuOpen = ref(false)

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}
</script>