package ws.furrify.tags.tag;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.shared.exception.RecordAlreadyExistsException;
import ws.furrify.tags.tag.vo.TagDescription;
import ws.furrify.tags.tag.vo.TagTitle;
import ws.furrify.tags.tag.vo.TagType;
import ws.furrify.tags.tag.vo.TagValue;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TagTest {

    // Mocked in beforeAll()
    private static TagRepository tagRepository;

    private TagSnapshot tagSnapshot;
    private Tag tag;

    @BeforeEach
    void setUp() {
        tagSnapshot = TagSnapshot.builder()
                .id(0L)
                .title("Title")
                .description("desc")
                .value("walking")
                .ownerId(UUID.randomUUID())
                .type(TagType.ACTION)
                .createDate(ZonedDateTime.now())
                .build();

        tag = Tag.restore(tagSnapshot);
    }

    @BeforeAll
    static void beforeAll() {
        tagRepository = mock(TagRepository.class);
    }

    @Test
    @DisplayName("Tag restore from snapshot")
    void restore() {
        // Given tagSnapshot
        // When restore() method called
        Tag tag = Tag.restore(tagSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(tagSnapshot, tag.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from tag")
    void getSnapshot() {
        // Given post
        // When getSnapshot() method called
        TagSnapshot tagSnapshot = tag.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.tagSnapshot, tagSnapshot, "Data was lost in snapshot.");
    }

    @Test
    @DisplayName("Update title")
    void updateTitle() {
        // Given title
        String newTitle = "Title2";
        // When updateTitle() method called
        // Then update title
        tag.updateTitle(
                TagTitle.of(newTitle)
        );

        assertEquals(newTitle, tag.getSnapshot().getTitle(), "Title was not updated.");
    }

    @Test
    @DisplayName("Update description")
    void updateDetails() {
        // Given description
        String newDesc = "dsadasdsa";
        // When updateDescription() method called
        // Then update description
        tag.updateDescription(
                TagDescription.of(newDesc)
        );

        assertEquals(newDesc, tag.getSnapshot().getDescription(), "Description was not updated.");
    }

    @Test
    @DisplayName("Update type")
    void updateType() {
        // Given title and description
        TagType newType = TagType.BACKGROUND;
        // When updateType() method called
        // Then update type
        tag.updateType(newType);

        assertEquals(newType, tag.getSnapshot().getType(), "Type was not updated.");
    }

    @Test
    @DisplayName("Update value")
    void updateValue() {
        // Given ownerId and value
        UUID ownerId = UUID.randomUUID();
        String newValue = "walking2";
        // When updateValue() method called
        when(tagRepository.existsByOwnerIdAndValue(ownerId, newValue)).thenReturn(false);
        // Then update value
        tag.updateValue(
                TagValue.of(newValue),
                tagRepository
        );

        assertEquals(newValue, tag.getSnapshot().getValue(), "Value was not updated.");
    }

    @Test
    @DisplayName("Update value with existing value")
    void updateValue2() {
        // Given ownerId and existing value
        String newValue = "walking2";
        // When updateValue() method called
        when(tagRepository.existsByOwnerIdAndValue(tagSnapshot.getOwnerId(), newValue)).thenReturn(true);
        // Then throw RecordAlreadyExistsException
        assertThrows(
                RecordAlreadyExistsException.class,
                () -> tag.updateValue(
                        TagValue.of(newValue),
                        tagRepository
                ),
                "Exception was not thrown."
        );
    }
}