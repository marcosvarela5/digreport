package es.marcos.digreport.domain.exception;

public class AiAnalysisException extends Exception {
        public AiAnalysisException(String message) {
        super(message);
    }

        public AiAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
