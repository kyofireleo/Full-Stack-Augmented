package com.example.restservice;

public record ResponseList (int status, String message, Object[] data){}
