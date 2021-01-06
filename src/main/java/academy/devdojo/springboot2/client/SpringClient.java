package academy.devdojo.springboot2.client;

import academy.devdojo.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes", Anime.class);
        log.info(entity);

        Anime object = new RestTemplate().getForObject("http://localhost:8080/animes", Anime.class);
        log.info(object);

        Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes", Anime[].class);
        log.info(Arrays.toString(animes));

        ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                });
        log.info(exchange.getBody());

        Anime kingdon = Anime.builder().name("kingdon").build();
        Anime kingdonSaved = new RestTemplate().postForObject("http://localhost:8080/animes", kingdon, Anime.class);
        log.info("saved anime {}",kingdonSaved);

        Anime samuraiChamploo = Anime.builder().name("samuraiChamploo").build();
        ResponseEntity<Anime> samuraiChamplooSaved = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.PUT, new HttpEntity<>(samuraiChamploo), Anime.class);
        log.info("saved anime {}",samuraiChamplooSaved);

        Anime animeUpdated = samuraiChamplooSaved.getBody();
        animeUpdated.setName("samuraiChamploo 2");
        ResponseEntity<Void> samuraiChamplooUpdated = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.PUT, new HttpEntity<>(animeUpdated, createJsonHeader()), Void.class);
        log.info("saved anime {}",samuraiChamplooUpdated);

        animeUpdated.setName("samuraiChamploo 2");
        ResponseEntity<Void> samuraiChamplooDelete = new RestTemplate().exchange("http://localhost:8080/animes/{id}",
                HttpMethod.DELETE, null, Void.class, animeUpdated.getId());
        log.info("saved anime {}",samuraiChamplooDelete);
    }

    public static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;

    }
}
