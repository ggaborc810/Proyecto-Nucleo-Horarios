UPDATE parametro_semestre
SET franja_inicio_sa = '07:00',
    franja_fin_sa = '13:00'
WHERE semestre = '2026-1';

UPDATE franja_horaria fh
SET es_valida = TRUE
FROM parametro_semestre ps
WHERE fh.id_parametro = ps.id_parametro
  AND ps.semestre = '2026-1'
  AND fh.dia_semana = 'SABADO'
  AND fh.hora_inicio >= '07:00'
  AND fh.hora_valida <= '13:00';

INSERT INTO franja_horaria (dia_semana, hora_inicio, hora_valida, es_valida, id_parametro)
SELECT 'SABADO', v.hora_inicio, v.hora_valida, TRUE, ps.id_parametro
FROM parametro_semestre ps
CROSS JOIN (
    VALUES
        (TIME '07:00', TIME '09:00'),
        (TIME '09:00', TIME '11:00'),
        (TIME '11:00', TIME '13:00')
) AS v(hora_inicio, hora_valida)
WHERE ps.semestre = '2026-1'
  AND NOT EXISTS (
      SELECT 1
      FROM franja_horaria fh
      WHERE fh.id_parametro = ps.id_parametro
        AND fh.dia_semana = 'SABADO'
        AND fh.hora_inicio = v.hora_inicio
        AND fh.hora_valida = v.hora_valida
  );
