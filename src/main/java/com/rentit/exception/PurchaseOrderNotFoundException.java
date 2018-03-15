package com.rentit.exception;


public class PurchaseOrderNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public PurchaseOrderNotFoundException(String id) {
        super(String.format("PO not found! (PO id: %s)", id));

    }
}
