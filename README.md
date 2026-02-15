# 🏋️‍♂️ Gestor del Club Deportivo — DAMA Sports (Edición MongoDB)

![Java](https://img.shields.io/badge/Java-21%2B-orange)
![MongoDB](https://img.shields.io/badge/MongoDB-NoSQL-green)
![Driver](https://img.shields.io/badge/Mongo_Driver-Sync-darkgreen)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-blueviolet)

Aplicación de escritorio para la gestión integral de **socios**, **pistas** y **reservas**.

> **Novedad en esta versión (UT4):** Se ha migrado toda la capa de persistencia de SQL (Relacional) a **NoSQL con MongoDB**, utilizando el driver nativo de Java y mapeo automático de POJOs.

---

## 👥 Equipo de Desarrollo

| Miembro | Rol Principal | Responsabilidades |
|---------|---------------|-------------------|
| **Luis** | 🧱 Arquitectura y Persistencia | Configuración de `MongoClient`, `CodecRegistry` para POJOs, y diseño de la clase `LogicaMongo`. |
| **Javi** | 🧠 Lógica y Frontend | Mapeo de entidades (`@BsonProperty`), validación de solapes en memoria e integración con JavaFX. |

---

## 📦 Estructura del Proyecto

El proyecto ha simplificado su arquitectura al eliminar la rigidez de las tablas relacionales, centralizando la lógica de acceso a datos.

```text
src/main/
├── java/
│   ├── controlador/   # Lógica de Negocio y Acceso a Datos
│   │   └── LogicaMongo.java  (Conexión, CRUD y Validaciones)
│   ├── modelo/        # Documentos POJO
│   │   ├── Socio.java
│   │   ├── Pista.java
│   │   └── Reserva.java (Con anotaciones @BsonProperty)
│   └── vista/         # Interfaz de Usuario (JavaFX)
│       ├── views/     # Formularios y Vistas (FXML o Java puro)
│       ├── Launcher.java
│       └── MainApp.java
└── resources/
    └── (No requiere persistence.xml, configuración en código)
```

---

## ⚙️ Tecnologías y Persistencia

### 1. NoSQL (MongoDB)
Abandonamos las tablas y claves foráneas por **Colecciones** y **Documentos**.
- **Colecciones:** `socios`, `pistas`, `reservas`.
- **Mapeo POJO:** Utilizamos `PojoCodecProvider` para mapear automáticamente las clases Java a documentos BSON.
- **Anotaciones:** Uso de `@BsonId` y `@BsonProperty` para vincular los atributos Java (camelCase) con los campos de la base de datos (snake_case).

### 2. Driver Nativo (MongoDB Sync)
En lugar de un ORM pesado como Hibernate, usamos el driver oficial:
- **Conexión:** `MongoClient` conectado a `mongodb://localhost:27017`.
- **Consultas:** Uso de `Filters` (`eq`, `and`, `gt`) para búsquedas precisas.
- **Actualizaciones:** Uso de `Updates` (`set`, `combine`) para modificaciones atómicas.

---

## 🧠 Lógica de Negocio

La clase `LogicaMongo` centraliza toda la inteligencia de la aplicación:
*   ✅ **Validación de Solapes:** Algoritmo en Java que recupera las reservas del día y compara rangos de `LocalTime` para evitar conflictos.
*   ✅ **Integridad de Datos:** Simulación de integridad referencial (ej. no permitir borrar un socio si tiene reservas futuras activas consultando la colección `reservas`).
*   ✅ **Cálculo de Precios:** Lógica aplicada en el momento de la inserción (10€/hora prorrateado).

---

## ▶️ Instalación y Ejecución

### Requisitos Previos
*   JDK 21 o superior.
*   MongoDB Server (Local o Atlas).
*   Maven.

### Pasos
1.  **Base de Datos:** Asegúrate de tener el servicio de MongoDB corriendo en el puerto por defecto (`27017`). La base de datos `dama_sports` se creará automáticamente al insertar el primer registro.
2.  **Configuración:**
    Si usas una configuración distinta a localhost, edita la cadena de conexión en `src/main/java/controlador/LogicaMongo.java`:
    ```java
    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
    ```
3.  **Ejecución:**
    Ejecuta la clase principal desde tu IDE:
    `src/main/java/vista/MainApp.java`

---

## 🔀 Flujo de Trabajo (Git)

Para garantizar la integridad del código durante la migración a NoSQL:

1.  🚀 **Infraestructura (Luis):** Setup del proyecto Maven con dependencias `mongodb-driver-sync` y configuración del `CodecRegistry`.
2.  💾 **Modelo (Javi):** Adaptación de las clases del modelo anterior, añadiendo anotaciones BSON y eliminando anotaciones JPA (`@Entity`).
3.  🧩 **Integración (Luis y Javi):** Reescritura de la lógica SQL a métodos de MongoDB (`insertOne`, `find`, `updateOne`) y conexión con la UI existente.

---

## 📝 Licencia
Proyecto académico para la asignatura de Acceso a Datos (UT4) - I.E.S. Vicente Medina.
