package com.example.airquality.service;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(Long id) {
        super("未找到编号为 " + id + " 的空气质量记录");
    }
}

