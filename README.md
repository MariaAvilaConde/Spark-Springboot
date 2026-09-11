# Spring Boot + Apache Spark — Integración con 3 Bases de Datos

Proyecto que combina **Spring Boot 3.2** con **Apache Spark 3.5** para realizar JOINs entre tablas almacenadas en **3 bases de datos relacionales en la nube**.

---

## Arquitectura

```
┌──────────────────────────────────────────────────────────────┐
│               Spring Boot 3.2  (API REST :8080)              │
│                                                              │
│   JoinController  ──►  SparkJoinService  ──►  SparkConfig   │
└──────────────────────────────────────────────────────────────┘
          │                    │                    │
          ▼                    ▼                    ▼
  ┌──────────────┐   ┌──────────────────┐   ┌─────────────────┐
  │ MySQL        │   │ PostgreSQL       │   │ PostgreSQL      │
  │ Aiven Cloud  │   │ Neon Cloud       │   │ Supabase Cloud  │
  │              │   │                  │   │                 │
  │  estudiante  │   │ carrera_universi.│   │   matricula     │
  └──────────────┘   └──────────────────┘   └─────────────────┘
          │                    │                    │
          └────────────────────┴────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │    Apache Spark      │
                    │  SparkSession JDBC   │
                    │  TempViews + SQL     │
                    │   INNER JOIN ×3      │
                    └─────────────────────┘
```

---

## Bases de Datos y Tablas

| BD | Motor | Proveedor Cloud | Tabla |
|----|-------|-----------------|-------|
| BD 1 | MySQL | Aiven | `estudiante` |
| BD 2 | PostgreSQL | Neon | `carrera_universidad` |
| BD 3 | PostgreSQL | Supabase | `matricula` |

---

## Estructura del Proyecto

```
spark-springboot-db/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/universidad/sparkdb/
    │   ├── SparkDbApplication.java          ← Punto de entrada Spring Boot
    │   ├── config/
    │   │   └── SparkConfig.java             ← Bean SparkSession
    │   ├── model/
    │   │   ├── Estudiante.java              ← POJO tabla estudiante
    │   │   ├── CarreraUniversidad.java      ← POJO tabla carrera_universidad
    │   │   └── Matricula.java               ← POJO tabla matricula
    │   ├── service/
    │   │   └── SparkJoinService.java        ← Lógica Spark JDBC + JOIN
    │   └── controller/
    │       └── JoinController.java          ← REST endpoints
    └── resources/
        ├── application.properties           ← Credenciales 3 BDs + Spark config
        └── sql/
            ├── mysql-init.sql               ← DDL + datos para MySQL (Aiven)
            ├── postgresql-init.sql          ← DDL + datos para PostgreSQL (Neon)
            └── supabase-init.sql            ← DDL + datos para PostgreSQL (Supabase)
```

---

## Tecnologías

| Tecnología | Versión | Propósito |
|---|---|---|
| Spring Boot | 3.2.0 | Framework REST |
| Apache Spark | 3.5.0 | Motor JOIN multi-BD |
| Scala Binary | 2.12 | Compatibilidad Spark |
| MySQL Connector/J | 8.3.0 | Driver JDBC MySQL |
| PostgreSQL Driver | 42.7.1 | Driver JDBC PostgreSQL |
| Lombok | Latest | Reducción de boilerplate |
| Java | 17 | Lenguaje base |

---

## Modelo de Datos

### BD 1 — MySQL (Aiven) — `estudiante`
| Columna | Tipo | Descripción |
|---|---|---|
| id | BIGINT PK | Identificador |
| nombre | VARCHAR(100) | Nombre |
| apellido | VARCHAR(100) | Apellido |
| email | VARCHAR(150) | Correo único |
| dni | VARCHAR(20) | Documento único |
| edad | INT | Edad |

### BD 2 — PostgreSQL Neon — `carrera_universidad`
| Columna | Tipo | Descripción |
|---|---|---|
| id | BIGSERIAL PK | Identificador |
| nombre | VARCHAR(200) | Nombre de carrera |
| facultad | VARCHAR(200) | Facultad |
| duracion_anios | INT | Duración en años |
| modalidad | VARCHAR(50) | PRESENCIAL / SEMIPRESENCIAL |

