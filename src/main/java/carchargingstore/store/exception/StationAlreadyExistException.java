package carchargingstore.store.exception;

public class StationAlreadyExistException extends RuntimeException{
    public StationAlreadyExistException(String message){
        super(message);
    }
}
