<template>
  <div class="protected-areas-container">
    <!-- Header -->
    <div class="protected-areas-header">

      <div class="header-content">
        <h2>🗺️ Zonas Protegidas</h2>
        <p class="subtitle">Monumentos y áreas arqueológicas bajo protección</p>
        <router-link to="/" class="back-link">⬅ Volver al inicio</router-link>
      </div>

      <div class="map-controls" v-if="isAuthority">
        <button @click="startDrawingMonument" class="map-btn map-btn-primary">
          📍 Añadir Monumento
        </button>
        <button @click="startDrawingArea" class="map-btn map-btn-secondary">
          🗺️ Añadir Área Protegida
        </button>
      </div>
    </div>

    <!-- Mapa -->
    <div id="protected-areas-map" ref="mapContainer"></div>

    <!-- Modal -->
    <div v-if="showModal" class="protected-areas-modal-overlay" @click="closeModal">
      <div class="protected-areas-modal-content" @click.stop>
        <h3>{{ isEditMode ? 'Editar' : 'Nueva' }} Zona Protegida</h3>

        <form @submit.prevent="saveProtectedArea">
          <div class="protected-areas-form-group">
            <label>Nombre *</label>
            <input
                v-model="formData.name"
                required
                type="text"
                placeholder="Ej: Torre de Hércules"
                class="protected-areas-input"
            />
          </div>

          <div class="protected-areas-form-group">
            <label>Descripción</label>
            <textarea
                v-model="formData.description"
                rows="3"
                placeholder="Información sobre la zona protegida..."
                class="protected-areas-textarea"
            ></textarea>
          </div>

          <div class="protected-areas-form-group">
            <label>Comunidad Autónoma</label>
            <select v-model="formData.ccaa" class="protected-areas-select">
              <option value="">Seleccionar...</option>
              <option value="Galicia">Galicia</option>
              <option value="Andalucía">Andalucía</option>
              <option value="Cataluña">Cataluña</option>
              <option value="Madrid">Madrid</option>
              <option value="Valencia">Valencia</option>
              <option value="País Vasco">País Vasco</option>
              <option value="Castilla y León">Castilla y León</option>
              <option value="Castilla-La Mancha">Castilla-La Mancha</option>
              <option value="Murcia">Murcia</option>
              <option value="Aragón">Aragón</option>
              <option value="Extremadura">Extremadura</option>
              <option value="Asturias">Asturias</option>
              <option value="Navarra">Navarra</option>
              <option value="Cantabria">Cantabria</option>
              <option value="La Rioja">La Rioja</option>
              <option value="Islas Baleares">Islas Baleares</option>
              <option value="Islas Canarias">Islas Canarias</option>
            </select>
          </div>

          <div class="protected-areas-form-group">
            <label>Tipo</label>
            <div class="protected-areas-type-badge">
              {{ formData.type === 'MONUMENT' ? '📍 Monumento (Punto)' : '🗺️ Área Protegida (Polígono)' }}
            </div>

            <!-- 🆕 NUEVO: Select de tipo de protección -->
            <div class="protected-areas-form-group">
              <label>Tipo de Protección *</label>
              <select
                  v-model="formData.protectionType"
                  class="protected-areas-select"
                  required
              >
                <option value="">Seleccionar...</option>

                <!-- Opciones para MONUMENTOS -->
                <template v-if="formData.type === 'MONUMENT'">
                  <option value="BIC">Bien de Interés Cultural (BIC)</option>
                  <option value="PATRIMONIO_ARQUEOLOGICO">Patrimonio Arqueológico</option>
                </template>

                <!-- Opciones para ÁREAS -->
                <template v-else>
                  <option value="LIC">LIC - Lugar de Importancia Comunitaria</option>
                  <option value="ZEPA">ZEPA - Zona de Especial Protección para las Aves</option>
                  <option value="RESERVA_BIOSFERA">Reserva de la Biosfera</option>
                  <option value="RED_NATURA_2000">Red Natura 2000</option>
                  <option value="PARQUE_NACIONAL">Parque Nacional</option>
                  <option value="PARQUE_NATURAL">Parque Natural</option>
                  <option value="ESPACIO_NATURAL_PROTEGIDO">Espacios Naturales Protegidos</option>
                </template>
              </select>
            </div>
          </div>

          <div class="protected-areas-form-actions">
            <button type="button" @click="closeModal" class="map-btn map-btn-cancel">
              Cancelar
            </button>
            <button type="submit" class="map-btn map-btn-primary" :disabled="saving">
              {{ saving ? 'Guardando...' : 'Guardar' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="protected-areas-loading">
      <div class="protected-areas-spinner"></div>
      <p>Cargando zonas protegidas...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import 'leaflet-draw'
import 'leaflet-draw/dist/leaflet.draw.css'
import { apiClient } from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import './ProtectedAreasMap.css'

// Fix iconos Leaflet
import icon from 'leaflet/dist/images/marker-icon.png'
import iconShadow from 'leaflet/dist/images/marker-shadow.png'
import iconRetina from 'leaflet/dist/images/marker-icon-2x.png'

let DefaultIcon = L.icon({
  iconUrl: icon,
  iconRetinaUrl: iconRetina,
  shadowUrl: iconShadow,
  iconSize: [25, 41],
  iconAnchor: [12, 41]
})
L.Marker.prototype.options.icon = DefaultIcon

const authStore = useAuthStore()
const isAuthority = computed(() => authStore.user?.role === 'AUTHORITY')

const mapContainer = ref<HTMLElement | null>(null)
let map: any
let drawnItems: any
let drawControl: any

const loading = ref(true)
const showModal = ref(false)
const isEditMode = ref(false)
const saving = ref(false)
const currentLayer = ref<any>(null)

const formData = ref({
  name: '',
  description: '',
  ccaa: '',
  type: 'MONUMENT' as 'MONUMENT' | 'AREA',
  protectionType: '',  // 🆕
  geometry: ''
})

onMounted(async () => {
  initializeMap()
  await loadProtectedAreas()
  loading.value = false
})

const initializeMap = () => {
  if (!mapContainer.value) return

  map = L.map(mapContainer.value).setView([40.4168, -3.7038], 6)

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors',
    maxZoom: 19
  }).addTo(map)

  drawnItems = new L.FeatureGroup()
  map.addLayer(drawnItems)

  if (isAuthority.value) {
    setupDrawingControls()
  }
}

