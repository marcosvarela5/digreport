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
  background: #2b6e01;
  color: #ffffff;
}

.priority-medium {
  background: #dab600;
  color: #ffffff;
}

.priority-high {
  background: #fa925d;
  color: #ffffff;
}

.priority-critical {
  background: #d90101;
  color: #ffffff;
}
</style>