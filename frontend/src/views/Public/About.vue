<template>
  <div>
    <PublicHeader />
    <section class="page-hero">
      <div class="hero-bg" :style="heroImage ? { backgroundImage: `url(${heroImage})` } : {}" />
      <div class="yq-container">
        <span class="yq-kicker">Company Profile</span>
        <h1>About YQ Metals</h1>
        <p>Your trusted partner for steel grating and wire mesh solutions.</p>
      </div>
    </section>

    <section class="section">
      <div class="yq-container narrow">
        <h2>Who We Are</h2>
        <p>Anping Yuanquan Metals Int&apos;l Co., Ltd. is a professional manufacturer and exporter based in Anping County, Hebei Province — known as the &apos;Hometown of Wire Mesh&apos; in China. Since 2005, we have been producing high-quality steel grating and galvanized steel wire products for clients around the world.</p>
        <p>Our factory covers over 20,000 square meters with modern CNC cutting machines, automated welding lines, and hot-dip galvanizing facilities. Every product undergoes strict quality inspection before shipment.</p>
      </div>
    </section>

    <section class="section alt">
      <div class="yq-container narrow">
        <h2>Why Choose Us</h2>
        <div class="why-grid">
          <div class="why-item"><strong>18+ Years</strong><span>of manufacturing experience</span></div>
          <div class="why-item"><strong>ISO Certified</strong><span>Quality management system</span></div>
          <div class="why-item"><strong>50+ Countries</strong><span>Served worldwide</span></div>
          <div class="why-item"><strong>OEM/ODM</strong><span>Custom sizes and specs welcome</span></div>
        </div>
      </div>
    </section>


    <section class="section capabilities-section">
      <div class="yq-container narrow">
        <h2>Our Capabilities</h2>
        <p class="capabilities-lead">Quantified capacity, scale, and certifications — designed for buyers who need verifiable numbers.</p>
        <div class="capabilities-grid">
          <div class="capability-item" v-if="capacity"><strong>{{ capacity }}</strong><span>Annual Production Capacity</span></div>
          <div class="capability-item" v-if="factoryArea"><strong>{{ factoryArea }}</strong><span>Factory Area</span></div>
          <div class="capability-item" v-if="certifications"><strong>{{ certifications }}</strong><span>Certifications &amp; Standards</span></div>
          <div class="capability-item" v-if="products"><strong>{{ products }}</strong><span>Main Products</span></div>
        </div>
      </div>
    </section>    <!-- Contact form -->
    <section class="section contact-section">
      <div class="yq-container">
        <h2>Contact Us</h2>
        <p>Send us your inquiry and our team will get back to you within 24 hours.</p>
        <div class="form-grid">
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
                <option value="">Select a country / region...</option>
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
                  <option>Serbia</option><option>Ukraine</option><option>Belarus</option>
                  <option>Russia</option><option>Lithuania</option><option>Latvia</option><option>Estonia</option>
                  <option>Malta</option><option>Luxembourg</option><option>Iceland</option>
                  <option>Albania</option><option>Bosnia and Herzegovina</option>
                  <option>Montenegro</option><option>North Macedonia</option>
                  <option>Moldova</option><option>Azerbaijan</option><option>Armenia</option>
                  <option>Georgia</option><option>Turkey</option><option>Cyprus</option>
                </optgroup>
                <optgroup label="North America">
                  <option>United States</option><option>Canada</option><option>Mexico</option>
                </optgroup>
                <optgroup label="Oceania">
                  <option>Australia</option><option>New Zealand</option>
                </optgroup>
                <optgroup label="Middle East">
                  <option>Saudi Arabia</option><option>United Arab Emirates</option><option>Qatar</option>
                  <option>Kuwait</option><option>Oman</option><option>Bahrain</option><option>Jordan</option>
                  <option>Lebanon</option><option>Iraq</option><option>Iran</option><option>Israel</option>
                </optgroup>
                <optgroup label="South Asia">
                  <option>India</option><option>Pakistan</option><option>Bangladesh</option>
                  <option>Sri Lanka</option><option>Nepal</option>
                </optgroup>
                <optgroup label="East Asia">
                  <option>Japan</option><option>South Korea</option><option>Mongolia</option>
                </optgroup>
                <optgroup label="Central Asia">
                  <option>Kazakhstan</option><option>Uzbekistan</option><option>Kyrgyzstan</option>
                </optgroup>
                <optgroup label="South America">
                  <option>Brazil</option><option>Argentina</option><option>Chile</option>
                  <option>Colombia</option><option>Peru</option><option>Ecuador</option><option>Uruguay</option>
                </optgroup>
                <optgroup label="Africa">
                  <option>South Africa</option><option>Egypt</option><option>Nigeria</option>
                  <option>Kenya</option><option>Morocco</option><option>Algeria</option>
                  <option>Ghana</option><option>Tanzania</option><option>Ethiopia</option>
                </optgroup>
                <optgroup label="Other">
                  <option>Other Countries / Regions</option>
                </optgroup>
              </select>
            </div>
            <div class="field">
              <label>Interested Product</label>
              <input v-model="form.interestedProduct" type="text" placeholder="e.g. Steel Grating 25x25, Galvanized Wire" />
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
import { ref, computed, onMounted } from 'vue'
import PublicHeader from '../../components/PublicHeader.vue'
import PublicFooter from '../../components/PublicFooter.vue'
import { breadcrumb, localBusiness, setJsonLd } from '../../composables/useMeta'
import { api } from '../../api'

