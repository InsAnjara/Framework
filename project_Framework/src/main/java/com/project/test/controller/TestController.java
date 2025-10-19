package com.project.test.controller;

import com.framework.servlet.annotation.method.GetAnnotation;
import com.framework.servlet.annotation.method.PostAnnotation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TestController {
    @GetAnnotation(url = "/test/get", priority = 5)
    public void handleGetTest(HttpServletRequest request, HttpServletResponse response) {
        
    }
    
    @PostAnnotation(url = "/test/post", priority = 3)
    public void handlePostTest(HttpServletRequest request, HttpServletResponse response) {
        // Method body would handle POST request logic (not implemented for this example)
    }
    
    @GetAnnotation(url = "/another/get")
    public void anotherGetMethod(HttpServletRequest request, HttpServletResponse response) {
        // Another example
    }
}
