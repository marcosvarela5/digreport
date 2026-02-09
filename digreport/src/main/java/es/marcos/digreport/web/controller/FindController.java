package es.marcos.digreport.web.controller;

import es.marcos.digreport.application.dto.find.*;
import es.marcos.digreport.application.port.in.FindService;
import es.marcos.digreport.domain.model.FindImage;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finds")
public class FindController {

    private final FindService findService;

    public FindController(FindService findService) {
        this.findService = findService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FindDto> createFind(
            Authentication authentication,
            @RequestPart("data") @Valid CreateFindRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        String email = authentication.getName();

        try {
            FindDto created = findService.createFind(email, request, images);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<FindDto>> getMyFinds(Authentication authentication) {
        String email = authentication.getName();
        List<FindDto> finds = findService.getMyFinds(email);
        return ResponseEntity.ok(finds);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindDto> getFindById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return findService.getFindById(id, email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ARCHAEOLOGIST') or hasRole('AUTHORITY')")
    public ResponseEntity<List<FindDto>> getPendingFinds() {
        List<FindDto> finds = findService.getPendingFinds();
        return ResponseEntity.ok(finds);
    }

    @PutMapping("/{id}/validate")
    @PreAuthorize("hasRole('ARCHAEOLOGIST')")
    public ResponseEntity<FindDto> validateFind(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody ValidateFindRequest request
    ) {
        String email = authentication.getName();
        FindDto validated = findService.validateFind(id, email, request);
        return ResponseEntity.ok(validated);
    }

    @GetMapping
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<List<FindDto>> getAllFinds() {
        List<FindDto> finds = findService.getAllFinds();
        return ResponseEntity.ok(finds);
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<List<ReviewNoteDto>> getReviewNotes(@PathVariable Long id) {
        List<ReviewNoteDto> notes = findService.getReviewNotes(id);
        return ResponseEntity.ok(notes);
    }

    @PostMapping("/{id}/notes")
    @PreAuthorize("hasRole('ARCHAEOLOGIST') or hasRole('AUTHORITY')")
    public ResponseEntity<ReviewNoteDto> addReviewNote(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody AddReviewNoteRequest request
    ) {
        String email = authentication.getName();
        ReviewNoteDto note = findService.addReviewNote(id, email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @GetMapping("/pending/count")
    @PreAuthorize("hasRole('ARCHAEOLOGIST') or hasRole('AUTHORITY')")
    public ResponseEntity<Integer> getPendingCount() {
        int count = findService.getPendingCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/my-validated")
    @PreAuthorize("hasRole('ARCHAEOLOGIST')")
    public ResponseEntity<List<FindDto>> getMyValidatedFinds(Authentication authentication) {
        String email = authentication.getName();
        List<FindDto> finds = findService.getMyValidatedFinds(email);
        return ResponseEntity.ok(finds);
    }

    @PostMapping(value = "/analyze-with-ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> analyzeImagesWithAi(
            @RequestParam("imagenes") List<MultipartFile> imagenes
    ) {
        try {
            // Validación básica
            if (imagenes == null || imagenes.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "No se proporcionaron imágenes"));
            }

            if (imagenes.size() > 5) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Máximo 5 imágenes permitidas"));
            }

            // Llamar al servicio
            String analysisJson = findService.analyzeImagesWithAi(imagenes);

            // Retornar JSON directamente
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(analysisJson);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<FindImageDto>> getFindImages(@PathVariable Long id) {
        List<FindImageDto> images = findService.getFindImages(id);
        return ResponseEntity.ok(images);
    }

    private FindImageDto toDto(FindImage image) {
        return new FindImageDto(
                image.getId(),
                image.getFindId(),
                image.getFilename(),
                image.getOriginalFilename(),
                image.getFilePath(),
                image.getFileSize(),
                image.getMimeType(),
                image.getDisplayOrder(),
                image.getIsPrimary()
        );
    }

}