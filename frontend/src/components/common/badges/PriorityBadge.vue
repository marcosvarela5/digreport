<template>
  <span :class="['priority-badge', priorityClass]">
    {{ priorityText }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { PRIORITY_MAP, type FindPriority } from '@/utils/constants'

const props = defineProps<{
  priority: FindPriority
}>()

const priorityClass = computed(() => PRIORITY_MAP[props.priority].class)
const priorityText = computed(() => PRIORITY_MAP[props.priority].text)
</script>

<style scoped>
.priority-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-weight: 600;
}

.priority-low {
  background: #f3f4f6;
  color: #6b7280;
}

.priority-medium {
  background: #dbeafe;
  color: #2563eb;
}

.priority-high {
  background: #fed7aa;
  color: #ea580c;
}

.priority-critical {
  background: #fee2e2;
  color: #dc2626;
}
</style>