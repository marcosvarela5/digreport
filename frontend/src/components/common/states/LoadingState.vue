<template>
  <div class="loading-state">
    <div class="spinner"></div>
    <p>{{ message || 'Cargando...' }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  message?: string
}>()
</script>

<style scoped>
.loading-state {
  background: white;
  padding: 4rem 2rem;
  border-radius: var(--radius-lg);
  text-align: center;
  max-width: 700px;
  margin: 3rem auto;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.spinner {
  width: 3rem;
  height: 3rem;
  border: 4px solid #e5e7eb;
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p {
  color: #6b7280;
  font-size: 0.9375rem;
  margin: 0;
}
</style>