package com.mythsman.server.exceptions;

/**
 * @author tusenpo
 * @date 6/5/21
 */
public class SaxParseTerminated extends RuntimeException {
    private static final long serialVersionUID = -1987811271346902991L;

    public SaxParseTerminated() {
    }

    public SaxParseTerminated(String message) {
        super(message);
    }

    public SaxParseTerminated(String message, Throwable cause) {
        super(message, cause);
    }

    public SaxParseTerminated(Throwable cause) {
        super(cause);
    }

    public SaxParseTerminated(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
