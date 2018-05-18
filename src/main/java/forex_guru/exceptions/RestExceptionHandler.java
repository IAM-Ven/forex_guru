package forex_guru.exceptions;

import forex_guru.model.RootResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value=KibotException.class)
    protected @ResponseBody
    RootResponse oandaError(KibotException ex) {
        return new RootResponse(ex.getStatus(), ex.getMessage(), null);
    }

    @ExceptionHandler(value=DatabaseException.class)
    protected @ResponseBody
    RootResponse databaseError(KibotException ex) {
        return new RootResponse(ex.getStatus(), ex.getMessage(), null);
    }

    @ExceptionHandler(value=ClientException.class)
    protected @ResponseBody
    RootResponse clientError(ClientException ex) {
        return new RootResponse(ex.getStatus(), ex.getMessage(), null);
    }
}
