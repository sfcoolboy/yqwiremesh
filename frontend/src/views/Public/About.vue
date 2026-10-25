<template>
  <div>
    <PublicHeader />
    <div class="page-head">
      <h1>About YQ Metals</h1>
      <p>Your trusted partner for steel grating, wire mesh, and guardrail solutions.</p>
    </div>

    <section class="section">
      <div class="container narrow">
        <h2>Who We Are</h2>
        <p>Anping YQ Metals Int&apos;l Co., Ltd. is a professional manufacturer and exporter based in Anping County, Hebei Province — known as the &apos;Hometown of Wire Mesh&apos; in China. Since 2005, we have been producing high-quality steel grating, galvanized steel wire products, and metal guardrails for clients across Southeast Asia, Europe, and beyond.</p>
        <p>Our factory covers over 20,000 square meters with modern CNC cutting machines, automated welding lines, and hot-dip galvanizing facilities. Every product undergoes strict quality inspection before shipment.</p>
      </div>
    </section>

    <section class="section alt">
      <div class="container narrow">
        <h2>Why Choose Us</h2>
        <div class="why-grid">
          <div class="why-item"><strong>18+ Years</strong><span>of manufacturing experience</span></div>
          <div class="why-item"><strong>ISO Certified</strong><span>Quality management system</span></div>
          <div class="why-item"><strong>50+ Countries</strong><span>Served across SE Asia & Europe</span></div>
          <div class="why-item"><strong>OEM/ODM</strong><span>Custom sizes and specs welcome</span></div>
        </div>
      </div>
    </section>

    <!-- Contact form -->
    <section class="section contact-section">
      <div class="container">
        <h2>Contact Us</h2>
        <p>Send us your inquiry and our team will get back to you within 24 hours.</p>
        <div class="form-grid">
          <div class="form-left">
            <h3>Get in Touch</h3>
            <p><strong>Company:</strong> Anping YQ Metals Int&apos;l Co., Ltd.</p>
            <p><strong>Location:</strong> Anping County, Hengshui, Hebei, China</p>
            <p><strong>Email:</strong> sophia@yqwiremesh.com</p>
            <p><strong>Main Markets:</strong> Southeast Asia · Europe</p>
          </div>
          <form class="form-right" @submit.prevent="submit">
            <div class="field">
              <label>Full Name *</label>
              <input v-model="form.name" type="text" required placeholder="Your name" />
            </div>
            <div class="field">
              <label>Company</label>
              <input v-model="form.company" type="text" placeholder="Company name" />
            </div>
            <div class="field">
              <label>Email *</label>
              <input v-model="form.email" type="email" required placeholder="your@email.com" />
            </div>
            <div class="field">
              <label>Country / Region</label>
              <select v-model="form.country">
                <option value="">Select a country...</option>
                <optgroup label="Southeast Asia">
                  <option>Vietnam</option><option>Thailand</option><option>Malaysia</option>
                  <option>Indonesia</option><option>Philippines</option><option>Singapore</option>
                  <option>Myanmar</option><option>Cambodia</option><option>Laos</option>
                  <option>Brunei</option><option>Timor-Leste</option>
                </optgroup>
                <optgroup label="Europe">
                  <option>Germany</option><option>United Kingdom</option><option>France</option>
                  <option>Spain</option><option>Italy</option><option>Netherlands</option>
                  <option>Belgium</option><option>Switzerland</option><option>Austria</option>
                  <option>Sweden</option><option>Norway</option><option>Denmark</option>
                  <option>Finland</option><option>Poland</option><option>Czech Republic</option>
                  <option>Hungary</option><option>Portugal</option><option>Greece</option>
                  <option>Ireland</option><option>Romania</option><option>Bulgaria</option>
                  <option>Croatia</option><option>Slovenia</option><option>Slovakia</option>
                  <option>Serbia</option><option>Ukraine</option><option>White Russia</option>
                  <option>Lithuania</option><option>Latvia</option><option>Estonia</option>
                  <option>Malta</option><option>Luxembourg</option><option>Iceland</option>
                  <option>Albania</option><option>Bosnia and Herzegovina</option>
                  <option>Montenegro</option><option>North Macedonia</option>
                  <option>Moldova</option><option>Azerbaijan</option><option>Armenia</option>
                  <option>Georgia</option><option>Turkey</option><option>Cyprus</option>
                </optgroup>
              </select>
            </div>
            <div class="field">
              <label>Interested Product</label>
              <select v-model="form.categoryId">
                <option :value="null">Select a product...</option>
                <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.nameEn }}</option>
              </select>
            </div>
            <div class="field">
              <label>Inquiry Message *</label>
              <textarea v-model="form.message" rows="5" required placeholder="Describe your requirements, specifications, quantity..."></textarea>
            </div>
            <button type="submit" class="btn-submit" :disabled="submitting">
              {{ submitting ? 'Sending...' : 'Submit Inquiry' }}
            </button>
            <p v-if="success" class="success-msg">Thank you! We will contact you shortly.</p>
            <p v-if="error" class="error-msg">{{ error }}</p>
          </form>
        </div>
      </div>
    </section>

    <PublicFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { api } from '../../api'

