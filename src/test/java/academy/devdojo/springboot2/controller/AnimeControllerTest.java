package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static academy.devdojo.springboot2.util.AnimeCreator.createValidAnime;
import static academy.devdojo.springboot2.util.AnimePostRequestBodyCreator.createAnimePostRequestBody;
import static academy.devdojo.springboot2.util.AnimePutRequestBodyCreator.createAnimePutRequestBody;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {
    @InjectMocks
    private AnimeController animeController;
    @Mock
    private AnimeService animeService;
    @Mock
    private DateUtil dateUtil;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(asList(createValidAnime()));
        BDDMockito.when(animeService.listAll(any()))
                .thenReturn(animePage);


        BDDMockito.when(animeService.findByIdOrThrowBadRequestException(anyLong()))
                .thenReturn(createValidAnime());

        BDDMockito.when(animeService.listAllNonPageable())
                .thenReturn(asList(createValidAnime()));

        BDDMockito.when(animeService.findByName(anyString()))
                .thenReturn(asList(createValidAnime()));

        BDDMockito.when(animeService.save(any(AnimePostRequestBody.class)))
                .thenReturn(createValidAnime());

        BDDMockito.when(animeService.replace(any(AnimePutRequestBody.class)))
                .thenReturn(createValidAnime());

        BDDMockito.doNothing().when(animeService).delete(anyLong());
    }

    @Test
    @DisplayName("List return list of anime inside page object when successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = createValidAnime().getName();
        Page<Anime> animePage = animeController.list(null).getBody();

        assertThat(animePage).isNotNull();
        assertThat(animePage.toList()).isNotEmpty().hasSize(1);
        assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("ListAll return list of anime when successful")
    void listAll_ReturnsListOfAnimes_WhenSuccessful() {
        String expectedName = createValidAnime().getName();
        List<Anime> animeList = animeController.listAll().getBody();

        assertThat(animeList).isNotNull().isNotEmpty().hasSize(1);
        assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById return anime when successful")
    void findById_ReturnsAnime_WhenSuccessful() {
        Long expectedId = createValidAnime().getId();
        Anime anime = animeController.findById(1L).getBody();

        assertThat(anime).isNotNull();
        assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName return list of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        String expectedName = createValidAnime().getName();
        List<Anime> animeList = animeController.findByName("anime").getBody();

        assertThat(animeList).isNotNull().isNotEmpty().hasSize(1);
        assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName return an empty list of anime when anime is not found")
    void findByName_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
        BDDMockito.when(animeService.findByName(anyString()))
                .thenReturn(emptyList());

        List<Anime> animeList = animeController.findByName("anime").getBody();

        assertThat(animeList).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save return anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        Anime anime = animeController.save(createAnimePostRequestBody()).getBody();

        assertThat(anime).isEqualTo(createValidAnime());
    }

    @Test
    @DisplayName("replace return anime when successful")
    void replace_ReturnsAnime_WhenSuccessful() {
        Anime anime = animeController.replace(createAnimePutRequestBody()).getBody();

        assertThat(anime).isEqualTo(createValidAnime());
    }

    @Test
    @DisplayName("Delete return anime when successful")
    void delete_ReturnsAnime_WhenSuccessful() {
        assertThatCode(() -> animeController.delete(1L)).doesNotThrowAnyException();

        ResponseEntity<Anime> entity = animeController.delete(1L);

        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}