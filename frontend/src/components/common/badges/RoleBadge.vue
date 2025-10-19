<template>
  <span :class="['role-badge', roleClass]">
    {{ roleText }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ROLE_MAP, type UserRole } from '@/utils/constants'

const props = defineProps<{
  role: UserRole
}>()

const roleClass = computed(() => ROLE_MAP[props.role].class)
const roleText = computed(() => ROLE_MAP[props.role].text)
</script>

<style scoped>
.role-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-weight: 600;
}

.role-user {
  background: #dbeafe;
  color: #2563eb;
}

.role-archaeologist {
  background: #e9d5ff;
  color: #9333ea;
}

.role-authority {
  background: #fce7f3;
  color: #db2777;
}
</style>