package com.rentit.common.infrastructure;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdentifierFactory {
    public String nextDomainObjectID() {
        return UUID.randomUUID().toString();
    }
}