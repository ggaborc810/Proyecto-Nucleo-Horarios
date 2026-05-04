# Base de Datos

**Motor**: PostgreSQL 14+

PostgreSQL ofrece restricciones declarativas (`UNIQUE`, `CHECK`, `FOREIGN KEY`) que actúan como segunda línea de defensa contra errores del motor. Los índices B-Tree garantizan verificaciones O(log n).

## Modelo Relacional

Entidad central: **`asignacion`** orquesta docente, aula, franja y grupo.
Fuente única de configuración: **`parametro_semestre`** (cumple RNF-07).

```
tipo_aula (1) ──< aula (N)
docente (1) ──< disponibilidad_docente (N)
docente (N) >──< curso (M)  [via compatibilidad_docente_curso]
curso (1) ──< grupo (N)
docente (1) ──< grupo (N)
grupo, aula, docente, franja, horario  ──> asignacion
parametro_semestre (1) ──< horario (N)
parametro_semestre (1) ──< franja_horaria (N)
```

## DDL Completo (orden de creación)

```sql
-- 1. TIPO DE AULA
CREATE TABLE tipo_aula (
    id_tipo_aula  SERIAL PRIMARY KEY,
    nombre_tipo   VARCHAR(50) NOT NULL UNIQUE
    -- Valores: 'CONVENCIONAL', 'LABORATORIO_COMPUTO', 'TALLER'
);

-- 2. AULA
CREATE TABLE aula (
    aula_id      SERIAL PRIMARY KEY,
    codigo_aula  VARCHAR(20)  NOT NULL UNIQUE,
    capacidad    INTEGER      NOT NULL CHECK (capacidad > 0),
    ubicacion    VARCHAR(100),
    activa       BOOLEAN      NOT NULL DEFAULT TRUE,
    id_tipo_aula INTEGER      NOT NULL REFERENCES tipo_aula(id_tipo_aula)
);
CREATE INDEX idx_aula_tipo   ON aula(id_tipo_aula);
CREATE INDEX idx_aula_activa ON aula(activa);

-- 3. DOCENTE
CREATE TABLE docente (
    docente_id        SERIAL PRIMARY KEY,
    numero_documento  VARCHAR(20)  NOT NULL UNIQUE,
    nombre_completo   VARCHAR(150) NOT NULL,
    tipo_vinculacion  VARCHAR(30)  NOT NULL
        CHECK (tipo_vinculacion IN ('TIEMPO_COMPLETO','TRES_CUARTOS','MEDIO_TIEMPO','CUARTO_TIEMPO')),
    horas_max_semana  INTEGER      NOT NULL CHECK (horas_max_semana > 0),
    email             VARCHAR(100) NOT NULL UNIQUE
);

-- 4. DISPONIBILIDAD DOCENTE
CREATE TABLE disponibilidad_docente (
    disponibilidad_id  SERIAL PRIMARY KEY,
    docente_id         INTEGER     NOT NULL REFERENCES docente(docente_id) ON DELETE CASCADE,
    dia_semana         VARCHAR(10) NOT NULL
        CHECK (dia_semana IN ('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO')),
    hora_inicio        TIME        NOT NULL,
    hora_fin           TIME        NOT NULL,
    CONSTRAINT chk_disp_horas CHECK (hora_fin > hora_inicio)
);
CREATE INDEX idx_disp_docente ON disponibilidad_docente(docente_id);

-- 5. CURSO
CREATE TABLE curso (
    curso_id           SERIAL PRIMARY KEY,
    codigo_curso       VARCHAR(20)  NOT NULL UNIQUE,
    nombre_curso       VARCHAR(150) NOT NULL,
    frecuencia_semanal INTEGER      NOT NULL CHECK (frecuencia_semanal BETWEEN 1 AND 4),
    semestre_nivel     INTEGER      NOT NULL CHECK (semestre_nivel BETWEEN 1 AND 10),
    id_tipo_aula       INTEGER      NOT NULL REFERENCES tipo_aula(id_tipo_aula)
);

-- 6. COMPATIBILIDAD DOCENTE-CURSO (HC-10)
CREATE TABLE compatibilidad_docente_curso (
    compatibilidad_id  SERIAL PRIMARY KEY,
    docente_id         INTEGER NOT NULL REFERENCES docente(docente_id) ON DELETE CASCADE,
    curso_id           INTEGER NOT NULL REFERENCES curso(curso_id)     ON DELETE CASCADE,
    CONSTRAINT uq_compat UNIQUE (docente_id, curso_id)
);
CREATE INDEX idx_compat_docente ON compatibilidad_docente_curso(docente_id);
CREATE INDEX idx_compat_curso   ON compatibilidad_docente_curso(curso_id);

-- 7. PARAMETRO_SEMESTRE
CREATE TABLE parametro_semestre (
    id_parametro      SERIAL PRIMARY KEY,
    semestre          VARCHAR(10)  NOT NULL UNIQUE,
    franja_inicio_lv  TIME         NOT NULL DEFAULT '07:00',
    franja_fin_lv     TIME         NOT NULL DEFAULT '22:00',
    franja_inicio_sa  TIME         NOT NULL DEFAULT '07:00',
    franja_fin_sa     TIME         NOT NULL DEFAULT '13:00',
    exclusion_inicio  TIME         NOT NULL DEFAULT '12:00',
    exclusion_fin     TIME         NOT NULL DEFAULT '13:00',
    cap_max_grupo     INTEGER      NOT NULL DEFAULT 40,
    umbral_cierre     INTEGER      NOT NULL DEFAULT 10,
    freq_max_sesion   INTEGER      NOT NULL DEFAULT 4,
    activo            BOOLEAN      NOT NULL DEFAULT TRUE
);

-- 8. HORARIO
CREATE TABLE horario (
    horario_id        SERIAL PRIMARY KEY,
    semestre          VARCHAR(10)  NOT NULL,
    estado            VARCHAR(20)  NOT NULL DEFAULT 'BORRADOR'
        CHECK (estado IN ('BORRADOR','PUBLICADO')),
    fecha_generacion  TIMESTAMP,
    fecha_publicacion TIMESTAMP,
    id_parametro      INTEGER REFERENCES parametro_semestre(id_parametro)
);
CREATE INDEX idx_horario_semestre ON horario(semestre);

-- 9. GRUPO
CREATE TABLE grupo (
    grupo_id      SERIAL PRIMARY KEY,
    seccion       VARCHAR(10)  NOT NULL,
    num_inscritos INTEGER      NOT NULL DEFAULT 0 CHECK (num_inscritos >= 0),
    estado        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO'
        CHECK (estado IN ('ACTIVO','CERRADO')),
    fecha_cierre  DATE,
    causa_cierre  VARCHAR(200),
    curso_id      INTEGER NOT NULL REFERENCES curso(curso_id),
    docente_id    INTEGER NOT NULL REFERENCES docente(docente_id)
);
CREATE INDEX idx_grupo_curso   ON grupo(curso_id);
CREATE INDEX idx_grupo_docente ON grupo(docente_id);
CREATE INDEX idx_grupo_estado  ON grupo(estado);

-- 10. FRANJA HORARIA (auto-generada al crear ParametroSemestre)
CREATE TABLE franja_horaria (
    franja_id    SERIAL PRIMARY KEY,
    dia_semana   VARCHAR(10) NOT NULL
        CHECK (dia_semana IN ('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO')),
    hora_inicio  TIME        NOT NULL,
    hora_valida  TIME        NOT NULL,   -- = hora_inicio + 2h
    es_valida    BOOLEAN     NOT NULL DEFAULT TRUE,
    id_parametro INTEGER     NOT NULL REFERENCES parametro_semestre(id_parametro)
);
CREATE INDEX idx_franja_dia       ON franja_horaria(dia_semana);
CREATE INDEX idx_franja_valida    ON franja_horaria(es_valida);
CREATE INDEX idx_franja_parametro ON franja_horaria(id_parametro);

-- 11. ASIGNACION (entidad central)
CREATE TABLE asignacion (
    id_asignacion    SERIAL PRIMARY KEY,
    grupo_id         INTEGER NOT NULL REFERENCES grupo(grupo_id),
    aula_id          INTEGER          REFERENCES aula(aula_id),
    franja_id        INTEGER          REFERENCES franja_horaria(franja_id),
    docente_id       INTEGER          REFERENCES docente(docente_id),
    horario_id       INTEGER NOT NULL REFERENCES horario(horario_id),
    fecha_asignacion TIMESTAMP        DEFAULT NOW(),
    hc_violado       VARCHAR(10),
    estado           VARCHAR(20) NOT NULL DEFAULT 'ASIGNADA'
        CHECK (estado IN ('ASIGNADA','CONFLICTO','MANUAL'))
);

-- Refuerzo HC-01 y HC-02 a nivel BD
CREATE UNIQUE INDEX uq_asig_docente_franja
    ON asignacion(docente_id, franja_id)
    WHERE estado != 'CONFLICTO' AND docente_id IS NOT NULL AND franja_id IS NOT NULL;
CREATE UNIQUE INDEX uq_asig_aula_franja
    ON asignacion(aula_id, franja_id)
    WHERE estado != 'CONFLICTO' AND aula_id IS NOT NULL AND franja_id IS NOT NULL;

-- Índices de rendimiento
CREATE INDEX idx_asig_horario ON asignacion(horario_id);
CREATE INDEX idx_asig_docente ON asignacion(docente_id);
CREATE INDEX idx_asig_aula    ON asignacion(aula_id);
CREATE INDEX idx_asig_franja  ON asignacion(franja_id);
CREATE INDEX idx_asig_estado  ON asignacion(estado);

-- 12. USUARIO
CREATE TABLE usuario (
    usuario_id  SERIAL PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(200) NOT NULL,  -- BCrypt
    rol         VARCHAR(20)  NOT NULL CHECK (rol IN ('ADMIN','DOCENTE')),
    docente_id  INTEGER      REFERENCES docente(docente_id),
    activo      BOOLEAN      NOT NULL DEFAULT TRUE
);
```

