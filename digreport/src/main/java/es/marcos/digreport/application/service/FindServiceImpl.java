package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.find.*;
import es.marcos.digreport.application.port.in.FindService;
import es.marcos.digreport.application.port.in.MemberService;
import es.marcos.digreport.application.port.out.AiAnalysisPort;
import es.marcos.digreport.application.port.out.FindRepositoryPort;
import es.marcos.digreport.application.port.out.FindReviewNoteRepositoryPort;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.exception.AiAnalysisException;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.domain.model.FindReviewNote;
import es.marcos.digreport.domain.model.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import es.marcos.digreport.application.port.out.FileStoragePort;
import es.marcos.digreport.application.port.out.FindImageRepositoryPort;
import es.marcos.digreport.domain.model.FindImage;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class FindServiceImpl implements FindService {

    private final FindRepositoryPort findRepository;
    private final MemberRepositoryPort memberRepository;
    private final FindReviewNoteRepositoryPort reviewNoteRepository;
    private final MemberService memberService;
    private final AiAnalysisPort aiAnalysisPort;
    private final FileStoragePort fileStoragePort;
    private final FindImageRepositoryPort findImageRepository;

    public FindServiceImpl(FindRepositoryPort findRepository, MemberRepositoryPort memberRepository,
                           FindReviewNoteRepositoryPort reviewNoteRepository, MemberService memberService,
                           AiAnalysisPort aiAnalysisPort,
                           FileStoragePort fileStoragePort, FindImageRepositoryPort findImageRepository) {
        this.findRepository = findRepository;
        this.memberRepository = memberRepository;
        this.reviewNoteRepository = reviewNoteRepository;
        this.memberService = memberService;
        this.aiAnalysisPort = aiAnalysisPort;
        this.findImageRepository = findImageRepository;
        this.fileStoragePort = fileStoragePort;
    }

    @Override
    @Transactional
    public FindDto createFind(String reporterEmail, CreateFindRequest request, List<MultipartFile> images) {
        Member reporter = memberRepository.findByEmail(reporterEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (reporter.getRole() != UserRole.USER) {
            throw new RuntimeException("Solo los ciudadanos pueden registrar hallazgos");
        }

        if (images != null && !images.isEmpty()) {
            validateImages(images);
        }

        // 1. Crear y guardar el hallazgo
        Find find = new Find();
        find.setDiscoveredAt(request.discoveredAt());
        find.setLatitude(request.latitude());
        find.setLongitude(request.longitude());
        find.setDescription(request.description());
        find.setReporterId(reporter.getId());
        find.setStatus(FindValidationStatus.PENDING);
        find.setCcaa(request.ccaa());

        // Información de IA si existe
        if (Boolean.TRUE.equals(request.descriptionGeneratedByAi())) {
            find.setDescriptionGeneratedByAi(true);
            find.setAiAnalysisJson(request.aiAnalysisJson());
        }

        Find saved = findRepository.save(find);

        // 2. Guardar imágenes si existen
        if (images != null && !images.isEmpty()) {
            try {
                List<FileStoragePort.StoredFileInfo> storedFiles = fileStoragePort.storeMultiple(images);

                List<FindImage> findImages = new ArrayList<>();
                for (int i = 0; i < storedFiles.size(); i++) {
                    FileStoragePort.StoredFileInfo fileInfo = storedFiles.get(i);

                    FindImage image = new FindImage();
                    image.setFindId(saved.getId());
                    image.setFilename(fileInfo.filename());
                    image.setOriginalFilename(fileInfo.originalFilename());
                    image.setFilePath(fileInfo.filePath());
                    image.setFileSize(fileInfo.fileSize());
                    image.setMimeType(fileInfo.mimeType());
                    image.setDisplayOrder(i);
                    image.setIsPrimary(i == 0);

                    findImages.add(image);
                }

                findImageRepository.saveAll(findImages);

            } catch (FileStoragePort.FileStorageException e) {
                throw new RuntimeException("Error al guardar las imágenes: " + e.getMessage(), e);
            }
        }

        return toDto(saved, reporter);
    }

    private void validateImages(List<MultipartFile> images) {
        if (images.size() > 10) {
            throw new IllegalArgumentException("Máximo 10 imágenes permitidas");
        }

        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                throw new IllegalArgumentException("Una de las imágenes está vacía");
            }

            if (image.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("Imagen excede el tamaño máximo de 10MB");
            }

            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Solo se permiten archivos de imagen");
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindDto> getMyFinds(String reporterEmail) {
        Member reporter = memberRepository.findByEmail(reporterEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return findRepository.findByReporterId(reporter.getId())
                .stream()
                .map(find -> toDto(find, reporter))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FindDto> getFindById(Long id, String userEmail) {
        Member user = memberRepository.findByEmail(userEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return findRepository.findById(id)
                .filter(find -> canAccessFind(find, user))
                .map(find -> {
                    Member reporter = memberRepository.findById(find.getReporterId())
                            .orElseThrow(() -> new RuntimeException("Reportero no encontrado"));
                    return toDto(find, reporter);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindDto> getPendingFinds() {
        return findRepository.findByStatus(FindValidationStatus.PENDING)
                .stream()
                .map(this::toDtoWithReporter)
                .toList();
    }

    @Override
    @Transactional
    public FindDto validateFind(Long findId, String archaeologistEmail, ValidateFindRequest request) {
        Member archaeologist = memberRepository.findByEmail(archaeologistEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (archaeologist.getRole() != UserRole.ARCHAEOLOGIST) {
            throw new RuntimeException("Solo los arqueólogos pueden validar hallazgos");
        }

        Find find = findRepository.findById(findId)
                .orElseThrow(() -> new RuntimeException("Hallazgo no encontrado"));

        if (find.getStatus() != FindValidationStatus.PENDING) {
            throw new RuntimeException("El hallazgo ya ha sido procesado");
        }

        find.setStatus(request.status());
        find.setValidatedBy(archaeologist.getId());
        if (request.priority() != null) {
            find.setFindPriority(request.priority());
        }

        Find saved = findRepository.save(find);


        if (request.status() == FindValidationStatus.VALIDATED) {
            memberService.incrementReputation(saved.getReporterId(), 1);
        }

        // Guardar comentario si existe
        if (request.reviewComment() != null && !request.reviewComment().isBlank()) {
            FindReviewNote note = new FindReviewNote();
            note.setFindId(findId);
            note.setReviewerId(archaeologist.getId());
            note.setComment(request.reviewComment());
            note.setCreatedAt(LocalDateTime.now());
            reviewNoteRepository.save(note);
        }

        Member reporter = memberRepository.findById(saved.getReporterId())
                .orElseThrow(() -> new RuntimeException("Reportero no encontrado"));

        return toDto(saved, reporter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindDto> getAllFinds() {
        return findRepository.findAll()
                .stream()
                .map(this::toDtoWithReporter)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewNoteDto> getReviewNotes(Long findId) {
        return reviewNoteRepository.findByFindId(findId)
                .stream()
                .map(note -> {
                    Member reviewer = memberRepository.findById(note.getReviewerId())
                            .orElseThrow(() -> new RuntimeException("Revisor no encontrado"));
                    return new ReviewNoteDto(
                            note.getId(),
                            note.getFindId(),
                            reviewer.getId(),
                            reviewer.getName() + " " + reviewer.getSurname1(),
                            note.getComment(),
                            note.getCreatedAt()
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public ReviewNoteDto addReviewNote(Long findId, String archaeologistEmail, AddReviewNoteRequest request) {
        Member archaeologist = memberRepository.findByEmail(archaeologistEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (archaeologist.getRole() != UserRole.ARCHAEOLOGIST && archaeologist.getRole() != UserRole.AUTHORITY) {
            throw new RuntimeException("Solo arqueólogos y autoridades pueden añadir comentarios");
        }

        findRepository.findById(findId)
                .orElseThrow(() -> new RuntimeException("Hallazgo no encontrado"));

        FindReviewNote note = new FindReviewNote();
        note.setFindId(findId);
        note.setReviewerId(archaeologist.getId());
        note.setComment(request.comment());
        note.setCreatedAt(LocalDateTime.now());

        FindReviewNote saved = reviewNoteRepository.save(note);

        return new ReviewNoteDto(
                saved.getId(),
                saved.getFindId(),
                archaeologist.getId(),
                archaeologist.getName() + " " + archaeologist.getSurname1(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public int getPendingCount() {
        return findRepository.findByStatus(FindValidationStatus.PENDING).size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FindDto> getMyValidatedFinds(String archaeologistEmail) {
        Member archaeologist = memberRepository.findByEmail(archaeologistEmail.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return findRepository.findByValidatedBy(archaeologist.getId())
                .stream()
                .map(this::toDtoWithReporter)
                .toList();
    }

    @Transactional(readOnly = true)
    public String analyzeImagesWithAi(List<MultipartFile> images) {
        try {
            return aiAnalysisPort.analyzeImages(images);
        } catch (AiAnalysisException e) {
            throw new RuntimeException("Error al analizar imágenes con IA: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FindImageDto> getFindImages(Long findId) {
        return findImageRepository.findByFindId(findId)
                .stream()
                .map(this::toImageDto)
                .toList();
    }

    private FindImageDto toImageDto(FindImage image) {
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



    private boolean canAccessFind(Find find, Member user) {
        return switch (user.getRole()) {
            case USER -> find.getReporterId().equals(user.getId());
            case ARCHAEOLOGIST, AUTHORITY -> true;  // ← Pueden ver TODOS
        };
    }

    private FindDto toDto(Find find, Member reporter) {
        return new FindDto(
                find.getId(),
                find.getDiscoveredAt(),
                find.getLatitude(),
                find.getLongitude(),
                find.getDescription(),
                find.getStatus(),
                find.getFindPriority(),
                reporter.getId(),
                reporter.getName() + " " + reporter.getSurname1(),
                LocalDateTime.now()
        );
    }

    private FindDto toDtoWithReporter(Find find) {
        Member reporter = memberRepository.findById(find.getReporterId())
                .orElseThrow(() -> new RuntimeException("Reportero no encontrado"));
        return toDto(find, reporter);
    }


}