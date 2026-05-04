# API — Endpoints REST y WebSocket

## Convenciones

- **Base URL**: `http://localhost:8080/api`
- **Formato**: JSON
- **Autenticación**: `Authorization: Bearer <JWT>` (excepto rutas públicas)
- **Content-Type**: `application/json`
- **Errores**: `{ "error": "CODIGO", "mensaje": "...", "detalles": [...] }`

## Códigos HTTP

| Código | Cuándo |
|--------|--------|
| 200 | Éxito |
| 201 | Recurso creado |
| 400 | Request inválido (validación) |
| 401 | Sin token o token inválido |
| 403 | Token válido pero rol insuficiente |
| 404 | Recurso no encontrado |
| 409 | Conflicto (HC violado, estado incompatible) |
| 422 | Entidad no procesable (datos maestros incompletos) |
| 500 | Error interno |

---

## Auth

### `POST /api/auth/login` — Sin auth

```json
// Request
{ "username": "admin", "password": "admin123" }

// Response 200
{ "token": "eyJ...", "rol": "ADMIN", "username": "admin" }

// Response 401
{ "error": "CREDENCIALES_INVALIDAS", "mensaje": "Credenciales inválidas" }
```

### `GET /api/auth/me` — JWT

```json
{ "username": "admin", "rol": "ADMIN", "docenteId": null }
```

---

## Horario

### `POST /api/horarios/generar` — ADMIN

```json
// Request
{ "semestre": "2026-1" }

// Response 200 — éxito completo
{
  "horarioId": 1,
  "semestre": "2026-1",
  "totalAsignadas": 118,
  "totalConflictos": 0,
  "tiempoEjecucionMs": 4231,
  "conflictos": []
}

// Response 200 — éxito parcial
{
  "horarioId": 1,
  "semestre": "2026-1",
  "totalAsignadas": 115,
  "totalConflictos": 3,
  "tiempoEjecucionMs": 12540,
  "conflictos": [
    {
      "grupoId": 7,
      "seccion": "G1",
      "nombreCurso": "Programación I",
      "nombreDocente": "García López",
      "sesionNumero": 2,
      "hcViolado": "HC-03",
      "descripcion": "No existe franja disponible donde el docente tenga disponibilidad declarada",
      "accionesCorrectivas": [
        "Ampliar disponibilidad horaria del docente García López",
        "Asignar otro docente compatible"
      ]
    }
  ]
}

// Response 422 — datos maestros incompletos
{
  "error": "DATOS_INCOMPLETOS",
  "mensaje": "No se puede generar el horario.",
  "detalles": [
    "Docente García López no tiene disponibilidad horaria registrada",
    "No existen aulas activas del tipo LABORATORIO_COMPUTO"
  ]
}
```

### `GET /api/horarios/{semestre}` — ADMIN

```json
{
  "horarioId": 1,
  "semestre": "2026-1",
  "estado": "BORRADOR",
  "fechaGeneracion": "2026-04-15T10:30:00",
  "fechaPublicacion": null,
  "totalConflictos": 3,
  "asignaciones": [
    {
      "idAsignacion": 1,
      "grupoId": 1,
      "seccionGrupo": "G1",
      "nombreCurso": "Programación I",
      "nombreDocente": "García López",
      "aulaId": 2,
      "codigoAula": "301-A",
      "franjaId": 5,
      "diaSemana": "LUNES",
      "horaInicio": "07:00",
      "horaFin": "09:00",
      "estado": "ASIGNADA",
      "hcViolado": null
    }
  ]
}
```

### `PUT /api/horarios/{id}/publicar` — ADMIN

```json
// Response 200
{ "horarioId": 1, "semestre": "2026-1", "estado": "PUBLICADO", "fechaPublicacion": "..." }

// Response 409 — conflictos pendientes
{ "error": "CONFLICTOS_PENDIENTES", "mensaje": "El horario tiene 3 conflictos sin resolver" }
```

### `GET /api/horarios/{semestre}/conflictos` — ADMIN

Retorna `ConflictoDTO[]`.

---

## Asignaciones (Drag-and-Drop)

### `PUT /api/asignaciones/{id}/mover` — ADMIN

