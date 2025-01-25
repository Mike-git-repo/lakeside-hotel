package com.lakesidehotel.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String messsage) {
        super(messsage);
    }
}
