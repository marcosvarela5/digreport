package es.marcos.digreport.application.service;

import es.marcos.digreport.application.dto.find.CreateFindRequest;
import es.marcos.digreport.application.dto.find.FindDto;
import es.marcos.digreport.application.port.in.FindService;
import es.marcos.digreport.application.port.out.FindRepositoryPort;
import es.marcos.digreport.application.port.out.MemberRepositoryPort;
import es.marcos.digreport.domain.enums.FindValidationStatus;
import es.marcos.digreport.domain.enums.UserRole;
import es.marcos.digreport.domain.model.Find;
import es.marcos.digreport.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("FindService - Test de Integración: Crear Hallazgo")
class FindServiceIntegrationTest {

    @Autowired
    private FindService findService;

    @Autowired
    private FindRepositoryPort findRepository;

    @Autowired
    private MemberRepositoryPort memberRepository;

    private Member testCitizen;
    private CreateFindRequest validRequest;

    @BeforeEach
    void setUp() {
        findRepository.findAll().forEach(find -> findRepository.deleteById(find.getId()));
        memberRepository.findAll().forEach(member -> memberRepository.deleteById(member.getId()));

        testCitizen = new Member(
                null,
                "Juan",
                "García",
                "López",
                "juan@test.com",
                "12345678A",
                "$2a$10$hashedPassword",
                "600000001",
                UserRole.USER,
                LocalDateTime.now(),
                "Galicia",
                0
        );
        testCitizen = memberRepository.save(testCitizen);

        validRequest = new CreateFindRequest(
                LocalDateTime.of(2024, 1, 15, 10, 0),
                43.3713618,
                -8.3959426,
                "Hallazgo de cerámica romana encontrada en excavación arqueológica",
                "Galicia",
                false,
                null
        );
    }

