package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.mapper.AnimeMapper;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static academy.devdojo.springboot2.util.AnimeCreator.createValidAnime;
import static academy.devdojo.springboot2.util.AnimePostRequestBodyCreator.createAnimePostRequestBody;
import static academy.devdojo.springboot2.util.AnimePutRequestBodyCreator.createAnimePutRequestBody;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;
    @Mock
    private AnimeRepository animeRepository;
    @Mock
    private AnimeMapper animeMapper;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(asList(createValidAnime()));
        BDDMockito.when(animeRepository.findAll(any(PageRequest.class)))
                .thenReturn(animePage);

        BDDMockito.when(animeRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(createValidAnime()));

        BDDMockito.when(animeRepository.findAll())
                .thenReturn(asList(createValidAnime()));

        BDDMockito.when(animeRepository.findByName(anyString()))
                .thenReturn(asList(createValidAnime()));

        BDDMockito.when(animeRepository.save(any(Anime.class)))
                .thenReturn(createValidAnime());

        BDDMockito.when(animeMapper.toAnime(any(AnimePostRequestBody.class)))
                .thenReturn(createValidAnime());

        BDDMockito.when(animeMapper.toAnime(any(AnimePutRequestBody.class)))
                .thenReturn(createValidAnime());

        BDDMockito.doNothing().when(animeRepository).delete(any(Anime.class));
    }

    @Test
    @DisplayName("ListAll return list of anime inside page object when successful")
    void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = createValidAnime().getName();
        Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));

        assertThat(animePage.toList()).isNotEmpty().hasSize(1);
        assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("ListAllNonPageable return list of anime when successful")
    void listAllNonPageable_ReturnsListOfAnimes_WhenSuccessful() {
        String expectedName = createValidAnime().getName();
        List<Anime> animeList = animeService.listAllNonPageable();

        assertThat(animeList).isNotNull().isNotEmpty().hasSize(1);
        assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException return anime when successful")
    void findByIdOrThrowBadRequestException_ReturnsAnime_WhenSuccessful() {
        Long expectedId = createValidAnime().getId();
        Anime anime = animeService.findByIdOrThrowBadRequestException(1L);

        assertThat(anime).isNotNull();
        assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException throws ThrowBadRequestException when anime is not found")
    void findByIdOrThrowBadRequestException_ThrowBadRequestException_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> animeService.findByIdOrThrowBadRequestException(1L));
    }

    @Test
    @DisplayName("findByName return list of anime when successful")
    void findByName_ReturnsListOfAnime_WhenSuccessful() {
        String expectedName = createValidAnime().getName();
        List<Anime> animeList = animeService.findByName("anime");

        assertThat(animeList).isNotNull().isNotEmpty().hasSize(1);
        assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName return an empty list of anime when anime is not found")
    void findByName_ReturnsAnEmptyListOfAnime_WhenAnimeIsNotFound() {
        BDDMockito.when(animeRepository.findByName(anyString()))
                .thenReturn(emptyList());

        List<Anime> animeList = animeService.findByName("anime");

        assertThat(animeList).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("save return anime when successful")
    void save_ReturnsAnime_WhenSuccessful() {
        Anime anime = animeService.save(createAnimePostRequestBody());

        assertThat(anime).isEqualTo(createValidAnime());
    }

    @Test
    @DisplayName("replace return anime when successful")
    void replace_ReturnsAnime_WhenSuccessful() {

        Anime anime = animeService.replace(createAnimePutRequestBody());

        assertThat(anime).isEqualTo(createValidAnime());
    }

    @Test
    @DisplayName("Delete return anime when successful")
    void delete_ReturnsAnime_WhenSuccessful() {
        assertThatCode(() -> animeService.delete(1L)).doesNotThrowAnyException();
    }
}