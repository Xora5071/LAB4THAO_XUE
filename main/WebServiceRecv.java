/**
 * Project: Pizza Integration Lab
 * Purpose Details: Web Service Receive
 * Course: IST 242
 * Author: Xue Thao
 * Date Developed: March 18, 2025,
 * Last Date Changed: March 25, 2025,
 * Rev: 8
 */
package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;

import java.io.*;
import java.net.InetSocketAddress;

/**
 * WebServiceRecv class that starts a simple HTTP server to handle pizza order.
 * The server listens on port 8000 and expects POST requests at the "/pizza".
 * Pizza order in JSON format is processed and returns a response.
 */
public class WebServiceRecv {

    /**
     * Main method to start the HTTP server and set up the /pizza endpoint.
     * Create HttpServer that listens on port 8000 and routes requests to the PizzaHandler.
     *
     * @param args Command-line arguments (not used in this case).
     * @throws Exception Possible Error in Server.
     */
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/pizza", new PizzaHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    /**
     * PizzaHandler class handles HTTP requests made to the "/pizza".
     * Processes POST requests containing a pizza order in JSON format.
     */
    static class PizzaHandler implements HttpHandler {

        /**
         * Handles the HTTP exchange for POST requests.
         * Method reads the JSON payload, deserializes it into a Pizza object.
         * Updates the pizza order and sends a response back to the client.
         *
         * @param exchange The HTTP exchange object containing the request and response.
         * @throws IOException If there is an I/O error during the request processing.
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Handle only POST requests
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read the JSON body from the request using InputStream.
                InputStream inputStream = exchange.getRequestBody();
                byte[] jsonBytes = inputStream.readAllBytes(); // Read the entire body as a byte array
                String json = new String(jsonBytes); // Convert bytes to String

                // Deserialize the JSON into a Pizza object
                Gson gson = new Gson();
                Pizza pizza = gson.fromJson(json, Pizza.class);

                // Print the Pizza Order
                System.out.println("Received Pizza order:");
                System.out.println("Name: " + pizza.getName());
                System.out.println("Size: " + pizza.getSize());
                System.out.println("Price: $" + pizza.getPrice());
                System.out.println("Toppings: " + String.join(", ", pizza.getToppings()));

                // Setter methods create object for new pizza order
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

                // Send a response
                String response = "Pizza order received!";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                // Handle invalid methods (not POST)
                String response = "Handle POST.";
                exchange.sendResponseHeaders(405, response.getBytes().length);  // 405 Method Not Allowed
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
