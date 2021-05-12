package ws.furrify.shared.exception;

import lombok.Getter;

/**
 * @author Skyte
 */
public class FileExtensionIsNotMatchingContentException extends RuntimeException implements RestException {

    @Getter
    private final HttpStatus status = HttpStatus.CONFLICT;

    public FileExtensionIsNotMatchingContentException(String message) {
        super(message);
    }

}