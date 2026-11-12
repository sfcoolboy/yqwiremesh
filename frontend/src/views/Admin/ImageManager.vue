<template>
  <div>
    <div class="header">
      <h1>图片管理</h1>
    </div>

    <div class="tabs">
      <button :class="{ active: tab === 'factory' }" @click="tab = 'factory'">工厂图片</button>
      <button :class="{ active: tab === 'logo' }" @click="tab = 'logo'">导航 Logo</button>
      <button :class="{ active: tab === 'home-img' }" @click="tab = 'home-img'">首页配图</button>
      <button :class="{ active: tab === 'page-hero' }" @click="tab = 'page-hero'">页面背景图</button>
    </div>


    <!-- 工厂图片 -->
    <div v-if="tab === 'factory'" class="section">
      <div class="section-header">
        <h2>工厂图片</h2>
        <button @click="openModal('factory')" class="btn-add">+ 添加图片</button>
      </div>
      <p class="hint">比例建议 4:3（560×420），点击可放大查看原图。</p>
      <div class="list">
        <div v-for="img in factoryImages" :key="img.id" class="row">
          <img v-if="img.imageUrl" :src="img.imageUrl" class="thumb" @click="preview(img.imageUrl)" />
          <span v-else class="thumb empty">未上传</span>
          <span class="caption">{{ img.caption || '（无说明）' }}</span>
          <span class="meta">排序: {{ img.sort }}</span>
          <button @click="openModal('factory', img)" class="btn-edit">编辑</button>
          <button @click="requestDelete(img)" class="btn-del">删除</button>
        </div>
        <div v-if="!factoryImages.length" class="empty-row">暂无图片</div>
      </div>
    </div>

    <!-- 导航 Logo -->
    <div v-if="tab === 'logo'" class="section">
      <div class="section-header">
        <h2>导航 Logo</h2>
        <button @click="openModal('logo')" class="btn-add">+ 上传 Logo</button>
      </div>
      <p class="hint">比例 1:1，推荐尺寸 480×480，前台显示 48×48。</p>
      <div class="list">
        <div v-for="img in logoImages" :key="img.id" class="row">
          <img v-if="img.imageUrl" :src="img.imageUrl" class="thumb" style="border-radius: 50%;" @click="preview(img.imageUrl)" />
          <span v-else class="thumb empty">未上传</span>
          <span class="meta">排序: {{ img.sort }}</span>
          <button @click="openModal('logo', img)" class="btn-edit">编辑</button>
          <button @click="requestDelete(img)" class="btn-del">删除</button>
        </div>
        <div v-if="!logoImages.length" class="empty-row">暂无 Logo</div>
      </div>
    </div>

    <!-- 首页配图 -->
    <div v-if="tab === 'home-img'" class="section">
      <div class="section-header">
        <h2>首页配图</h2>
        <button @click="openModal('home-img')" class="btn-add">+ 添加配图</button>
      </div>
      <p class="hint">用于首页 About、FAQ 和 Quality Control 区域配图。</p>
      
      <!-- About 配图 -->
      <div class="img-group">
        <h3>首页 About 配图</h3>
        <p class="hint">比例 1:1，推荐尺寸 432×432</p>
        <div class="list">
          <div v-for="img in homeAboutImages" :key="img.id" class="row">
            <img v-if="img.imageUrl" :src="img.imageUrl" class="thumb" @click="preview(img.imageUrl)" />
            <span v-else class="thumb empty">未上传</span>
            <span class="caption">{{ img.caption }}</span>
            <button @click="openModal('home-img', img)" class="btn-edit">编辑</button>
            <button @click="requestDelete(img)" class="btn-del">删除</button>
          </div>
          <div v-if="!homeAboutImages.length" class="empty-row">暂无配图</div>
        </div>
      </div>

      <!-- FAQ 配图 -->
      <div class="img-group">
        <h3>首页 FAQ 配图</h3>
        <p class="hint">比例 3:4，推荐尺寸 420×560</p>
        <div class="list">
          <div v-for="img in homeFaqImages" :key="img.id" class="row">
            <img v-if="img.imageUrl" :src="img.imageUrl" class="thumb" @click="preview(img.imageUrl)" />
            <span v-else class="thumb empty">未上传</span>
            <span class="caption">{{ img.caption }}</span>
            <button @click="openModal('home-img', img)" class="btn-edit">编辑</button>
            <button @click="requestDelete(img)" class="btn-del">删除</button>
          </div>
          <div v-if="!homeFaqImages.length" class="empty-row">暂无配图</div>
        </div>
      </div>

      <!-- Quality 配图 -->
      <div class="img-group">
        <h3>首页 Quality 配图</h3>
        <p class="hint">比例 2:1，推荐尺寸 1920×960，用于 Quality Control 模块背景。</p>
        <div class="list">
          <div v-for="img in homeQualityImages" :key="img.id" class="row">
            <img v-if="img.imageUrl" :src="img.imageUrl" class="thumb quality-thumb" @click="preview(img.imageUrl)" />
            <span v-else class="thumb empty">未上传</span>
            <span class="caption">{{ img.caption }}</span>
            <button @click="openModal('home-img', img)" class="btn-edit">编辑</button>
            <button @click="requestDelete(img)" class="btn-del">删除</button>
          </div>
          <div v-if="!homeQualityImages.length" class="empty-row">暂无配图</div>
        </div>
      </div>
    </div>

    <!-- 页面背景图 -->
    <div v-if="tab === 'page-hero'" class="section">
      <div class="section-header">
        <h2>页面背景图</h2>
        <button @click="openModal('page-hero')" class="btn-add">+ 添加背景图</button>
      </div>
      <p class="hint">比例 2:1（1920×960），用于各一级页面 Hero 区域背景。</p>
      <div class="list">
        <div v-for="img in pageHeroImages" :key="img.id" class="row">
          <img v-if="img.imageUrl" :src="img.imageUrl" class="thumb" @click="preview(img.imageUrl)" />
          <span v-else class="thumb empty">未上传</span>
          <span class="caption">{{ getPageLabel(img.page) }}</span>
          <span class="meta">排序: {{ img.sort }}</span>
          <button @click="openModal('page-hero', img)" class="btn-edit">编辑</button>
          <button @click="requestDelete(img)" class="btn-del">删除</button>
        </div>
        <div v-if="!pageHeroImages.length" class="empty-row">暂无背景图</div>
      </div>
    </div>

    <!-- 弹窗 -->
    <div v-if="modalOpen" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h2>{{ modalTitle }}</h2>
        
        <!-- Logo 专用 -->
        <template v-if="modalType === 'logo'">
          <div class="field">
            <label>Logo 图片</label>
            <input type="file" accept="image/*" @change="onFileSelect" />
            <div v-if="form.imageUrl" class="preview-wrap">
              <img :src="form.imageUrl" class="preview" />
              <p class="size-hint">推荐 480×480 (1:1)，前台显示 48×48</p>
            </div>
          </div>
          <label class="checkbox-row">
            <span class="checkbox-label">在工厂实景页面展示</span>
            <input v-model="form.showOnFactory" type="checkbox" class="checkbox-input" />
          </label>
        </template>

        <!-- 工厂图片 -->
        <template v-if="modalType === 'factory'">
          <div class="field">
            <label>工厂图片</label>
            <input type="file" accept="image/*" @change="onFileSelect" />
            <div v-if="form.imageUrl" class="preview-wrap">
              <img :src="form.imageUrl" class="preview" />
              <p class="size-hint">推荐 560×420 (4:3)，点击可放大查看</p>
            </div>
          </div>
          <div class="field"><label>说明文字</label><input v-model="form.caption" placeholder="如：Production Workshop" /></div>
          <div class="field"><label>排序</label><input v-model.number="form.sort" type="number" /></div>
          <label class="checkbox-row">
            <span class="checkbox-label">在工厂实景页面展示</span>
            <input v-model="form.showOnFactory" type="checkbox" class="checkbox-input" />
          </label>
        </template>

        <!-- 首页配图 -->
        <template v-if="modalType === 'home-img'">
          <div class="field">
            <label>配图图片</label>
            <input type="file" accept="image/*" @change="onFileSelect" />
            <p class="size-hint">About 配图: 432×432 (1:1) | FAQ 配图: 420×560 (3:4) | Quality 配图: 1920×960 (2:1)</p>
            <div v-if="form.imageUrl" class="preview-wrap">
              <img :src="form.imageUrl" class="preview" />
            </div>
          </div>
          <div class="field">
            <label>配图类型</label>
            <select v-model="form.page">
              <option value="about">首页 About 配图</option>
              <option value="faq">首页 FAQ 配图</option>
              <option value="quality">首页 Quality 配图</option>
            </select>
          </div>
          <div class="field"><label>说明文字</label><input v-model="form.caption" placeholder="可选" /></div>
          <label class="checkbox-row">
            <span class="checkbox-label">在工厂实景页面展示</span>
            <input v-model="form.showOnFactory" type="checkbox" class="checkbox-input" />
          </label>
        </template>

        <!-- 页面背景图 -->
        <template v-if="modalType === 'page-hero'">
          <div class="field">
            <label>背景图片</label>
            <input type="file" accept="image/*" @change="onFileSelect" />
            <div v-if="form.imageUrl" class="preview-wrap">
              <img :src="form.imageUrl" class="preview" />
              <p class="size-hint">推荐 1920×960 (2:1)</p>
            </div>
          </div>
          <div class="field">
            <label>关联页面</label>
            <select v-model="form.page">
              <option value="home">首页</option>
              <option value="products">Products</option>
              <option value="about">About</option>
              <option value="news">News</option>
              <option value="factory">Factory</option>
            </select>
          </div>
          <div class="field"><label>说明文字</label><input v-model="form.caption" placeholder="可选" /></div>
          <label class="checkbox-row">
            <span class="checkbox-label">在工厂实景页面展示</span>
            <input v-model="form.showOnFactory" type="checkbox" class="checkbox-input" />
          </label>
        </template>

        <div v-if="saveError" class="save-error">{{ saveError }}</div>
        <div v-if="saveSuccess" class="save-success">已保存</div>
        <div class="actions">
          <button @click="save" class="btn-save" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
          <button @click="closeModal" class="btn-cancel">取消</button>
        </div>
      </div>
    </div>

    <!-- 删除确认 -->
    <ConfirmDialog
      :visible="!!deleteTarget"
      title="删除图片"
      :message="`确定要删除图片「${deleteTarget?.caption || deleteTarget?.page || '未命名'}」吗？删除后不可恢复。`"
      :loading="deleting"
      @cancel="deleteTarget = null"
      @confirm="doDelete"
    />

    <!-- 图片预览 -->
    <div v-if="previewImg" class="preview-overlay" @click="previewImg = null">
      <img :src="previewImg" class="preview-full" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { api, IMAGE_SIZE_LIMIT_MESSAGE, MAX_IMAGE_UPLOAD_SIZE } from '../../api'
