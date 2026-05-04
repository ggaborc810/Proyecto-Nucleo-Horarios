package co.edu.unbosque.horarios.repository;

import co.edu.unbosque.horarios.model.Docente;
import co.edu.unbosque.horarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByDocente(Docente docente);
}
