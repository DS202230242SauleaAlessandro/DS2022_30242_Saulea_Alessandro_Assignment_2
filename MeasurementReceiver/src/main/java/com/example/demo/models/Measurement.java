package com.example.demo.models;

import java.util.Arrays;

public record Measurement(byte[] deviceId, long timestamp, float consumption) {

    @Override
    public String toString() {
        return "Measurement{" +
                "deviceId=" + Arrays.toString(deviceId) +
                ", timestamp=" + timestamp +
                ", consumption=" + consumption +
                '}';
    }
}
