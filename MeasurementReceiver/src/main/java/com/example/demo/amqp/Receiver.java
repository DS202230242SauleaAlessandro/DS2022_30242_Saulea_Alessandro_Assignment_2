package com.example.demo.amqp;

import com.example.demo.services.MeasurementService;
import com.example.demo.models.Measurement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeoutException;

import static java.lang.Math.ceil;

// https://www.baeldung.com/jackson-json-view-annotation
// https://www.baeldung.com/jackson-deserialization
// https://www.postgresqltutorial.com/postgresql-jdbc/connecting-to-postgresql-database/
// https://docs.oracle.com/javase/tutorial/jdbc/basics/processingsqlstatements.html
// https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
// https://www.baeldung.com/java-byte-array-to-uuid
// https://spring.io/guides/gs/messaging-stomp-websocket/
// https://stackoverflow.com/questions/54275069/module-not-found-error-cant-resolve-net-in-node-modules-stompjs-lib

@Component
public class Receiver implements CommandLineRunner {
    @Override
    public void run(String... args) {
        var factory = new ConnectionFactory();
        factory.setHost("localhost");

        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        var measurementService = new MeasurementService();

        try {
            String QUEUE_NAME = "metering_queue";
            var channel = factory.newConnection().createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("Waiting for messages");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                var measurement = mapper.readValue(delivery.getBody(), Measurement.class);
                System.out.println(measurement);
                var timestamp = new Timestamp(measurement.timestamp()).toLocalDateTime();
                var onlyHour = Timestamp.valueOf(
                        LocalDateTime.of(timestamp.toLocalDate(), LocalTime.of(timestamp.getHour(), 0)));

                if (measurementService.find(measurement.deviceId(), onlyHour))
                    measurementService.update(measurement.deviceId(), onlyHour, (int)ceil(measurement.consumption()));
                else
                    measurementService.create(measurement.deviceId(), onlyHour, (int)ceil(measurement.consumption()));

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {});

        } catch (IOException | TimeoutException e) {
            System.out.println(e.getMessage());
        }
    }
}
