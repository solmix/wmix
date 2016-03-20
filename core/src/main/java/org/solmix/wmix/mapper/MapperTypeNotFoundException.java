package org.solmix.wmix.mapper;


public class MapperTypeNotFoundException extends RuntimeException
{

    public MapperTypeNotFoundException() {
    }

    public MapperTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperTypeNotFoundException(String message) {
        super(message);
    }

    public MapperTypeNotFoundException(Throwable cause) {
        super(cause);
    }
    private static final long serialVersionUID = 5664117149033680949L;

}
