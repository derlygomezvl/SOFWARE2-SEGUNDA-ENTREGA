package com.unicauca.facade_service.config;

import org.springframework.core.io.ByteArrayResource;


public class JsonResource extends ByteArrayResource {
    public JsonResource(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String getFilename() {
        return "data.json";
    }
}