const settings = ref<Record<string, string>>({})

const submitting = ref(false)
const success = ref(false)
const error = ref('')
const heroImage = ref<string | null>(null)

const capacity = computed(() => settings.value['company.annual.capacity'] || '')
const factoryArea = computed(() => settings.value['company.factory.area'] || '')
const certifications = computed(() => settings.value['company.certifications'] || '')
const products = computed(() => settings.value['company.products'] || '')

const form = ref({ name: '', company: '', email: '', country: '', interestedProduct: '', message: '' })

onMounted(async () => {
    
  try {
    settings.value = await api.settings.publicGet()

  } catch {
    settings.value = {}
  }

  try {
    const data = await api.pageHeroes.getByPage('about')
    if (data && data.imageUrl) heroImage.value = data.imageUrl
  } catch { heroImage.value = null }

  setJsonLd([localBusiness(settings.value), breadcrumb(['Home', 'About'], '/about')])
})

async function submit() {
  submitting.value = true
  success.value = false
  error.value = ''
  try {
    await api.inquiries.publicSubmit(form.value)
    success.value = true
    form.value = { name: '', company: '', email: '', country: '', interestedProduct: '', message: '' }
  } catch (e: any) {
    error.value = e.message || 'Failed to submit. Please try again.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.section { padding: 64px 0; }
.section.alt { background: var(--yq-gray-50); }
.narrow { max-width: 800px; }
.narrow h2 { font-size: clamp(26px, 3vw, 36px); margin-bottom: 16px; }
.narrow p { font-size: 15px; color: var(--yq-gray-600); line-height: 1.8; margin-bottom: 12px; }
.why-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-top: 20px; }
.why-item { background: #fff; border: 1px solid var(--yq-gray-200); border-top: 3px solid var(--yq-red); padding: 20px 16px; text-align: center; }
.why-item strong { display: block; font-size: 20px; color: var(--yq-red); margin-bottom: 4px; }
.why-item span { font-size: 12px; color: #888; }
.contact-section h2 { font-size: 24px; text-align: center; margin-bottom: 8px; }
.contact-section > .yq-container > p { text-align: center; color: #666; margin-bottom: 32px; }
.form-grid { display: grid; grid-template-columns: 1fr; gap: 0; max-width: 720px; margin: 0 auto; }
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
.capabilities-section { background: var(--yq-gray-50); padding: 72px 0; }
.capabilities-section h2 { font-size: clamp(24px, 3vw, 36px); margin-bottom: 10px; }
.capabilities-lead { font-size: 15px; color: var(--yq-gray-600); line-height: 1.7; margin-bottom: 36px; }
.capabilities-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-top: 20px; }
.capability-item { background: #fff; border: 1px solid var(--yq-gray-200); border-top: 3px solid var(--yq-red); padding: 20px 16px; text-align: center; }
.capability-item strong { display: block; font-size: 20px; color: var(--yq-red); margin-bottom: 4px; }
.capability-item span { font-size: 12px; color: #888; }
@media (max-width: 768px) {
  .why-grid { grid-template-columns: 1fr 1fr; }
}
</style>
