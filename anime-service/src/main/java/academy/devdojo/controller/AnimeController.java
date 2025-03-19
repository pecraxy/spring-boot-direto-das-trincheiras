package academy.devdojo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("v1/animes")
@Slf4j
public class AnimeController {
    @GetMapping
    public List<String> listAll(){
        log.info(Thread.currentThread().getName());
        return List.of("Tokyo Ghoul", "Dungeon nii deai", "Kill la Kill", "Saiki Kusuo no Psi Nan");
    }
}
