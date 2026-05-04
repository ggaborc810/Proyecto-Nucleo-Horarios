-- ============================================================
-- seed.sql — Datos Completos para Simulación
-- Universidad El Bosque — Sistema de Horarios Académicos
-- Ejecutar DESPUÉS de init.sql
-- ============================================================
-- Contraseña de todos los usuarios: admin123
-- Hash BCrypt: $2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq

-- ============================================================
-- 1. TIPOS DE AULA
-- ============================================================
INSERT INTO tipo_aula (nombre_tipo) VALUES
    ('CONVENCIONAL'),         -- id=1
    ('LABORATORIO_COMPUTO'),  -- id=2
    ('TALLER');               -- id=3

-- ============================================================
-- 2. PARÁMETRO DE SEMESTRE
-- ============================================================
INSERT INTO parametro_semestre (semestre) VALUES ('2026-1'); -- id=1

-- ============================================================
-- 3. AULAS (25 aulas para todos los programas)
-- ============================================================
INSERT INTO aula (codigo_aula, capacidad, ubicacion, id_tipo_aula) VALUES
    -- Convencionales Bloque A
    ('A-101', 45, 'Bloque A Piso 1', 1),   -- 1
    ('A-102', 45, 'Bloque A Piso 1', 1),   -- 2
    ('A-103', 40, 'Bloque A Piso 1', 1),   -- 3
    ('A-201', 45, 'Bloque A Piso 2', 1),   -- 4
    ('A-202', 45, 'Bloque A Piso 2', 1),   -- 5
    ('A-203', 40, 'Bloque A Piso 2', 1),   -- 6
    ('A-301', 35, 'Bloque A Piso 3', 1),   -- 7
    ('A-302', 35, 'Bloque A Piso 3', 1),   -- 8
    -- Convencionales Bloque B
    ('B-101', 45, 'Bloque B Piso 1', 1),   -- 9
    ('B-102', 45, 'Bloque B Piso 1', 1),   -- 10
    ('B-201', 40, 'Bloque B Piso 2', 1),   -- 11
    ('B-202', 40, 'Bloque B Piso 2', 1),   -- 12
    ('B-301', 35, 'Bloque B Piso 3', 1),   -- 13
    ('B-302', 35, 'Bloque B Piso 3', 1),   -- 14
    -- Convencionales Bloque C (auditorios)
    ('C-101', 50, 'Bloque C Piso 1', 1),   -- 15
    ('C-102', 50, 'Bloque C Piso 1', 1),   -- 16
    -- Laboratorios de cómputo Bloque D
    ('LAB-C01', 30, 'Bloque D Piso 1', 2), -- 17
    ('LAB-C02', 30, 'Bloque D Piso 1', 2), -- 18
    ('LAB-C03', 28, 'Bloque D Piso 2', 2), -- 19
    ('LAB-C04', 28, 'Bloque D Piso 2', 2), -- 20
    ('LAB-C05', 25, 'Bloque D Piso 3', 2), -- 21
    ('LAB-C06', 25, 'Bloque D Piso 3', 2), -- 22
    -- Laboratorio Biomédica Bloque F
    ('LAB-B01', 24, 'Bloque F Piso 1', 2), -- 23
    -- Talleres Bloque E
    ('TALL-01', 20, 'Bloque E Piso 1', 3), -- 24
    ('TALL-02', 20, 'Bloque E Piso 2', 3); -- 25

