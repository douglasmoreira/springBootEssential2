package academy.devdojo.springboot2.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePostRequestBody {
    @NotEmpty(message = "The anime name can't be empty")
    @NotNull(message = "The anime name can't be null")
    @Schema(description = "This is the anime's name", example = "Naruto", required = true)
    private String name;
}
