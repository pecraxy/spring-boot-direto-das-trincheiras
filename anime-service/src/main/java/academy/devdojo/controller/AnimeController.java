package academy.devdojo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("v1/animes")
public class AnimeController {
    private List<String> animes = List.of("Tokyo Ghoul", "Dungeon nii deai", "Kill la Kill", "Saiki Kusuo no Psi Nan");
    @GetMapping
    public ResponseEntity<List<String>> listAll(){
        return ResponseEntity.ok().body(animes);
    }
}
