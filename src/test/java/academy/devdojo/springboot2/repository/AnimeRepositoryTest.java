package academy.devdojo.springboot2.repository;

import academy.devdojo.springboot2.domain.Anime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static academy.devdojo.springboot2.util.AnimeCreator.createAnimeToBeSaved;
import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@DisplayName("Test for Anime Repository")
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("save create anime when successful")
    void save_PersistAnime_WhenSuccessful() {
        Anime anime = createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(anime);
        assertThat(animeSaved).isNotNull();
        assertThat(animeSaved.getId()).isNotNull();
        assertThat(animeSaved.getName()).isEqualTo(anime.getName());
    }

    @Test
    @DisplayName("save create anime when successful")
    void save_UpdateAnime_WhenSuccessful() {
        Anime anime = createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(anime);

        animeSaved.setName("Overlord");

        Anime animeUpdate = this.animeRepository.save(animeSaved);

        assertThat(animeUpdate).isNotNull();
        assertThat(animeUpdate.getId()).isNotNull();
        assertThat(animeUpdate.getName()).isEqualTo(animeSaved.getName());
    }

    @Test
    @DisplayName("Delete remove anime when successful")
    void delete_RemoveAnime_WhenSuccessful() {
        Anime anime = createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(anime);


        this.animeRepository.delete(animeSaved);

        Optional<Anime> animeOptional = this.animeRepository.findById(animeSaved.getId());

        assertThat(animeOptional).isEmpty();
    }

    @Test
    @DisplayName("Find By Name return list of anime when successful")
    void findByName_ReturnListOfAnime_WhenSuccessful() {
        Anime anime = createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(anime);


        String name = animeSaved.getName();

        List<Anime> animes = this.animeRepository.findByName(name);

        assertThat(animes).isNotEmpty().contains(animeSaved);
    }

    @Test
    @DisplayName("Find By Name return empty list when no anime is not found")
    void findByName_ReturnEmptyList_WhenNoAnimeIsNotFound() {

        List<Anime> animes = this.animeRepository.findByName("name");

        assertThat(animes).isEmpty();
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    void save_ThrowConstraintViolationException_WhenNoAnimeIsNotFound() {

        Anime anime = new Anime();
//        assertThatThrownBy(() -> this.animeRepository.save(anime))
//                .isInstanceOf(ConstraintViolationException.class);

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> this.animeRepository.save(anime))
                .withMessageContaining("The anime name can't be empty");
    }



}