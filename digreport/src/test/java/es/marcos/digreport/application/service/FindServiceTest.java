package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.find.*;
import es.marcos.digreport.application.port.in.MemberService;
import es.marcos.digreport.application.port.out.*;
import es.marcos.digreport.domain.enums.FindPriority;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.exception.AiAnalysisException;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.domain.model.FindReviewNote;
import es.marcos.digreport.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindService - Tests Unitarios")
class FindServiceImplTest {

    @Mock
    private FindRepositoryPort findRepository;

    @Mock
    private MemberRepositoryPort memberRepository;

    @Mock
    private FindReviewNoteRepositoryPort reviewNoteRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private AiAnalysisPort aiAnalysisPort;

    @Mock
    private FileStoragePort fileStoragePort;

    @Mock
    private FindImageRepositoryPort findImageRepository;

    @InjectMocks
    private FindServiceImpl findService;

    private Member citizenMember;
    private Member archaeologistMember;
    private Member authorityMember;
    private Find testFind;
    private CreateFindRequest createRequest;

    @BeforeEach
    void setUp() {
        citizenMember = new Member(
                1L, "Juan", "García", "López",
                "citizen@test.com", "12345678A", "pass", "600000001",
                UserRole.USER, LocalDateTime.now(), "Galicia", 100
        );

        archaeologistMember = new Member(
                2L, "Ana", "Pérez", "Martínez",
                "archaeologist@test.com", "87654321B", "pass", "600000002",
                UserRole.ARCHAEOLOGIST, LocalDateTime.now(), "Madrid", 200
        );

        authorityMember = new Member(
                3L, "Carlos", "Ruiz", "Sánchez",
                "authority@test.com", "11111111C", "pass", "600000003",
                UserRole.AUTHORITY, LocalDateTime.now(), "Barcelona", 300
        );

        testFind = new Find();
        testFind.setId(1L);
        testFind.setDiscoveredAt(LocalDateTime.of(2024, 1, 15, 10, 0));
        testFind.setLatitude(43.0);
        testFind.setLongitude(-8.0);
        testFind.setDescription("Descripción del hallazgo");
        testFind.setReporterId(1L);
        testFind.setStatus(FindValidationStatus.PENDING);
        testFind.setCcaa("Galicia");

        createRequest = new CreateFindRequest(
                LocalDateTime.of(2024, 1, 15, 10, 0),
                43.0,
                -8.0,
                "Descripción del hallazgo de prueba",
                "Galicia",
                false,
                null
        );
    }

    // ========== TESTS DE createFind ==========

