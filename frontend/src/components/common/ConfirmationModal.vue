<template>
  <transition name="modal-fade">
    <div v-if="isVisible" class="modal-overlay" @click.self="handleCancel">
      <div class="modal-container">
        <div class="modal-header">
          <div class="modal-icon" :class="iconClass">
            <i :class="iconType"></i>
          </div>
          <h3 class="modal-title">{{ title }}</h3>
        </div>

        <div class="modal-body">
          <p>{{ message }}</p>
          <p v-if="details" class="modal-details">{{ details }}</p>
        </div>

        <div class="modal-footer">
          <button
              class="btn btn-cancel"
              @click="handleCancel"
              :disabled="loading"
          >
            {{ cancelText }}
          </button>
          <button
              class="btn btn-confirm"
              :class="confirmButtonClass"
              @click="handleConfirm"
              :disabled="loading"
          >
            <span v-if="loading" class="spinner"></span>
            {{ loading ? loadingText : confirmText }}
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'ConfirmationModal',
  props: {
    isVisible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: 'Confirmar acción'
    },
    message: {
      type: String,
      required: true
    },
    details: {
      type: String,
      default: ''
    },
    confirmText: {
      type: String,
      default: 'Aceptar'
    },
    cancelText: {
      type: String,
      default: 'Cancelar'
    },
    loadingText: {
      type: String,
      default: 'Procesando...'
    },
    variant: {
      type: String,
      default: 'warning', // warning, danger, info, success
      validator: (value) => ['warning', 'danger', 'info', 'success'].includes(value)
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    iconClass() {
      return `modal-icon-${this.variant}`;
    },
    iconType() {
      const icons = {
        warning: 'fas fa-exclamation-triangle',
        danger: 'fas fa-exclamation-circle',
        info: 'fas fa-info-circle',
        success: 'fas fa-check-circle'
      };
      return icons[this.variant];
    },
    confirmButtonClass() {
      return `btn-${this.variant}`;
    }
  },
  methods: {
    handleConfirm() {
      if (!this.loading) {
        this.$emit('confirm');
      }
    },
    handleCancel() {
      if (!this.loading) {
        this.$emit('cancel');
      }
    }
  },
  watch: {
    isVisible(newValue) {
      if (newValue) {
        document.body.style.overflow = 'hidden';
      } else {
        document.body.style.overflow = '';
      }
    }
  },
  beforeUnmount() {
    document.body.style.overflow = '';
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 10px 20px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
  overflow: auto;
}

.modal-header {
  padding: 24px 24px 16px;
  text-align: center;
}

.modal-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
}

.modal-icon-warning {
  background-color: #fef3cd;
  color: #856404;
}

.modal-icon-danger {
  background-color: #f8d7da;
  color: #721c24;
}

.modal-icon-info {
  background-color: #d1ecf1;
  color: #0c5460;
}

.modal-icon-success {
  background-color: #d4edda;
  color: #155724;
}

.modal-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

.modal-body {
  padding: 16px 24px;
  text-align: center;
}

.modal-body p {
  margin: 0 0 12px;
  font-size: 1rem;
  color: #666;
  line-height: 1.5;
}

.modal-body p:last-child {
  margin-bottom: 0;
}

.modal-details {
  font-size: 0.9rem;
  color: #999;
  font-style: italic;
}

.modal-footer {
  padding: 16px 24px 24px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn {
  padding: 10px 24px;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-cancel {
  background-color: #e0e0e0;
  color: #333;
}

.btn-cancel:hover:not(:disabled) {
  background-color: #d0d0d0;
}

.btn-confirm {
  color: white;
}

.btn-warning {
  background-color: #ffc107;
}

.btn-warning:hover:not(:disabled) {
  background-color: #e0a800;
}

.btn-danger {
  background-color: #dc3545;
}

.btn-danger:hover:not(:disabled) {
  background-color: #c82333;
}

.btn-info {
  background-color: #17a2b8;
}

.btn-info:hover:not(:disabled) {
  background-color: #138496;
}

.btn-success {
  background-color: #28a745;
}

.btn-success:hover:not(:disabled) {
  background-color: #218838;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Animaciones del modal */
.modal-fade-enter-active, .modal-fade-leave-active {
  transition: opacity 0.3s;
}

.modal-fade-enter-from, .modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-container {
  animation: modal-slide-in 0.3s;
}

.modal-fade-leave-active .modal-container {
  animation: modal-slide-out 0.3s;
}

@keyframes modal-slide-in {
  from {
    transform: translateY(-50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes modal-slide-out {
  from {
    transform: translateY(0);
    opacity: 1;
  }
  to {
    transform: translateY(-50px);
    opacity: 0;
  }
}
</style>