-- ============================================================
-- 4. DOCENTES (25 docentes pre-cargados — sin CRUD en UI)
-- ============================================================
INSERT INTO docente (numero_documento, nombre_completo, tipo_vinculacion, horas_max_semana, email) VALUES
    -- IS: Programación y Algoritmos
    ('11000001', 'Carlos Andrés Rodríguez Peña',      'TIEMPO_COMPLETO', 20, 'c.rodriguez@unbosque.edu.co'),  -- 1
    ('11000002', 'Claudia Patricia Ramírez Vega',      'TIEMPO_COMPLETO', 20, 'c.ramirez@unbosque.edu.co'),   -- 2
    ('11000003', 'Diego Alejandro Castro Nieto',       'TRES_CUARTOS',    15, 'd.castro@unbosque.edu.co'),     -- 3
    -- IS: Bases de Datos y Redes
    ('11000004', 'María Fernanda Torres Álvarez',      'TIEMPO_COMPLETO', 20, 'm.torres@unbosque.edu.co'),     -- 4
    ('11000005', 'Luis Eduardo Martínez Gómez',        'TIEMPO_COMPLETO', 20, 'l.martinez@unbosque.edu.co'),   -- 5
    -- IS: Ingeniería de Software y Proyectos
    ('11000006', 'Sandra Milena Gómez Herrera',        'TIEMPO_COMPLETO', 20, 's.gomez@unbosque.edu.co'),      -- 6
    ('11000007', 'Jorge Iván Herrera Salazar',         'TIEMPO_COMPLETO', 20, 'j.herrera@unbosque.edu.co'),    -- 7
    ('11000008', 'Paola Andrea Jiménez Rojas',         'TIEMPO_COMPLETO', 20, 'p.jimenez@unbosque.edu.co'),    -- 8
    -- IS: IA, Sistemas Inteligentes y Compiladores
    ('11000009', 'Ricardo Ernesto Peña Castro',        'MEDIO_TIEMPO',    10, 'r.pena@unbosque.edu.co'),       -- 9
    -- IS: Sistemas y Arquitectura
    ('11000010', 'Andrés Felipe Moreno Díaz',          'TRES_CUARTOS',    15, 'a.moreno@unbosque.edu.co'),     -- 10
    -- Matemáticas (compartido entre programas)
    ('11000011', 'Ana María López Suárez',             'TIEMPO_COMPLETO', 20, 'a.lopez@unbosque.edu.co'),      -- 11
    ('11000012', 'Alejandro José Vargas Ríos',         'TIEMPO_COMPLETO', 20, 'a.vargas@unbosque.edu.co'),     -- 12
    -- Estadística, Dinámica y Modelado
    ('11000013', 'Héctor Manuel Méndez Parra',         'TIEMPO_COMPLETO', 20, 'h.mendez@unbosque.edu.co'),     -- 13
    -- Física (compartido)
    ('11000014', 'Fernanda Isabel Ríos Quintero',      'TIEMPO_COMPLETO', 20, 'f.rios@unbosque.edu.co'),       -- 14
    -- Humanidades y Ciencias Sociales (compartido)
    ('11000015', 'Valentina Cruz Montaño',             'MEDIO_TIEMPO',    10, 'v.cruz@unbosque.edu.co'),       -- 15
    ('11000016', 'Andrés Santiago Ospina Rueda',       'CUARTO_TIEMPO',    5, 'a.ospina@unbosque.edu.co'),     -- 16
    -- Ingeniería Biomédica
    ('11000017', 'Beatriz Elena Salcedo Pinto',        'TIEMPO_COMPLETO', 20, 'b.salcedo@unbosque.edu.co'),    -- 17
    ('11000018', 'Mauricio Alberto Pardo Cano',        'TIEMPO_COMPLETO', 20, 'm.pardo@unbosque.edu.co'),      -- 18
    ('11000019', 'Patricia Lorena Lozano Sierra',      'TRES_CUARTOS',    15, 'p.lozano@unbosque.edu.co'),     -- 19
    ('11000020', 'Jaime Orlando Suárez Becerra',       'TRES_CUARTOS',    15, 'j.suarez@unbosque.edu.co'),     -- 20
    -- Ingeniería Industrial
    ('11000021', 'Gloria Inés Medina Chaparro',        'TIEMPO_COMPLETO', 20, 'g.medina@unbosque.edu.co'),     -- 21
    ('11000022', 'Felipe Armando Ruiz Castillo',       'TIEMPO_COMPLETO', 20, 'f.ruiz@unbosque.edu.co'),       -- 22
    ('11000023', 'Marcela Patricia Osorio León',       'TIEMPO_COMPLETO', 20, 'm.osorio@unbosque.edu.co'),     -- 23
    ('11000024', 'Natalia Eugenia Cárdenas Mora',      'TRES_CUARTOS',    15, 'n.cardenas@unbosque.edu.co'),   -- 24
    ('11000025', 'Iván Darío Guzmán Torres',           'MEDIO_TIEMPO',    10, 'i.guzman@unbosque.edu.co');     -- 25

