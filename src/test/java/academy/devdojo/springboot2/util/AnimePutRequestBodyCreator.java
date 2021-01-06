package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.requests.AnimePutRequestBody;

import static academy.devdojo.springboot2.util.AnimeCreator.createAnimeToBeSaved;
import static academy.devdojo.springboot2.util.AnimeCreator.createValidAnime;

public class AnimePutRequestBodyCreator {

    public static AnimePutRequestBody createAnimePutRequestBody() {
        return AnimePutRequestBody.builder()
                .id(createValidAnime().getId())
                .name(createValidAnime().getName())
                .build();
    }

}
