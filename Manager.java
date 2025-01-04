import java.io.*;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Manager {
    private QueueOfCustomers customerQueue;
    private ParcelMap parcelMap;
    private Worker worker;
    private Log log;
    private Scanner scanner; // Declare a single Scanner object

    public Manager() {
        customerQueue = new QueueOfCustomers();
        parcelMap = new ParcelMap();
        worker = new Worker();
        log = Log.getInstance();
        scanner = new Scanner(System.in); // Initialize the Scanner object
    }

    public void loadData() {
        loadParcels("Parcels.csv");
        loadCustomers("Custs.csv");
    }

    private void loadParcels(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 4) {
                    String id = data[0].trim();
                    double weight = Double.parseDouble(data[1].trim());
                    String dimensions = data[2].trim() + "x" + data[3].trim() + "x" + data[4].trim();
                    Parcel parcel = new Parcel(id, weight, dimensions);
                    parcelMap.addParcel(parcel);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading parcels: " + e.getMessage());
        }
    }

    private void loadCustomers(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;
            int sequence = 1;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String name = data[0].trim();
                    String parcelId = data[1].trim();
                    Customer customer = new Customer(name, parcelId, sequence++);
                    customerQueue.addCustomer(customer);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    public void runApplication(JTextArea displayArea) {
    boolean running = true;

    while (running) {
        displayMenu();
        String choice = JOptionPane.showInputDialog("Enter your choice:");

        if (choice == null || choice.trim().isEmpty()) {
            displayArea.append("Invalid choice. Please try again.\n");
            continue;
        }

        switch (choice) {
            case "1":
                String parcelId = JOptionPane.showInputDialog("Enter Parcel ID to process:");
                if (parcelId != null && !parcelId.trim().isEmpty()) {
                    processNextCustomer(parcelId, displayArea);
                } else {
                    displayArea.append("Invalid Parcel ID.\n");
                }
                break;
            case "2":
                displayCustomerQueue(displayArea);
                break;
            case "3":
                displayParcels(displayArea);
                break;
            case "4":
                displayLog(displayArea);
                break;
            case "5":
                String name = JOptionPane.showInputDialog("Enter Customer Name:");
                String parcelIdForCustomer = JOptionPane.showInputDialog("Enter Parcel ID:");
                if (name != null && parcelIdForCustomer != null && !name.trim().isEmpty() && !parcelIdForCustomer.trim().isEmpty()) {
                    addNewCustomer(name, parcelIdForCustomer, displayArea);
                } else {
                    displayArea.append("Invalid Customer Name or Parcel ID.\n");
                }
                break;
            case "6":
                String newParcelId = JOptionPane.showInputDialog("Enter Parcel ID:");
                String weightStr = JOptionPane.showInputDialog("Enter Parcel Weight:");
                String dimensions = JOptionPane.showInputDialog("Enter Parcel Dimensions (LxWxH):");
                try {
                    double weight = Double.parseDouble(weightStr);
                    if (newParcelId != null && dimensions != null && !newParcelId.trim().isEmpty() && !dimensions.trim().isEmpty()) {
                        addNewParcel(newParcelId, weight, dimensions, displayArea);
                    } else {
                        displayArea.append("Invalid Parcel ID or Dimensions.\n");
                    }
                } catch (NumberFormatException ex) {
                    displayArea.append("Invalid weight input.\n");
                }
                break;
            case "7":
                String customerNameToRemove = JOptionPane.showInputDialog("Enter Customer Name:");
                String parcelIdToRemove = JOptionPane.showInputDialog("Enter Parcel ID:");
                if (customerNameToRemove != null && parcelIdToRemove != null && !customerNameToRemove.trim().isEmpty() && !parcelIdToRemove.trim().isEmpty()) {
                    removeCustomer(customerNameToRemove, parcelIdToRemove, displayArea);
                } else {
                    displayArea.append("Invalid Customer Name or Parcel ID.\n");
                }
                break;
            case "8":
                String parcelIdToRemoveParcel = JOptionPane.showInputDialog("Enter Parcel ID to remove:");
                if (parcelIdToRemoveParcel != null && !parcelIdToRemoveParcel.trim().isEmpty()) {
                    removeParcel(parcelIdToRemoveParcel, displayArea);
                } else {
                    displayArea.append("Invalid Parcel ID.\n");
                }
                break;
            case "9":
                running = false;
                displayArea.append("Exiting application.\n");
                break;
            default:
                displayArea.append("Invalid choice. Please try again.\n");
        }
    }
}

    private void displayMenu() {
        System.out.println("\n=== Depot Parcel Processing System ===");
        System.out.println("1. Process next customer");
        System.out.println("2. Display customer queue");
        System.out.println("3. Display all parcels");
        System.out.println("4. Display log");
        System.out.println("5. Add new customer");
        System.out.println("6. Add new parcel");
        System.out.println("7. Remove customer");
        System.out.println("8. Remove parcel");
        System.out.println("9. Exit");
        System.out.print("Enter your choice: ");
    }

    public void processNextCustomer(String parcelId, JTextArea displayArea) {
    if (parcelId == null || parcelId.trim().isEmpty()) {
        displayArea.append("Parcel ID cannot be empty.\n");
        return;
    }

    // Check if parcel exists
    Parcel parcel = parcelMap.getParcel(parcelId);
    if (parcel == null) {
        displayArea.append("Parcel not found: " + parcelId + "\n");
        log.addEntry("Failed to process parcel: " + parcelId + " - not found");
        return;
    }

    // Check if customer exists with this parcel
    Queue<Customer> queue = customerQueue.getQueue();
    Customer customerToProcess = null;

    for (Customer customer : queue) {
        if (customer.getParcelId().equals(parcelId)) {
            customerToProcess = customer;
            break;
        }
    }

    if (customerToProcess == null) {
        displayArea.append("No customer found with this parcel ID: " + parcelId + "\n");
        log.addEntry("Failed to process parcel: " + parcelId + " - no customer found");
        return;
    }

    // Calculate the fee using the Worker class
    double processingFee = worker.calculateFee(parcel);

    // Process the customer using the Worker class
    worker.processCustomer(customerToProcess, parcelMap, log);

    // Add details to released.csv
    try (FileWriter fw = new FileWriter("released.csv", true);
         BufferedWriter bw = new BufferedWriter(fw);
         PrintWriter out = new PrintWriter(bw)) {

        out.println(customerToProcess.getName() + "," + customerToProcess.getParcelId() + "," +
                    parcel.getWeight() + "," + parcel.getDimensions() + "," +
                    parcel.getStatus() + ",\u00A3" + String.format("%.2f", processingFee));
        displayArea.append("Details added to released.csv\n");
    } catch (IOException e) {
        displayArea.append("Error updating released file: " + e.getMessage() + "\n");
        log.addEntry("Error adding release details to released.csv: " + e.getMessage());
    }

    // Remove from Custs.csv
    try {
        File inputFile = new File("Custs.csv");
        File tempFile = new File("Custs_temp.csv");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length >= 2 && data[0].trim().equals(customerToProcess.getName()) && data[1].trim().equals(parcelId)) {
                continue; // Skip the line for the processed customer
            }
            writer.write(line + System.lineSeparator());
        }
        writer.close();
        reader.close();

        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
            log.addEntry("Removed customer from Custs.csv: " + customerToProcess.getName());
        } else {
            displayArea.append("Error updating Custs.csv.\n");
        }
    } catch (IOException e) {
        displayArea.append("Error updating Custs.csv: " + e.getMessage() + "\n");
        log.addEntry("Error removing customer from Custs.csv: " + e.getMessage());
    }

    // Remove from Parcels.csv
    try {
        File inputFile = new File("Parcels.csv");
        File tempFile = new File("Parcels_temp.csv");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length >= 1 && data[0].trim().equals(parcelId)) {
                continue; // Skip the line for the released parcel
            }
            writer.write(line + System.lineSeparator());
        }
        writer.close();
        reader.close();

        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
            log.addEntry("Removed parcel from Parcels.csv: " + parcelId);
        } else {
            displayArea.append("Error updating Parcels.csv.\n");
        }
    } catch (IOException e) {
        displayArea.append("Error updating Parcels.csv: " + e.getMessage() + "\n");
        log.addEntry("Error removing parcel from Parcels.csv: " + e.getMessage());
    }

    // Remove customer from the in-memory queue
    customerQueue.removeCustomer();

    displayArea.append("Processed customer: " + customerToProcess.getName() + " with parcel: " + parcelId +
                       ". Fee: \u00A3" + String.format("%.2f", processingFee) + "\n");
}

    
    
        