const categories = ref<any[]>([])
const submitting = ref(false)
const success = ref(false)
const error = ref('')

const form = ref({ name: '', company: '', email: '', country: '', categoryId: null as number | null, message: '' })

onMounted(async () => { categories.value = await api.categories.tree() })

async function submit() {
  submitting.value = true
  success.value = false
  error.value = ''
  try {
    await api.inquiries.publicSubmit(form.value)
    success.value = true
    form.value = { name: '', company: '', email: '', country: '', categoryId: null, message: '' }
  } catch (e: any) {
    error.value = e.message || 'Failed to submit. Please try again.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page-head { background: var(--yq-gray-900); color: #fff; padding: 48px 0; text-align: center; }
.page-head h1 { font-size: 36px; margin-bottom: 8px; }
.page-head p { opacity: 0.7; }
.section { padding: 64px 0; }
.section.alt { background: var(--yq-gray-50); }
.container { max-width: 1200px; margin: 0 auto; padding: 0 24px; }
.container.narrow { max-width: 800px; }
.container.narrow h2 { font-size: 24px; margin-bottom: 16px; }
.container.narrow p { font-size: 15px; color: #444; line-height: 1.8; margin-bottom: 12px; }
.why-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-top: 20px; }
.why-item { background: #fff; border: 1px solid var(--yq-gray-200); border-radius: 6px; padding: 16px; text-align: center; }
.why-item strong { display: block; font-size: 18px; color: var(--yq-red); margin-bottom: 4px; }
.why-item span { font-size: 12px; color: #888; }
.contact-section h2 { font-size: 24px; text-align: center; margin-bottom: 8px; }
.contact-section > .container > p { text-align: center; color: #666; margin-bottom: 32px; }
.form-grid { display: grid; grid-template-columns: 1fr 1.4fr; gap: 40px; }
.form-left h3 { font-size: 18px; margin-bottom: 16px; }
.form-left p { font-size: 14px; color: #555; line-height: 2; }
.form-right { display: flex; flex-direction: column; gap: 14px; }
.field label { display: block; font-size: 13px; font-weight: 600; color: var(--yq-gray-800); margin-bottom: 4px; }
.field input, .field select, .field textarea {
  width: 100%; padding: 10px 12px; border: 1px solid var(--yq-gray-200); border-radius: 4px;
  font-size: 14px; font-family: inherit; transition: border-color 0.15s;
}
.field input:focus, .field select:focus, .field textarea:focus { outline: none; border-color: var(--yq-red); }
.btn-submit {
  background: var(--yq-red); color: #fff; border: none; padding: 12px 28px; border-radius: 4px;
  font-size: 15px; font-weight: 600; cursor: pointer; transition: background 0.15s;
}
.btn-submit:hover { background: var(--yq-red-light); }
.btn-submit:disabled { opacity: 0.6; }
.success-msg { color: green; font-size: 14px; }
.error-msg { color: red; font-size: 14px; }
@media (max-width: 768px) {
  .why-grid { grid-template-columns: 1fr 1fr; }
  .form-grid { grid-template-columns: 1fr; }
}
</style>
