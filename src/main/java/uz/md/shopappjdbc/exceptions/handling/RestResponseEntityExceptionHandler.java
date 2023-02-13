package uz.md.shopappjdbc.exceptions.handling;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uz.md.shopappjdbc.dtos.ErrorData;
import uz.md.shopappjdbc.exceptions.*;


@ControllerAdvice
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorData> handleException(Exception exception) {
        return new ResponseEntity<>(new ErrorData(exception.getMessage(),
                "General Error",
                HttpStatus.NOT_FOUND.value()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({AlreadyExistsException.class})
    public ResponseEntity<ErrorData> handleAccessDeniedException(Exception ex) {
        return new ResponseEntity<>(new ErrorData(ex.getMessage(),
                messageSource.getMessage(ex.getMessage(),
                        null,
                        LocaleContextHolder.getLocale()),
                HttpStatus.FORBIDDEN.value()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            AccessKeyInvalidException.class,
            InvalidUserNameOrPasswordException.class,
            NotAllowedException.class,
            NotEnabledException.class})
    public ResponseEntity<ErrorData> handleAllException(Exception ex) {
        return new ResponseEntity<>(new ErrorData(ex.getMessage(),
                messageSource.getMessage(ex.getMessage(), null, LocaleContextHolder.getLocale()),
                HttpStatus.FORBIDDEN.value()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({IllegalRequestException.class})
    public ResponseEntity<ErrorData> handleIllegalRequest(Exception exception) {
        return new ResponseEntity<>(new ErrorData(exception.getMessage(),
                messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale()),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<ErrorData> handleConflictException(Exception exception) {
        return new ResponseEntity<>(new ErrorData(exception.getMessage(),
                messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale()),
                HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorData> handleNotFoundException(Exception exception) {
        return new ResponseEntity<>(new ErrorData(exception.getMessage(),
                messageSource.getMessage(exception.getMessage(),
                        null,
                        LocaleContextHolder.getLocale()),
                HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

}