```json
// Request
{ "nuevaFranjaId": 10, "nuevaAulaId": 3 }

// Response 200
{
  "idAsignacion": 1,
  "estado": "MANUAL",
  "diaSemana": "MARTES",
  "horaInicio": "09:00",
  "codigoAula": "302-B"
}

// Response 409 — HC violado
{
  "error": "HC-01",
  "mensaje": "Conflicto HC-01: el docente García López ya tiene una sesión en MARTES 09:00-11:00"
}
```

### `GET /api/asignaciones/validar-movimiento` — ADMIN

```
?asignacionId=1&nuevaFranjaId=10&nuevaAulaId=3
```

```json
// Response 200 — válido
{ "valido": true, "hcViolado": null, "mensajeError": null }

// Response 200 — inválido (siempre 200; la invalidez está en el body)
{
  "valido": false,
  "hcViolado": "HC-02",
  "mensajeError": "Conflicto HC-02: el aula 302-B ya está ocupada en ese bloque horario"
}
```

---

## Docentes

### `GET /api/docentes` — ADMIN

```json
[
  {
    "docenteId": 1,
    "nombreCompleto": "García López",
    "tipoVinculacion": "TIEMPO_COMPLETO",
    "horasMaxSemana": 20,
    "email": "garcia@unbosque.edu.co",
    "totalDisponibilidades": 8,
    "totalCompatibilidades": 5
  }
]
```

### `POST /api/docentes` — ADMIN

```json
// Request
{
  "numeroDocumento": "12345678",
  "nombreCompleto": "García López",
  "tipoVinculacion": "TIEMPO_COMPLETO",
  "horasMaxSemana": 20,
  "email": "garcia@unbosque.edu.co"
}
```

### `PUT /api/docentes/{id}` — ADMIN

### `DELETE /api/docentes/{id}` — ADMIN

### `GET /api/docentes/{id}/disponibilidad` — ADMIN | DOCENTE (propio)

```json
[
  { "disponibilidadId": 1, "diaSemana": "LUNES", "horaInicio": "07:00", "horaFin": "13:00" },
  { "disponibilidadId": 2, "diaSemana": "MIERCOLES", "horaInicio": "14:00", "horaFin": "20:00" }
]
```

### `POST /api/docentes/{id}/disponibilidad` — ADMIN | DOCENTE (propio)

```json
// Request
{ "diaSemana": "LUNES", "horaInicio": "07:00", "horaFin": "13:00" }

// Response 422 — fuera de franja permitida
{ "error": "FRANJA_INVALIDA", "mensaje": "El horario declarado cae fuera de la franja permitida" }
```

### `DELETE /api/docentes/{id}/disponibilidad/{dispId}` — ADMIN | DOCENTE (propio)

### `GET /api/docentes/{id}/compatibilidades` — ADMIN

```json
[
  { "compatibilidadId": 1, "cursoId": 3, "codigoCurso": "IS-301", "nombreCurso": "Programación I" }
]
```

### `POST /api/docentes/{id}/compatibilidades` — ADMIN

```json
{ "cursoId": 3 }
```

---

## Aulas

### `GET /api/aulas` — ADMIN

```json
[
  {
    "aulaId": 1,
    "codigoAula": "301-A",
    "capacidad": 35,
    "ubicacion": "Edificio A",
    "activa": true,
    "tipoAula": "CONVENCIONAL"
  }
]
```

### `GET /api/aulas/disponibles?franjaId=5` — ADMIN

Retorna aulas no ocupadas en esa franja.

### `POST /api/aulas`, `PUT /api/aulas/{id}` — ADMIN

```json
{
  "codigoAula": "302-B",
  "capacidad": 40,
  "ubicacion": "Edificio B",
  "activa": true,
  "idTipoAula": 1
}
```

---

## Cursos

### `GET /api/cursos` — ADMIN

```json
[
  {
    "cursoId": 1,
    "codigoCurso": "IS-101",
    "nombreCurso": "Fundamentos de Programación",
    "frecuenciaSemanal": 2,
    "semestreNivel": 1,
    "tipoAulaRequerida": "LABORATORIO_COMPUTO"
  }
]
```

