package ws.furrify.posts.attachment;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.posts.attachment.dto.command.AttachmentCreateCommandDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.HardLimitForEntityTypeException;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/users/{userId}/posts/{postId}/attachments")
@RequiredArgsConstructor
class CommandUserAttachmentController {

    private final AttachmentFacade attachmentFacade;
    private final SqlAttachmentRepository sqlAttachmentRepository;

    @Value("${furrify.limits.attachments}")
    private long attachmentsLimitPerUser;

    @PostMapping
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('create_post_attachment') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> createAttachment(@PathVariable UUID userId,
                                              @PathVariable UUID postId,
                                              @RequestPart("attachment") @Validated AttachmentCreateCommandDTO attachmentCreateCommandDTO,
                                              @RequestPart("file") MultipartFile mediaFile,
                                              KeycloakAuthenticationToken keycloakAuthenticationToken,
                                              HttpServletResponse response) {
        // Hard limit for attachments
        long userAttachmentsCount = sqlAttachmentRepository.countAttachmentsByUserId(userId);
        if (userAttachmentsCount >= attachmentsLimitPerUser) {
            throw new HardLimitForEntityTypeException(
                    Errors.HARD_LIMIT_FOR_ENTITY_TYPE.getErrorMessage(attachmentsLimitPerUser, "Attachment")
            );
        }

        AttachmentDTO attachmentDTO = attachmentCreateCommandDTO.toDTO();

        response.addHeader("Id",
                attachmentFacade.createAttachment(userId, postId, attachmentDTO, mediaFile).toString()
        );

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{attachmentId}")
    @PreAuthorize(
            "hasRole('admin') ||" +
                    "(hasRole('delete_post_attachment') && #userId == @keycloakAuthorizationUtilsImpl.getCurrentUserId(#keycloakAuthenticationToken))"
    )
    public ResponseEntity<?> deleteAttachment(@PathVariable UUID userId,
                                              @PathVariable UUID postId,
                                              @PathVariable UUID attachmentId,
                                              KeycloakAuthenticationToken keycloakAuthenticationToken) {
        attachmentFacade.deleteAttachment(userId, postId, attachmentId);

        return ResponseEntity.accepted().build();
    }

}
