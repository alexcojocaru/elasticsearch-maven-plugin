package com.github.alexcojocaru.mojo.elasticsearch.v2.configuration;

public class ArtifactException extends Exception {

    private static final long serialVersionUID = -3217334010123902842L;

    public ArtifactException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtifactException(Throwable cause) {
        super(cause);
    }

    public ArtifactException(String message) {
        super(message);
    }

}