### BD 3 — PostgreSQL Supabase — `matricula`
| Columna | Tipo | Descripción |
|---|---|---|
| id | BIGSERIAL PK | Identificador |
| estudiante_id | BIGINT | FK → estudiante.id (MySQL) |
| carrera_id | BIGINT | FK → carrera_universidad.id (Neon) |
| anio_academico | VARCHAR(10) | Periodo (ej: 2024-I) |
| estado | VARCHAR(20) | ACTIVO / INACTIVO / GRADUADO / RETIRADO |
| fecha_matricula | DATE | Fecha de matrícula |

---

## Configuración

Edita `src/main/resources/application.properties` con tus credenciales:

```properties
# MySQL — Aiven
spring.datasource.mysql.url=jdbc:mysql://HOST:PORT/defaultdb?useSSL=true&requireSSL=true&serverTimezone=UTC
spring.datasource.mysql.username=avnadmin
spring.datasource.mysql.password=TU_PASSWORD

# PostgreSQL — Neon
spring.datasource.neon.url=jdbc:postgresql://HOST/neondb?sslmode=require
spring.datasource.neon.username=neondb_owner
spring.datasource.neon.password=TU_PASSWORD

# PostgreSQL — Supabase
spring.datasource.supabase.url=jdbc:postgresql://HOST:5432/postgres?sslmode=require
spring.datasource.supabase.username=postgres.PROJECT_ID
spring.datasource.supabase.password=TU_PASSWORD
```

---

## Inicializar las Tablas

Ejecuta cada script en la consola SQL de su respectivo proveedor:

| Script | Proveedor | Consola |
|---|---|---|
| `sql/mysql-init.sql` | Aiven | console.aiven.io → Query Editor |
| `sql/postgresql-init.sql` | Neon | console.neon.tech → SQL Editor |
| `sql/supabase-init.sql` | Supabase | supabase.com → SQL Editor |

---

## Ejecución

```bash
cd spark-springboot-db

# Compilar (primera vez tarda ~5 min descargando Spark)
mvn clean package -DskipTests

# Ejecutar
mvn spring-boot:run
```

La aplicación inicia en `http://localhost:8080`

---

## Endpoints REST

### `GET /api/spark/join`
JOIN completo entre las 3 bases de datos:
`Matricula (Supabase)` + `Estudiante (MySQL)` + `Carrera_Universidad (Neon)`

```json
[
  {
    "estudiante_id": 1,
    "nombre": "Carlos",
    "apellido": "Garcia",
    "email": "carlos.garcia@uni.edu",
    "dni": "12345678",
    "carrera_id": 1,
    "carrera": "Ingenieria de Sistemas",
    "facultad": "Facultad de Ingenieria",
    "duracion_anios": 5,
    "modalidad": "PRESENCIAL",
    "anio_academico": "2024-I",
    "estado_matricula": "ACTIVO",
    "fecha_matricula": "2024-03-01"
  }
]
```

### `GET /api/spark/stats`
Estadísticas agregadas de las 3 BDs.

```json
{
  "total_estudiantes": 10,
  "total_carreras": 8,
  "total_matriculas": 10,
  "fuentes": {
    "estudiante": "MySQL — Aiven",
    "carrera_universidad": "PostgreSQL — Neon",
    "matricula": "PostgreSQL — Supabase"
  },
  "estudiantes_por_carrera_activa": [...]
}
```

### `GET /api/spark/sin-matricula`
Estudiantes sin ninguna matrícula registrada (LEFT JOIN entre MySQL y Supabase).

---

## Cómo funciona el JOIN multi-BD con Spark

```
1. SparkSession lee `estudiante`          via JDBC desde MySQL   (Aiven)
2. SparkSession lee `carrera_universidad` via JDBC desde PostgreSQL (Neon)
3. SparkSession lee `matricula`           via JDBC desde PostgreSQL (Supabase)
4. Cada Dataset<Row> se registra como TempView en Spark
5. Se ejecuta Spark SQL con INNER JOIN entre las 3 vistas
6. Spark procesa los datos en memoria (modo local)
7. El resultado se serializa a JSON y se retorna vía REST
```

Spark actúa como **capa de integración** entre motores heterogéneos sin necesidad de replicación ni ETL previo.
