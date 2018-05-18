package forex_guru.exceptions;

import forex_guru.model.RootResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value=CustomException.class)
    protected @ResponseBody
    RootResponse error(CustomException ex) {
        return new RootResponse(ex.getStatus(), ex.getMessage(), null);
    }

}