-- ============================================================
-- 5. DISPONIBILIDADES DOCENTES
-- Perfiles de horario:
--   Diurno          (07:00-17:00): solo bloques hasta 15:00-17:00, sin tarde ni noche
--   Estandar-noche  (07:00-21:00): cubre hasta bloque 19:00-21:00 (docs 1,2,4,5,6,8,11,12)
--   Estandar        (07:00-19:00): cubre hasta bloque 17:00-19:00, sin noche (docs 14,17,18,22,23)
--   Manana          (07:00-15:00 o 08:00-15:00): solo bloques matutinos
--   Noche           (14:00-21:00): cubre bloque 19:00-21:00 (docs 9, 25)
--   Sabado          (07:00-13:00): disponibilidad sabado (docs 3,4,5,7,8,16,22)
-- NOTA: docs 1,2,4,5,6,8,11,12 extendidos a 21:00 para permitir pruebas nocturnas con 8+ grupos
-- ============================================================
INSERT INTO disponibilidad_docente (docente_id, dia_semana, hora_inicio, hora_fin) VALUES
    -- Doc 1 — TC extendido lun-vie (hasta 21:00, cubre noche)
    (1,'LUNES','07:00','21:00'),(1,'MARTES','07:00','21:00'),(1,'MIERCOLES','07:00','21:00'),
    (1,'JUEVES','07:00','21:00'),(1,'VIERNES','07:00','21:00'),
    -- Doc 2 — TC extendido lun-vie (hasta 21:00, cubre noche)
    (2,'LUNES','07:00','21:00'),(2,'MARTES','07:00','21:00'),(2,'MIERCOLES','07:00','21:00'),
    (2,'JUEVES','07:00','21:00'),(2,'VIERNES','07:00','21:00'),
    -- Doc 3 — TQ diurno lun-jue + sabado
    (3,'LUNES','07:00','17:00'),(3,'MARTES','07:00','17:00'),(3,'MIERCOLES','07:00','17:00'),
    (3,'JUEVES','07:00','17:00'),(3,'SABADO','07:00','13:00'),
    -- Doc 4 — TC extendido lun-vie (hasta 21:00, cubre noche) + sabado
    (4,'LUNES','07:00','21:00'),(4,'MARTES','07:00','21:00'),(4,'MIERCOLES','07:00','21:00'),
    (4,'JUEVES','07:00','21:00'),(4,'VIERNES','07:00','21:00'),(4,'SABADO','07:00','13:00'),
    -- Doc 5 — TC extendido lun-vie (hasta 21:00, cubre noche) + sabado
    (5,'LUNES','07:00','21:00'),(5,'MARTES','07:00','21:00'),(5,'MIERCOLES','07:00','21:00'),
    (5,'JUEVES','07:00','21:00'),(5,'VIERNES','07:00','21:00'),(5,'SABADO','07:00','13:00'),
    -- Doc 6 — TC extendido lun-vie (hasta 21:00, cubre noche)
    (6,'LUNES','07:00','21:00'),(6,'MARTES','07:00','21:00'),(6,'MIERCOLES','07:00','21:00'),
    (6,'JUEVES','07:00','21:00'),(6,'VIERNES','07:00','21:00'),
    -- Doc 7 — TC diurno lun-vie + sabado
    (7,'LUNES','07:00','17:00'),(7,'MARTES','07:00','17:00'),(7,'MIERCOLES','07:00','17:00'),
    (7,'JUEVES','07:00','17:00'),(7,'VIERNES','07:00','17:00'),(7,'SABADO','07:00','13:00'),
    -- Doc 8 — TC extendido lun-vie (hasta 21:00, cubre noche) + sabado
    (8,'LUNES','07:00','21:00'),(8,'MARTES','07:00','21:00'),(8,'MIERCOLES','07:00','21:00'),
    (8,'JUEVES','07:00','21:00'),(8,'VIERNES','07:00','21:00'),(8,'SABADO','07:00','13:00'),
    -- Doc 9 — MT noche mar/jue/vie (cubre bloque 19:00-21:00)
    (9,'MARTES','14:00','21:00'),(9,'JUEVES','14:00','21:00'),(9,'VIERNES','14:00','21:00'),
    -- Doc 10 — TQ mananas lun-vie (solo hasta 15:00)
    (10,'LUNES','07:00','15:00'),(10,'MARTES','07:00','15:00'),(10,'MIERCOLES','07:00','15:00'),
    (10,'JUEVES','07:00','15:00'),(10,'VIERNES','07:00','15:00'),
    -- Doc 11 — TC extendido lun-vie (hasta 21:00, cubre noche)
    (11,'LUNES','07:00','21:00'),(11,'MARTES','07:00','21:00'),(11,'MIERCOLES','07:00','21:00'),
    (11,'JUEVES','07:00','21:00'),(11,'VIERNES','07:00','21:00'),
    -- Doc 12 — TC extendido lun-vie (hasta 21:00, cubre noche)
    (12,'LUNES','07:00','21:00'),(12,'MARTES','07:00','21:00'),(12,'MIERCOLES','07:00','21:00'),
    (12,'JUEVES','07:00','21:00'),(12,'VIERNES','07:00','21:00'),
    -- Doc 13 — TC diurno lun-vie (no trabaja noche)
    (13,'LUNES','07:00','17:00'),(13,'MARTES','07:00','17:00'),(13,'MIERCOLES','07:00','17:00'),
    (13,'JUEVES','07:00','17:00'),(13,'VIERNES','07:00','17:00'),
    -- Doc 14 — TC estandar lun-vie (hasta 19:00)
    (14,'LUNES','07:00','19:00'),(14,'MARTES','07:00','19:00'),(14,'MIERCOLES','07:00','19:00'),
    (14,'JUEVES','07:00','19:00'),(14,'VIERNES','07:00','19:00'),
    -- Doc 15 — MT mananas lun-vie (entra a las 08:00, sale a las 15:00)
    (15,'LUNES','08:00','15:00'),(15,'MARTES','08:00','15:00'),(15,'MIERCOLES','08:00','15:00'),
    (15,'JUEVES','08:00','15:00'),(15,'VIERNES','08:00','15:00'),
    -- Doc 16 — CT solo sabado (no trabaja entre semana)
    (16,'SABADO','07:00','13:00'),
    -- Doc 17 — TC estandar lun-vie (hasta 19:00)
    (17,'LUNES','07:00','19:00'),(17,'MARTES','07:00','19:00'),(17,'MIERCOLES','07:00','19:00'),
    (17,'JUEVES','07:00','19:00'),(17,'VIERNES','07:00','19:00'),
    -- Doc 18 — TC estandar lun-vie (hasta 19:00)
    (18,'LUNES','07:00','19:00'),(18,'MARTES','07:00','19:00'),(18,'MIERCOLES','07:00','19:00'),
    (18,'JUEVES','07:00','19:00'),(18,'VIERNES','07:00','19:00'),
    -- Doc 19 — TQ diurno lun-jue (no trabaja noche)
    (19,'LUNES','07:00','17:00'),(19,'MARTES','07:00','17:00'),(19,'MIERCOLES','07:00','17:00'),
    (19,'JUEVES','07:00','17:00'),
    -- Doc 20 — TQ diurno lun-vie sin jueves (no trabaja noche)
    (20,'LUNES','07:00','17:00'),(20,'MARTES','07:00','17:00'),(20,'MIERCOLES','07:00','17:00'),
    (20,'VIERNES','07:00','17:00'),
    -- Doc 21 — TC diurno lun-vie (no trabaja noche)
    (21,'LUNES','07:00','17:00'),(21,'MARTES','07:00','17:00'),(21,'MIERCOLES','07:00','17:00'),
    (21,'JUEVES','07:00','17:00'),(21,'VIERNES','07:00','17:00'),
    -- Doc 22 — TC estandar lun-vie (hasta 19:00) + sabado
    (22,'LUNES','07:00','19:00'),(22,'MARTES','07:00','19:00'),(22,'MIERCOLES','07:00','19:00'),
    (22,'JUEVES','07:00','19:00'),(22,'VIERNES','07:00','19:00'),(22,'SABADO','07:00','13:00'),
    -- Doc 23 — TC estandar lun-vie (hasta 19:00)
    (23,'LUNES','07:00','19:00'),(23,'MARTES','07:00','19:00'),(23,'MIERCOLES','07:00','19:00'),
    (23,'JUEVES','07:00','19:00'),(23,'VIERNES','07:00','19:00'),
    -- Doc 24 — TQ diurno lun-jue (no trabaja noche)
    (24,'LUNES','07:00','17:00'),(24,'MARTES','07:00','17:00'),(24,'MIERCOLES','07:00','17:00'),
    (24,'JUEVES','07:00','17:00'),
    -- Doc 25 — MT noche lun/mie/vie (cubre bloque 19:00-21:00)
    (25,'LUNES','14:00','21:00'),(25,'MIERCOLES','14:00','21:00'),(25,'VIERNES','14:00','21:00');

