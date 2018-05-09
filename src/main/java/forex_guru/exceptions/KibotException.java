package forex_guru.exceptions;

import org.springframework.http.HttpStatus;

public class KibotException extends Exception {

    private HttpStatus status;
    private String message;

    public KibotException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
