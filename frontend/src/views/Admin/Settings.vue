<template>
  <div>
    <div class="header"><h1>系统设置</h1></div>

    <div class="panel">
      <h2>首页与公司信息</h2>
      <p class="hint">首页背景图用于首页首屏（顶部大图）区域。上传或移除图片后会自动保存，前台刷新页面即可生效。</p>
      <div class="field">
        <label>首页主标题</label>
        <input v-model="form['hero.title']" placeholder="Professional Metal Grating & Wire Mesh Solutions" />
      </div>
      <div class="field">
        <label>首页副标题</label>
        <textarea v-model="form['hero.subtitle']" rows="2" placeholder="Trusted by builders and contractors worldwide since 2005." />
      </div>
      <div class="field">
        <label>首页背景图</label>
        <input type="file" accept="image/*" @change="onHeroImage" />
        <img v-if="form['hero.image']" :src="form['hero.image']" class="preview" />
        <button v-if="form['hero.image']" class="btn-clear" @click="removeHeroImage">移除背景图</button>
      </div>
      <div class="field"><label>联系电话</label><input v-model="form['company.phone']" placeholder="+86 ..." /></div>
      <div class="field"><label>公司地址</label><input v-model="form['company.address']" placeholder="No. 229, Chengnan Industrial Park, Anping County, Hebei, China" /></div>
      <div class="field"><label>主要市场</label><input v-model="form['company.markets']" placeholder="" /></div>
    </div>
    <div class="panel">
      <h2>公司信息（GEO）</h2>
      <p class="hint">影响前台 schema.org 元数据和 AI 引用。可留空，前台会使用硬编码默认值。</p>
      <div class="field"><label>公司法定名称</label><input v-model="form['company.name']" placeholder="Anping Yuanquan Metals Int'l Co., Ltd." /></div>
      <div class="field"><label>品牌/简称</label><input v-model="form['company.trading.name']" placeholder="YQ Metals" /></div>
      <div class="field"><label>认证（逗号分隔）</label><input v-model="form['company.certifications']" placeholder="ISO 9001:2015, ASTM A36, BS 4592" /></div>
      <div class="field"><label>年产能</label><input v-model="form['company.annual.capacity']" placeholder="15,000 tons" /></div>
      <div class="field"><label>工厂面积</label><input v-model="form['company.factory.area']" placeholder="20,000 m²" /></div>
      <div class="field"><label>主营产品</label><textarea v-model="form['company.products']" rows="2" placeholder="Steel grating, galvanized steel wire, wire mesh panels"></textarea></div>
    </div>

    <div class="panel">
      <h2>首页内容</h2>
      <p class="hint">首页“为什么选择我们”“品质保证”“常见问题”区块的文案，留空时前台使用默认英文内容。</p>
      <div class="field"><label>优势区块标题</label><input v-model="form['home.why.title']" placeholder="Your trusted partner for industrial metal products" /></div>
      <div v-for="i in 4" :key="'why' + i" class="sub-group">
        <div class="sub-title">优势 {{ i }}</div>
        <div class="field"><label>标题</label><input v-model="form['home.why.' + i + '.title']" /></div>
        <div class="field"><label>描述</label><textarea v-model="form['home.why.' + i + '.desc']" rows="2" /></div>
      </div>
      <div class="field"><label>品质保证标题</label><input v-model="form['home.quality.title']" placeholder="Quality first. Service foremost." /></div>
      <div class="field"><label>品质保证描述</label><textarea v-model="form['home.quality.desc']" rows="3" /></div>
      <div v-for="i in 4" :key="'faq' + i" class="sub-group">
        <div class="sub-title">常见问题 {{ i }}</div>
        <div class="field"><label>问题</label><input v-model="form['home.faq.' + i + '.q']" /></div>
        <div class="field"><label>回答</label><textarea v-model="form['home.faq.' + i + '.a']" rows="2" /></div>
      </div>
    </div>

    <div v-if="saveError" class="save-error">{{ saveError }}</div>
    <div v-if="saved" class="save-success">已保存</div>
    <div class="actions">
      <button @click="save" class="btn-save" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../api'

