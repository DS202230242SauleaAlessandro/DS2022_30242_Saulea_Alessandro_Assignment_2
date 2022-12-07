package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.UUID;

public record MeasurementId(UUID deviceId, LocalDateTime timestamp) {
}
