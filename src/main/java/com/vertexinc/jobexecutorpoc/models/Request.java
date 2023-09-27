package com.vertexinc.jobexecutorpoc.models;

import lombok.Data;

@Data
public class Request {
    public int requestId;
    public String requestType;
    public int numberOfInstance;
    public String image;
}
