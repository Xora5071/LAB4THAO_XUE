package main;

import com.google.gson.Gson;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * Project: Pizza Integration Lab
 * Purpose Details: Web Service Send
 * Course: IST 242
 * Author: Xue Thao
 * Date Developed: March 18, 2025,
 * Last Date Changed: April, 2025,
 * Rev: 11 add HMAC functionality
 */
@SuppressWarnings("ALL")
public class WebServiceSend {

    /**
     * The URL of the web service to which the pizza order is sent.
     * Specifies the address of the receiving service.
     */
    private static final String SERVICE_URL = "http://localhost:8000/pizza";

    /**
     * The shared secret key used to generate the HMAC (SHA256) value.
     * Ensures the integrity of the transmitted message.
     */
    private static final String SECRET_KEY = "shared_secret_key";

    /**
     * Main method to send Pizza Order to the web service.
     * Creates a Pizza Order, generates an HMAC, serializes the order to JSON,
     * and sends both the order and HMAC to the server. Prints the server's response.
     *
     * @param args Command-line arguments (not used here).
     */
    public static void main(String[] args) {
        try {
            // CRUD Create a Pizza object with initial order details
            Pizza pizza = new Pizza("Pepperoni", "Medium", 14.99, new String[]{"Pepperoni", "Mushroom", "Sausage"});

            // Serialize the Pizza object into JSON format using Gson
            Gson gson = new Gson();
            String jsonPayload = gson.toJson(pizza);

            // Generate an HMAC (SHA256) for the JSON payload
            String hmac = generateHMAC(jsonPayload, SECRET_KEY);

            // Configure and send the HTTP POST request with the JSON payload and HMAC
            HttpURLConnection con = getHttpURLConnection(hmac);
            OutputStream os = con.getOutputStream();
            os.write(jsonPayload.getBytes()); // Write the JSON payload to the request body
            os.flush(); // Ensure all data is sent
            os.close();

            // Read the response code from the server
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the server's response from the input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine); // Append each line of the response
            }
            in.close(); // Close the input stream

            // Print the response from the server
            System.out.println("Response: " + response);

        } catch (Exception e) {
            // Handle exceptions during HMAC generation or HTTP request
            e.printStackTrace();
        }
    }

    /**
     * Generates an HMAC (SHA256) for the given data using the specified secret key.
     * Combines the message (JSON payload) and the shared key to create a secure hash value.
     *
     * @param data The message to hash (e.g., JSON payload).
     * @param key  The shared secret key used for HMAC generation.
     * @return The generated HMAC as a Base64-encoded string.
     * @throws Exception If an error occurs during HMAC generation.
     */
    private static String generateHMAC(String data, String key) throws Exception {
        // Create a Mac instance configured with the HmacSHA256 algorithm
        Mac mac = Mac.getInstance("HmacSHA256");

        // Initialize the Mac instance with the provided secret key
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);

        // Compute the HMAC value for the input data
        byte[] hmacBytes = mac.doFinal(data.getBytes());

        // Encode the HMAC as a Base64 string for transmission
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    /**
     * Creates and configures an HttpURLConnection for sending a POST request to the pizza web service.
     * Adds the generated HMAC as a custom HTTP header to the request.
     *
     * @param hmac The generated HMAC (SHA256) to include in the request headers.
     * @return A configured HttpURLConnection object ready for sending the request.
     * @throws IOException If there is an error while opening the connection.
     */
    private static HttpURLConnection getHttpURLConnection(String hmac) throws IOException {
        // Create a URL object representing the web service endpoint
        URL url = new URL(SERVICE_URL);

        // Open a connection to the URL
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        con.setRequestMethod("POST");

        // Set request headers, including the Content-Type and the HMAC
        con.setRequestProperty("Content-Type", "application/json"); // Indicate JSON payload
        con.setRequestProperty("HMAC", hmac); // Add the HMAC to the headers for integrity verification

        // Enable input/output streams for sending and receiving data
        con.setDoOutput(true);

        return con; // Return the configured HttpURLConnection instance
    }
}
