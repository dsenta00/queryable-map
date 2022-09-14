package dsenta.queryablemap.exception;

public class ShouldNeverHappenException extends RuntimeException {
    public ShouldNeverHappenException() {
        super("Unknown");
    }
}