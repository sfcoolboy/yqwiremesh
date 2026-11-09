<template>
  <div v-if="visible" class="confirm-overlay">
    <div class="confirm-dialog">
      <h3>{{ title }}</h3>
      <p class="confirm-message">{{ message }}</p>
      <div class="confirm-actions">
        <button class="btn-cancel" @click="$emit('cancel')">{{ cancelText }}</button>
        <button class="btn-confirm" :disabled="loading" @click="$emit('confirm')">
          {{ loading ? '处理中...' : confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  visible: boolean
  title?: string
  message: string
  confirmText?: string
  cancelText?: string
  loading?: boolean
}>(), {
  title: '确认删除',
  confirmText: '确定删除',
  cancelText: '取消',
  loading: false,
})

defineEmits<{ confirm: []; cancel: [] }>()
</script>

<style scoped>
.confirm-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
}
.confirm-dialog {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  width: 380px;
  max-width: 90vw;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
}
.confirm-dialog h3 {
  font-size: 17px;
  margin-bottom: 10px;
  color: var(--yq-gray-900);
}
.confirm-message {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
  margin-bottom: 20px;
}
.confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.btn-cancel {
  background: var(--yq-gray-200);
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}
.btn-confirm {
  background: var(--yq-red);
  color: #fff;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}
.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
