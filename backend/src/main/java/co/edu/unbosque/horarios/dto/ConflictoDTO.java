package co.edu.unbosque.horarios.dto;

import co.edu.unbosque.horarios.model.Asignacion;

import java.util.List;

public record ConflictoDTO(
    Integer grupoId,
    String seccion,
    String nombreCurso,
    String nombreDocente,
    int sesionNumero,
    String hcViolado,
    String descripcion,
    List<String> accionesCorrectivas
) {
    public static ConflictoDTO from(Asignacion a) {
        String hc = a.getHcViolado();
        return new ConflictoDTO(
            a.getGrupo().getGrupoId(),
            a.getGrupo().getSeccion(),
            a.getGrupo().getCurso().getNombreCurso(),
            a.getDocente() != null ? a.getDocente().getNombreCompleto() : "Sin docente",
            0,
            hc,
            descripcionHC(hc),
            accionesHC(hc)
        );
    }

    private static String descripcionHC(String hc) {
        if (hc == null) return "Conflicto de asignación";
        return switch (hc) {
            case "HC-01" -> "El docente ya tiene una sesión asignada en esa franja horaria";
            case "HC-02" -> "El aula requerida ya está ocupada en esa franja horaria";
            case "HC-03" -> "El docente no tiene disponibilidad declarada en ninguna franja libre";
            case "HC-04" -> "No existen aulas activas del tipo requerido por el curso";
            case "HC-05" -> "Ninguna aula tiene capacidad suficiente para el número de inscritos";
            case "HC-06" -> "La franja horaria está fuera del rango permitido por los parámetros";
            case "HC-07" -> "La franja horaria cae en el bloque de exclusión del mediodía";
            case "HC-08" -> "La duración de la sesión no es de exactamente 2 horas";
            case "HC-09" -> "El grupo ya tiene todas sus sesiones semanales asignadas";
            case "HC-10" -> "El docente no tiene compatibilidad registrada con este curso";
            case "HC-11" -> "La franja está fuera del rango horario permitido para este curso";
            case "HC-12" -> "Otro grupo del mismo semestre ya está asignado en esa franja horaria";
            case "HC-13" -> "El grupo ya tiene una sesión en este bloque horario; sus sesiones deben estar en horarios distintos";
            case "TIMEOUT" -> "El algoritmo alcanzó el límite de iteraciones o tiempo máximo";
            default -> "Restricción violada: " + hc;
        };
    }

    private static List<String> accionesHC(String hc) {
        if (hc == null) return List.of("Revisar la configuración de datos maestros");
        return switch (hc) {
            case "HC-01" -> List.of(
                "Asignar otro docente compatible con el curso",
                "Revisar y redistribuir la carga del docente en el calendario"
            );
            case "HC-02" -> List.of(
                "Agregar más aulas del tipo requerido o aumentar las existentes",
                "Revisar si hay aulas inactivas que puedan habilitarse"
            );
            case "HC-03" -> List.of(
                "Ampliar la disponibilidad horaria del docente en la sección de Docentes",
                "Asignar un docente alternativo con mayor disponibilidad"
            );
            case "HC-04" -> List.of(
                "Verificar que existan aulas activas del tipo requerido por el curso",
                "Activar o crear aulas del tipo correspondiente en la sección de Aulas"
            );
            case "HC-05" -> List.of(
                "Aumentar la capacidad del aula o habilitar un aula de mayor aforo",
                "Dividir el grupo en secciones más pequeñas"
            );
            case "HC-09" -> List.of(
                "Reducir la frecuencia semanal del curso o agregar más franjas disponibles"
            );
            case "HC-10" -> List.of(
                "Registrar la compatibilidad docente-curso en el módulo de Docentes",
                "Asignar un docente que ya tenga compatibilidad con el curso"
            );
            case "HC-11" -> List.of(
                "Ajustar el rango horario permitido del curso (hora inicio / hora fin)",
                "Modificar la disponibilidad del docente para cubrir el rango requerido"
            );
            case "HC-12" -> List.of(
                "El semestre ya tiene todas sus franjas ocupadas; reducir la cantidad de grupos activos de ese semestre",
                "Ampliar la franja horaria del semestre agregando disponibilidad en más días"
            );
            case "HC-13" -> List.of(
                "El grupo necesita más bloques horarios disponibles de los que tiene el docente declarados",
                "Aumentar la disponibilidad del docente para cubrir distintos bloques horarios (07-09, 09-11, 13-15…)"
            );
            case "TIMEOUT" -> List.of(
                "El sistema no pudo encontrar solución en el tiempo límite",
                "Revisar si hay docentes con carga excesiva o aulas insuficientes",
                "Intentar regenerar el horario después de ajustar los datos maestros"
            );
            default -> List.of("Revisar la configuración de datos maestros y reintentar la generación");
        };
    }
}
