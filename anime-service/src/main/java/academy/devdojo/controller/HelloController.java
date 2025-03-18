package academy.devdojo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class HelloController {
    @GetMapping("hi")
    public ResponseEntity<String> hi(){
        return ResponseEntity.ok().body("OMAE WA MOU SHINDEIRU");
    }
}
