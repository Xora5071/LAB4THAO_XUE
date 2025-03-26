/**
 * Project: Pizza Integration Lab
 * Purpose Details: Web Service Send
 * Course: IST 242
 * Author: Xue Thao
 * Date Developed: March 18, 2025,
 * Last Date Changed: March 25, 2025,
 * Rev: 8
 */
package main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * WebServiceSend class sends  pizza order to a web service via a POST request.
 * Pizza object is created and serialized in JSON format and sent to server.
 * Prints response from the server.
 */
public class WebServiceSend {

    /**
     * The URL of the web service to which the pizza order is sent.
     */
    private static final String SERVICE_URL = "http://localhost:8000/pizza";

    /**
     * Main method to send Pizza Order to the web service.
     * Create Pizza Order and serialize to JSON. Send to the server.
     * Print server's response.
     *
     * @param args Command-line arguments (not used here).
     */
    public static void main(String[] args) {
        try {
            // Create Pizza object
            Pizza pizza = new Pizza("Pepperoni", "Medium", 14.99, new String[]{"Pepperoni", "Mushroom", "Sausage"});

            // Serialize the Pizza object to JSON using Gson
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(pizza);

            // Specify the URL of the web service
            HttpURLConnection con = getHttpURLConnection();

            // Send the JSON payload
            OutputStream os = con.getOutputStream();
            os.write(jsonPayload.getBytes());
            os.flush();

            // Get the response code
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            // Read the response from the web service
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the response from the server
            System.out.println("Response: " + response); // parse the JSON response as needed
        } catch (IOException e) {
            /* Handle exceptions, getting inspection on replace with more robust logging. */
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    /**
     * Creates and configures an HttpURLConnection for sending a POST request to the pizza web service.
     *
     * @return A configured HttpURLConnection object ready for sending the request.
     * @throws IOException If there is an error while opening the connection.
     */
    private static HttpURLConnection getHttpURLConnection() throws IOException {
        // Create a URL object. Getting a deprecation warning from Intellij.
        @SuppressWarnings("deprecation") URL obj = new URL(SERVICE_URL);

        // Open a connection to the URL
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Set the request method
        con.setRequestMethod("POST");

        // Set the request headers
        con.setRequestProperty("Content-Type", "application/json");

        // Enable input/output streams
        con.setDoOutput(true);

        return con;
    }
}
