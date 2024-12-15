package com.sj_mart_morshed;
/**
 * Author@
 * MORSHED MD MONOARUL
 * ID: 22012874
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;

public class DatabaseConnection {
    private Connection connection;

    // Constructor - Establishes the database connection
    public DatabaseConnection(String databaseName) {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to the specified database
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
            System.out.println("Connected to the database.");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
    }

    // Close the database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from the database.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close the database connection: " + e.getMessage());
        }
    }

    // Create the database schema for the application
    public void createSchema() {
        try (Statement statement = connection.createStatement()) {
            // Create the products table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    price REAL NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )""");

            // Create the categories table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL
                )""");

            // Create the product_categories table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS product_categories (
                    product_id INTEGER,
                    category_id INTEGER,
                    FOREIGN KEY (product_id) REFERENCES products(id),
                    FOREIGN KEY (category_id) REFERENCES categories(id),
                    PRIMARY KEY (product_id, category_id)
                )""");

            System.out.println("Schema created successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to create schema: " + e.getMessage());
        }
    }

    // Insert a product into the products table
    public void insertProduct(String name, int quantity, double price) {
        String query = "INSERT INTO products (name, quantity, price) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, quantity);
            statement.setDouble(3, price);

            int rowsInserted = statement.executeUpdate();
            System.out.println(rowsInserted > 0 ? "Product inserted successfully." : "Failed to insert product.");
        } catch (SQLException e) {
            System.err.println("Failed to insert product: " + e.getMessage());
        }
    }

    // Insert a category into the categories table
    public void insertCategory(String name) {
        String query = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);

            int rowsInserted = statement.executeUpdate();
            System.out.println(rowsInserted > 0 ? "Category inserted successfully." : "Failed to insert category.");
        } catch (SQLException e) {
            System.err.println("Failed to insert category: " + e.getMessage());
        }
    }

    // Assign a category to a product in the product_categories table
    public void assignCategoryToProduct(int productId, int categoryId) {
        String query = "INSERT INTO product_categories (product_id, category_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            statement.setInt(2, categoryId);

            int rowsInserted = statement.executeUpdate();
            System.out.println(rowsInserted > 0 ? "Category assigned to product successfully." : "Failed to assign category to product.");
        } catch (SQLException e) {
            System.err.println("Failed to assign category to product: " + e.getMessage());
        }
    }

    // Update the quantity of a product for a purchase
    public void purchaseProduct(int productId, int quantityToPurchase) {
        String query = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, quantityToPurchase);
            statement.setInt(2, productId);

            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Product purchased successfully." : "Failed to purchase product.");
        } catch (SQLException e) {
            System.err.println("Failed to purchase product: " + e.getMessage());
        }
    }

    // Retrieve all products
    public void getAllProducts(DefaultTableModel tableModel) {
        String query = "SELECT * FROM products";
        tableModel.setRowCount(0);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price"),
                        resultSet.getString("created_at"),
                        resultSet.getString("updated_at")
                });
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve products: " + e.getMessage());
        }
    }

    // Retrieve products by category
    public void getProductsByCategory(int categoryId, DefaultTableModel tableModel) {
        String query = """
                SELECT p.* FROM products p
                JOIN product_categories pc ON p.id = pc.product_id
                WHERE pc.category_id = ?
                """;
        tableModel.setRowCount(0);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, categoryId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tableModel.addRow(new Object[]{
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getInt("quantity"),
                            resultSet.getDouble("price"),
                            resultSet.getString("created_at"),
                            resultSet.getString("updated_at")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve products by category: " + e.getMessage());
        }
    }

    // Delete a product by ID
    public void deleteProduct(int productId) {
        executeDelete("DELETE FROM products WHERE id = ?", productId);
    }

    // Delete a category by ID
    public void deleteCategory(int categoryId) {
        executeDelete("DELETE FROM categories WHERE id = ?", categoryId);
    }

    private void executeDelete(String query, int id) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Record deleted successfully." : "Failed to delete record. Record not found.");
        } catch (SQLException e) {
            System.err.println("Failed to delete record: " + e.getMessage());
        }
    }

    // Retrieve all categories
    public void getAllCategories(DefaultTableModel tableModel) {
        String query = "SELECT * FROM categories";
        tableModel.setRowCount(0);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                tableModel.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                });
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve categories: " + e.getMessage());
        }
    }

    // Update ComboBox with ID and names
    public void updateComboBox(JComboBox<String> comboBox) {
        String query = "SELECT id, name FROM categories";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                comboBox.addItem(resultSet.getInt("id") + "|" + resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to update ComboBox: " + e.getMessage());
        }
    }

    // Method to sell multiple products
    public String sellMultipleProducts(List<String> productIds, List<String> quantities) {
        if (productIds.size() != quantities.size()) {
            return "Number of product IDs must match the number of quantities.";
        }

        String updateQuery = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            for (int i = 0; i < productIds.size(); i++) {
                statement.setInt(1, Integer.parseInt(quantities.get(i)));
                statement.setInt(2, Integer.parseInt(productIds.get(i)));
                statement.executeUpdate();
            }
            return "Products sold successfully.";
        } catch (SQLException | NumberFormatException e) {
            return "Failed to sell products: " + e.getMessage();
        }
    }
}
