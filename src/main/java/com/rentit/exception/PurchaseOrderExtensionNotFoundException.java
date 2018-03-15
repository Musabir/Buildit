package com.rentit.exception;

import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.domain.model.PurchaseOrderExtension;

public class PurchaseOrderExtensionNotFoundException extends Exception {

    public PurchaseOrderExtensionNotFoundException(String s){
        super(s);
    }
}
