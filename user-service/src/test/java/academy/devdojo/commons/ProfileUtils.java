package academy.devdojo.commons;

import academy.devdojo.domain.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileUtils {
    public List<Profile> newProfileList(){
        Profile profile1 = Profile.builder().id(1L).name("Dj Talala").description("O Bruxo da Putaria").build();
        Profile profile2 = Profile.builder().id(2L).name("Dj Blakes").description("Lança as pura porra, só mandelão original").build();
        Profile profile3 = Profile.builder().id(3L).name("Bryan Pinaffo").description("Batata").build();
        Profile profile4 = Profile.builder().id(4L).name("Devdojo").description("Os Melhores Cursos de Todos").build();
        Profile profile5 = Profile.builder().id(5L).name("Boca de Pelo").description("Beijou a Mina que Mamou o Bonde Inteiro").build();
        Profile profile6 = Profile.builder().id(6L).name("Alexandre Demorais").description("Demorando aqui fio").build();
        Profile profile7 = Profile.builder().id(7L).name("Unchroma").description("Código UNCHROMA10 na Blaze").build();
        return new ArrayList<>(List.of(profile1, profile2, profile3, profile4, profile5, profile6, profile7));
    }

    public Profile newProfileToCreate(){
        return Profile.builder().name("Cocota Feia").description("Ninguém pra quebrar meu galho?").build();
    }
}