-- ============================================================
-- 6. CURSOS
-- ============================================================
-- Programa: Ingeniería de Sistemas (cursos del plan de estudios)
INSERT INTO curso (codigo_curso, nombre_curso, frecuencia_semanal, semestre_nivel, id_tipo_aula) VALUES
    -- Semestre 1
    ('696',   'Matemáticas Básicas',                                                          2, 1, 1),  -- 1
    ('15040', 'Lógica Matemática',                                                            1, 1, 1),  -- 2
    ('15271', 'Fundamentos de Programación',                                                  2, 1, 2),  -- 3
    ('15272', 'Introducción a la Ingeniería de Sistemas',                                     2, 1, 1),  -- 4
    ('15273', 'Estructuras del Pensamiento 1 - Procesos Lógicos de Expresión',                1, 1, 1),  -- 5
    -- Semestre 2
    ('12693', 'Matemáticas 1 - Cálculo Diferencial',                                         2, 2, 1),  -- 6
    ('700',   'Álgebra Lineal',                                                               2, 2, 1),  -- 7
    ('12696', 'Física 1',                                                                     2, 2, 1),  -- 8
    ('12992', 'Programación 1',                                                               2, 2, 2),  -- 9
    ('15285', 'Estructuras del Pensamiento 2 - Argumentación y Procesos Algorítmicos',        1, 2, 1),  -- 10
    ('1146',  'Matemáticas Discretas',                                                        2, 2, 1),  -- 11
    -- Semestre 3
    ('12598', 'Matemáticas 2 - Cálculo Integral y Ecuaciones Diferenciales',                  2, 3, 1),  -- 12
    ('12609', 'Estadística y Probabilidad',                                                   1, 3, 1),  -- 13
    ('12599', 'Física 2',                                                                     2, 3, 1),  -- 14
    ('12994', 'Programación 2',                                                               2, 3, 2),  -- 15
    ('12995', 'Estructuras de Datos',                                                         2, 3, 2),  -- 16
    ('15292', 'Estructuras del Pensamiento 3 - Descripción y Análisis Proc. Multidisciplinar',1, 3, 1),  -- 17
    ('27',    'Seminario de Bioética',                                                        1, 3, 1),  -- 18
    -- Semestre 4
    ('15321', 'Matemáticas Aplicadas',                                                        2, 4, 1),  -- 19
    ('12998', 'Bases de Datos 1',                                                             2, 4, 2),  -- 20
    ('15327', 'Sistemas Digitales y Arquitectura de Computador',                              1, 4, 2),  -- 21
    ('12996', 'Microprocesadores y Programación Assembler',                                   1, 4, 2),  -- 22
    ('15318', 'Proyecto Núcleo 1',                                                            1, 4, 1),  -- 23
    ('15301', 'Estructuras del Pensamiento 4 - Descripción y Análisis de Contexto',           1, 4, 1),  -- 24
    ('13018', 'Electiva 1',                                                                   1, 4, 1),  -- 25
    -- Semestre 5
    ('13017', 'Dinámica de Sistemas',                                                         2, 5, 1),  -- 26
    ('12999', 'Complejidad Algorítmica',                                                      2, 5, 2),  -- 27
    ('13002', 'Ingeniería de Software 1',                                                     2, 5, 1),  -- 28
    ('13001', 'Bases de Datos 2',                                                             2, 5, 2),  -- 29
    ('12997', 'Sistemas Operacionales',                                                       2, 5, 2),  -- 30
    ('15328', 'Dirección y Planeación',                                                       2, 5, 1),  -- 31
    ('15149', 'Lógica Difusa',                                                                1, 5, 1),  -- 32
    ('15313', 'Historia y Filosofía de la Ingeniería',                                        1, 5, 1),  -- 33
    ('13019', 'Electiva 2',                                                                   1, 5, 1),  -- 34
    ('13020', 'Electiva 3',                                                                   1, 5, 1),  -- 35
    -- Semestre 6
    ('1142',  'Labor Social',                                                                 1, 6, 1),  -- 36
    ('13011', 'Modelos y Simulación de Sistemas',                                             2, 6, 2),  -- 37
    ('13005', 'Ingeniería de Software 2',                                                     2, 6, 1),  -- 38
    ('13000', 'Redes de Datos 1',                                                             2, 6, 2),  -- 39
    ('13007', 'Gestión de Proyectos',                                                         2, 6, 1),  -- 40
    ('15319', 'Proyecto Núcleo 2',                                                            1, 6, 1),  -- 41
    ('13006', 'Investigación Tecnológica y de Ingeniería',                                    1, 6, 1),  -- 42
    -- Semestre 7
    ('13021', 'Electiva Profesional 1',                                                       1, 7, 1),  -- 43
    ('15341', 'Ingeniería de Software 3',                                                     2, 7, 1),  -- 44
    ('15340', 'Redes de Datos 2',                                                             2, 7, 2),  -- 45
    ('13024', 'Gestión de Tecnología e Innovación',                                           1, 7, 1),  -- 46
    ('13053', 'Sistemas Inteligentes',                                                        1, 7, 2),  -- 47
    ('13022', 'Electiva 4',                                                                   1, 7, 1),  -- 48
    ('13049', 'Electiva 5',                                                                   1, 7, 1),  -- 49
    ('13050', 'Electiva 6',                                                                   1, 7, 1),  -- 50
    ('13051', 'Electiva 7',                                                                   1, 7, 1),  -- 51
    -- Semestre 8
    ('12991', 'Taller de Práctica Profesional',                                               1, 8, 1),  -- 52
    ('13009', 'Seguridad de la Información',                                                  2, 8, 2),  -- 53
    ('1119',  'Compiladores',                                                                 2, 8, 2),  -- 54
    ('12993', 'Legislación, Normatividad y Ciudadanía',                                       2, 8, 1),  -- 55
    ('13013', 'Proyecto de Grado 1',                                                          2, 8, 1),  -- 56
    -- Semestre 9
    ('13023', 'Electiva Profesional 2',                                                       1, 9, 1),  -- 57
    ('13012', 'Electiva Profesional 3',                                                       1, 9, 1),  -- 58
    ('13010', 'Seminario de Investigación',                                                   2, 9, 1),  -- 59
    -- Semestre 10
    ('13014', 'Práctica Profesional',                                                         3,10, 1),  -- 60
    ('13015', 'Proyecto de Grado 2',                                                          1,10, 1);  -- 61

