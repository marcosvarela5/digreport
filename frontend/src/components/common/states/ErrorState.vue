<template>
  <div class="error-state">
    <svg class="error-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
    </svg>
    <p>{{ message }}</p>
    <button v-if="onRetry" @click="onRetry" class="retry-btn">
      Reintentar
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  message: string
  onRetry?: () => void
}>()
</script>

<style scoped>
.error-state {
  background: white;
  padding: 4rem 2rem;
  border-radius: var(--radius-lg);
  text-align: center;
  max-width: 700px;
  margin: 3rem auto;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.error-icon {
  width: 4rem;
  height: 4rem;
  color: #dc2626;
  margin: 0 auto 1rem;
}

.error-state p {
  color: #dc2626;
  font-size: 0.9375rem;
  margin: 0 0 1rem 0;
}

.retry-btn {
  padding: 0.625rem 1.5rem;
  border: none;
  border-radius: var(--radius-md);
  background: var(--primary-color);
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.retry-btn:hover {
  background: var(--secondary-color);
  transform: translateY(-2px);
}
</style>