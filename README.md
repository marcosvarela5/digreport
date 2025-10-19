# DIGREPORT 🏛️

**Autor:** Marcos Francisco Varela Marcos  

---

## 📝 Descripción

DIGREPORT es una aplicación web que busca facilitar la colaboración entre **autoridades**, **arqueólogos** y **detectoaficionados** para la protección y correcta gestión del patrimonio histórico.  

El sistema permite:
- 📍 Registrar hallazgos con trazabilidad (fecha, ubicación, usuario, descripción, imágenes)
- 🏺 Validar piezas arqueológicas por profesionales
- 🗺️ Consultar zonas protegidas mediante un mapa interactivo
- 🔐 Gestionar información y permisos según el rol del usuario
- 📊 Proporcionar un panel de administración con estadísticas y control de accesos

---

## 💻 Tecnologías utilizadas

### 🔙 Backend
- <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" width="20"/> **Java 17**
- <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original.svg" width="20"/> **Spring Boot** (REST API, inyección de dependencias)
- <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original.svg" width="20"/> **Spring Security + JWT** (autenticación y autorización)
- <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/hibernate/hibernate-original.svg" width="20"/> **JPA / Hibernate** (persistencia)
- <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/postgresql/postgresql-original.svg" width="20"/> **PostgreSQL** (base de datos relacional)
- 🏗️ **Arquitectura hexagonal** (puertos y adaptadores)

### 🖥️ Frontend
- <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/vuejs/vuejs-original.svg" width="20"/> **Vue.js 3**
- <img src="https://pinia.vuejs.org/logo.svg" width="20"/> **Pinia** (gestión de estado)
- <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/vite/vite-original.svg" width="20"/> **Vite** (entorno de desarrollo)
- <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/javascript/javascript-original.svg" width="20"/> **Axios** (consumo de API REST)

### 🧪 Pruebas
- <img src="https://upload.wikimedia.org/wikipedia/commons/5/50/JUnit_5_Banner.png" width="50"/> **JUnit 5**
- <img src="https://avatars.githubusercontent.com/u/12101536?s=200&v=4" width="20"/> **Mockito**

---

## ⚙️ Requisitos previos

- ☕ **Java 17+**
- 📦 **Maven 3+**
- 🟢 **Node.js 18+** y `npm`
- 🐘 **PostgreSQL** (con una base de datos creada, por ejemplo `digreport`)
- 🐙 **Git** (opcional, para clonar el repositorio)

---

## Ejecución

### 1️⃣ Clonar el repositorio
```bash
git clone git@github.com:marcosvarela5/digreport.git
cd digreport
```

### 2️⃣ Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 3️⃣ Frontend
```bash
cd frontend
npm install
npm run dev
```

## 📜 Licencia

Este proyecto forma parte del Trabajo de Fin de Grado de Marcos Varela y su uso está restringido al contexto académico correspondiente.  


  
## 🖥️ Capturas de pantalla

### Autenticación
#### Login
<img src="./docs/digreport_loginform.png" width="750">

### Registro y Perfil
#### Registro
<img src="./docs/digreport_registerform.png" width="750">

#### Editar perfil
<img src="./docs/digreport_editprofile.png" width="750">

### Panel de administración (solo rol autoridad)
#### Panel de administración 1
<img src="./docs/digreport_adminpanel_!.png" width="750">

#### Panel de administración 2
<img src="./docs/digreport_adminpanel_2.png" width="750">

#### Panel de administración 3
<img src="./docs/digreport_adminpanel_3.png" width="750">

### Informes (usuarios)
#### Home
<img src="./docs/digreport_home.png" width="750">

#### Formulario de informe de hallazgo
<img src="./docs/digreport_findreportform.png" width="750">

#### Mis informes
<img src="./docs/digreport_myreports.png" width="750">

#### Detalles y validación (solo rol profesional tiene botón validar)
<img src="./docs/digreport_detailsandvalidate.png" width="750">

### Validaciones (solo rol profesional)
#### Mis validaciones
<img src="./docs/digreport_myvalidations.png" width="750">

#### Validaciones pendientes
<img src="./docs/digreport_pendingvalidations.png" width="750">

