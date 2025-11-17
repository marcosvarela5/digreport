package es.marcos.digreport.infrastructure.storage;

import es.marcos.digreport.application.port.out.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación de almacenamiento local de archivos.
 * Los archivos se guardan en: uploads/YYYY/MM/UUID.extension
 */
@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    private final Path rootLocation;
    private final String baseUrl;

    public LocalFileStorageAdapter(
            @Value("${file.upload.dir:uploads}") String uploadDir,
            @Value("${file.base.url:http://localhost:8080/uploads}") String baseUrl) {
        this.rootLocation = Paths.get(uploadDir);
        this.baseUrl = baseUrl;
        initStorage();
    }

    private void initStorage() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar el almacenamiento", e);
        }
    }

    @Override
    public StoredFileInfo store(MultipartFile file) throws FileStorageException {
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("El archivo está vacío");
            }

            // Validar tipo de archivo
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new FileStorageException("Solo se permiten imágenes");
            }

            // Generar nombre único con UUID
            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID() + extension;

            // Crear estructura de carpetas por fecha: uploads/2025/01/
            LocalDate now = LocalDate.now();
            Path yearMonthPath = rootLocation.resolve(
                    String.format("%d/%02d", now.getYear(), now.getMonthValue())
            );
            Files.createDirectories(yearMonthPath);

            // Guardar archivo
            Path destinationFile = yearMonthPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Construir ruta relativa
            String relativePath = String.format("%d/%02d/%s",
                    now.getYear(), now.getMonthValue(), uniqueFilename);

            return new StoredFileInfo(
                    uniqueFilename,
                    originalFilename,
                    relativePath,
                    file.getSize(),
                    contentType
            );

        } catch (IOException e) {
            throw new FileStorageException("Error al almacenar el archivo", e);
        }
    }

    @Override
    public List<StoredFileInfo> storeMultiple(List<MultipartFile> files) throws FileStorageException {
        List<StoredFileInfo> storedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            storedFiles.add(store(file));
        }

        return storedFiles;
    }

    @Override
    public void delete(String filePath) throws FileStorageException {
        try {
            Path file = rootLocation.resolve(filePath);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new FileStorageException("Error al eliminar el archivo", e);
        }
    }

    @Override
    public boolean exists(String filePath) {
        Path file = rootLocation.resolve(filePath);
        return Files.exists(file);
    }

    @Override
    public String getPublicUrl(String filePath) {
        return baseUrl + "/" + filePath;
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }
}