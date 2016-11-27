package alsa.general.exception;

public class ResponseParseError extends Exception {
    public ResponseParseError() {
    }

    public ResponseParseError(String message) {
        super(message);
    }

    public ResponseParseError(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseParseError(Throwable cause) {
        super(cause);
    }

    public ResponseParseError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