import ConfirmDialog from '../../components/ConfirmDialog.vue'

const tab = ref('factory')

const factoryImages = ref<any[]>([])
const logoImages = ref<any[]>([])
const homeAboutImages = ref<any[]>([])
const homeFaqImages = ref<any[]>([])
const homeQualityImages = ref<any[]>([])
const pageHeroImages = ref<any[]>([])

const modalOpen = ref(false)
const modalType = ref('')
const editing = ref<any>(null)
const saving = ref(false)
const saveError = ref('')
const saveSuccess = ref(false)
const deleteTarget = ref<any>(null)
const deleting = ref(false)
type ImageForm = {
  imageUrl: string
  caption: string
  sort: number
  page: string
  type: string
  showOnFactory: boolean
}
const form = ref<ImageForm>({ imageUrl: '', caption: '', sort: 0, page: 'home', type: '', showOnFactory: false })
const previewImg = ref<string | null>(null)

const modalTitle = computed(() => {
  const titles: Record<string, string> = {
    'factory': editing.value?.id ? '编辑工厂图片' : '添加工厂图片',
    'logo': editing.value?.id ? '编辑 Logo' : '上传 Logo',
    'home-img': editing.value?.id ? '编辑配图' : '添加配图',
    'page-hero': editing.value?.id ? '编辑背景图' : '添加背景图'
  }
  return titles[modalType.value] || ''
})

