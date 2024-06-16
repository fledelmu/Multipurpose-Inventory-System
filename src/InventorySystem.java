import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class InventorySystem extends JFrame implements ActionListener {
    private JLabel itemlbl, stocklbl, typelbl, sortlbl, sublbl, namelbl;
    private JTextField itemField, stockField, typeField, subField, nameField;
    private JButton confirmBtn, sortBtn, subStockBtn, saveBtn, loadBtn;
    private DefaultTableModel tableModel;

    private JComboBox<String> sortCB; // You stopped at making the combobox for the sorting functionality
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

        // Adding items group
        itemlbl = new JLabel("Item name: ");
        add(itemlbl);

        itemField = new JTextField(10);
        add(itemField);

        stocklbl = new JLabel("Stock: ");
        add(stocklbl);

        stockField = new JTextField(10);
        add(stockField);

        typelbl = new JLabel("Type: ");
        add(typelbl);

        typeField = new JTextField(10);
        add(typeField);

        // Confirm Button
        confirmBtn = new JButton("Confirm");
        confirmBtn.setPreferredSize(new Dimension(80, 20));
        confirmBtn.addActionListener(this);
        add(confirmBtn);

        // Save and Load Group
        saveBtn = new JButton("Save");
        saveBtn.setPreferredSize(new Dimension(70, 20));
        saveBtn.addActionListener(this);
        add(saveBtn);

        loadBtn = new JButton("Load");
        loadBtn.setPreferredSize(new Dimension(70, 20));
        loadBtn.addActionListener(this);
        add(loadBtn);

        // Table
        String[] columns = {"Name", "Stock", "Type"};
        tableModel = new DefaultTableModel(columns, 0);
        infoTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(infoTable);
        scrollPane.setPreferredSize(new Dimension(750, 200));
        add(scrollPane);

        // Subtract Stock Group
        namelbl = new JLabel("Item Name: ");
        add(namelbl);

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(80, 20));
        add(nameField);

        sublbl = new JLabel("Amount: ");
        add(sublbl);

        subField = new JTextField();
        subField.setPreferredSize(new Dimension(80, 20));
        add(subField);

        subStockBtn = new JButton("Subtract");
        subStockBtn.setPreferredSize(new Dimension(100, 20));
        add(subStockBtn);
        subStockBtn.addActionListener(this);

        // Sort group
        sortlbl = new JLabel("Sort by: ");
        add(sortlbl);

        sortCB = new JComboBox<>();
        sortCB.setPreferredSize(new Dimension(70, 20));
        add(sortCB);

        sortBtn = new JButton("Sort");
        sortBtn.setPreferredSize(new Dimension(80, 20));
        add(sortBtn);
        sortBtn.addActionListener(this);


        setVisible(true);
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
            // Clear existing table data
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
            String name = nameField.getText();
            String stock = subField.getText();

            int amount = Integer.parseInt(stock);

            boolean itemExists = false;
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                if (tableModel.getValueAt(row, 0).equals(name)) {
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
                JOptionPane.showMessageDialog(this, "Item not found.");
            }

            nameField.setText("");
            subField.setText("");
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
    }
}