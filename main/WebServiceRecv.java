package main;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Base64;

/**
 * Project: Pizza Integration Lab
 * Purpose Details: Web Service Receive
 * Course: IST 242
 * Author: Xue Thao
 * Date Developed: March 18, 2025,
 * Last Date Changed: April 8, 2025,
 * Rev: 11 add HMAC functionality
 */
@SuppressWarnings("ALL")
public class WebServiceRecv {

    //Shared secret key for HMAC functionality
    private static final String SECRET_KEY = "shared_secret_key";

    /**
     * Main method to start the HTTP server and set up the /pizza endpoint.
     * Creates an HttpServer that listens on port 8000 and routes requests to the PizzaHandler.
     *
     * @param args Command-line arguments (not used in this case).
     * @throws Exception Handles potential server startup exceptions.
     */
    public static void main(String[] args) throws Exception {
        // Create an HTTP server instance listening on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        // Map the "/pizza" context to the PizzaHandler
        server.createContext("/pizza", new PizzaHandler());
        // Set the default executor for handling HTTP requests
        server.setExecutor(null); // creates a default executor
        // Start the server
        server.start();
    }

    /**
     * PizzaHandler class handles HTTP requests made to the "/pizza" endpoint.
     * Processes POST requests containing a pizza order in JSON format.
     */
    static class PizzaHandler implements HttpHandler {

        /**
         * Handles the HTTP exchange for POST requests.
         * Reads the JSON payload, deserializes it into a Pizza object, validates the HMAC,
         * and updates the pizza order. Responds with success or failure message.
         *
         * @param exchange The HTTP exchange object containing the request and response.
         * @throws IOException If there is an I/O error during the request processing.
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Ensure the request is a POST request
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read the JSON body from the request using InputStream
                InputStream inputStream = exchange.getRequestBody();
                byte[] jsonBytes = inputStream.readAllBytes(); // Read the request body as bytes
                String json = new String(jsonBytes); // Convert bytes into a string representation

                // Extract the HMAC from the request headers
                String receivedHMAC = exchange.getRequestHeaders().getFirst("HMAC");

                // Generate HMAC for the received payload using the shared secret key
                String generatedHMAC;
                try {
                    generatedHMAC = generateHMAC(json, SECRET_KEY); // Generate the HMAC
                } catch (Exception e) {
                    // Handle any exceptions during HMAC generation
                    e.printStackTrace();
                    String response = "Error generating HMAC.";
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    return;
                }

                // Print the received and generated HMAC values for debugging
                System.out.println("Received HMAC (SHA): " + receivedHMAC);
                System.out.println("Generated HMAC (SHA): " + generatedHMAC);

                // Verify the integrity of the payload by comparing HMAC values
                if (generatedHMAC.equals(receivedHMAC)) {
                    // HMAC verification passed
                    System.out.println("HMAC Verified! Payload integrity confirmed.");

                    // Deserialize the JSON into a Pizza object
                    Gson gson = new Gson();
                    Pizza pizza = gson.fromJson(json, Pizza.class);

                    // Print the details of the received pizza order
                    System.out.println("Received Pizza order:");
                    System.out.println("Name: " + pizza.getName());
                    System.out.println("Size: " + pizza.getSize());
                    System.out.println("Price: $" + pizza.getPrice());
                    System.out.println("Toppings: " + String.join(", ", pizza.getToppings()));

                    // Update the pizza order using setter methods
                    pizza.setName("Hawaiian");
                    pizza.setSize("Large");
                    pizza.setPrice(16.99);
                    pizza.setToppings(new String[]{"Pineapple", "Ham", "Bacon"});

                    // Print the updated pizza order
                    System.out.println("Updated Pizza order:");
                    System.out.println("Name: " + pizza.getName());
                    System.out.println("Size: " + pizza.getSize());
                    System.out.println("Price: $" + pizza.getPrice());
                    System.out.println("Toppings: " + String.join(", ", pizza.getToppings()));

                    // Send a success response to the client
                    String response = "HMAC Verified! Order received.";
                    exchange.sendResponseHeaders(200, response.getBytes().length); // HTTP 200 OK
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    // HMAC verification failed
                    System.out.println("HMAC Verification Failed! Payload integrity compromised.");

                    // Respond with an error message and HTTP status 403 Forbidden
                    String response = "HMAC Verification Failed!";
                    exchange.sendResponseHeaders(403, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } else {
                // Respond with HTTP status 405 Method Not Allowed for non-POST requests
                String response = "Only POST requests are supported.";
                exchange.sendResponseHeaders(405, response.getBytes().length); // HTTP 405
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        /**
         * Generates an HMAC (SHA256) for the given data using the specified key.
         * Combines the message (JSON payload) and the key to produce a secure hash.
         *
         * @param data The input string (e.g., JSON payload).
         * @param key  The shared secret key used for HMAC generation.
         * @return The generated HMAC as a Base64-encoded string.
         * @throws IOException If an error occurs during HMAC generation.
         */
        private String generateHMAC(String data, String key) throws IOException {
            try {
                // Create an HMAC instance using SHA256 algorithm
                Mac mac = Mac.getInstance("HmacSHA256");
                // Initialize the HMAC with the provided secret key
                SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
                mac.init(secretKeySpec);
                // Compute the HMAC for the input data
                byte[] hmacBytes = mac.doFinal(data.getBytes());
                // Return the HMAC data
                return Base64.getEncoder().encodeToString(hmacBytes);
            } catch (Exception e) {
                throw new IOException("Error while generating HMAC", e);
            }
        }
    }
}