const getPageLabel = (page: string) => {
  const labels: Record<string, string> = {
    'home': '首页',
    'products': 'Products',
    'about': 'About',
    'news': 'News',
    'factory': 'Factory'
  }
  return labels[page] || page
}

async function load() {
  try {
    const all = await api.factoryImages.adminList()
    factoryImages.value = all.filter((i: any) => i.type === 'factory')
    logoImages.value = all.filter((i: any) => i.type === 'logo')
    homeAboutImages.value = all.filter((i: any) => i.type === 'home-img' && i.page === "about")
    homeFaqImages.value = all.filter((i: any) => i.type === 'home-img' && i.page === "faq")
    homeQualityImages.value = all.filter((i: any) => i.type === 'home-img' && i.page === "quality")
    pageHeroImages.value = all.filter((i: any) => i.type === 'page-hero')
  } catch (e: any) {
    saveError.value = '加载失败: ' + e.message
  }
}

function openModal(type: string, img?: any) {
  modalType.value = type
  editing.value = img || null
  if (img) {
    form.value = {
      imageUrl: img.imageUrl ?? '',
      caption: img.caption ?? '',
      sort: img.sort ?? 0,
      page: img.page ?? (modalType.value === 'home-img' ? 'about' : 'home'),
      type: img.type ?? type, showOnFactory: img.showOnFactory ?? false
    }
  } else {
    form.value = {
      imageUrl: '',
      caption: '',
      sort: 1,
      page: modalType.value === 'home-img' ? 'about' : 'home',
      type, showOnFactory: false
    }
  }
  saveError.value = ''
  saveSuccess.value = false
  modalOpen.value = true
}

function closeModal() {
  modalOpen.value = false
  saveSuccess.value = false
}

