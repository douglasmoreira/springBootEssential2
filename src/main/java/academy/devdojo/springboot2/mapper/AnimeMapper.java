package academy.devdojo.springboot2.mapper;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import org.springframework.stereotype.Component;

@Component
public class AnimeMapper {

    public Anime toAnime(AnimePostRequestBody animePostRequestBody) {
        return Anime.builder().name(animePostRequestBody.getName()).build();
    }

    public Anime toAnime(AnimePutRequestBody animePutRequestBody) {
        return Anime.builder().name(animePutRequestBody.getName()).id(animePutRequestBody.getId()).build();
    }
}
