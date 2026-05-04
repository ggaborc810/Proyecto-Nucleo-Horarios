package co.edu.unbosque.horarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GenerarHorarioRequest(
    @NotBlank String semestre,
    @NotNull @Size(min = 2, max = 8, message = "Debes seleccionar entre 2 y 8 materias")
    List<Integer> cursoIds
) {}
