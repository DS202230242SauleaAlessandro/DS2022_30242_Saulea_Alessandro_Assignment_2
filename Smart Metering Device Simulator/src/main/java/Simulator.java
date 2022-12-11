import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Simulator {
    public static void main(String[] args) {
        final String user = "kdayjlqa";
        final String password = "Jh7phazVIlRJrACUZBYSMlLD23aj9dCA";
        final String host = "goose.rmq2.cloudamqp.com";
        final String QUEUE_NAME = "metering_queue";

        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        prop.setProperty("DEVICE_ID", args[0]);
        var deviceId = UUID.fromString(prop.getProperty("DEVICE_ID"));

        var factory = new ConnectionFactory();
        try {
            factory.setUri("amqps://" + user + ":" + password + "@" + host + "/" + user);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            System.out.println(e.getMessage());
        }
        factory.setConnectionTimeout(30000);

        try {
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
