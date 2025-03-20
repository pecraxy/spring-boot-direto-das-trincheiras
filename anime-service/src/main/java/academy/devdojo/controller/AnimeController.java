package academy.devdojo.controller;

import academy.devdojo.domain.Anime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("v1/animes")
@Slf4j
public class AnimeController {


    @GetMapping
    public List<Anime> listAll(@RequestParam(required = false) String name){
        if (name == null) return Anime.getAnimes();
        return Anime.getAnimes().stream()
                .filter(anime -> anime.getName().contains(name))
                .toList();
    }

    @PostMapping
    public Anime save(@RequestBody Anime anime){
        anime.setId(ThreadLocalRandom.current().nextLong(1, 100));
        Anime.getAnimes().add(anime);
        return anime;
    }

    @GetMapping("{id}")
    public Anime findById(@PathVariable Long id){
        return Anime.getAnimes().stream()
                .filter(anime -> anime.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