-- Programa: Ingeniería Biomédica
-- Comparte: 696(MAT BAS), 12693(MAT1), 12696(FIS1), 12598(MAT2), 12609(ESTADÍSTICA), 27(BIOÉTICA)
INSERT INTO curso (codigo_curso, nombre_curso, frecuencia_semanal, semestre_nivel, id_tipo_aula) VALUES
    ('IB-101', 'Introducción a la Ingeniería Biomédica',       2, 1, 1),  -- 62
    ('IB-202', 'Anatomía y Fisiología para Ingenieros',        2, 2, 1),  -- 63
    ('IB-303', 'Bioinstrumentación 1',                         2, 3, 2),  -- 64
    ('IB-304', 'Química General para Ingenieros',              2, 3, 1),  -- 65
    ('IB-404', 'Bioinstrumentación 2',                         2, 4, 2),  -- 66
    ('IB-505', 'Procesamiento de Señales Biomédicas',          2, 5, 2),  -- 67
    ('IB-506', 'Electrónica Biomédica',                        2, 5, 2),  -- 68
    ('IB-606', 'Imágenes Médicas',                             2, 6, 2),  -- 69
    ('IB-707', 'Biomecánica',                                  2, 7, 1),  -- 70
    ('IB-808', 'Dispositivos Médicos y Regulación',            2, 8, 1);  -- 71

