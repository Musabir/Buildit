package com.rentit.exception;


public class PurchaseOrderCancellationException extends Exception {

    private static final long serialVersionUID = 1L;

    public PurchaseOrderCancellationException() {
        super("Plant cancelation is not available.");

    }
}