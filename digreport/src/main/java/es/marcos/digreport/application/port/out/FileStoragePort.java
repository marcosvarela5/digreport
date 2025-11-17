package es.marcos.digreport.application.port.out;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Port de salida para gestión de almacenamiento de archivos.
 * Puede tener múltiples implementaciones: local, S3, Azure Blob, etc.
 */
public interface FileStoragePort {

    /**
     * Almacena un archivo y devuelve información sobre él
     *
     * @param file Archivo a almacenar
     * @return Información del archivo almacenado
     * @throws FileStorageException si hay error al almacenar
     */
    StoredFileInfo store(MultipartFile file) throws FileStorageException;

    /**
     * Almacena múltiples archivos
     */
    List<StoredFileInfo> storeMultiple(List<MultipartFile> files) throws FileStorageException;

    /**
     * Elimina un archivo del almacenamiento
     */
    void delete(String filePath) throws FileStorageException;

    /**
     * Verifica si un archivo existe
     */
    boolean exists(String filePath);

    /**
     * Obtiene la URL pública de un archivo
     */
    String getPublicUrl(String filePath);

    /**
     * Información sobre un archivo almacenado
     */
    record StoredFileInfo(
            String filename,
            String originalFilename,
            String filePath,
            Long fileSize,
            String mimeType
    ) {}

    /**
     * Excepción para errores de almacenamiento
     */
    class FileStorageException extends Exception {
        public FileStorageException(String message) {
            super(message);
        }

        public FileStorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}