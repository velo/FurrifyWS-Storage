package ws.furrify.artists.artist.dto.command;


import lombok.ToString;
import lombok.Value;
import ws.furrify.artists.artist.dto.ArtistDTO;
import ws.furrify.shared.dto.CommandDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author Skyte
 */
@Value
@ToString
public class ArtistCreateCommandDTO implements CommandDTO<ArtistDTO> {

    @Size(min = 1, max = 64)
    Set<@NotBlank @Size(max = 256) String> nicknames;

    @NotBlank
    @Size(min = 1, max = 256)
    @Pattern(regexp = "^[A-Za-z0-9._\\-]+$")
    String preferredNickname;

    @Override
    public ArtistDTO toDTO() {
        return ArtistDTO.builder()
                .nicknames(nicknames)
                .preferredNickname(preferredNickname.strip())
                .build();
    }
}