const setupDrawingControls = () => {
  drawControl = new L.Control.Draw({
    position: 'topright',
    edit: {
      featureGroup: drawnItems,
      remove: true
    },
    draw: {
      polygon: {
        allowIntersection: false,
        shapeOptions: {
          color: '#e74c3c',
          fillColor: '#e74c3c',
          fillOpacity: 0.3,
          weight: 2
        },
        showArea: false,
        repeatMode: false
      },
      marker: {
        icon: DefaultIcon,
        repeatMode: false
      },
      polyline: false,
      circle: false,
      rectangle: false,
      circlemarker: false
    }
  })
  map.addControl(drawControl)

  map.on(L.Draw.Event.CREATED, (event: any) => {
    console.log('✅ Draw Created Event:', event.layerType)

    const layer = event.layer
    currentLayer.value = layer

    const geoJSON = layer.toGeoJSON()
    console.log('📍 GeoJSON:', geoJSON)

    formData.value.geometry = JSON.stringify(geoJSON.geometry)
    formData.value.type = event.layerType === 'marker' ? 'MONUMENT' : 'AREA'

    console.log('📝 Abriendo modal...')
    showModal.value = true
  })
}

const loadProtectedAreas = async () => {
  try {
    const response = await apiClient.get('/api/protected-areas')
    const areas = response.data

    areas.forEach((area: any) => {
      addAreaToMap(area)
    })
  } catch (error) {
    console.error('Error cargando zonas protegidas:', error)
    alert('Error al cargar las zonas protegidas')
  }
}