-- Programa: Ingeniería Industrial
-- Comparte: 696(MAT BAS), 12693(MAT1), 700(ÁLGEBRA), 12609(ESTADÍSTICA), 27(BIOÉTICA), 15328(DIR Y PLAN)
INSERT INTO curso (codigo_curso, nombre_curso, frecuencia_semanal, semestre_nivel, id_tipo_aula) VALUES
    ('II-101', 'Introducción a la Ingeniería Industrial',      2, 1, 1),  -- 72
    ('II-202', 'Economía General',                             2, 2, 1),  -- 73
    ('II-303', 'Investigación de Operaciones 1',               2, 3, 1),  -- 74
    ('II-304', 'Costos y Presupuestos',                        2, 3, 1),  -- 75
    ('II-404', 'Investigación de Operaciones 2',               2, 4, 1),  -- 76
    ('II-505', 'Gestión de la Producción',                     2, 5, 1),  -- 77
    ('II-506', 'Ergonomía y Seguridad Industrial',             2, 5, 1),  -- 78
    ('II-606', 'Logística y Cadena de Suministro',             2, 6, 1),  -- 79
    ('II-707', 'Control de Calidad',                           2, 7, 1),  -- 80
    ('II-808', 'Simulación de Sistemas Industriales',          2, 8, 2);  -- 81

-- ============================================================
-- 7. COMPATIBILIDADES DOCENTE-CURSO
-- ============================================================
INSERT INTO compatibilidad_docente_curso (docente_id, curso_id) VALUES
    -- Doc 1 — Carlos Rodríguez: Programación
    (1,3),(1,9),(1,15),(1,16),
    -- Doc 2 — Claudia Ramírez: Lógica, Algoritmos, Programación
    (2,2),(2,3),(2,9),(2,15),(2,16),(2,27),
    -- Doc 3 — Diego Castro: Arquitectura y Sistemas
    (3,9),(3,21),(3,22),(3,30),
    -- Doc 4 — María Torres: Bases de Datos y Modelado
    (4,20),(4,29),(4,37),
    -- Doc 5 — Luis Martínez: Redes y Seguridad
    (5,30),(5,39),(5,45),(5,53),
    -- Doc 6 — Sandra Gómez: Ingeniería de Software
    (6,28),(6,38),(6,44),
    -- Doc 7 — Jorge Herrera: IS + Proyectos + Intro IS
    (7,4),(7,28),(7,38),(7,44),(7,56),(7,61),
    -- Doc 8 — Paola Jiménez: Gestión y Proyectos
    (8,4),(8,23),(8,31),(8,40),(8,46),
    -- Doc 9 — Ricardo Peña: IA, Compiladores, Electivas
    (9,32),(9,47),(9,48),(9,49),(9,50),(9,51),(9,54),
    -- Doc 10 — Andrés Moreno: Sistemas y Redes
    (10,21),(10,22),(10,30),(10,39),(10,45),
    -- Doc 11 — Ana López: Matemáticas (todas)
    (11,1),(11,6),(11,12),(11,19),
    -- Doc 12 — Alejandro Vargas: Álgebra, Discreta, Estadística, Lógica
    (12,1),(12,2),(12,6),(12,7),(12,11),(12,13),
    -- Doc 13 — Héctor Méndez: Estadística, Dinámica, Modelado
    (13,13),(13,26),(13,37),
    -- Doc 14 — Fernanda Ríos: Física
    (14,8),(14,14),
    -- Doc 15 — Valentina Cruz: Humanidades y Pensamiento
    (15,5),(15,10),(15,17),(15,18),(15,24),(15,33),(15,36),(15,41),(15,42),
    -- Doc 16 — Andrés Ospina: Bioética (sábados)
    (16,18),
    -- Doc 17 — Beatriz Salcedo: Bioinstrumentación y Señales
    (17,64),(17,66),(17,67),(17,68),
    -- Doc 18 — Mauricio Pardo: Imágenes y Bioinstrumentación
    (18,64),(18,66),(18,69),
    -- Doc 19 — Patricia Lozano: Biomecánica y Dispositivos
    (19,70),(19,71),
    -- Doc 20 — Jaime Suárez: Intro IB, Anatomía, Química
    (20,62),(20,63),(20,65),
    -- Doc 21 — Gloria Medina: Investigación de Operaciones
    (21,74),(21,76),(21,81),
    -- Doc 22 — Felipe Ruiz: Gestión Industrial y Logística
    (22,77),(22,79),(22,80),
    -- Doc 23 — Marcela Osorio: Calidad, Costos, Ergonomía
    (23,75),(23,78),(23,80),
    -- Doc 24 — Natalia Cárdenas: Economía e Intro II
    (24,72),(24,73),(24,75),
    -- Doc 25 — Iván Guzmán: Simulación y Producción Industrial
    (25,76),(25,77),(25,81);