    @Test
    @DisplayName("Debería crear un hallazgo correctamente sin imágenes")
    void shouldCreateFindWithoutImages() throws FileStoragePort.FileStorageException {
        // Given
        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));
        when(findRepository.save(any(Find.class))).thenReturn(testFind);

        // When
        FindDto result = findService.createFind("citizen@test.com", createRequest, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.reporterId()).isEqualTo(1L);
        assertThat(result.status()).isEqualTo(FindValidationStatus.PENDING);

        verify(memberRepository).findByEmail("citizen@test.com");
        verify(findRepository).save(any(Find.class));
        verify(fileStoragePort, never()).storeMultiple(anyList());
    }

    @Test
    @DisplayName("Debería crear un hallazgo con imágenes correctamente")
    void shouldCreateFindWithImages() throws FileStoragePort.FileStorageException {
        // Given
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(false);
        when(mockImage.getSize()).thenReturn(1024L);
        when(mockImage.getContentType()).thenReturn("image/jpeg");
        List<MultipartFile> images = List.of(mockImage);

        FileStoragePort.StoredFileInfo storedFileInfo = new FileStoragePort.StoredFileInfo(
                "uuid-file.jpg",
                "original.jpg",
                "/uploads/uuid-file.jpg",
                1024L,
                "image/jpeg"
        );

        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));
        when(findRepository.save(any(Find.class))).thenReturn(testFind);
        when(fileStoragePort.storeMultiple(images)).thenReturn(List.of(storedFileInfo));

        // When
        FindDto result = findService.createFind("citizen@test.com", createRequest, images);

        // Then
        assertThat(result).isNotNull();
        verify(fileStoragePort).storeMultiple(images);
        verify(findImageRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(memberRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> findService.createFind("nonexistent@test.com", createRequest, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no es ciudadano")
    void shouldThrowExceptionWhenUserIsNotCitizen() {
        // Given
        when(memberRepository.findByEmail("archaeologist@test.com")).thenReturn(Optional.of(archaeologistMember));

        // When & Then
        assertThatThrownBy(() -> findService.createFind("archaeologist@test.com", createRequest, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Solo los ciudadanos pueden registrar hallazgos");
    }

    @Test
    @DisplayName("Debería lanzar excepción si se suben más de 10 imágenes")
    void shouldThrowExceptionWhenMoreThan10Images() {
        // Given
        List<MultipartFile> tooManyImages = Collections.nCopies(11, mock(MultipartFile.class));
        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));

        // When & Then
        assertThatThrownBy(() -> findService.createFind("citizen@test.com", createRequest, tooManyImages))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Máximo 10 imágenes permitidas");
    }

    @Test
    @DisplayName("Debería lanzar excepción si una imagen está vacía")
    void shouldThrowExceptionWhenImageIsEmpty() {
        // Given
        MultipartFile emptyImage = mock(MultipartFile.class);
        when(emptyImage.isEmpty()).thenReturn(true);
        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));

        // When & Then
        assertThatThrownBy(() -> findService.createFind("citizen@test.com", createRequest, List.of(emptyImage)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Una de las imágenes está vacía");
    }

    @Test
    @DisplayName("Debería lanzar excepción si una imagen excede 10MB")
    void shouldThrowExceptionWhenImageExceedsSize() {
        // Given
        MultipartFile largeImage = mock(MultipartFile.class);
        when(largeImage.isEmpty()).thenReturn(false);
        when(largeImage.getSize()).thenReturn(11 * 1024 * 1024L); // 11MB
        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));

        // When & Then
        assertThatThrownBy(() -> findService.createFind("citizen@test.com", createRequest, List.of(largeImage)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Imagen excede el tamaño máximo de 10MB");
    }

    @Test
    @DisplayName("Debería lanzar excepción si el archivo no es una imagen")
    void shouldThrowExceptionWhenFileIsNotImage() {
        // Given
        MultipartFile nonImage = mock(MultipartFile.class);
        when(nonImage.isEmpty()).thenReturn(false);
        when(nonImage.getSize()).thenReturn(1024L);
        when(nonImage.getContentType()).thenReturn("application/pdf");
        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));

        // When & Then
        assertThatThrownBy(() -> findService.createFind("citizen@test.com", createRequest, List.of(nonImage)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Solo se permiten archivos de imagen");
    }

    @Test
    @DisplayName("Debería guardar información de IA si está presente")
    void shouldSaveAiInfoWhenProvided() {
        // Given
        CreateFindRequest requestWithAi = new CreateFindRequest(
                LocalDateTime.now(),
                43.0,
                -8.0,
                "Descripción generada por IA",
                "Galicia",
                true,
                "{\"analysis\": \"data\"}"
        );

        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));

        ArgumentCaptor<Find> findCaptor = ArgumentCaptor.forClass(Find.class);
        when(findRepository.save(findCaptor.capture())).thenReturn(testFind);

        // When
        findService.createFind("citizen@test.com", requestWithAi, null);

        // Then
        Find savedFind = findCaptor.getValue();
        assertThat(savedFind.getDescriptionGeneratedByAi()).isTrue();
        assertThat(savedFind.getAiAnalysisJson()).isEqualTo("{\"analysis\": \"data\"}");
    }

    // ========== TESTS DE getMyFinds ==========

    @Test
    @DisplayName("Debería obtener todos los hallazgos de un usuario")
    void shouldGetMyFinds() {
        // Given
        List<Find> userFinds = List.of(testFind);
        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));
        when(findRepository.findByReporterId(1L)).thenReturn(userFinds);

        // When
        List<FindDto> result = findService.getMyFinds("citizen@test.com");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).reporterId()).isEqualTo(1L);
    }

    // ========== TESTS DE getFindById ==========

    @Test
    @DisplayName("Debería obtener un hallazgo por ID si es el reportero")
    void shouldGetFindByIdWhenUserIsReporter() {
        // Given
        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));
        when(findRepository.findById(1L)).thenReturn(Optional.of(testFind));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(citizenMember));

        // When
        Optional<FindDto> result = findService.getFindById(1L, "citizen@test.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debería obtener un hallazgo si es arqueólogo")
    void shouldGetFindByIdWhenUserIsArchaeologist() {
        // Given
        when(memberRepository.findByEmail("archaeologist@test.com")).thenReturn(Optional.of(archaeologistMember));
        when(findRepository.findById(1L)).thenReturn(Optional.of(testFind));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(citizenMember));

        // When
        Optional<FindDto> result = findService.getFindById(1L, "archaeologist@test.com");

        // Then
        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("No debería obtener un hallazgo si es de otro usuario")
    void shouldNotGetFindByIdWhenNotOwner() {
        // Given
        Member otherCitizen = new Member(
                99L, "Pedro", "Otros", null,
                "other@test.com", "99999999Z", "pass", "699999999",
                UserRole.USER, LocalDateTime.now(), "Galicia", 50
        );

        when(memberRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherCitizen));
        when(findRepository.findById(1L)).thenReturn(Optional.of(testFind));

        // When
        Optional<FindDto> result = findService.getFindById(1L, "other@test.com");

        // Then
        assertThat(result).isEmpty();
    }

    // ========== TESTS DE getPendingFinds ==========

    @Test
    @DisplayName("Debería obtener todos los hallazgos pendientes")
    void shouldGetPendingFinds() {
        // Given
        when(findRepository.findByStatus(FindValidationStatus.PENDING)).thenReturn(List.of(testFind));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(citizenMember));

        // When
        List<FindDto> result = findService.getPendingFinds();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(FindValidationStatus.PENDING);
    }

    // ========== TESTS DE validateFind ==========

    @Test
    @DisplayName("Debería validar un hallazgo correctamente")
    void shouldValidateFind() {
        // Given
        ValidateFindRequest validateRequest = new ValidateFindRequest(
                FindValidationStatus.VALIDATED,
                "Hallazgo auténtico",
                FindPriority.HIGH
        );

        when(memberRepository.findByEmail("archaeologist@test.com")).thenReturn(Optional.of(archaeologistMember));
        when(findRepository.findById(1L)).thenReturn(Optional.of(testFind));
        when(findRepository.save(any(Find.class))).thenReturn(testFind);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(citizenMember));

        // When
        FindDto result = findService.validateFind(1L, "archaeologist@test.com", validateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(memberService).incrementReputation(1L, 1);
        verify(reviewNoteRepository).save(any(FindReviewNote.class));
    }

    @Test
    @DisplayName("Debería rechazar un hallazgo sin incrementar reputación")
    void shouldRejectFindWithoutIncrementingReputation() {
        // Given
        ValidateFindRequest validateRequest = new ValidateFindRequest(
                FindValidationStatus.REJECTED,
                "No es auténtico",
                null
        );

        when(memberRepository.findByEmail("archaeologist@test.com")).thenReturn(Optional.of(archaeologistMember));
        when(findRepository.findById(1L)).thenReturn(Optional.of(testFind));
        when(findRepository.save(any(Find.class))).thenReturn(testFind);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(citizenMember));

        // When
        findService.validateFind(1L, "archaeologist@test.com", validateRequest);

        // Then
        verify(memberService, never()).incrementReputation(anyLong(), anyInt());
        verify(reviewNoteRepository).save(any(FindReviewNote.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción si no es arqueólogo")
    void shouldThrowExceptionWhenNotArchaeologist() {
        // Given
        ValidateFindRequest validateRequest = new ValidateFindRequest(
                FindValidationStatus.VALIDATED,
                null,
                null
        );

        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));

        // When & Then
        assertThatThrownBy(() -> findService.validateFind(1L, "citizen@test.com", validateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Solo los arqueólogos pueden validar hallazgos");
    }

    @Test
    @DisplayName("Debería lanzar excepción si el hallazgo ya fue procesado")
    void shouldThrowExceptionWhenFindAlreadyProcessed() {
        // Given
        testFind.setStatus(FindValidationStatus.VALIDATED);
        ValidateFindRequest validateRequest = new ValidateFindRequest(
                FindValidationStatus.VALIDATED,
                null,
                null
        );

        when(memberRepository.findByEmail("archaeologist@test.com")).thenReturn(Optional.of(archaeologistMember));
        when(findRepository.findById(1L)).thenReturn(Optional.of(testFind));

        // When & Then
        assertThatThrownBy(() -> findService.validateFind(1L, "archaeologist@test.com", validateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El hallazgo ya ha sido procesado");
    }

    // ========== TESTS DE getAllFinds ==========

    @Test
    @DisplayName("Debería obtener todos los hallazgos")
    void shouldGetAllFinds() {
        // Given
        when(findRepository.findAll()).thenReturn(List.of(testFind));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(citizenMember));

        // When
        List<FindDto> result = findService.getAllFinds();

        // Then
        assertThat(result).hasSize(1);
    }

    // ========== TESTS DE getReviewNotes ==========

    @Test
    @DisplayName("Debería obtener todas las notas de revisión de un hallazgo")
    void shouldGetReviewNotes() {
        // Given
        FindReviewNote note = new FindReviewNote(
                1L, 1L, 2L,
                "Comentario de prueba",
                LocalDateTime.now()
        );

        when(reviewNoteRepository.findByFindId(1L)).thenReturn(List.of(note));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(archaeologistMember));

        // When
        List<ReviewNoteDto> result = findService.getReviewNotes(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).comment()).isEqualTo("Comentario de prueba");
        assertThat(result.get(0).reviewerName()).isEqualTo("Ana Pérez");
    }

    // ========== TESTS DE addReviewNote ==========

    @Test
    @DisplayName("Debería añadir una nota de revisión como arqueólogo")
    void shouldAddReviewNoteAsArchaeologist() {
        // Given
        AddReviewNoteRequest request = new AddReviewNoteRequest("Nuevo comentario");
        FindReviewNote savedNote = new FindReviewNote(
                1L, 1L, 2L,
                "Nuevo comentario",
                LocalDateTime.now()
        );

        when(memberRepository.findByEmail("archaeologist@test.com")).thenReturn(Optional.of(archaeologistMember));
        when(findRepository.findById(1L)).thenReturn(Optional.of(testFind));
        when(reviewNoteRepository.save(any(FindReviewNote.class))).thenReturn(savedNote);

        // When
        ReviewNoteDto result = findService.addReviewNote(1L, "archaeologist@test.com", request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.comment()).isEqualTo("Nuevo comentario");
        verify(reviewNoteRepository).save(any(FindReviewNote.class));
    }

    @Test
    @DisplayName("Debería añadir una nota de revisión como autoridad")
    void shouldAddReviewNoteAsAuthority() {
        // Given
        AddReviewNoteRequest request = new AddReviewNoteRequest("Comentario de autoridad");
        FindReviewNote savedNote = new FindReviewNote(
                1L, 1L, 3L,
                "Comentario de autoridad",
                LocalDateTime.now()
        );

        when(memberRepository.findByEmail("authority@test.com")).thenReturn(Optional.of(authorityMember));
        when(findRepository.findById(1L)).thenReturn(Optional.of(testFind));
        when(reviewNoteRepository.save(any(FindReviewNote.class))).thenReturn(savedNote);

        // When
        ReviewNoteDto result = findService.addReviewNote(1L, "authority@test.com", request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.comment()).isEqualTo("Comentario de autoridad");
    }

    @Test
    @DisplayName("Debería lanzar excepción si un ciudadano intenta añadir nota")
    void shouldThrowExceptionWhenCitizenTriesToAddNote() {
        // Given
        AddReviewNoteRequest request = new AddReviewNoteRequest("Comentario");
        when(memberRepository.findByEmail("citizen@test.com")).thenReturn(Optional.of(citizenMember));

        // When & Then
        assertThatThrownBy(() -> findService.addReviewNote(1L, "citizen@test.com", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Solo arqueólogos y autoridades pueden añadir comentarios");
    }

    // ========== TESTS DE getPendingCount ==========

    @Test
    @DisplayName("Debería contar correctamente los hallazgos pendientes")
    void shouldGetPendingCount() {
        // Given
        when(findRepository.findByStatus(FindValidationStatus.PENDING))
                .thenReturn(Arrays.asList(testFind, testFind, testFind));

        // When
        int count = findService.getPendingCount();

        // Then
        assertThat(count).isEqualTo(3);
    }

    // ========== TESTS DE getMyValidatedFinds ==========

    @Test
    @DisplayName("Debería obtener hallazgos validados por un arqueólogo")
    void shouldGetMyValidatedFinds() {
        // Given
        testFind.setValidatedBy(2L);
        when(memberRepository.findByEmail("archaeologist@test.com")).thenReturn(Optional.of(archaeologistMember));
        when(findRepository.findByValidatedBy(2L)).thenReturn(List.of(testFind));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(citizenMember));

        // When
        List<FindDto> result = findService.getMyValidatedFinds("archaeologist@test.com");

        // Then
        assertThat(result).hasSize(1);
    }

    // ========== TESTS DE analyzeImagesWithAi ==========

    @Test
    @DisplayName("Debería analizar imágenes con IA correctamente")
    void shouldAnalyzeImagesWithAi() throws AiAnalysisException {
        // Given
        MultipartFile mockImage = mock(MultipartFile.class);
        List<MultipartFile> images = List.of(mockImage);
        String expectedAnalysis = "{\"result\": \"analysis\"}";

        when(aiAnalysisPort.analyzeImages(images)).thenReturn(expectedAnalysis);

        // When
        String result = findService.analyzeImagesWithAi(images);

        // Then
        assertThat(result).isEqualTo(expectedAnalysis);
        verify(aiAnalysisPort).analyzeImages(images);
    }

    @Test
    @DisplayName("Debería lanzar excepción si falla el análisis de IA")
    void shouldThrowExceptionWhenAiAnalysisFails() throws AiAnalysisException {
        // Given
        MultipartFile mockImage = mock(MultipartFile.class);
        List<MultipartFile> images = List.of(mockImage);

        when(aiAnalysisPort.analyzeImages(images))
                .thenThrow(new AiAnalysisException("Error de IA"));

        // When & Then
        assertThatThrownBy(() -> findService.analyzeImagesWithAi(images))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error al analizar imágenes con IA");
    }
}