<template>
  <div id="app">
    <RouterView />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: #333;
  background-color: #fff;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  min-height: 100vh;
}

/* Variables CSS globales para DIGREPORT */
:root {
  /* Colores principales */
  --primary-color: #2c5aa0;
  --primary-dark: #234a87;
  --secondary-color: #1a365d;
  --accent-color: #ffd700;

  /* Colores de estado */
  --success-color: #10b981;
  --warning-color: #f59e0b;
  --error-color: #ef4444;
  --info-color: #3b82f6;

  /* Colores de texto */
  --text-primary: #1f2937;
  --text-secondary: #6b7280;
  --text-light: #9ca3af;
  --text-white: #ffffff;

  /* Colores de fondo */
  --bg-primary: #ffffff;
  --bg-secondary: #f8f9fa;
  --bg-dark: #1a365d;

  /* Sombras */
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.1);
  --shadow-xl: 0 20px 25px rgba(0, 0, 0, 0.1);

  /* Espaciados */
  --spacing-xs: 0.25rem;
  --spacing-sm: 0.5rem;
  --spacing-md: 1rem;
  --spacing-lg: 1.5rem;
  --spacing-xl: 2rem;
  --spacing-2xl: 3rem;

  /* Tamaños de fuente */
  --text-xs: 0.75rem;
  --text-sm: 0.875rem;
  --text-base: 1rem;
  --text-lg: 1.125rem;
  --text-xl: 1.25rem;
  --text-2xl: 1.5rem;
  --text-3xl: 1.875rem;
  --text-4xl: 2.25rem;

  /* Border radius */
  --radius-sm: 0.25rem;
  --radius-md: 0.5rem;
  --radius-lg: 0.75rem;
  --radius-xl: 1rem;

  /* Transiciones */
  --transition-fast: 0.15s ease-in-out;
  --transition-base: 0.3s ease-in-out;
  --transition-slow: 0.5s ease-in-out;

  /* Contenedor máximo */
  --max-width: 1200px;
}

/* Utilidades globales */
.container {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 0 var(--spacing-lg);
}

.text-center {
  text-align: center;
}

.text-left {
  text-align: left;
}

.text-right {
  text-align: right;
}

/* Clases de botones globales */
.btn {
  display: inline-block;
  padding: 12px 24px;
  text-decoration: none;
  border-radius: var(--radius-md);
  font-weight: 600;
  text-align: center;
  transition: all var(--transition-base);
  cursor: pointer;
  border: none;
  font-size: var(--text-base);
  line-height: 1.5;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: var(--primary-color);
  color: var(--text-white);
}

.btn-primary:hover:not(:disabled) {
  background: var(--primary-dark);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn-secondary {
  border: 2px solid var(--primary-color);
  color: var(--primary-color);
  background: transparent;
}

.btn-secondary:hover:not(:disabled) {
  background: var(--primary-color);
  color: var(--text-white);
}

.btn-success {
  background: var(--success-color);
  color: var(--text-white);
}

.btn-success:hover:not(:disabled) {
  background: #059669;
}

.btn-warning {
  background: var(--warning-color);
  color: var(--text-white);
}

.btn-warning:hover:not(:disabled) {
  background: #d97706;
}

.btn-error {
  background: var(--error-color);
  color: var(--text-white);
}

.btn-error:hover:not(:disabled) {
  background: #dc2626;
}

.btn-outline {
  border: 2px solid var(--text-white);
  color: var(--text-white);
  background: transparent;
}

.btn-outline:hover:not(:disabled) {
  background: var(--text-white);
  color: var(--primary-color);
}

.btn-small {
  padding: 8px 16px;
  font-size: var(--text-sm);
}

.btn-large {
  padding: 16px 32px;
  font-size: var(--text-lg);
}

/* Cards */
.card {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-xl);
  transition: box-shadow var(--transition-base);
}

.card:hover {
  box-shadow: var(--shadow-lg);
}

.card-header {
  margin-bottom: var(--spacing-lg);
}

.card-title {
  font-size: var(--text-xl);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.card-subtitle {
  font-size: var(--text-base);
  color: var(--text-secondary);
}

/* Formularios */
.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-label {
  display: block;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  transition: border-color var(--transition-base), box-shadow var(--transition-base);
}

.form-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(44, 90, 160, 0.1);
}

.form-input:invalid {
  border-color: var(--error-color);
}

.form-error {
  color: var(--error-color);
  font-size: var(--text-sm);
  margin-top: var(--spacing-xs);
}

.form-help {
  color: var(--text-secondary);
  font-size: var(--text-sm);
  margin-top: var(--spacing-xs);
}

/* Alerts */
.alert {
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-lg);
  border-left: 4px solid;
}

.alert-success {
  background: #ecfdf5;
  border-color: var(--success-color);
  color: #065f46;
}

.alert-warning {
  background: #fffbeb;
  border-color: var(--warning-color);
  color: #92400e;
}

.alert-error {
  background: #fef2f2;
  border-color: var(--error-color);
  color: #991b1b;
}

.alert-info {
  background: #eff6ff;
  border-color: var(--info-color);
  color: #1e40af;
}

/* Loading */
.loading {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive utilities */
.hidden {
  display: none;
}

@media (max-width: 768px) {
  .container {
    padding: 0 var(--spacing-md);
  }

  .btn-large {
    padding: 14px 24px;
    font-size: var(--text-base);
  }

  .md-hidden {
    display: none;
  }
}

@media (max-width: 480px) {
  .sm-hidden {
    display: none;
  }

  .btn {
    width: 100%;
    margin-bottom: var(--spacing-sm);
  }
}

/* Accesibilidad */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* Focus visible para teclado */
.btn:focus-visible,
.form-input:focus-visible {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
}

/* Print styles */
@media print {
  .btn,
  .card:hover {
    box-shadow: none;
  }

  .btn {
    border: 1px solid #ccc;
  }
}
</style>