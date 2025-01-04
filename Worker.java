public class Worker {
    private static final double BASE_FEE = 10.0;
    private static final double WEIGHT_RATE = 0.5;
    private static final double DAYS_RATE = 1.0;

    public double calculateFee(Parcel parcel) {
        double fee = BASE_FEE;
        fee += parcel.getWeight() * WEIGHT_RATE;
        fee += parcel.getDaysInDepot() * DAYS_RATE;
        return fee;
    }

    public void processCustomer(Customer customer, ParcelMap parcelMap, Log log) {
        Parcel parcel = parcelMap.getParcel(customer.getParcelId());
        if (parcel != null) {
            double fee = calculateFee(parcel);
            releaseParcel(parcel);
            log.addEntry(String.format("Customer %s collected parcel %s. Fee: £%.2f",
                    customer.getName(), parcel.getId(), fee));
        }
    }

    private void releaseParcel(Parcel parcel) {
        parcel.setStatus("Released");
        
    }
}