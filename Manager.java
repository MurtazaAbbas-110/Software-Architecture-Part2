import java.io.*;
import java.util.Queue;
import java.util.Scanner;

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

    public void run() {
        boolean running = true;

        while (running) {
            displayMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    processNextCustomer();
                    break;
                case "2":
                    displayCustomerQueue();
                    break;
                case "3":
                    displayParcels();
                    break;
                case "4":
                    displayLog();
                    break;
                case "5":
                    addNewCustomer();
                    break;
                case "6":
                    addNewParcel();
                    break;
                case "7":
                    removeCustomer();
                    break;
                case "8":
                    removeParcel();
                    break;
                case "9":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close(); // Close the Scanner only when the program exits
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

    private void processNextCustomer() {
        System.out.print("Enter parcel ID to process: ");
        String parcelId = scanner.nextLine(); // Use the class-level Scanner instance
    
        // Check if parcel exists
        Parcel parcel = parcelMap.getParcel(parcelId);
        if (parcel == null) {
            System.out.println("Parcel not found");
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
            System.out.println("No customer found with this parcel ID");
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
                        parcel.getStatus() + ",£" + String.format("%.2f", processingFee));
            System.out.println("Details added to released.csv");
        } catch (IOException e) {
            System.err.println("Error updating released file: " + e.getMessage());
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
                writer.write(line + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
    
            inputFile.delete();
            tempFile.renameTo(inputFile);
            log.addEntry("Removed customer from Custs.csv: " + customerToProcess.getName());
        } catch (IOException e) {
            System.err.println("Error updating Custs.csv: " + e.getMessage());
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
                writer.write(line + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
    
            inputFile.delete();
            tempFile.renameTo(inputFile);
            log.addEntry("Removed parcel from Parcels.csv: " + parcelId);
        } catch (IOException e) {
            System.err.println("Error updating Parcels.csv: " + e.getMessage());
            log.addEntry("Error removing parcel from Parcels.csv: " + e.getMessage());
        }
    
        // Remove customer from the in-memory queue
        customerQueue.removeCustomer();
    
        System.out.println("Processed customer: " + customerToProcess.getName() + " with parcel: " + parcelId +
                           ". Fee: £" + String.format("%.2f", processingFee));
    }
    
    
        

    private void displayCustomerQueue() {
        System.out.println("\nCustomer Queue:");
        customerQueue.getQueue().forEach(System.out::println);
    }

    private void displayParcels() {
        System.out.println("\nParcels in Depot:");
        parcelMap.getAllParcels().forEach(System.out::println);
    }

    private void displayLog() {
        System.out.println("\nSystem Log:");
        System.out.println(log.getLog());
    }

    private void addNewCustomer() {
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        System.out.print("Enter parcel ID: ");
        String parcelId = scanner.nextLine();

        // Check if parcel exists
        if (parcelMap.getParcel(parcelId) == null) {
            System.out.println("Error: Parcel ID does not exist.");
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
            log.addEntry("Added new customer: " + name + " with parcel ID: " + parcelId);
        } catch (IOException e) {
            System.err.println("Error updating customer file: " + e.getMessage());
            log.addEntry("Error adding customer to file: " + e.getMessage());
        }
    }

    private void addNewParcel() {
        System.out.print("Enter parcel ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter weight: ");
        double weight = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter dimensions (length width height): ");
        String dimensions = scanner.nextLine();

        Parcel newParcel = new Parcel(id, weight, dimensions);
        parcelMap.addParcel(newParcel);

        try (FileWriter fw = new FileWriter("Parcels.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            String[] dims = dimensions.split(" ");
            out.println(id + "," + weight + "," + String.join(",", dims));
            log.addEntry("Added new parcel: " + id + " with weight: " + weight + " and dimensions: " + dimensions);
        } catch (IOException e) {
            System.err.println("Error updating parcel file: " + e.getMessage());
            log.addEntry("Error adding parcel to file: " + e.getMessage());
        }
    }

    private void removeCustomer() {
        System.out.print("Enter customer name to remove: ");
        String name = scanner.nextLine();
        System.out.print("Enter parcel ID: ");
        String parcelId = scanner.nextLine();

        Parcel parcel = parcelMap.getParcel(parcelId);
        if (parcel != null && !parcel.getStatus().equals("Released")) {
            System.out.println("Cannot remove customer: associated parcel is still in depot");
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
                writer.write(line + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();

            if (removed) {
                inputFile.delete();
                tempFile.renameTo(inputFile);
                log.addEntry("Removed customer: " + name + " with parcel ID: " + parcelId);
            } else {
                tempFile.delete();
                System.out.println("Customer not found");
                log.addEntry("Failed to remove customer: " + name + " - not found in records");
            }
        } catch (IOException e) {
            System.err.println("Error updating customer file: " + e.getMessage());
            log.addEntry("Error removing customer from file: " + e.getMessage());
        }
    }

    private void removeParcel() {
        System.out.print("Enter parcel ID to remove: ");
        String id = scanner.nextLine();

        Parcel parcel = parcelMap.getParcel(id);
        if (parcel == null) {
            System.out.println("Parcel not found");
            log.addEntry("Failed to remove parcel " + id + ": not found");
            return;
        }

        if (!parcel.getStatus().equals("Released")) {
            System.out.println("Cannot remove parcel: still in depot");
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
                writer.write(line + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();

            if (removed) {
                inputFile.delete();
                tempFile.renameTo(inputFile);
                log.addEntry("Removed parcel: " + id);
            } else {
                tempFile.delete();
                System.out.println("Parcel not found in file");
                log.addEntry("Failed to remove parcel " + id + " from file: not found");
            }
        } catch (IOException e) {
            System.err.println("Error updating parcel file: " + e.getMessage());
            log.addEntry("Error removing parcel from file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.loadData();
        manager.run();
    }
}
