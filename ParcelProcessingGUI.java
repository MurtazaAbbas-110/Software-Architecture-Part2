import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParcelProcessingGUI {

    private Manager manager;

    public ParcelProcessingGUI() {
        manager = new Manager();
        manager.loadData();
        initializeWelcomeGUI();
    }

    private void initializeWelcomeGUI() {
        JFrame welcomeFrame = new JFrame("Welcome to Depot Parcel Processing System");
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setSize(600, 400);
        welcomeFrame.setLayout(new BorderLayout(10, 10));

        JLabel welcomeLabel = new JLabel("Welcome to Depot Parcel Processing System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JTextArea instructions = new JTextArea("Instructions:\n- Use the dashboard to manage customers and parcels.\n- Click on the respective buttons for actions.\n- Details will update in real-time.");
        instructions.setFont(new Font("Arial", Font.PLAIN, 14));
        instructions.setEditable(false);
        instructions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton proceedButton = new JButton("Proceed to Dashboard");
        proceedButton.addActionListener(e -> {
            welcomeFrame.dispose();
            initializeMainGUI();
        });

        welcomeFrame.add(welcomeLabel, BorderLayout.NORTH);
        welcomeFrame.add(instructions, BorderLayout.CENTER);
        welcomeFrame.add(proceedButton, BorderLayout.SOUTH);

        welcomeFrame.setVisible(true);
    }

    private void initializeMainGUI() {
        // Main Frame
        JFrame frame = new JFrame("Depot Parcel Processing System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Depot Parcel Processing System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Dashboard Panel (Buttons)
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 5, 15, 15));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton processCustomerBtn = new JButton("Process Customer");
        JButton addCustomerBtn = new JButton("Add Customer");
        JButton removeCustomerBtn = new JButton("Remove Customer");
        JButton addParcelBtn = new JButton("Add Parcel");
        JButton removeParcelBtn = new JButton("Remove Parcel");
        JButton displayCustomersBtn = new JButton("Display Customers");
        JButton displayParcelsBtn = new JButton("Display Parcels");
        JButton displayLogBtn = new JButton("Display Log");
        JButton exitBtn = new JButton("Exit");

        dashboardPanel.add(processCustomerBtn);
        dashboardPanel.add(addCustomerBtn);
        dashboardPanel.add(removeCustomerBtn);
        dashboardPanel.add(addParcelBtn);
        dashboardPanel.add(removeParcelBtn);
        dashboardPanel.add(displayCustomersBtn);
        dashboardPanel.add(displayParcelsBtn);
        dashboardPanel.add(displayLogBtn);
        dashboardPanel.add(exitBtn);

        frame.add(dashboardPanel, BorderLayout.CENTER);

        // Real-time Status Panel
        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Current Status"));

        JTextArea currentParcelArea = new JTextArea("Current Parcel: None");
        currentParcelArea.setFont(new Font("Arial", Font.PLAIN, 14));
        currentParcelArea.setEditable(false);

        JTextArea customerQueueArea = new JTextArea("Customer Queue:\nNo customers in queue.");
        customerQueueArea.setFont(new Font("Arial", Font.PLAIN, 14));
        customerQueueArea.setEditable(false);

        JTextArea parcelQueueArea = new JTextArea("Parcel Queue:\nNo parcels in queue.");
        parcelQueueArea.setFont(new Font("Arial", Font.PLAIN, 14));
        parcelQueueArea.setEditable(false);

        statusPanel.add(new JScrollPane(currentParcelArea));
        statusPanel.add(new JScrollPane(customerQueueArea));
        statusPanel.add(new JScrollPane(parcelQueueArea));

        frame.add(statusPanel, BorderLayout.SOUTH);

        // Button Action Listeners
        processCustomerBtn.addActionListener(e -> {
            openProcessCustomerWindow(currentParcelArea, customerQueueArea, parcelQueueArea);
        });
        addCustomerBtn.addActionListener(e -> openAddCustomerWindow());
        removeCustomerBtn.addActionListener(e -> openRemoveCustomerWindow());
        addParcelBtn.addActionListener(e -> openAddParcelWindow());
        removeParcelBtn.addActionListener(e -> openRemoveParcelWindow());
        displayCustomersBtn.addActionListener(e -> openDisplayCustomersWindow());
        displayParcelsBtn.addActionListener(e -> openDisplayParcelsWindow());
        displayLogBtn.addActionListener(e -> openDisplayLogWindow());
        exitBtn.addActionListener(e -> System.exit(0));

        // Apply Nimbus Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            System.err.println("Failed to apply Nimbus Look and Feel.");
        }

        frame.setVisible(true);
    }

    private void openProcessCustomerWindow(JTextArea currentParcelArea, JTextArea customerQueueArea, JTextArea parcelQueueArea) {
        JFrame processWindow = new JFrame("Process Customer");
        processWindow.setSize(400, 300);
        processWindow.setLayout(new BorderLayout(10, 10));

        JLabel instructionLabel = new JLabel("Enter Parcel ID to Process:", JLabel.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField parcelIdField = new JTextField();
        JButton processBtn = new JButton("Process");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        processBtn.addActionListener(e -> {
            String parcelId = parcelIdField.getText();
            if (parcelId != null && !parcelId.trim().isEmpty()) {
                manager.processNextCustomer(parcelId, resultArea);
                currentParcelArea.setText("Current Parcel: " + parcelId);
                customerQueueArea.setText("Customer Queue:\n" + manager.getCustomerQueueStatus());
                parcelQueueArea.setText("Parcel Queue:\n" + manager.getParcelQueueStatus());
            } else {
                resultArea.append("Invalid Parcel ID.\n");
            }
        });

        processWindow.add(instructionLabel, BorderLayout.NORTH);
        processWindow.add(parcelIdField, BorderLayout.CENTER);
        processWindow.add(processBtn, BorderLayout.EAST);
        processWindow.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        processWindow.setVisible(true);
    }

    private void openAddCustomerWindow() {
        JFrame addCustomerWindow = new JFrame("Add Customer");
        addCustomerWindow.setSize(400, 300);
        JPanel contentPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField parcelIdField = new JTextField();
        JButton addBtn = new JButton("Add Customer");

        addBtn.addActionListener(e -> {
            String name = nameField.getText();
            String parcelId = parcelIdField.getText();
            JTextArea resultArea = new JTextArea();

            if (name != null && parcelId != null && !name.trim().isEmpty() && !parcelId.trim().isEmpty()) {
                manager.addNewCustomer(name, parcelId, resultArea);
                JOptionPane.showMessageDialog(addCustomerWindow, "Customer Added Successfully.");
            } else {
                JOptionPane.showMessageDialog(addCustomerWindow, "Invalid Inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.add(new JLabel("Customer Name:"));
        contentPanel.add(nameField);
        contentPanel.add(new JLabel("Parcel ID:"));
        contentPanel.add(parcelIdField);
        contentPanel.add(addBtn);

        addCustomerWindow.add(contentPanel);
        addCustomerWindow.setVisible(true);
    }

    private void openRemoveCustomerWindow() {
        JFrame removeCustomerWindow = new JFrame("Remove Customer");
        removeCustomerWindow.setSize(400, 300);
        JPanel contentPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField parcelIdField = new JTextField();
        JButton removeBtn = new JButton("Remove Customer");

        removeBtn.addActionListener(e -> {
            String name = nameField.getText();
            String parcelId = parcelIdField.getText();
            JTextArea resultArea = new JTextArea();

            if (name != null && parcelId != null && !name.trim().isEmpty() && !parcelId.trim().isEmpty()) {
                manager.removeCustomer(name, parcelId, resultArea);
                JOptionPane.showMessageDialog(removeCustomerWindow, "Customer Removed Successfully.");
            } else {
                JOptionPane.showMessageDialog(removeCustomerWindow, "Invalid Inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.add(new JLabel("Customer Name:"));
        contentPanel.add(nameField);
        contentPanel.add(new JLabel("Parcel ID:"));
        contentPanel.add(parcelIdField);
        contentPanel.add(removeBtn);

        removeCustomerWindow.add(contentPanel);
        removeCustomerWindow.setVisible(true);
    }

    private void openAddParcelWindow() {
        JFrame addParcelWindow = new JFrame("Add Parcel");
        addParcelWindow.setSize(400, 300);
        JPanel contentPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField parcelIdField = new JTextField();
        JLabel weightLabel = new JLabel("Enter Weight:");
        JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.1));
        JTextField dimensionsField = new JTextField();
        JButton addBtn = new JButton("Add Parcel");

        addBtn.addActionListener(e -> {
            try {
                String parcelId = parcelIdField.getText();
                double weight = (double) weightSpinner.getValue();
                String dimensions = dimensionsField.getText();
                JTextArea resultArea = new JTextArea();

                if (!parcelId.trim().isEmpty() && !dimensions.trim().isEmpty()) {
                    manager.addNewParcel(parcelId, weight, dimensions, resultArea);
                    JOptionPane.showMessageDialog(addParcelWindow, "Parcel Added Successfully.");
                } else {
                    JOptionPane.showMessageDialog(addParcelWindow, "Invalid Inputs.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addParcelWindow, "Invalid Input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.add(new JLabel("Parcel ID:"));
        contentPanel.add(parcelIdField);
        contentPanel.add(weightLabel);
        contentPanel.add(weightSpinner);
        contentPanel.add(new JLabel("Dimensions (LxWxH):"));
        contentPanel.add(dimensionsField);
        contentPanel.add(addBtn);

        addParcelWindow.add(contentPanel);
        addParcelWindow.setVisible(true);
    }

    private void openRemoveParcelWindow() {
        JFrame removeParcelWindow = new JFrame("Remove Parcel");
        removeParcelWindow.setSize(400, 300);
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField parcelIdField = new JTextField();
        JButton removeBtn = new JButton("Remove Parcel");

        removeBtn.addActionListener(e -> {
            String parcelId = parcelIdField.getText();
            JTextArea resultArea = new JTextArea();

            if (parcelId != null && !parcelId.trim().isEmpty()) {
                manager.removeParcel(parcelId, resultArea);
                JOptionPane.showMessageDialog(removeParcelWindow, "Parcel Removed Successfully.");
            } else {
                JOptionPane.showMessageDialog(removeParcelWindow, "Invalid Inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.add(new JLabel("Parcel ID:"));
        contentPanel.add(parcelIdField);
        contentPanel.add(removeBtn);

        removeParcelWindow.add(contentPanel);
        removeParcelWindow.setVisible(true);
    }

    private void openDisplayCustomersWindow() {
        JFrame displayCustomersWindow = new JFrame("Customer Queue");
        displayCustomersWindow.setSize(400, 300);
        JTextArea customersArea = new JTextArea();
        customersArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(customersArea);

        manager.displayCustomerQueue(customersArea);

        displayCustomersWindow.add(scrollPane);
        displayCustomersWindow.setVisible(true);
    }

    private void openDisplayParcelsWindow() {
        JFrame displayParcelsWindow = new JFrame("Parcels in Depot");
        displayParcelsWindow.setSize(400, 300);
        JTextArea parcelsArea = new JTextArea();
        parcelsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(parcelsArea);

        manager.displayParcels(parcelsArea);

        displayParcelsWindow.add(scrollPane);
        displayParcelsWindow.setVisible(true);
    }

    private void openDisplayLogWindow() {
        JFrame displayLogWindow = new JFrame("System Log");
        displayLogWindow.setSize(400, 300);
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        manager.displayLog(logArea);

        displayLogWindow.add(scrollPane);
        displayLogWindow.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParcelProcessingGUI::new);
    }
}
