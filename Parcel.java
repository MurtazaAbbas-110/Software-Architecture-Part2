public class Parcel {
    private String id;
    private double weight;
    private String dimensions;
    private int daysInDepot;
    private String status;

    public Parcel(String id, double weight, String dimensions) {
        this.id = id;
        this.weight = weight;
        this.dimensions = dimensions;
        this.daysInDepot = 0;
        this.status = "In Depot";
    }

    public String getId() { return id; }
    public double getWeight() { return weight; }
    public String getDimensions() { return dimensions; }
    public int getDaysInDepot() { return daysInDepot; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public void incrementDaysInDepot() {
        daysInDepot++;
    }

    @Override
    public String toString() {
        return String.format("Parcel[ID=%s, Weight=%.2f, Dimensions=%s, Days=%d, Status=%s]",
                id, weight, dimensions, daysInDepot, status);
    }
}
