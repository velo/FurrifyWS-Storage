package ws.furrify.tags.tag;

import ws.furrify.tags.tag.dto.TagDTO;

import java.util.UUID;

interface UpdateTag {

    void updateTag(final UUID userId, final String value, final TagDTO tagDTO);

}
