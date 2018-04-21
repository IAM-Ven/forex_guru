package forex_guru.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value=OandaException.class)
    protected @ResponseBody OandaException invalidKey(OandaException ex) {
        return ex;
    }
}
