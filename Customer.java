public class Customer {
    private String name;
    private String parcelId;
    private int sequenceNumber;

    public Customer(String name, String parcelId, int sequenceNumber) {
        this.name = name;
        this.parcelId = parcelId;
        this.sequenceNumber = sequenceNumber;
    }

    public String getName() { return name; }
    public String getParcelId() { return parcelId; }
    public int getSequenceNumber() { return sequenceNumber; }

    @Override
    public String toString() {
        return String.format("Customer[Name=%s, ParcelID=%s, Sequence=%d]",
                name, parcelId, sequenceNumber);
    }
}