const addAreaToMap = (area: any) => {
  try {
    const geoJSON = JSON.parse(area.geometry)

    // 🆕 Función helper para mostrar el tipo de protección legible
    const getProtectionTypeLabel = (type: string) => {
      const labels: Record<string, string> = {
        'BIC': 'Bien de Interés Cultural',
        'PATRIMONIO_ARQUEOLOGICO': 'Patrimonio Arqueológico',
        'LIC': 'LIC',
        'ZEPA': 'ZEPA',
        'RESERVA_BIOSFERA': 'Reserva de la Biosfera',
        'RED_NATURA_2000': 'Red Natura 2000',
        'PARQUE_NACIONAL': 'Parque Nacional',
        'PARQUE_NATURAL': 'Parque Natural',
        'ESPACIO_NATURAL_PROTEGIDO': 'Espacio Natural Protegido'
      }
      return labels[type] || type
    }

    const style = {
      color: area.type === 'AREA' ? '#e74c3c' : '#3498db',
      fillColor: area.type === 'AREA' ? '#e74c3c' : '#3498db',
      fillOpacity: 0.3,
      weight: 2
    }

    const layer = L.geoJSON(geoJSON, {
      style: style,
      pointToLayer: (_feature, latlng) => {
        return L.marker(latlng, { icon: DefaultIcon })
      }
    })

    const popupContent = `
      <div style="min-width: 200px;">
        <h4 style="margin: 0 0 8px 0; color: #2c3e50;">${area.name}</h4>
        ${area.description ? `<p style="margin: 0 0 8px 0; font-size: 14px;">${area.description}</p>` : ''}
        <div style="font-size: 12px; color: #7f8c8d;">
          <strong>Tipo:</strong> ${area.type === 'MONUMENT' ? '📍 Monumento' : '🗺️ Área Protegida'}<br>
          <strong>Protección:</strong> ${getProtectionTypeLabel(area.protectionType)}<br>
          ${area.ccaa ? `<strong>CCAA:</strong> ${area.ccaa}<br>` : ''}
          <strong>Fecha:</strong> ${new Date(area.createdAt).toLocaleDateString('es-ES')}
        </div>
      </div>
    `

    layer.bindPopup(popupContent)
    layer.addTo(drawnItems)

    // @ts-ignore
    layer._areaId = area.id
  } catch (error) {
    console.error('Error añadiendo área al mapa:', error)
  }
}

const startDrawingMonument = () => {
  new L.Draw.Marker(map, drawControl.options.draw.marker).enable()
}

const startDrawingArea = () => {
  new L.Draw.Polygon(map, drawControl.options.draw.polygon).enable()
}

const saveProtectedArea = async () => {
  if (!formData.value.name.trim()) {
    alert('El nombre es obligatorio')
    return
  }

  saving.value = true

  try {
    const response = await apiClient.post('/api/protected-areas', formData.value)

    if (currentLayer.value) {
      drawnItems.addLayer(currentLayer.value)
    }

    addAreaToMap(response.data)

    alert('✅ Zona protegida guardada correctamente')
    closeModal()
  } catch (error: any) {
    console.error('Error guardando zona protegida:', error)
    const errorMsg = error.response?.data?.message || 'Error al guardar la zona protegida'
    alert('❌ ' + errorMsg)
  } finally {
    saving.value = false
  }
}

const closeModal = () => {
  showModal.value = false
  formData.value = {
    name: '',
    description: '',
    ccaa: '',
    type: 'MONUMENT',
    protectionType: '',  // 🆕
    geometry: ''
  }

  if (currentLayer.value && !drawnItems.hasLayer(currentLayer.value)) {
    currentLayer.value.remove()
  }
  currentLayer.value = null
}
</script>