-- ============================================================
-- 8. GRUPOS SEMESTRE 2026-1
-- ============================================================
-- Formato: (seccion, num_inscritos, curso_id, docente_id)
-- IS Semestre 1
INSERT INTO grupo (seccion, num_inscritos, curso_id, docente_id) VALUES
    ('A', 38, 1,  11),  -- Matemáticas Básicas IS-A
    ('B', 35, 1,  12),  -- Matemáticas Básicas IS-B
    ('A', 30, 2,  12),  -- Lógica Matemática IS-A
    ('B', 28, 2,   2),  -- Lógica Matemática IS-B
    ('A', 25, 3,   1),  -- Fundamentos de Programación IS-A
    ('B', 24, 3,   2),  -- Fundamentos de Programación IS-B
    ('A', 40, 4,   7),  -- Introducción a IS IS-A
    ('B', 38, 4,   8),  -- Introducción a IS IS-B
    ('A', 40, 5,  15),  -- Estructuras del Pensamiento 1 IS-A

-- IS Semestre 2
    ('A', 35, 6,  11),  -- Matemáticas 1 IS-A
    ('B', 30, 6,  12),  -- Matemáticas 1 IS-B
    ('A', 33, 7,  12),  -- Álgebra Lineal IS-A
    ('A', 35, 8,  14),  -- Física 1 IS-A
    ('B', 32, 8,  14),  -- Física 1 IS-B
    ('A', 25, 9,   1),  -- Programación 1 IS-A
    ('B', 24, 9,   3),  -- Programación 1 IS-B
    ('A', 30, 11, 12),  -- Matemáticas Discretas IS-A

-- IS Semestre 3
    ('A', 33, 12, 11),  -- Matemáticas 2 IS-A
    ('A', 30, 13, 13),  -- Estadística y Probabilidad IS-A
    ('A', 35, 14, 14),  -- Física 2 IS-A
    ('A', 25, 15,  1),  -- Programación 2 IS-A
    ('A', 24, 16,  2),  -- Estructuras de Datos IS-A
    ('A', 38, 18, 15),  -- Seminario de Bioética IS-A

-- IS Semestre 4
    ('A', 30, 19, 11),  -- Matemáticas Aplicadas IS-A
    ('A', 25, 20,  4),  -- Bases de Datos 1 IS-A
    ('A', 24, 21,  3),  -- Sistemas Digitales IS-A
    ('A', 24, 22,  3),  -- Microprocesadores IS-A
    ('A', 35, 23,  8),  -- Proyecto Núcleo 1 IS-A

-- IS Semestre 5
    ('A', 35, 26, 13),  -- Dinámica de Sistemas IS-A
    ('A', 25, 27,  2),  -- Complejidad Algorítmica IS-A
    ('A', 35, 28,  6),  -- Ingeniería de Software 1 IS-A
    ('A', 25, 29,  4),  -- Bases de Datos 2 IS-A
    ('A', 25, 30,  5),  -- Sistemas Operacionales IS-A

-- IS Semestre 6
    ('A', 25, 37,  4),  -- Modelos y Simulación IS-A
    ('A', 35, 38,  6),  -- Ingeniería de Software 2 IS-A
    ('A', 25, 39,  5),  -- Redes de Datos 1 IS-A
    ('A', 35, 40,  8),  -- Gestión de Proyectos IS-A

-- Ingeniería Biomédica — cursos propios
    ('A', 30, 62, 20),  -- Introducción a Ing Biomédica IB-A
    ('A', 22, 64, 17),  -- Bioinstrumentación 1 IB-A
    ('A', 22, 66, 18),  -- Bioinstrumentación 2 IB-A
    ('A', 22, 67, 17),  -- Procesamiento de Señales IB-A

-- Ingeniería Biomédica — cursos compartidos (secciones propias)
    ('C', 32, 1,  11),  -- Matemáticas Básicas IB-C (misma materia, sección IB)
    ('C', 32, 8,  14),  -- Física 1 IB-C

-- Ingeniería Industrial — cursos propios
    ('A', 35, 72, 24),  -- Introducción a Ing Industrial II-A
    ('A', 32, 73, 24),  -- Economía General II-A
    ('A', 30, 74, 21),  -- Investigación de Operaciones 1 II-A

-- Ingeniería Industrial — cursos compartidos (secciones propias)
    ('D', 35, 1,  12),  -- Matemáticas Básicas II-D (misma materia, sección II)
    ('D', 32, 7,  12);  -- Álgebra Lineal II-D

