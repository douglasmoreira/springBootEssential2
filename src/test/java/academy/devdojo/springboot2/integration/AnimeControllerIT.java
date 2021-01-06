package academy.devdojo.springboot2.integration;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.DevDojoUser;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.DevDojoUserRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static academy.devdojo.springboot2.util.AnimeCreator.createAnimeToBeSaved;
import static academy.devdojo.springboot2.util.AnimeCreator.createValidAnime;
import static academy.devdojo.springboot2.util.AnimePostRequestBodyCreator.createAnimePostRequestBody;
import static academy.devdojo.springboot2.util.AnimePutRequestBodyCreator.createAnimePutRequestBody;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {
    @Autowired
    @Qualifier(value = ("testRestTemplateRoleUser"))
    private TestRestTemplate testRestTemplateRoleUser;
    @Autowired
    @Qualifier(value = ("testRestTemplateRoleAdmin"))
    private TestRestTemplate testRestTemplateRoleAdmin;
    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private DevDojoUserRepository devDojoUserRepository;
    private static DevDojoUser USER = DevDojoUser.builder()
            .name("devdojo")
            .password("{bcrypt}$2a$10$DbyN0UNd6kYdi6kWKEENwulquefdNE1SH0lOY1S4A5l4Rs4Bx8NBO")
            .userName("devdojo")
            .authorities("ROLE_USER")
            .build();
    private static DevDojoUser ADMIN = DevDojoUser.builder()
            .name("douglas")
            .password("{bcrypt}$2a$10$DbyN0UNd6kYdi6kWKEENwulquefdNE1SH0lOY1S4A5l4Rs4Bx8NBO")
            .userName("douglas")
            .authorities("ROLE_ADMIN, ROLE_USER")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("devdojo", "academy");
            return new TestRestTemplate(restTemplateBuilder);
        }
        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("douglas", "academy");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }
    @Test
    @DisplayName("ListAll return list of anime inside page object when successful")
    void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(createAnimeToBeSaved());

        devDojoUserRepository.save(USER);
        String expectedName = savedAnime.getName();

        PageableResponse<Anime> exchange = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        assertThat(exchange.toList()).isNotEmpty().hasSize(1);
        assertThat(exchange.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("ListAll return list of anime when successful")
    void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(createAnimeToBeSaved());
        devDojoUserRepository.save(USER);
        String expectedName = savedAnime.getName();
        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        assertThat(animes).isNotNull().isNotEmpty().hasSize(1);
        assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById return anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(createAnimeToBeSaved());
        devDojoUserRepository.save(USER);
        Long expectedId = savedAnime.getId();
        Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

        assertThat(anime).isNotNull();
        assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName return list of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(createAnimeToBeSaved());
        devDojoUserRepository.save(USER);
        String expectedName = savedAnime.getName();
        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        assertThat(animes).isNotNull().isNotEmpty().hasSize(1);
        assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName return an empty list of anime when anime is not found")
    void findByName_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
        String url = String.format("/animes/find?name=%s", "DBZ");
        devDojoUserRepository.save(USER);

        List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        assertThat(animes).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save return anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        AnimePostRequestBody animePostRequestBody = createAnimePostRequestBody();
        devDojoUserRepository.save(USER);
        ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Anime.class);


        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(animeResponseEntity.getBody()).isNotNull();
        assertThat(animeResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("replace return anime when successful")
    void replace_ReturnsAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(createAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        savedAnime.setName("new name");

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes", HttpMethod.PUT, new HttpEntity<>(savedAnime), Void.class);

        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DisplayName("Delete return anime when successful")
    void delete_ReturnsAnime_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(createAnimeToBeSaved());
        devDojoUserRepository.save(ADMIN);

        savedAnime.setName("new name");

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}",
                HttpMethod.DELETE, null, Void.class, savedAnime.getId());

        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Delete return status code 403 anime when user is not Admin")
    void delete_ReturnsStatusCode403_WhenUserIsNotAdmin() {
        Anime savedAnime = animeRepository.save(createAnimeToBeSaved());
        devDojoUserRepository.save(USER);

        savedAnime.setName("new name");

        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin/{id}",
                HttpMethod.DELETE, null, Void.class, savedAnime.getId());

        assertThat(animeResponseEntity).isNotNull();
        assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