const form = ref<Record<string, string>>({
  'hero.title': 'Professional Metal Grating & Wire Mesh Solutions',
  'hero.subtitle': 'Trusted by builders and contractors worldwide since 2005.',
  'hero.image': '',
  'company.phone': '',
  'company.address': 'No. 229, Chengnan Industrial Park, Anping County, Hebei, China',
  'company.markets': '',
  'company.name': '',
  'company.trading.name': '',
  'company.certifications': '',
  'company.annual.capacity': '',
  'company.factory.area': '',
  'company.products': '',
  'home.why.title': '',
  'home.why.1.title': '', 'home.why.1.desc': '',
  'home.why.2.title': '', 'home.why.2.desc': '',
  'home.why.3.title': '', 'home.why.3.desc': '',
  'home.why.4.title': '', 'home.why.4.desc': '',
  'home.quality.title': '', 'home.quality.desc': '',
  'home.faq.1.q': '', 'home.faq.1.a': '',
  'home.faq.2.q': '', 'home.faq.2.a': '',
  'home.faq.3.q': '', 'home.faq.3.a': '',
  'home.faq.4.q': '', 'home.faq.4.a': '',
})
const saving = ref(false)
const saved = ref(false)
const saveError = ref('')
const loaded = ref(false)

async function load() {
  try {
    const settings = await api.settings.get()
    form.value = { ...form.value, ...settings }
    loaded.value = true
  } catch (e: any) {
    saveError.value = '加载失败，请刷新页面后重试: ' + e.message
    loaded.value = false
  }
}

async function onHeroImage(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  try {
    const res = await api.upload.image(file)
    form.value['hero.image'] = res.url
    await save()
  } catch (err: any) {
    alert('上传失败: ' + err.message)
  }
}

async function removeHeroImage() {
  form.value['hero.image'] = ''
  await save()
}

async function save() {
  if (!loaded.value) {
    saveError.value = '设置尚未加载成功，为避免覆盖已有配置，请刷新页面后重试'
    return
  }
  saving.value = true
  saved.value = false
  saveError.value = ''
  try {
    await api.settings.save(form.value)
    saved.value = true
  } catch (e: any) {
    saveError.value = '保存失败: ' + e.message
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.header { margin-bottom: 20px; }
h1 { font-size: 22px; }
.panel { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 24px; max-width: 680px; margin-bottom: 20px; }
.panel h2 { font-size: 17px; margin-bottom: 6px; }
.hint { font-size: 13px; color: #888; margin-bottom: 20px; }
.field { margin-bottom: 14px; }
.field label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 4px; }
.field input, .field textarea { width: 100%; padding: 8px 10px; border: 1px solid var(--yq-gray-200); border-radius: 4px; font-size: 14px; font-family: inherit; }
.field input:focus, .field textarea:focus { outline: none; border-color: var(--yq-red); }
.preview { width: 100%; max-height: 180px; object-fit: cover; border-radius: 4px; margin-top: 8px; border: 1px solid #ddd; }
.btn-clear { margin-top: 8px; padding: 4px 12px; border: 1px solid #fcc; color: red; background: #fff; border-radius: 4px; font-size: 12px; cursor: pointer; }
.save-error { color: red; font-size: 13px; margin: 8px 0; }
.save-success { color: #155724; font-size: 13px; margin: 8px 0; }
.sub-group { border-top: 1px dashed var(--yq-gray-200); padding-top: 12px; margin-top: 12px; }
.sub-title { font-size: 12px; font-weight: 700; color: var(--yq-red); margin-bottom: 8px; }
.actions { margin-bottom: 32px; }
.btn-save { background: var(--yq-red); color: #fff; border: none; padding: 9px 24px; border-radius: 4px; font-size: 13px; cursor: pointer; }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
