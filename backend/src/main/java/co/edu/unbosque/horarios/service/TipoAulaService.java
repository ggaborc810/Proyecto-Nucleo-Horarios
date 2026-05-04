package co.edu.unbosque.horarios.service;

import co.edu.unbosque.horarios.dto.TipoAulaDTO;
import co.edu.unbosque.horarios.repository.TipoAulaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoAulaService {

    private final TipoAulaRepository tipoAulaRepo;

    public List<TipoAulaDTO> listarTodos() {
        return tipoAulaRepo.findAll().stream().map(TipoAulaDTO::from).toList();
    }

    public TipoAulaDTO obtener(Integer id) {
        return tipoAulaRepo.findById(id)
            .map(TipoAulaDTO::from)
            .orElseThrow(() -> new EntityNotFoundException("TipoAula no encontrado: " + id));
    }
}