async function onFileSelect(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (file.size > MAX_IMAGE_UPLOAD_SIZE) {
    saveError.value = IMAGE_SIZE_LIMIT_MESSAGE
    return
  }
  try {
    const res = await api.upload.image(file)
    form.value.imageUrl = res.url
  } catch (err: any) {
    saveError.value = '上传失败: ' + err.message
  }
}

async function save() {
  saving.value = true
  saveError.value = ''
  saveSuccess.value = false
  try {
    if (!form.value.imageUrl) {
      saveError.value = '请先上传图片'
      saving.value = false
      return
    }
    form.value.type = modalType.value
    if (editing.value?.id) {
      await api.factoryImages.update(editing.value.id, form.value)
    } else {
      await api.factoryImages.create(form.value)
    }
    saveSuccess.value = true
    await load()
    setTimeout(() => closeModal(), 900)
  } catch (err: any) {
    saveError.value = '保存失败: ' + err.message
  } finally {
    saving.value = false
  }
}

function requestDelete(img: any) {
  deleteTarget.value = img
}

async function doDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await api.factoryImages.delete(deleteTarget.value.id)
    deleteTarget.value = null
    await load()
  } catch (err: any) {
    alert('删除失败: ' + err.message)
  } finally {
    deleting.value = false
  }
}

function preview(url: string) {
  previewImg.value = url
}

onMounted(load)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
h1 { font-size: 22px; }
.tabs { display: flex; gap: 8px; margin-bottom: 24px; border-bottom: 1px solid var(--yq-gray-200); padding-bottom: 0; }
.tabs button { padding: 10px 20px; border: none; background: none; cursor: pointer; font-size: 14px; font-weight: 600; color: var(--yq-gray-600); border-bottom: 2px solid transparent; margin-bottom: -1px; transition: all 0.2s; }
.tabs button.active { color: var(--yq-red); border-bottom-color: var(--yq-red); }
.section { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 20px; margin-bottom: 24px; }
.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.section-header h2 { font-size: 16px; margin: 0; }
.hint { font-size: 12px; color: #888; margin-bottom: 16px; }
.btn-add { background: var(--yq-red); color: #fff; border: none; padding: 8px 16px; border-radius: 4px; font-size: 13px; cursor: pointer; }
.list { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 8px; }
.row { display: flex; align-items: center; gap: 12px; padding: 10px 12px; border-radius: 4px; font-size: 14px; }
.row:hover { background: var(--yq-gray-50); }
.thumb { width: 80px; height: 60px; border-radius: 4px; object-fit: cover; flex-shrink: 0; border: 1px solid var(--yq-gray-200); cursor: pointer; transition: transform 0.2s; }
.thumb:hover { transform: scale(1.05); }
.thumb.empty { display: flex; align-items: center; justify-content: center; background: var(--yq-gray-100); color: #999; font-size: 11px; }
.quality-thumb { width: 96px; height: 48px; }
.caption { flex: 1; font-weight: 600; }
.meta { font-size: 12px; color: #999; }
.btn-edit, .btn-del { padding: 4px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 12px; cursor: pointer; background: #fff; }
.btn-del { color: red; border-color: #fcc; }
.empty-row { text-align: center; color: #999; padding: 24px; }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100; display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; padding: 24px; border-radius: 8px; width: 480px; max-width: 90vw; max-height: 90vh; overflow-y: auto; }
.modal h2 { margin-bottom: 16px; }
.field { margin-bottom: 12px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.field input, .field select { width: 100%; padding: 8px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; }
.preview-wrap { margin-top: 8px; }
.preview { width: 100%; max-height: 220px; object-fit: cover; border-radius: 4px; margin-top: 8px; border: 1px solid #ddd; }
.size-hint { font-size: 11px; color: #888; margin-top: 4px; }
.save-error { color: red; font-size: 13px; margin-bottom: 12px; }
.save-success { color: #155724; font-size: 13px; margin-bottom: 12px; }
.actions { display: flex; gap: 8px; margin-top: 16px; }
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-cancel { background: var(--yq-gray-200); border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; }
.preview-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.9); z-index: 200; display: flex; align-items: center; justify-content: center; }
.preview-full { max-width: 90vw; max-height: 90vh; border-radius: 4px; }
.img-group { margin-bottom: 32px; }
.img-group:last-child { margin-bottom: 0; }
.img-group h3 { font-size: 15px; margin: 0 0 8px; color: var(--yq-gray-800); }
.checkbox-row { display: flex; align-items: center; gap: 8px; margin-top: 8px; line-height: 1; cursor: pointer; user-select: none; }
.checkbox-row { height: 20px; }
.checkbox-label { font-size: 13px; font-weight: 600; line-height: 1; color: var(--yq-gray-700); white-space: nowrap; }
.checkbox-input { display: block; flex: none; width: 16px; height: 16px; min-width: 16px; margin: 0; padding: 0; accent-color: var(--yq-red); cursor: pointer; }
</style>
