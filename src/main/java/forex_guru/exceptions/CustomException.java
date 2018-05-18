package forex_guru.exceptions;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception {

    private String message;
    private HttpStatus status;

    public CustomException(HttpStatus status, String message) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
