import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Simulator {
    public static void main(String[] args) {
        final String QUEUE_NAME = "metering_queue";

        Properties prop = new Properties();
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            prop.load(new FileInputStream("src/main/resources/config.properties"));
            prop.setProperty("DEVICE_ID", args[0]);

            var deviceId = UUID.fromString(prop.getProperty("DEVICE_ID"));

            var factory = new ConnectionFactory();
            factory.setHost("localhost");

            var connection = factory.newConnection();
            var channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            var in = new Scanner(new File("src/main/resources/sensor.csv"));
            while (in.hasNextLine()) {
                var measurementJSON = new MeasurementJSON(deviceId, System.currentTimeMillis(), Float.parseFloat(in.nextLine()));
                System.out.println("Sending " + measurementJSON);
                channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, mapper.writeValueAsBytes(measurementJSON));
                Thread.sleep(1000);
            }
            channel.close();
            connection.close();

        } catch (IOException | TimeoutException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
