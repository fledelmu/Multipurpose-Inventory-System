import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class InventorySystem extends JFrame implements ActionListener {
    private JLabel itemlbl, stocklbl, typelbl, sortlbl, namelbl, amountlbl;
    private JTextField itemField, stockField, typeField, subField, nameField, delField, amountField;
    private JButton confirmBtn, sortBtn, subStockBtn, saveBtn, loadBtn, deleteBtn, addStockBtn;
    private DefaultTableModel tableModel;
    private JPanel inputPanel,tablePanel,btnPanel;

    private JComboBox<String> sortCB;
    private JTable infoTable;

    public InventorySystem() {
        WindowSetup();
    }

    public void WindowSetup() {
        // Window
        setTitle("Inventory System");
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
        setSize(800, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        inputPanel = inputPanelSetup();
        btnPanel = btnPanelSetup();
        tablePanel = tablePanelSetup();

        add(inputPanel);
        add(tablePanel);
        add(btnPanel);


        setVisible(true);
    }

    private JPanel inputPanelSetup(){
        JPanel panel = new JPanel();

        itemlbl = new JLabel("Item name: ");
        panel.add(itemlbl);

        itemField = new JTextField(10);
        panel.add(itemField);

        stocklbl = new JLabel("Stock: ");
        panel.add(stocklbl);

        stockField = new JTextField(10);
        panel.add(stockField);

        typelbl = new JLabel("Type: ");
        panel.add(typelbl);

        typeField = new JTextField(10);
        panel.add(typeField);

        confirmBtn = new JButton("Confirm");
        confirmBtn.setPreferredSize(new Dimension(80, 20));
        confirmBtn.addActionListener(this);
        panel.add(confirmBtn);

        // Save and Load Buttons
        saveBtn = new JButton("Save");
        saveBtn.setPreferredSize(new Dimension(70, 20));
        saveBtn.addActionListener(this);
        panel.add(saveBtn);

        loadBtn = new JButton("Load");
        loadBtn.setPreferredSize(new Dimension(70, 20));
        loadBtn.addActionListener(this);
        panel.add(loadBtn);

        return panel;
    }

    private JPanel tablePanelSetup(){
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Name", "Stock", "Type"};
        tableModel = new DefaultTableModel(columns, 0);
        infoTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(infoTable);
        scrollPane.setPreferredSize(new Dimension(750, 200));
        panel.add(scrollPane);

        return panel;
    }

    private JPanel btnPanelSetup() {
        JPanel finPanel = new JPanel();
        finPanel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel for Item Name and Amount
        JPanel inputPanel = new JPanel(new BorderLayout());

        JPanel lblandField1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namelbl = new JLabel("Item Name: ");
        namelbl.setPreferredSize(new Dimension(80, 20));
        lblandField1.add(namelbl);

        nameField = new JTextField(10);
        lblandField1.add(nameField);
        inputPanel.add(lblandField1, BorderLayout.NORTH);

        JPanel lblandField2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountlbl = new JLabel("Amount: ");
        amountlbl.setPreferredSize(new Dimension(80, 20));
        lblandField2.add(amountlbl);

        amountField = new JTextField(10);
        lblandField2.add(amountField);
        inputPanel.add(lblandField2, BorderLayout.CENTER);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Panel for Add and Subtract Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addStockBtn = new JButton("Add");
        addStockBtn.setPreferredSize(new Dimension(100, 20));
        buttonPanel.add(addStockBtn);
        addStockBtn.addActionListener(this);

        subStockBtn = new JButton("Subtract");
        subStockBtn.setPreferredSize(new Dimension(100, 20));
        buttonPanel.add(subStockBtn);
        subStockBtn.addActionListener(this);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Sort Panel
        JPanel sortdelContainer = new JPanel(new BorderLayout());
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortlbl = new JLabel("Sort by: ");
        sortlbl.setPreferredSize(new Dimension(80,20));
        sortPanel.add(sortlbl);

        sortCB = new JComboBox<>();
        sortCB.setPreferredSize(new Dimension(80, 20));
        sortPanel.add(sortCB);

        sortBtn = new JButton("Sort");
        sortBtn.setPreferredSize(new Dimension(80, 20));
        sortPanel.add(sortBtn);

        sortBtn.addActionListener(this);
        sortdelContainer.add(sortPanel, BorderLayout.NORTH);

        // Delete Panel
        JPanel delPanel = new JPanel();
        JLabel deleteLbl = new JLabel("Remove Item: ");
        delPanel.add(deleteLbl);

        delField = new JTextField();
        delField.setPreferredSize(new Dimension(80, 20));
        delPanel.add(delField);

        deleteBtn = new JButton("Remove");
        deleteBtn.setPreferredSize(new Dimension(80, 20));
        delPanel.add(deleteBtn);
        deleteBtn.addActionListener(this);
        sortdelContainer.add(delPanel, BorderLayout.CENTER);

        finPanel.add(sortdelContainer, BorderLayout.EAST);

        finPanel.add(mainPanel, BorderLayout.WEST);
        return finPanel;
    }

    private void updateSortComboBox () {
        Set<String> types = new HashSet<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            types.add((String) tableModel.getValueAt(row, 2));
        }
        String selectedItem = (String) sortCB.getSelectedItem();
        sortCB.removeAllItems();
        sortCB.addItem("Name");
        for (String type : types) {
            sortCB.addItem(type);
        }
        sortCB.setSelectedItem(selectedItem);
    }

    private void sortTable (String sortBy){
        ArrayList<Object[]> tableData = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object[] rowData = new Object[tableModel.getColumnCount()];
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                rowData[j] = tableModel.getValueAt(i, j);
            }
            tableData.add(rowData);
        }

        Comparator<Object[]> comparator;
        if ("Name".equals(sortBy)) {
            comparator = Comparator.comparing(o -> (String) o[0]);
        } else {
            comparator = (o1, o2) -> {
                String type1 = (String) o1[2];
                String type2 = (String) o2[2];
                if (type1.equals(sortBy) && !type2.equals(sortBy)) {
                    return -1;
                } else if (!type1.equals(sortBy) && type2.equals(sortBy)) {
                    return 1;
                } else {
                    return type1.compareTo(type2);
                }
            };
        }

        tableData.sort(comparator);
        tableModel.setRowCount(0);
        for (Object[] rowData : tableData) {
            tableModel.addRow(rowData);
        }
    }

    private void Save(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String name = (String) tableModel.getValueAt(row, 0);
                int stock = (int) tableModel.getValueAt(row, 1);
                String type = (String) tableModel.getValueAt(row, 2);
                writer.write(name + "," + stock + "," + type);
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Inventory data saved successfully.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving inventory data: " + e.getMessage());
        }
    }

    private void Load(String filename) {
        File file = new File(filename);

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "File not found: " + filename);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            tableModel.setRowCount(0);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    int stock = Integer.parseInt(parts[1].trim());
                    String type = parts[2].trim();
                    tableModel.addRow(new Object[]{name, stock, type});
                }
            }
            updateSortComboBox();
            JOptionPane.showMessageDialog(this, "Inventory data loaded successfully.");
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error loading inventory data: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmBtn) {
            String item = itemField.getText();
            String stock = stockField.getText();
            String type = typeField.getText();

            int amount = Integer.parseInt(stock);

            boolean itemExists = false;
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                if (tableModel.getValueAt(row, 0).equals(item)) {
                    int currentStock = (int) tableModel.getValueAt(row, 1);
                    tableModel.setValueAt(currentStock + amount, row, 1);
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                tableModel.addRow(new Object[]{item, amount, type});
                updateSortComboBox();
            }

            itemField.setText("");
            stockField.setText("");
            typeField.setText("");
        }

        if (e.getSource() == subStockBtn) {
            String itemName = nameField.getText();
            String stock = amountField.getText();

            if (!itemName.isEmpty() && !stock.isEmpty()) {
                try {
                    int amount = Integer.parseInt(stock);

                    boolean itemExists = false;
                    for (int row = 0; row < tableModel.getRowCount(); row++) {
                        if (tableModel.getValueAt(row, 0).equals(itemName)) {
                            int currentStock = (int) tableModel.getValueAt(row, 1);
                            int newStock = currentStock - amount;

                            if (newStock < 0) {
                                JOptionPane.showMessageDialog(this, "Cannot subtract more than available stock.");
                                return;
                            }

                            tableModel.setValueAt(newStock, row, 1);
                            itemExists = true;
                            break;
                        }
                    }

                    if (!itemExists) {
                        JOptionPane.showMessageDialog(this, "Item not found in inventory.");
                    }

                    // Clear input fields
                    nameField.setText("");
                    amountField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid stock amount. Please enter a valid number.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter item name and amount.");
            }
        }

        if (e.getSource() == addStockBtn) {
            String itemName = nameField.getText();
            String stock = amountField.getText();

            if (!itemName.isEmpty() && !stock.isEmpty()) {
                int amount = Integer.parseInt(stock);

                boolean itemExists = false;
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    if (tableModel.getValueAt(row, 0).equals(itemName)) {
                        int currentStock = (int) tableModel.getValueAt(row, 1);
                        tableModel.setValueAt(currentStock + amount, row, 1);
                        itemExists = true;
                        break;
                    }
                }

                if (!itemExists) {
                    JOptionPane.showMessageDialog(this, "Item not found in inventory.");
                }
                nameField.setText("");
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter item name and amount.");
            }
        }

        if (e.getSource() == sortBtn) {
            String sortBy = (String) sortCB.getSelectedItem();
            sortTable(sortBy);
        }

        if (e.getSource() == saveBtn) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String filePath = file.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".txt")) {
                    filePath += ".txt";
                }
                Save(filePath);
            }
        }

        if (e.getSource() == loadBtn) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
            int option = fileChooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                Load(file.getAbsolutePath());
            }
        }

        if (e.getSource() == deleteBtn) {
            String itemNameToDelete = delField.getText().trim();

            if (!itemNameToDelete.isEmpty()) {
                boolean itemFound = false;

                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    String itemName = (String) tableModel.getValueAt(row, 0);
                    if (itemName.equals(itemNameToDelete)) {
                        tableModel.removeRow(row);
                        updateSortComboBox();
                        itemFound = true;
                        break;
                    }
                }

                if (!itemFound) {
                    JOptionPane.showMessageDialog(this, "Item not found in the inventory.");
                } else {
                    JOptionPane.showMessageDialog(this, "Item successfully removed from the inventory.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter the item name to remove.");
            }

            delField.setText("");
        }
    }
}
