package dsenta.queryablemap.exception;

public class DivideByZeroException extends RuntimeException {
    public DivideByZeroException() {
        super("Dividing by zero");
    }
}