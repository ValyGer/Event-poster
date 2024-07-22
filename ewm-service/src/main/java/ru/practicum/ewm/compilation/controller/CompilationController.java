package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompilationController {

    private final CompilationService compilationService;

    // часть admin

    @PostMapping(value = "/admin/compilations")
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody
                                                            NewCompilationDto newCompilationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.createCompilation(newCompilationDto));
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(
            @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(compilationService.updateCompilation(compId, updateCompilationRequest));
    }


    // часть public

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationDto>> getAllCompilations(
            @RequestParam(required = false, defaultValue = "false") String pinned,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return ResponseEntity.status(HttpStatus.OK).body(compilationService.getAllCompilations(
                Boolean.valueOf(pinned), from, size));
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable Long compId) {
        return ResponseEntity.status(HttpStatus.OK).body(compilationService.getCompilationById(compId));
    }
}
