import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Manager {
    private QueueOfCustomers customerQueue;
    private ParcelMap parcelMap;
    private Worker worker;
    private Log log;

    public Manager() {
        customerQueue = new QueueOfCustomers();
        parcelMap = new ParcelMap();
        worker = new Worker();
        log = new Log();
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
        Scanner scanner = new Scanner(System.in);
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
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n=== Depot Parcel Processing System ===");
        System.out.println("1. Process next customer");
        System.out.println("2. Display customer queue");
        System.out.println("3. Display all parcels");
        System.out.println("4. Display log");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private void processNextCustomer() {
        if (customerQueue.isEmpty()) {
            System.out.println("No customers in queue.");
            return;
        }

        Customer customer = customerQueue.removeCustomer();
        worker.processCustomer(customer, parcelMap, log);
        System.out.println("Processed customer: " + customer.getName());
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

    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.loadData();
        manager.run();
    }
}