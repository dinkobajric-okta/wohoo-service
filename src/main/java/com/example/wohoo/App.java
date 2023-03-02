package com.example.wohoo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api/v1")
public class App {
  @Autowired
  private StreamBridge streamBridge;

  public static void main(String[] args) {
    SpringApplication.run((App.class), args);
  }

  @PostMapping(path = "/publish")
  public ResponseEntity<?> publish(@RequestBody String message) {
    streamBridge.send("high-priority", message);
    streamBridge.send("medium-priority", message);
    streamBridge.send("low-priority", message);

    return ResponseEntity.accepted().build();
  }
}