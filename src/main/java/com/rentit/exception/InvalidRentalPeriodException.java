package com.rentit.exception;


public class InvalidRentalPeriodException extends Exception{

    private static final long serialVersionUID = 1L;

    public InvalidRentalPeriodException() {
        super("Rental period is not valid.");
    }
}