-- ============================================================
-- 9. HORARIO BORRADOR VACÍO (punto de partida para el motor Greedy)
-- El admin debe generar y luego publicar para que sea visible públicamente.
-- ============================================================
INSERT INTO horario (semestre, estado, fecha_generacion, id_parametro)
VALUES ('2026-1', 'BORRADOR', NOW(), 1);

-- ============================================================
-- 10. USUARIOS (admin + 1 por docente — sin CRUD en UI)
-- Contraseña para todos: admin123
-- ============================================================
INSERT INTO usuario (username, password, rol, docente_id) VALUES
    ('admin', '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'ADMIN', NULL);

INSERT INTO usuario (username, password, rol, docente_id) VALUES
    ('c.rodriguez',  '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE',  1),
    ('c.ramirez',    '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE',  2),
    ('d.castro',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE',  3),
    ('m.torres',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE',  4),
    ('l.martinez',   '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE',  5),
    ('s.gomez',      '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE',  6),
    ('j.herrera',    '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE',  7),
    ('p.jimenez',    '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE',  8),
    ('r.pena',       '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE',  9),
    ('a.moreno',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 10),
    ('a.lopez',      '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 11),
    ('a.vargas',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 12),
    ('h.mendez',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 13),
    ('f.rios',       '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 14),
    ('v.cruz',       '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 15),
    ('a.ospina',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 16),
    ('b.salcedo',    '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 17),
    ('m.pardo',      '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 18),
    ('p.lozano',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 19),
    ('j.suarez',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 20),
    ('g.medina',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 21),
    ('f.ruiz',       '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 22),
    ('m.osorio',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 23),
    ('n.cardenas',   '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 24),
    ('i.guzman',     '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'DOCENTE', 25);

-- ESTUDIANTES (3 perfiles de muestra — docente_id NULL)
INSERT INTO usuario (username, password, rol, docente_id) VALUES
    ('est.sistemas',  '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'ESTUDIANTE', NULL),
    ('est.biomedica', '$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'ESTUDIANTE', NULL),
    ('est.industrial','$2a$10$IFiBCx1sGsbpoolfDT5DSeBh2NlLQIK5kyXFGK.wHaGbI8G78vKmq', 'ESTUDIANTE', NULL);

-- ============================================================
-- FRANJAS HORARIAS (generadas automáticamente desde ParametroSemestre)
-- LV: 07:00–22:00 en bloques de 2h | SA: 07:00–13:00 en bloques de 2h
-- Exclusión mediodía: 12:00–13:00 → franjas que solapan son inválidas
-- ============================================================
DO $$
DECLARE
    param_id INTEGER;
    v_inicio_lv TIME := '07:00';
    v_fin_lv    TIME := '22:00';
    v_inicio_sa TIME := '07:00';
    v_fin_sa    TIME := '13:00';
    v_excl_ini  TIME := '12:00';
    v_excl_fin  TIME := '13:00';
    v_cursor    TIME;
    v_hora_fin  TIME;
    v_solapa    BOOLEAN;
    dias_lv     TEXT[] := ARRAY['LUNES','MARTES','MIERCOLES','JUEVES','VIERNES'];
    dia         TEXT;
BEGIN
    SELECT id_parametro INTO param_id
    FROM parametro_semestre WHERE semestre = '2026-1' LIMIT 1;

    IF param_id IS NULL THEN RETURN; END IF;

    IF EXISTS (SELECT 1 FROM franja_horaria WHERE id_parametro = param_id) THEN
        RETURN;
    END IF;

    -- Lunes a Viernes
    FOREACH dia IN ARRAY dias_lv LOOP
        v_cursor := v_inicio_lv;
        WHILE v_cursor + INTERVAL '2 hours' <= v_fin_lv LOOP
            v_hora_fin := v_cursor + INTERVAL '2 hours';
            v_solapa := v_cursor < v_excl_fin AND v_hora_fin > v_excl_ini;
            INSERT INTO franja_horaria (dia_semana, hora_inicio, hora_valida, es_valida, id_parametro)
            VALUES (dia, v_cursor, v_hora_fin, NOT v_solapa, param_id);
            v_cursor := v_hora_fin;
        END LOOP;
    END LOOP;

    -- Sábado
    v_cursor := v_inicio_sa;
    WHILE v_cursor + INTERVAL '2 hours' <= v_fin_sa LOOP
        v_hora_fin := v_cursor + INTERVAL '2 hours';
        v_solapa := v_cursor < v_excl_fin AND v_hora_fin > v_excl_ini;
        INSERT INTO franja_horaria (dia_semana, hora_inicio, hora_valida, es_valida, id_parametro)
        VALUES ('SABADO', v_cursor, v_hora_fin, NOT v_solapa, param_id);
        v_cursor := v_hora_fin;
    END LOOP;
END $$;
