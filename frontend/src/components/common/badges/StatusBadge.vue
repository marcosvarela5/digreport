<template>
  <span :class="['status-badge', statusClass]">
    {{ statusText }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { STATUS_MAP, type FindStatus } from '@/utils/constants'

const props = defineProps<{
  status: FindStatus
}>()

const statusClass = computed(() => STATUS_MAP[props.status].class)
const statusText = computed(() => STATUS_MAP[props.status].text)
</script>

<style scoped>
.status-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-weight: 600;
}

.status-pending {
  background: #fef3c7;
  color: #f59e0b;
}

.status-validated {
  background: #d1fae5;
  color: #059669;
}

.status-rejected {
  background: #fee2e2;
  color: #dc2626;
}
</style>