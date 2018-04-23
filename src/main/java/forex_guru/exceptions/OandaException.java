package forex_guru.exceptions;

import org.springframework.http.HttpStatus;

public class OandaException extends Exception {

    private String message;
    private HttpStatus status;

    public OandaException(String message, HttpStatus status) {
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
