<template>
  <div class="empty-state">
    <div class="empty-icon">{{ icon }}</div>
    <h3>{{ title }}</h3>
    <p>{{ message }}</p>
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  icon?: string
  title?: string
  message: string
}>(), {
  icon: '📭',
  title: 'No hay datos'
})
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  color: #6b7280;
  background: #f9fafb;
  border-radius: var(--radius-md);
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.empty-state h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #374151;
  margin: 0 0 0.5rem 0;
}

.empty-state p {
  font-size: 0.9375rem;
  margin: 0;
}
</style>