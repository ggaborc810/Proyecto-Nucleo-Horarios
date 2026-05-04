package co.edu.unbosque.horarios.service;

import co.edu.unbosque.horarios.dto.FranjaDTO;
import co.edu.unbosque.horarios.dto.ParametroSemestreDTO;
import co.edu.unbosque.horarios.model.FranjaHoraria;
import co.edu.unbosque.horarios.model.ParametroSemestre;
import co.edu.unbosque.horarios.repository.FranjaHorarioRepository;
import co.edu.unbosque.horarios.repository.ParametroSemestreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParametroSemestreService {

    private final ParametroSemestreRepository paramRepo;
    private final FranjaHorarioRepository franjaRepo;

    public List<ParametroSemestreDTO> listarTodos() {
        return paramRepo.findAll().stream().map(ParametroSemestreDTO::from).toList();
    }

    public ParametroSemestreDTO obtenerActivo() {
        return paramRepo.findByActivoTrue()
            .map(ParametroSemestreDTO::from)
            .orElseThrow(() -> new EntityNotFoundException("No hay parámetro de semestre activo"));
    }

    public ParametroSemestreDTO obtener(Integer id) {
        return paramRepo.findById(id)
            .map(ParametroSemestreDTO::from)
            .orElseThrow(() -> new EntityNotFoundException("Parámetro no encontrado: " + id));
    }

    @Transactional
    public ParametroSemestreDTO crear(ParametroSemestreDTO dto) {
        ParametroSemestre p = paramRepo.save(dto.toEntity());
        generarFranjas(p);
        return ParametroSemestreDTO.from(p);
    }

    @Transactional
    public ParametroSemestreDTO actualizar(Integer id, ParametroSemestreDTO dto) {
        ParametroSemestre p = paramRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Parámetro no encontrado: " + id));
        p.setSemestre(dto.semestre());
        p.setFranjaInicioLV(dto.franjaInicioLV());
        p.setFranjaFinLV(dto.franjaFinLV());
        p.setFranjaInicioSA(dto.franjaInicioSA());
        p.setFranjaFinSA(dto.franjaFinSA());
        p.setExclusionInicio(dto.exclusionInicio());
        p.setExclusionFin(dto.exclusionFin());
        p.setCapMaxGrupo(dto.capMaxGrupo());
        p.setUmbralCierre(dto.umbralCierre());
        p.setFreqMaxSesion(dto.freqMaxSesion());
        p.setActivo(dto.activo());
        return ParametroSemestreDTO.from(paramRepo.save(p));
    }

    public List<FranjaDTO> listarFranjasActivo() {
        ParametroSemestre p = paramRepo.findByActivoTrue()
            .orElseThrow(() -> new EntityNotFoundException("No hay parámetro activo"));
        return franjaRepo.findByParametroAndEsValidaTrue(p)
            .stream().map(FranjaDTO::from).toList();
    }

    @Transactional
    public int regenerarFranjas(Integer id) {
        ParametroSemestre p = paramRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Parámetro no encontrado: " + id));
        franjaRepo.deleteAll(franjaRepo.findByParametro(p));
        generarFranjas(p);
        return franjaRepo.findByParametro(p).size();
    }

    private void generarFranjas(ParametroSemestre p) {
        List<FranjaHoraria> franjas = new ArrayList<>();
        String[] diasLV = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"};
        for (String dia : diasLV) {
            franjas.addAll(generarFranjasDia(dia, p.getFranjaInicioLV(), p.getFranjaFinLV(), p));
        }
        franjas.addAll(generarFranjasDia("SABADO", p.getFranjaInicioSA(), p.getFranjaFinSA(), p));
        franjaRepo.saveAll(franjas);
    }

    private List<FranjaHoraria> generarFranjasDia(String dia, LocalTime inicio, LocalTime fin,
                                                    ParametroSemestre p) {
        List<FranjaHoraria> resultado = new ArrayList<>();
        LocalTime cursor = inicio;
        while (!cursor.plusHours(2).isAfter(fin)) {
            LocalTime horaValida = cursor.plusHours(2);
            boolean solapa = cursor.isBefore(p.getExclusionFin())
                          && horaValida.isAfter(p.getExclusionInicio());
            resultado.add(FranjaHoraria.builder()
                .diaSemana(dia)
                .horaInicio(cursor)
                .horaValida(horaValida)
                .esValida(!solapa)
                .parametro(p)
                .build());
            cursor = horaValida;
        }
        return resultado;
    }
}
