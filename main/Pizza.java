/**
 * Project: Pizza Integration Lab
 * Purpose Details: File Files, RabbitMQ, and Web Service Integration
 * Course: IST 242
 * Author: Xue Thao
 * Date Developed: March 18, 2025,
 * Last Date Changed: March 25, 2025,
 * Rev: 6
 */
package main;
import java.util.Arrays;

public class Pizza {

    /** The name of the pizza */
    private String name;

    /** The size of the pizza */
    private String size;

    /** The price of the pizza */
    private double price;

    /** The list of toppings on the pizza */
    private String[] toppings;

    /**
     * Constructs a main.Pizza object with the specified properties.
     *
     * @param name    The name of the pizza
     * @param size    The size of the pizza
     * @param price   The price of the pizza
     * @param toppings The array of toppings for the pizza
     */
    public Pizza(String name, String size, double price, String[] toppings) {
        this.name = name;
        this.size = size;
        this.price = price;
        this.toppings = toppings;
    }

    /**
     * Gets the name of the pizza.
     *
     * @return The name of the pizza
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the pizza.
     *
     * @param name The new name for the pizza
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the size of the pizza.
     *
     * @return The size of the pizza
     */
    public String getSize() {
        return this.size;
    }

    /**
     * Sets the size of the pizza.
     *
     * @param size The new size for the pizza
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Gets the price of the pizza.
     *
     * @return The price of the pizza
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * Sets the price of the pizza.
     *
     * @param price The new price for the pizza
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the toppings of the pizza.
     *
     * @return An array of toppings on the pizza
     */
    public String[] getToppings() {
        return this.toppings;
    }

    /**
     * Sets the toppings of the pizza.
     *
     * @param toppings The new array of toppings for the pizza
     */
    public void setToppings(String[] toppings) {
        this.toppings = toppings;
    }

    @Override
    public String toString() {
        return "main.Pizza{name='" + name + "', size='" + size + "', price=" + price + ", toppings=" + Arrays.toString(toppings) + "}";
    }
}