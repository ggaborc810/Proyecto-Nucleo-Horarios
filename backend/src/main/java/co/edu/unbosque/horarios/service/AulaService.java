package co.edu.unbosque.horarios.service;

import co.edu.unbosque.horarios.dto.AulaDTO;
import co.edu.unbosque.horarios.model.Aula;
import co.edu.unbosque.horarios.model.TipoAula;
import co.edu.unbosque.horarios.repository.AulaRepository;
import co.edu.unbosque.horarios.repository.TipoAulaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AulaService {

    private final AulaRepository aulaRepo;
    private final TipoAulaRepository tipoAulaRepo;

    public List<AulaDTO> listarTodos() {
        return aulaRepo.findAll().stream().map(AulaDTO::from).toList();
    }

    public List<AulaDTO> listarActivas() {
        return aulaRepo.findByActivaTrue().stream().map(AulaDTO::from).toList();
    }

    public AulaDTO obtener(Integer id) {
        return aulaRepo.findById(id)
            .map(AulaDTO::from)
            .orElseThrow(() -> new EntityNotFoundException("Aula no encontrada: " + id));
    }

    @Transactional
    public AulaDTO crear(AulaDTO dto) {
        TipoAula tipo = tipoAulaRepo.findById(dto.tipoAulaId())
            .orElseThrow(() -> new EntityNotFoundException("TipoAula no encontrado: " + dto.tipoAulaId()));
        Aula aula = Aula.builder()
            .codigoAula(dto.codigoAula())
            .capacidad(dto.capacidad())
            .ubicacion(dto.ubicacion())
            .activa(dto.activa() != null ? dto.activa() : true)
            .tipoAula(tipo)
            .build();
        return AulaDTO.from(aulaRepo.save(aula));
    }

    @Transactional
    public AulaDTO actualizar(Integer id, AulaDTO dto) {
        Aula aula = aulaRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aula no encontrada: " + id));
        TipoAula tipo = tipoAulaRepo.findById(dto.tipoAulaId())
            .orElseThrow(() -> new EntityNotFoundException("TipoAula no encontrado: " + dto.tipoAulaId()));
        aula.setCodigoAula(dto.codigoAula());
        aula.setCapacidad(dto.capacidad());
        aula.setUbicacion(dto.ubicacion());
        aula.setActiva(dto.activa() != null ? dto.activa() : aula.getActiva());
        aula.setTipoAula(tipo);
        return AulaDTO.from(aulaRepo.save(aula));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!aulaRepo.existsById(id)) throw new EntityNotFoundException("Aula no encontrada: " + id);
        aulaRepo.deleteById(id);
    }
}
