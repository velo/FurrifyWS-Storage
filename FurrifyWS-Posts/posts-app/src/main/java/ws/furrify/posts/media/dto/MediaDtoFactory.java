package ws.furrify.posts.media.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ws.furrify.posts.media.MediaEvent;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.media.MediaQueryRepository;
import ws.furrify.posts.media.MediaStatus;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Creates PostDTO from PostEvent.
 * `
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class MediaDtoFactory {

    private final MediaQueryRepository mediaQueryRepository;

    @SneakyThrows
    public MediaDTO from(UUID key, MediaEvent mediaEvent) {
        Instant createDateInstant = mediaEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var mediaId = UUID.fromString(mediaEvent.getMediaId());

        return MediaDTO.builder()
                .id(
                        mediaQueryRepository.getIdByMediaId(mediaId)
                )
                .mediaId(mediaId)
                .postId(UUID.fromString(mediaEvent.getData().getPostId()))
                .ownerId(key)
                .priority(mediaEvent.getData().getPriority())
                .extension(
                        (mediaEvent.getData().getExtension() != null) ?
                                MediaExtension.valueOf(mediaEvent.getData().getExtension()) :
                                null
                )
                .filename(mediaEvent.getData().getFilename())
                .fileUrl(
                        (mediaEvent.getData().getFileUrl() != null) ?
                                new URL(mediaEvent.getData().getFileUrl()) :
                                null
                )
                .md5(mediaEvent.getData().getMd5())
                .thumbnailUrl(
                        (mediaEvent.getData().getThumbnailUrl() != null) ?
                                new URL(mediaEvent.getData().getThumbnailUrl()) :
                                null

                )
                .status(
                        (mediaEvent.getData().getStatus() != null) ?
                                MediaStatus.valueOf(mediaEvent.getData().getStatus()) :
                                null
                )
                .createDate(createDate)
                .build();
    }

}