/**
 * Project: Pizza Integration Lab
 * Purpose Details: RabbitMQ Receive
 * Course: IST 242
 * Author: Xue Thao
 * Date Developed: March 18, 2025,
 * Last Date Changed: March 25, 2025,
 * Rev: 6
 */

package main;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.google.gson.Gson;

import java.util.Arrays;

public class Recv {

    /** RabbitMQ will show pizzaQueue attached in Capture*/
    private final static String QUEUE_NAME = "pizzaQueue";

    /**
     * Set Up the RabbitMQ connection and creates a channel.
     * Listen for messages on the pizzaQueue.
     * The RECV class getting from pizza order deserializes the JSON data to a main.Pizza object.
     * Prints the output details to the console.
     *
     * @param argv Command-line arguments (not used in this implementation)
     * @throws Exception If there is any issue during the connection, message consumption, or JSON deserialization
     */
    public static void main(String[] argv) throws Exception {
        // Set up the RabbitMQ connection and channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare the queue that we are going to consume messages from
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // Set up the message consumer
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // Convert the message body to a string (UTF-8 encoding)
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received Pizza Queue: '" + message + "'");

            // Deserialize the JSON message to a Pizza object
            Pizza pizza = deserializeJsonToPizza(message);

            if (pizza != null) {
                // Print the main.Pizza object details
                System.out.println("Pizza Queue:");
                System.out.println(" - Name: " + pizza.getName());
                System.out.println(" - Size: " + pizza.getSize());
                System.out.println(" - Price: $" + pizza.getPrice());
                // Toppings printed once using Arrays.toString
                System.out.println(" - Toppings: " + Arrays.toString(pizza.getToppings()));

                // Update with Setter method to update Pizza object.
                pizza.setName("Hawaiian");  // Setting the name
                pizza.setSize("Large");     // Setting the size
                pizza.setPrice(17.99);      // Setting the price
                pizza.setToppings(new String[]{"Pineapple", "Bacon", "Ham"});  // Setting the toppings

                // Print the updated Pizza object details
                System.out.println("New Pizza Queue:");
                System.out.println(" - Name: " + pizza.getName());
                System.out.println(" - Size: " + pizza.getSize());
                System.out.println(" - Price: $" + pizza.getPrice());
                System.out.println(" - Toppings: " + Arrays.toString(pizza.getToppings()));

            } else {
                System.out.println("Failed to deserialize Pizza object.");
            }
        };

        // Start consuming the messages from the queue
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }

    /**
     * Deserializes a JSON string into a main.Pizza object using GSON library.
     *
     * @param json The JSON string representation of a main.Pizza object
     * @return A main.Pizza object or null if the deserialization fails
     */
    private static Pizza deserializeJsonToPizza(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Pizza.class);
    }
}