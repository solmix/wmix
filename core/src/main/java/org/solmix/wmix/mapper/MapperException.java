package org.solmix.wmix.mapper;


public class MapperException extends RuntimeException
{

    public MapperException() {
    }

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(String message) {
        super(message);
    }

    public MapperException(Throwable cause) {
        super(cause);
    }
    private static final long serialVersionUID = -5058026694023029309L;

}