### `POST /api/cursos`, `PUT /api/cursos/{id}` — ADMIN

---

## Grupos

### `GET /api/grupos?estado=ACTIVO` — ADMIN

```json
[
  {
    "grupoId": 1,
    "seccion": "G1",
    "numInscritos": 28,
    "estado": "ACTIVO",
    "cursoId": 1,
    "nombreCurso": "IS-101",
    "docenteId": 3,
    "nombreDocente": "García López"
  }
]
```

### `POST /api/grupos`, `PUT /api/grupos/{id}` — ADMIN

### `PUT /api/grupos/{id}/cerrar` — ADMIN

```json
// Request
{ "causa": "BAJA_INSCRIPCION_MANUAL" }

// Response 200
{ "grupoId": 1, "estado": "CERRADO", "fechaCierre": "2026-04-15", "causaCierre": "..." }
```

### `POST /api/grupos/cerrar-automatico` — ADMIN

Cierra todos los grupos activos con `numInscritos < umbral_cierre`.

```json
// Response 200
{
  "gruposCerrados": 3,
  "detalle": [
    { "grupoId": 5, "seccion": "G2", "numInscritos": 7 }
  ]
}
```

---

## Parámetros del Semestre

### `GET /api/parametros` — ADMIN

```json
[
  {
    "idParametro": 1,
    "semestre": "2026-1",
    "franjaInicioLV": "07:00",
    "franjaFinLV": "22:00",
    "franjaInicioSA": "07:00",
    "franjaFinSA": "13:00",
    "exclusionInicio": "12:00",
    "exclusionFin": "13:00",
    "capMaxGrupo": 40,
    "umbralCierre": 10,
    "freqMaxSesion": 4,
    "activo": true
  }
]
```

### `GET /api/parametros/activo` — ADMIN

### `POST /api/parametros` — ADMIN

Al crear, el sistema auto-genera todas las `FranjaHoraria` válidas.

### `PUT /api/parametros/{id}` — ADMIN

---

## Vista Pública (Sin auth)

### `GET /api/publico/horario/{semestre}` — sin auth

Solo retorna horarios con `estado = 'PUBLICADO'`. Si no hay publicado: 404.

```json
{
  "semestre": "2026-1",
  "fechaPublicacion": "2026-03-01T08:00:00",
  "asignaciones": [ /* AsignacionDTO[] */ ]
}
```

### `GET /api/publico/horario/{semestre}/docente/{id}` — sin auth

Filtrado por docente.

### `GET /api/publico/horario/{semestre}/aula/{id}` — sin auth

### `GET /api/publico/horario/{semestre}/curso/{id}` — sin auth

---

## WebSocket — STOMP

### Endpoint

```
ws://localhost:8080/ws (con fallback SockJS)
```

### Publicar (Frontend → Backend)

```
Destination: /app/validar-movimiento
Body: { "asignacionId": 1, "nuevaFranjaId": 10, "nuevaAulaId": 3 }
```

### Suscribir (Backend → Frontend)

```
Topic: /topic/validacion-movimiento
Body: { "valido": true|false, "hcViolado": "HC-01"|null, "mensajeError": "..."|null }
```

---

## Acciones Correctivas — Mapeo HC → Mensaje

| HC | Acción correctiva sugerida |
|----|---------------------------|
| HC-01 | Asignar otro docente compatible con el curso |
| HC-02 | Agregar más aulas del tipo requerido |
| HC-03 | Ampliar disponibilidad horaria del docente |
| HC-04 | Verificar que existan aulas activas del tipo requerido |
| HC-05 | Aumentar capacidad del aula o dividir el grupo |
| HC-06 | Revisar parámetros de franja horaria |
| HC-07 | Ajustar la franja de exclusión de mediodía |
| HC-08 | (No debería ocurrir — franjas son siempre 2h por construcción) |
| HC-09 | Reducir frecuencia semanal o agregar más recursos |
| HC-10 | Registrar compatibilidad docente-curso |
| HC-11 | Ajustar la ventana horaria permitida del curso |
| HC-12 | Distribuir grupos del mismo semestre en franjas distintas |
| HC-13 | Ampliar disponibilidad del docente o permitir bloques horarios distintos para el grupo |