    @Test
    @DisplayName("Debería crear un hallazgo y persistirlo correctamente en la base de datos")
    void shouldCreateFindAndPersistInDatabase() {
        Long initialCount = findRepository.count();

        FindDto result = findService.createFind("juan@test.com", validRequest, null);

        assertThat(result).isNotNull();
        assertThat(result.id()).isNotNull();
        assertThat(result.reporterId()).isEqualTo(testCitizen.getId());
        assertThat(result.reporterName()).isEqualTo("Juan García");
        assertThat(result.status()).isEqualTo(FindValidationStatus.PENDING);
        assertThat(result.description()).isEqualTo("Hallazgo de cerámica romana encontrada en excavación arqueológica");
        assertThat(result.latitude()).isEqualTo(43.3713618);
        assertThat(result.longitude()).isEqualTo(-8.3959426);

        assertThat(findRepository.count()).isEqualTo(initialCount + 1);

        Optional<Find> savedFind = findRepository.findById(result.id());
        assertThat(savedFind).isPresent();

        Find fromDb = savedFind.get();
        assertThat(fromDb.getId()).isEqualTo(result.id());
        assertThat(fromDb.getReporterId()).isEqualTo(testCitizen.getId());
        assertThat(fromDb.getStatus()).isEqualTo(FindValidationStatus.PENDING);
        assertThat(fromDb.getDescription()).isEqualTo("Hallazgo de cerámica romana encontrada en excavación arqueológica");
        assertThat(fromDb.getLatitude()).isEqualTo(43.3713618);
        assertThat(fromDb.getLongitude()).isEqualTo(-8.3959426);
        assertThat(fromDb.getCcaa()).isEqualTo("Galicia");
        assertThat(fromDb.getDiscoveredAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 0));
        assertThat(fromDb.getDescriptionGeneratedByAi()).isFalse();
        assertThat(fromDb.getAiAnalysisJson()).isNull();
    }

    @Test
    @DisplayName("Debería guardar información de IA cuando está presente")
    void shouldPersistAiInformationWhenProvided() {
        CreateFindRequest requestWithAi = new CreateFindRequest(
                LocalDateTime.now(),
                43.0,
                -8.0,
                "Descripción generada por IA sobre cerámica romana antigua",
                "Galicia",
                true,
                "{\"type\":\"ceramic\",\"period\":\"Roman\",\"confidence\":0.95}"
        );

        FindDto result = findService.createFind("juan@test.com", requestWithAi, null);

        Find fromDb = findRepository.findById(result.id()).orElseThrow();
        assertThat(fromDb.getDescriptionGeneratedByAi()).isTrue();
        assertThat(fromDb.getAiAnalysisJson())
                .isEqualTo("{\"type\":\"ceramic\",\"period\":\"Roman\",\"confidence\":0.95}");
    }

    @Test
    @DisplayName("Debería normalizar el email del ciudadano al buscar en BD")
    void shouldNormalizeEmailWhenSearchingInDatabase() {
        String upperCaseEmail = "JUAN@TEST.COM";

        FindDto result = findService.createFind(upperCaseEmail, validRequest, null);

        assertThat(result).isNotNull();
        assertThat(result.reporterId()).isEqualTo(testCitizen.getId());

        Find fromDb = findRepository.findById(result.id()).orElseThrow();
        assertThat(fromDb.getReporterId()).isEqualTo(testCitizen.getId());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario no existe en BD")
    void shouldThrowExceptionWhenUserNotFoundInDatabase() {
        String nonExistentEmail = "noexiste@test.com";

        assertThatThrownBy(() -> findService.createFind(nonExistentEmail, validRequest, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        assertThat(findRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debería lanzar excepción si el usuario es arqueólogo")
    void shouldThrowExceptionWhenUserIsNotCitizen() {
        Member archaeologist = new Member(
                null,
                "Ana",
                "Pérez",
                null,
                "archaeologist@test.com",
                "87654321B",
                "$2a$10$hashedPassword",
                "600000002",
                UserRole.ARCHAEOLOGIST,
                LocalDateTime.now(),
                "Madrid",
                0
        );
        memberRepository.save(archaeologist);

        assertThatThrownBy(() ->
                findService.createFind("archaeologist@test.com", validRequest, null)
        ).isInstanceOf(RuntimeException.class)
                .hasMessage("Solo los ciudadanos pueden registrar hallazgos");

        assertThat(findRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Debería crear múltiples hallazgos para el mismo usuario")
    void shouldCreateMultipleFindsSameCitizen() {
        CreateFindRequest request1 = new CreateFindRequest(
                LocalDateTime.of(2024, 1, 10, 10, 0),
                43.0, -8.0,
                "Primer hallazgo de cerámica con descripción suficiente",
                "Galicia", false, null
        );
        CreateFindRequest request2 = new CreateFindRequest(
                LocalDateTime.of(2024, 1, 11, 11, 0),
                43.1, -8.1,
                "Segundo hallazgo de monedas con descripción suficiente",
                "Galicia", false, null
        );
        CreateFindRequest request3 = new CreateFindRequest(
                LocalDateTime.of(2024, 1, 12, 12, 0),
                43.2, -8.2,
                "Tercer hallazgo de herramientas con descripción suficiente",
                "Galicia", false, null
        );

        FindDto find1 = findService.createFind("juan@test.com", request1, null);
        FindDto find2 = findService.createFind("juan@test.com", request2, null);
        FindDto find3 = findService.createFind("juan@test.com", request3, null);

        assertThat(find1.id()).isNotNull();
        assertThat(find2.id()).isNotNull();
        assertThat(find3.id()).isNotNull();
        assertThat(find1.id()).isNotEqualTo(find2.id());
        assertThat(find2.id()).isNotEqualTo(find3.id());

        assertThat(findRepository.count()).isEqualTo(3);
        assertThat(findRepository.findByReporterId(testCitizen.getId())).hasSize(3);
    }

    @Test
    @DisplayName("Debería mantener transaccionalidad con rollback en caso de error")
    void shouldRollbackTransactionOnError() {
        Long initialCount = findRepository.count();
        assertThat(initialCount).isEqualTo(0);

        try {
            findService.createFind(null, validRequest, null);
        } catch (Exception e) {
            // Esperamos la excepción
        }

        assertThat(findRepository.count()).isEqualTo(initialCount);
    }
}