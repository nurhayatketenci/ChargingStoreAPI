package carchargingstore.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler({SessionNotAvailableException.class})
    public ResponseEntity<Object> handleSessionNotAvailableException(SessionNotAvailableException exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }

    @ExceptionHandler({SessionNotFoundException.class})
    public ResponseEntity<Object> handleSessionNotFoundException(SessionNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }

    @ExceptionHandler({StationAlreadyExistException.class})
    public ResponseEntity<Object> handleStationAlreadyExistException(StationAlreadyExistException exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
}
