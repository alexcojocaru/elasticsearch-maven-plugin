package com.github.alexcojocaru.mojo.elasticsearch.v2.configuration;

/** 
 * 
 * @author Alex Cojocaru
 *
 */
@SuppressWarnings("serial")
public class InvalidCoordinatesException
        extends RuntimeException
{

    public InvalidCoordinatesException(String coordinates)
    {
        super("Could not create a dependency from " + coordinates);
    }
}