public void displayCustomerQueue(JTextArea displayArea) {
    displayArea.append("\nCustomer Queue:\n");
    customerQueue.getQueue().forEach(customer -> displayArea.append(customer.toString() + "\n"));
}

public void displayParcels(JTextArea displayArea) {
    displayArea.append("\nParcels in Depot:\n");
    parcelMap.getAllParcels().forEach(parcel -> displayArea.append(parcel.toString() + "\n"));
}

public void displayLog(JTextArea displayArea) {
    displayArea.append("\nSystem Log:\n");
    displayArea.append(log.getLog() + "\n");
}

    public void addNewCustomer(String name, String parcelId, JTextArea displayArea) {
        if (name == null || name.trim().isEmpty() || parcelId == null || parcelId.trim().isEmpty()) {
            displayArea.append("Error: Customer name and parcel ID cannot be empty.\n");
            return;
        }
    
        // Check if parcel exists
        if (parcelMap.getParcel(parcelId) == null) {
            displayArea.append("Error: Parcel ID does not exist.\n");
            log.addEntry("Failed to add customer " + name + ": Parcel ID " + parcelId + " not found");
            return;
        }
    
        int sequence = customerQueue.size() + 1;
        Customer newCustomer = new Customer(name, parcelId, sequence);
        customerQueue.addCustomer(newCustomer);
    
        try (FileWriter fw = new FileWriter("Custs.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
    
            out.println(name + "," + parcelId);
            displayArea.append("Customer added successfully: " + name + " with parcel ID: " + parcelId + "\n");
            log.addEntry("Added new customer: " + name + " with parcel ID: " + parcelId);
        } catch (IOException e) {
            displayArea.append("Error updating customer file: " + e.getMessage() + "\n");
            log.addEntry("Error adding customer to file: " + e.getMessage());
        }
    }
    

    public void addNewParcel(String id, double weight, String dimensions, JTextArea displayArea) {
        if (id == null || id.trim().isEmpty() || dimensions == null || dimensions.trim().isEmpty()) {
            displayArea.append("Error: Parcel ID and dimensions cannot be empty.\n");
            return;
        }
    
        Parcel newParcel = new Parcel(id, weight, dimensions);
        parcelMap.addParcel(newParcel);
    
        try (FileWriter fw = new FileWriter("Parcels.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
    
            String[] dims = dimensions.split(" ");
            out.println(id + "," + weight + "," + String.join(",", dims));
            displayArea.append("Parcel added successfully: " + id + " with weight: " + weight + " and dimensions: " + dimensions + "\n");
            log.addEntry("Added new parcel: " + id + " with weight: " + weight + " and dimensions: " + dimensions);
        } catch (IOException e) {
            displayArea.append("Error updating parcel file: " + e.getMessage() + "\n");
            log.addEntry("Error adding parcel to file: " + e.getMessage());
        }
    }
    

    public void removeCustomer(String name, String parcelId, JTextArea displayArea) {
        if (name == null || name.trim().isEmpty() || parcelId == null || parcelId.trim().isEmpty()) {
            displayArea.append("Error: Customer name and parcel ID cannot be empty.\n");
            return;
        }
    
        Parcel parcel = parcelMap.getParcel(parcelId);
        if (parcel != null && !parcel.getStatus().equals("Released")) {
            displayArea.append("Cannot remove customer: associated parcel is still in depot\n");
            log.addEntry("Failed to remove customer " + name + ": Parcel " + parcelId + " is still in depot");
            return;
        }
    
        try {
            File inputFile = new File("Custs.csv");
            File tempFile = new File("Custs_temp.csv");
    
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    
            String line;
            boolean removed = false;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].trim().equals(name) && data[1].trim().equals(parcelId)) {
                    removed = true;
                    continue;
                }
                writer.write(line + System.lineSeparator());
            }
            writer.close();
            reader.close();
    
            if (removed) {
                if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                    displayArea.append("Customer removed successfully: " + name + " with parcel ID: " + parcelId + "\n");
                    log.addEntry("Removed customer: " + name + " with parcel ID: " + parcelId);
                } else {
                    displayArea.append("Error finalizing customer removal.\n");
                }
            } else {
                tempFile.delete();
                displayArea.append("Customer not found: " + name + " with parcel ID: " + parcelId + "\n");
                log.addEntry("Failed to remove customer: " + name + " - not found in records");
            }
        } catch (IOException e) {
            displayArea.append("Error updating customer file: " + e.getMessage() + "\n");
            log.addEntry("Error removing customer from file: " + e.getMessage());
        }
    }
    

    public void removeParcel(String id, JTextArea displayArea) {
        if (id == null || id.trim().isEmpty()) {
            displayArea.append("Error: Parcel ID cannot be empty.\n");
            return;
        }
    
        Parcel parcel = parcelMap.getParcel(id);
        if (parcel == null) {
            displayArea.append("Parcel not found: " + id + "\n");
            log.addEntry("Failed to remove parcel " + id + ": not found");
            return;
        }
    
        if (!parcel.getStatus().equals("Released")) {
            displayArea.append("Cannot remove parcel: still in depot\n");
            log.addEntry("Failed to remove parcel " + id + ": still in depot");
            return;
        }
    
        try {
            File inputFile = new File("Parcels.csv");
            File tempFile = new File("Parcels_temp.csv");
    
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
    
            String line;
            boolean removed = false;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 1 && data[0].trim().equals(id)) {
                    removed = true;
                    continue;
                }
                writer.write(line + System.lineSeparator());
            }
            writer.close();
            reader.close();
    
            if (removed) {
                if (inputFile.delete() && tempFile.renameTo(inputFile)) {
                    displayArea.append("Parcel removed successfully: " + id + "\n");
                    log.addEntry("Removed parcel: " + id);
                } else {
                    displayArea.append("Error finalizing parcel removal.\n");
                }
            } else {
                tempFile.delete();
                displayArea.append("Parcel not found in file: " + id + "\n");
                log.addEntry("Failed to remove parcel " + id + " from file: not found");
            }
        } catch (IOException e) {
            displayArea.append("Error updating parcel file: " + e.getMessage() + "\n");
            log.addEntry("Error removing parcel from file: " + e.getMessage());
        }
    }
    

    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.loadData();
    
        // Create a JTextArea for displaying logs and output
        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
    
        // Run the application
        manager.runApplication(displayArea);
    }
    
}
