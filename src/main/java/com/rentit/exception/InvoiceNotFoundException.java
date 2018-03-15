package com.rentit.exception;


public class InvoiceNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvoiceNotFoundException(String id) {
        super(String.format("Invoice not found! (Invoice id: %s)", id));

    }

    public InvoiceNotFoundException() {
    }
}
