package com.sj_mart_morshed;
/**
 * Author@
 * MORSHED MD MONOARUL
 * ID: 22012874
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class AddProductWindow extends JFrame {
    private final DatabaseConnection connection;
    private final JTextField nameField;
    private final JTextField quantityField;
    private final JTextField priceField;

    public AddProductWindow(DatabaseConnection connection) {
        // Set up the JFrame properties
        setTitle("Add Product");
        setBounds(100, 100, 440, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.connection = connection;
        getContentPane().setLayout(null);

        // Create and add labels, text fields, and the add button
        JLabel lblName = new JLabel("Name:");
        lblName.setBounds(0, 1, 220, 67);
        getContentPane().add(lblName);

        nameField = new JTextField();
        nameField.setBounds(220, 1, 220, 67);
        nameField.setColumns(10);
        getContentPane().add(nameField);

        JLabel lblQuantity = new JLabel("Quantity:");
        lblQuantity.setBounds(0, 68, 220, 67);
        getContentPane().add(lblQuantity);

        quantityField = new JTextField();
        quantityField.setBounds(220, 68, 220, 67);
        quantityField.setColumns(10);
        getContentPane().add(quantityField);

        JLabel lblPrice = new JLabel("Price:");
        lblPrice.setBounds(0, 135, 220, 67);
        getContentPane().add(lblPrice);

        priceField = new JTextField();
        priceField.setBounds(220, 135, 220, 67);
        priceField.setColumns(10);
        getContentPane().add(priceField);

        JButton btnAdd = new JButton("Add");
        btnAdd.setBounds(118, 203, 220, 67);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Insert the product into the database
                    String name = nameField.getText();
                    int quantity = Integer.parseInt(quantityField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    connection.insertProduct(name, quantity, price);
                    JOptionPane.showMessageDialog(btnAdd, "Product added successfully.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(btnAdd, "Please enter valid numbers for quantity and price.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(btnAdd, "Error adding product: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        getContentPane().add(btnAdd);
    }
}
