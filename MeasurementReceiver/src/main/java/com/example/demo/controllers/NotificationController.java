package com.example.demo.controllers;

import com.example.demo.models.MeasurementId;
import com.example.demo.services.DeviceService;
import com.example.demo.services.MeasurementService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.nio.ByteBuffer;
import java.sql.Timestamp;

@Controller
public class NotificationController {
    private final DeviceService deviceService;
    private final MeasurementService measurementService;

    public NotificationController(DeviceService deviceService, MeasurementService measurementService) {
        this.deviceService = deviceService;
        this.measurementService = measurementService;
    }

    @MessageMapping("/measurement")
    @SendTo("/topic/notify")
    public String exceedingMaxConsumption(MeasurementId measurementId){
        var bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(measurementId.deviceId().getMostSignificantBits());
        bb.putLong(measurementId.deviceId().getLeastSignificantBits());
        int maxConsumption = deviceService.consumption(bb.array());

        int consumption = measurementService.consumption(bb.array(), Timestamp.valueOf(measurementId.timestamp()));
        if (consumption > maxConsumption){
            return "Your device has consumed too much energy at " + measurementId.timestamp();
        }
        return null;
    }
}
