# Repositorios

Paquete: `co.edu.unbosque.horarios.repository`. Todas las interfaces extienden `JpaRepository<Entidad, IdType>`.

## AulaRepository

```java
public interface AulaRepository extends JpaRepository<Aula, Integer> {
    List<Aula> findByActivaTrue();
    List<Aula> findByCapacidadGreaterThanEqualAndActivaTrue(Integer capacidad);
    List<Aula> findByTipoAulaAndActivaTrue(TipoAula tipoAula);
}
```

## DocenteRepository

```java
public interface DocenteRepository extends JpaRepository<Docente, Integer> {
    Optional<Docente> findByEmail(String email);
    List<Docente> findByTipoVinculacion(String tipo);
    Optional<Docente> findByNumeroDocumento(String numeroDocumento);
}
```

## DisponibilidadDocenteRepository

```java
public interface DisponibilidadDocenteRepository
        extends JpaRepository<DisponibilidadDocente, Integer> {
    List<DisponibilidadDocente> findByDocente(Docente docente);
    List<DisponibilidadDocente> findByDocenteAndDiaSemana(Docente docente, String dia);
    void deleteByDocente(Docente docente);
}
```

## CursoRepository

```java
public interface CursoRepository extends JpaRepository<Curso, Integer> {
    Optional<Curso> findByCodigoCurso(String codigo);
    List<Curso> findByTipoAulaRequerida(TipoAula tipo);
    List<Curso> findBySemestreNivel(Integer semestreNivel);
}
```

## CompatibilidadDocenteCursoRepository

```java
public interface CompatibilidadDocenteCursoRepository
        extends JpaRepository<CompatibilidadDocenteCurso, Integer> {
    boolean existsByDocenteAndCurso(Docente docente, Curso curso);
    List<CompatibilidadDocenteCurso> findByDocente(Docente docente);
    List<CompatibilidadDocenteCurso> findByCurso(Curso curso);
    void deleteByDocenteAndCurso(Docente docente, Curso curso);
}
```

## ParametroSemestreRepository

```java
public interface ParametroSemestreRepository
        extends JpaRepository<ParametroSemestre, Integer> {
    Optional<ParametroSemestre> findBySemestre(String semestre);
    Optional<ParametroSemestre> findByActivoTrue();
}
```

## FranjaHorarioRepository

```java
public interface FranjaHorarioRepository extends JpaRepository<FranjaHoraria, Integer> {
    List<FranjaHoraria> findByEsValidaTrue();
    List<FranjaHoraria> findByDiaSemana(String dia);
    List<FranjaHoraria> findByParametro(ParametroSemestre parametro);
    List<FranjaHoraria> findByParametroAndEsValidaTrue(ParametroSemestre parametro);
}
```

## GrupoRepository

```java
public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
    List<Grupo> findByEstado(String estado);
    List<Grupo> findByCurso(Curso curso);
    List<Grupo> findByDocente(Docente docente);
    List<Grupo> findByEstadoAndNumInscritosLessThan(String estado, Integer umbral);
}
```

## HorarioRepository

```java
public interface HorarioRepository extends JpaRepository<Horario, Integer> {
    Optional<Horario> findBySemestre(String semestre);
    Optional<Horario> findBySemestreAndEstado(String semestre, String estado);
    List<Horario> findByEstado(String estado);
}
```

## AsignacionRepository

```java
public interface AsignacionRepository extends JpaRepository<Asignacion, Integer> {
    List<Asignacion> findByHorario(Horario horario);
    List<Asignacion> findByHorarioAndHcVioladoIsNotNull(Horario horario);
    List<Asignacion> findByHorarioAndHcVioladoIsNull(Horario horario);
    
    // Para detectar conflictos antes de persistir movimiento manual
    Optional<Asignacion> findByDocenteAndFranjaAndEstado(Docente d, FranjaHoraria f, String estado);
    Optional<Asignacion> findByAulaAndFranjaAndEstado(Aula a, FranjaHoraria f, String estado);
    
    // Vista pública filtrada
    List<Asignacion> findByHorarioAndDocente(Horario horario, Docente docente);
    List<Asignacion> findByHorarioAndAula(Horario horario, Aula aula);
    
    @Query("SELECT a FROM Asignacion a WHERE a.horario = :horario AND a.grupo.curso = :curso")
    List<Asignacion> findByHorarioAndCurso(@Param("horario") Horario horario,
                                            @Param("curso") Curso curso);
}
```

## UsuarioRepository

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByDocente(Docente docente);
}
```

## TipoAulaRepository

```java
public interface TipoAulaRepository extends JpaRepository<TipoAula, Integer> {
    Optional<TipoAula> findByNombreTipo(String nombre);
}
```

## Patrón de uso

Spring inyecta automáticamente las implementaciones generadas por Spring Data JPA. En tests unitarios del motor, se mockean con Mockito:

```java
@Mock private DocenteRepository docenteRepo;
when(docenteRepo.findAll()).thenReturn(listaMock);
```
