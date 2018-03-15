package com.rentit.exception;


public class PlantUnavailableException extends Exception{

    private static final long serialVersionUID = 1L;

    public PlantUnavailableException() {
        super("Plant is not available.");

    }
}