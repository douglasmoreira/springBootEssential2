package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.requests.AnimePostRequestBody;

import static academy.devdojo.springboot2.util.AnimeCreator.createAnimeToBeSaved;

public class AnimePostRequestBodyCreator {

    public static AnimePostRequestBody createAnimePostRequestBody() {
        return AnimePostRequestBody.builder()
                .name(createAnimeToBeSaved().getName())
                .build();
    }

}
