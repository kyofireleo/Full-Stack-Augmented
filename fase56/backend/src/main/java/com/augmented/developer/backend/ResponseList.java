package com.augmented.developer.backend;

public record ResponseList (int status, String message, Object[] data, int pageSize){}