## Generación Automática de Franjas

Al crear un `ParametroSemestre`, el sistema genera todas las `FranjaHoraria` válidas:

- **Lun-Vie**: bloques de 2h desde `franja_inicio_lv` hasta `franja_fin_lv - 2h`
- **Sábado**: bloques de 2h desde `franja_inicio_sa` hasta `franja_fin_sa - 2h`
- **Exclusión**: marcar `es_valida = false` en bloques que solapen con `exclusion_inicio`–`exclusion_fin`

**Ejemplo (defaults):**
- 07:00-09:00 ✅ · 09:00-11:00 ✅ · 11:00-13:00 ❌ · 13:00-15:00 ✅ ... 20:00-22:00 ✅
- Sábado efectivo: 07:00-09:00, 09:00-11:00 (luego entra exclusión)

## Datos Iniciales Mínimos

```sql
INSERT INTO tipo_aula (nombre_tipo) VALUES ('CONVENCIONAL'), ('LABORATORIO_COMPUTO'), ('TALLER');

INSERT INTO parametro_semestre (semestre) VALUES ('2026-1');

-- Password generado con BCrypt para "admin123"
INSERT INTO usuario (username, password, rol)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');
```

## Capacidad Máxima de Grupo

`cap_max_grupo × 1.1` = techo de inscripción. Default: 40 × 1.1 = **44 estudiantes**.

## Tipos de Vinculación (carga máxima default)

| Tipo | h/sem |
|------|-------|
| TIEMPO_COMPLETO | 20 |
| TRES_CUARTOS | 15 |
| MEDIO_TIEMPO | 10 |
| CUARTO_TIEMPO | 5 |

Configurables en `parametro_semestre` (ampliable en futuras versiones).
