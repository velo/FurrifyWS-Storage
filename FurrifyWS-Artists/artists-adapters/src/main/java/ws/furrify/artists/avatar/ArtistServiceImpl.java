package ws.furrify.artists.avatar;

import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import ws.furrify.artists.artist.ArtistServiceClient;
import ws.furrify.artists.artist.query.ArtistDetailsQueryDTO;
import ws.furrify.shared.exception.ChainOfRequestsBrokenException;
import ws.furrify.shared.exception.ChainOfRequestsUnauthorizedException;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.HttpStatus;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ArtistServiceImpl implements ArtistServiceClient {

    private final ArtistServiceClientImpl artistServiceClient;

    private final static String NAME = "artists";

    @Bulkhead(name = "getUserArtist", fallbackMethod = "getUserArtistFallback")
    @Override
    public ArtistDetailsQueryDTO getUserArtist(final UUID userId, final UUID artistId) {
        return artistServiceClient.getUserArtist(userId, artistId);
    }

    private ArtistDetailsQueryDTO getUserArtistFallback(Throwable throwable) {
        var exception = (FeignException) throwable;

        HttpStatus status = HttpStatus.of(exception.status());

        switch (status) {
            case NOT_FOUND -> {
                return null;
            }

            case FORBIDDEN -> throw new ChainOfRequestsUnauthorizedException(Errors.CHAIN_OF_REQUESTS_UNAUTHORIZED.getErrorMessage(NAME));

            default -> throw new ChainOfRequestsBrokenException(Errors.CHAIN_OF_REQUESTS_BROKEN.getErrorMessage(NAME));
        }
    }

    /**
     * Implements Artist Service Client as a Feign Client.
     */
    @FeignClient(name = "ARTISTS-SERVICE")
    private interface ArtistServiceClientImpl extends ArtistServiceClient {
    }
}
