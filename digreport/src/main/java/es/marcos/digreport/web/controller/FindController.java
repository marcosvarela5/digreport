package es.marcos.digreport.web.controller;

import es.marcos.digreport.application.dto.find.*;
import es.marcos.digreport.application.port.in.FindService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finds")
public class FindController {

    private final FindService findService;

    public FindController(FindService findService) {
        this.findService = findService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FindDto> createFind(
            Authentication authentication,
            @Valid @RequestBody CreateFindRequest request
    ) {
        String email = authentication.getName();
        FindDto created = findService.createFind(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
}