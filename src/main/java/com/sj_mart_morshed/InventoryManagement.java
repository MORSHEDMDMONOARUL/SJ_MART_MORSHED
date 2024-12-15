package com.sj_mart_morshed;
/**
 * Author@
 * MORSHED MD MONOARUL
 * ID: 22012874
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class InventoryManagement {
    private final DatabaseConnection connection = new DatabaseConnection("InventoryDB");
    private JFrame frame;
    private JTable purchaseTable;
    private final DefaultTableModel productTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Quantity", "Price", "Created", "Updated"}, 0);
    private final DefaultTableModel categoriesTableModel = new DefaultTableModel(new Object[]{"ID", "Name"}, 0);
    private final DefaultTableModel inventoryTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Quantity", "Price", "Created", "Updated"}, 0);
    private JTextField idField;
    private JTextField quantityField;
    private JTable inventoryTable;
    private JTextField productIDField;
    private JTextField categoryIDField;
    private JTextField pidField;
    private JTextField quantitiesField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                InventoryManagement window = new InventoryManagement();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public InventoryManagement() {
        connection.createSchema();
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 700, 550);
        frame.setTitle("SJ_MART - MORSHED");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(0, 0, 700, 520);
        frame.getContentPane().add(tabbedPane);

        createPurchaseTab(tabbedPane);
        createInventoryTab(tabbedPane);
    }

    /**
     * Create the Purchase tab.
     */
    private void createPurchaseTab(JTabbedPane tabbedPane) {
        JPanel purchasePanel = new JPanel();
        tabbedPane.addTab("Purchase", null, purchasePanel, null);
        purchasePanel.setLayout(null);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(143, 240, 164));
        buttonsPanel.setBounds(0, 425, 700, 70);
        purchasePanel.add(buttonsPanel);
        buttonsPanel.setLayout(new GridLayout(1, 0, 5, 5));

        addPurchaseButtons(buttonsPanel);

        JToggleButton toggleButton = new JToggleButton("Products");
        toggleButton.setBounds(0, 0, 167, 25);
        toggleButton.addActionListener(e -> toggleProductCategoryView(toggleButton));
        purchasePanel.add(toggleButton);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 25, 700, 300);
        purchasePanel.add(scrollPane);

        purchaseTable = new JTable(productTableModel);
        scrollPane.setViewportView(purchaseTable);

        createPurchaseFields(purchasePanel);
    }

    /**
     * Add buttons for the Purchase tab.
     */
    private void addPurchaseButtons(JPanel buttonsPanel) {
        JButton btnDeleteCategory = new JButton("Delete Category");
        btnDeleteCategory.addActionListener(e -> deleteCategory());
        buttonsPanel.add(btnDeleteCategory);

        JButton btnDeleteProduct = new JButton("Delete Product");
        btnDeleteProduct.addActionListener(e -> deleteProduct());
        buttonsPanel.add(btnDeleteProduct);

        JButton btnAddNewCategory = new JButton("Add New Category");
        btnAddNewCategory.addActionListener(e -> addNewCategory());
        buttonsPanel.add(btnAddNewCategory);

        JButton btnAddNewProduct = new JButton("Add New Product");
        btnAddNewProduct.addActionListener(e -> openAddProductWindow());
        buttonsPanel.add(btnAddNewProduct);
    }

    /**
     * Create fields for the Purchase tab.
     */
    private void createPurchaseFields(JPanel purchasePanel) {
        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(10, 393, 70, 25);
        purchasePanel.add(lblId);

        idField = new JTextField();
        idField.setBounds(78, 393, 114, 25);
        purchasePanel.add(idField);
        idField.setColumns(10);

        JLabel lblQuantity = new JLabel("Quantity:");
        lblQuantity.setBounds(210, 393, 70, 25);
        purchasePanel.add(lblQuantity);

        quantityField = new JTextField();
        quantityField.setBounds(298, 393, 114, 25);
        purchasePanel.add(quantityField);
        quantityField.setColumns(10);

        JButton btnPurchase = new JButton("Purchase");
        btnPurchase.addActionListener(e -> purchaseProduct());
        btnPurchase.setBounds(438, 393, 117, 25);
        purchasePanel.add(btnPurchase);

        JLabel lblProductId = new JLabel("Product ID:");
        lblProductId.setBounds(12, 337, 100, 25);
        purchasePanel.add(lblProductId);

        productIDField = new JTextField();
        productIDField.setBounds(107, 337, 114, 25);
        purchasePanel.add(productIDField);
        productIDField.setColumns(10);

        JLabel lblCategoryId = new JLabel("Category ID:");
        lblCategoryId.setBounds(222, 337, 100, 25);
        purchasePanel.add(lblCategoryId);

        categoryIDField = new JTextField();
        categoryIDField.setBounds(323, 337, 114, 25);
        purchasePanel.add(categoryIDField);
        categoryIDField.setColumns(10);

        JButton btnSet = new JButton("Set");
        btnSet.addActionListener(e -> assignCategoryToProduct());
        btnSet.setBounds(453, 337, 70, 25);
        purchasePanel.add(btnSet);
    }

    /**
     * Create the Inventory tab.
     */
    private void createInventoryTab(JTabbedPane tabbedPane) {
        JPanel inventoryPanel = new JPanel();
        tabbedPane.addTab("Inventory & Sales", null, inventoryPanel, null);
        inventoryPanel.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 25, 700, 350);
        inventoryPanel.add(scrollPane);

        inventoryTable = new JTable(inventoryTableModel);
        scrollPane.setViewportView(inventoryTable);

        JLabel lblSelectCategory = new JLabel("Select Category:");
        lblSelectCategory.setBounds(0, 0, 125, 15);
        inventoryPanel.add(lblSelectCategory);

        JComboBox<String> categoryComboBox = new JComboBox<>();
        categoryComboBox.setBounds(125, 0, 125, 24);
        inventoryPanel.add(categoryComboBox);
        connection.updateComboBox(categoryComboBox);

        createInventoryFields(inventoryPanel, categoryComboBox);
    }

    /**
     * Create fields for the Inventory tab.
     */
    private void createInventoryFields(JPanel inventoryPanel, JComboBox<String> categoryComboBox) {
        JLabel lblPIDs = new JLabel("Enter PIDs separated by \",\":");
        lblPIDs.setBounds(10, 387, 200, 15);
        inventoryPanel.add(lblPIDs);

        pidField = new JTextField();
        pidField.setBounds(263, 387, 420, 25);
        inventoryPanel.add(pidField);
        pidField.setColumns(10);

        JLabel lblQuantities = new JLabel("Enter Quantities separated by \",\":");
        lblQuantities.setBounds(0, 414, 240, 15);
        inventoryPanel.add(lblQuantities);

        quantitiesField = new JTextField();
        quantitiesField.setBounds(263, 414, 420, 25);
        inventoryPanel.add(quantitiesField);
        quantitiesField.setColumns(10);

        JButton btnSellProducts = new JButton("Sell Products");
        btnSellProducts.addActionListener(e -> sellProducts());
        btnSellProducts.setBounds(254, 451, 130, 25);
        inventoryPanel.add(btnSellProducts);

        categoryComboBox.addActionListener(e -> filterProductsByCategory(categoryComboBox));
    }

    /**
     * Toggle between Product and Category view.
     */
    private void toggleProductCategoryView(JToggleButton toggleButton) {
        if ("Products".equals(toggleButton.getText())) {
            toggleButton.setText("Categories");
            purchaseTable.setModel(categoriesTableModel);
            connection.getAllCategories(categoriesTableModel);
        } else {
            toggleButton.setText("Products");
            purchaseTable.setModel(productTableModel);
            connection.getAllProducts(productTableModel);
        }
    }

    private void deleteCategory() {
        String id = JOptionPane.showInputDialog("Enter the category ID to delete:");
        try {
            connection.deleteCategory(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        String id = JOptionPane.showInputDialog("Enter the product ID to delete:");
        try {
            connection.deleteProduct(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewCategory() {
        String name = JOptionPane.showInputDialog("Enter the category name to add:");
        connection.insertCategory(name);
    }

    private void openAddProductWindow() {
        AddProductWindow window = new AddProductWindow(connection);
        window.setVisible(true);
    }

    private void purchaseProduct() {
        try {
            int productId = Integer.parseInt(idField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            connection.purchaseProduct(productId, quantity);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter valid values.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignCategoryToProduct() {
        try {
            int productId = Integer.parseInt(productIDField.getText());
            int categoryId = Integer.parseInt(categoryIDField.getText());
            connection.assignCategoryToProduct(productId, categoryId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid IDs", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sellProducts() {
        ArrayList<String> productIds = new ArrayList<>(Arrays.asList(pidField.getText().split(",")));
        ArrayList<String> quantities = new ArrayList<>(Arrays.asList(quantitiesField.getText().split(",")));
        JOptionPane.showMessageDialog(null, connection.sellMultipleProducts(productIds, quantities));
    }

    private void filterProductsByCategory(JComboBox<String> categoryComboBox) {
        String selectedItem = (String) categoryComboBox.getSelectedItem();
        if (selectedItem != null) {
            int categoryId = Integer.parseInt(selectedItem.split("\\|")[0]);
            connection.getProductsByCategory(categoryId, inventoryTableModel);
        }
    }
}
