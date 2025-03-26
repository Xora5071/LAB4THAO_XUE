/**
 * Project: Pizza Integration Lab
 * Purpose Details: RabbitMQ Send
 * Course: IST 242
 * Author: Xue Thao
 * Date Developed: March 18, 2025,
 * Last Date Changed: March 25, 2025,
 * Rev: 6
 */
package main;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.google.gson.Gson;

/**
 * RabbitMQSEND sends Pizza Orders as JSON messages to pizzaQueue.
 * Creates Pizza object, serializes it to JSON, and sends it to the pizzaQueue.
 */
public class Send {
    /**
     * The name of the queue where the pizza orders are sent.
     */
    private final static String QUEUE_NAME = "pizzaQueue";

    /**
     * Serializes a Pizza object into a JSON string using Gson.
     *
     * @param pizza The Pizza object to be serialized.
     * @return A JSON string representation of the Pizza object.
     */
    private static String serializePizzaToJson(Pizza pizza) {
        Gson gson = new Gson();
        return gson.toJson(pizza);
    }

    /**
     * Main method that creates a Pizza object, serializes it to JSON, and sends it to pizzaQueue.
     *
     * @param argv Command-line arguments (not used in this example).
     * @throws Exception If an error occurs during the connection or messaging process.
     */
    public static void main(String[] argv) throws Exception {
        // Create a Pizza Order
        Pizza pizza = new Pizza("Pepperoni", "Medium", 14.99, new String[]{"Pepperoni", "Mushroom", "Sausage"});

        // Serialize the Pizza object to a JSON string
        String message = serializePizzaToJson(pizza);

        // Send the JSON string to RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");  // Set RabbitMQ server to localhost
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // Declare a queue to send the message to
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            // Send the message (JSON string) to the queue
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());

            // Output confirmation message
            System.out.println(" [x] Sent: '" + message + "'");
        }
    }
}