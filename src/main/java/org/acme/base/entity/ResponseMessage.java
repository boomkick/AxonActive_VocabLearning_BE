package org.acme.base.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@Data
public class ResponseMessage implements Serializable {
    private Boolean success;
    private int statusCode;
    private String message;
}
