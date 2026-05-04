package co.edu.unbosque.horarios.testutil;

import co.edu.unbosque.horarios.model.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Utilidades de construcción de objetos de prueba sin Spring ni BD. */
public final class TestDataFactory {

    private TestDataFactory() {}

    public static Docente docente(int id) {
        Docente d = new Docente();
        d.setDocenteId(id);
        d.setNombreCompleto("Docente " + id);
        d.setNumeroDocumento(String.valueOf(10000000 + id));
        d.setTipoVinculacion("TIEMPO_COMPLETO");
        d.setHorasMaxSemana(20);
        d.setEmail("docente" + id + "@test.co");
        d.setDisponibilidades(new ArrayList<>());
        return d;
    }

    public static Docente docenteConDisponibilidad(String dia, String horaInicio, String horaFin) {
        Docente d = docente(1);
        DisponibilidadDocente disp = new DisponibilidadDocente();
        disp.setDisponibilidadId(1);
        disp.setDocente(d);
        disp.setDiaSemana(dia);
        disp.setHoraInicio(LocalTime.parse(horaInicio));
        disp.setHoraFin(LocalTime.parse(horaFin));
        d.getDisponibilidades().add(disp);
        return d;
    }

    public static Docente docenteConDisponibilidadCompleta() {
        Docente d = docente(1);
        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"};
        int dispId = 1;
        for (String dia : dias) {
            DisponibilidadDocente disp = new DisponibilidadDocente();
            disp.setDisponibilidadId(dispId++);
            disp.setDocente(d);
            disp.setDiaSemana(dia);
            disp.setHoraInicio(LocalTime.of(7, 0));
            disp.setHoraFin(LocalTime.of(22, 0));
            d.getDisponibilidades().add(disp);
        }
        return d;
    }

    public static TipoAula tipoAula(int id, String nombre) {
        TipoAula t = new TipoAula();
        t.setIdTipoAula(id);
        t.setNombreTipo(nombre);
        return t;
    }

    public static Aula aula(int id) {
        return aula(id, 40, tipoAula(1, "CONVENCIONAL"));
    }

    public static Aula aula(int id, int capacidad) {
        return aula(id, capacidad, tipoAula(1, "CONVENCIONAL"));
    }

    public static Aula aula(int id, int capacidad, TipoAula tipo) {
        Aula a = new Aula();
        a.setAulaId(id);
        a.setCodigoAula("A-" + id);
        a.setCapacidad(capacidad);
        a.setActiva(true);
        a.setTipoAula(tipo);
        return a;
    }

    public static Curso curso(int id) {
        return curso(id, 2, tipoAula(1, "CONVENCIONAL"));
    }

    public static Curso curso(int id, TipoAula tipo) {
        return curso(id, 2, tipo);
    }

    public static Curso curso(int id, int frecuencia, TipoAula tipo) {
        Curso c = new Curso();
        c.setCursoId(id);
        c.setCodigoCurso("IS-" + (100 + id));
        c.setNombreCurso("Curso " + id);
        c.setFrecuenciaSemanal(frecuencia);
        c.setSemestreNivel(3);
        c.setTipoAulaRequerida(tipo);
        return c;
    }

    public static Grupo grupo(int id) {
        return grupo(id, 25);
    }

    public static Grupo grupo(int id, int inscritos) {
        Grupo g = new Grupo();
        g.setGrupoId(id);
        g.setSeccion("A");
        g.setNumInscritos(inscritos);
        g.setEstado("ACTIVO");
        g.setCurso(curso(id));
        g.setDocente(docente(id));
        return g;
    }

    public static Grupo grupo(Curso curso) {
        Grupo g = new Grupo();
        g.setGrupoId(1);
        g.setSeccion("A");
        g.setNumInscritos(25);
        g.setEstado("ACTIVO");
        g.setCurso(curso);
        g.setDocente(docente(1));
        return g;
    }

    public static Grupo grupo(int id, Curso curso, Docente docente, int inscritos) {
        Grupo g = new Grupo();
        g.setGrupoId(id);
        g.setSeccion("A");
        g.setNumInscritos(inscritos);
        g.setEstado("ACTIVO");
        g.setCurso(curso);
        g.setDocente(docente);
        return g;
    }

    public static Grupo grupoConFrecuencia(int id, int frecuencia) {
        Grupo g = grupo(id, 20);
        g.getCurso().setFrecuenciaSemanal(frecuencia);
        return g;
    }

    public static FranjaHoraria franja(int id, String dia, String horaInicio) {
        FranjaHoraria f = new FranjaHoraria();
        f.setFranjaId(id);
        f.setDiaSemana(dia);
        f.setHoraInicio(LocalTime.parse(horaInicio));
        f.setHoraValida(LocalTime.parse(horaInicio).plusHours(2));
        f.setEsValida(true);
        return f;
    }

    public static FranjaHoraria franjaInvalida(int id, String dia, String horaInicio) {
        FranjaHoraria f = franja(id, dia, horaInicio);
        f.setEsValida(false);
        return f;
    }

    /** Genera 10 franjas válidas distribuidas en 5 días (2 por día). */
    public static List<FranjaHoraria> generarFranjas10Bloques() {
        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"};
        String[] horas = {"07:00", "09:00"};
        List<FranjaHoraria> result = new ArrayList<>();
        int id = 1;
        for (String dia : dias) {
            for (String hora : horas) {
                result.add(franja(id++, dia, hora));
            }
        }
        return result;
    }

    public static ParametroSemestre parametros() {
        ParametroSemestre p = new ParametroSemestre();
        p.setIdParametro(1);
        p.setSemestre("2026-1");
        p.setFranjaInicioLV(LocalTime.of(7, 0));
        p.setFranjaFinLV(LocalTime.of(22, 0));
        p.setFranjaInicioSA(LocalTime.of(7, 0));
        p.setFranjaFinSA(LocalTime.of(13, 0));
        p.setExclusionInicio(LocalTime.of(12, 0));
        p.setExclusionFin(LocalTime.of(13, 0));
        p.setCapMaxGrupo(40);
        p.setUmbralCierre(10);
        p.setFreqMaxSesion(4);
        p.setActivo(true);
        return p;
    }

    public static ParametroSemestre parametros(int umbralCierre) {
        ParametroSemestre p = parametros();
        p.setUmbralCierre(umbralCierre);
        return p;
    }
}
