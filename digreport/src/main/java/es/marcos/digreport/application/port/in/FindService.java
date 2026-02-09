package es.marcos.digreport.application.port.in;

import es.marcos.digreport.application.dto.find.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface FindService {

    FindDto createFind(String reporterEmail, CreateFindRequest request, List<MultipartFile> images);

    List<FindDto> getMyFinds(String reporterEmail);

    Optional<FindDto> getFindById(Long id, String userEmail);

    List<FindDto> getPendingFinds();

    FindDto validateFind(Long findId, String archaeologistEmail, ValidateFindRequest request);

    List<FindDto> getAllFinds();

    List<ReviewNoteDto> getReviewNotes(Long findId);

    ReviewNoteDto addReviewNote(Long findId, String archaeologistEmail, AddReviewNoteRequest request);

    int getPendingCount();

    List<FindDto> getMyValidatedFinds(String archaeologistEmail);

    String analyzeImagesWithAi(List<MultipartFile> multipartFiles);

    List<FindImageDto> getFindImages(Long findId);

}