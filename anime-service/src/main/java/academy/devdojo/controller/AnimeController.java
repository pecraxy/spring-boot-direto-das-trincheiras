package academy.devdojo.controller;

import academy.devdojo.domain.Anime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/animes")
@Slf4j
public class AnimeController {
    @GetMapping
    public List<Anime> listAll(@RequestParam(required = false) String name){
        List<Anime> animes = Anime.getAnimes();
        if (name == null) return animes;
        return Anime.getAnimes().stream()
                .filter(anime -> anime.getName().contains(name))
                .toList();
    }


    @GetMapping("{id}")
    public Anime filterById(@PathVariable Long id){
        List<Anime> animeList = Anime.getAnimes();
        return animeList.stream()
                .filter(anime -> anime.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
