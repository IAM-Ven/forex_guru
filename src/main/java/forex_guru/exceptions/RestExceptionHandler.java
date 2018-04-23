package forex_guru.exceptions;

import forex_guru.model.internal.RootResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value=OandaException.class)
    protected @ResponseBody
    RootResponse oandaError(OandaException ex) {
        return new RootResponse(ex.getStatus(), ex.getMessage(), null);
    }
}
