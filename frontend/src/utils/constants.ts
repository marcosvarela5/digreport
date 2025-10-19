export const STATUS_MAP = {
    PENDING: { text: 'Pendiente', class: 'status-pending', color: 'warning' },
    VALIDATED: { text: 'Validado', class: 'status-validated', color: 'success' },
    REJECTED: { text: 'Rechazado', class: 'status-rejected', color: 'danger' }
} as const

export const PRIORITY_MAP = {
    LOW: { text: 'Baja', class: 'priority-low' },
    MEDIUM: { text: 'Media', class: 'priority-medium' },
    HIGH: { text: 'Alta', class: 'priority-high' },
    CRITICAL: { text: 'Crítica', class: 'priority-critical' }
} as const

export const ROLE_MAP = {
    USER: { text: 'Ciudadano', class: 'role-user' },
    ARCHAEOLOGIST: { text: 'Arqueólogo', class: 'role-archaeologist' },
    AUTHORITY: { text: 'Autoridad', class: 'role-authority' }
} as const

export type FindStatus = keyof typeof STATUS_MAP
export type FindPriority = keyof typeof PRIORITY_MAP
export type UserRole = keyof typeof ROLE_MAP