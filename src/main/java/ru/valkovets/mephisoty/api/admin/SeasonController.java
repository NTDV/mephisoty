package ru.valkovets.mephisoty.api.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.season.SeasonDto;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.service.season.SeasonService;

@RestController
@RequestMapping("/admin/season")
@RequiredArgsConstructor
@Tag(name = "Сезоны конкурса")
public class SeasonController {
private final SeasonService seasonService;

@GetMapping("/{id}")
@Operation(summary = "Получить информацию о сезоне")
public Season get(@PathVariable final Long id) {
    return seasonService.findById(id);
}

@PostMapping
@Operation(summary = "Добавить сезон")
public Season create(@RequestBody final SeasonDto dto) {
    return seasonService.save(dto);
}

@PutMapping("/{id}")
@Operation(summary = "Редактировать сезон")
public Season edit(@PathVariable final Long id, @RequestBody final SeasonDto dto) {
    return seasonService.edit(id, dto);
}

@DeleteMapping("/{id}")
@Operation(summary = "Удаление сезона")
public void delete(@PathVariable final Long id) {
    seasonService.delete(id);
}
}
