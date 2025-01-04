import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class ParcelMap {
    private Map<String, Parcel> parcels;

    public ParcelMap() {
        parcels = new HashMap<>();
    }

    public void addParcel(Parcel parcel) {
        parcels.put(parcel.getId(), parcel);
    }

    public Parcel getParcel(String id) {
        return parcels.get(id);
    }

    public Collection<Parcel> getAllParcels() {
        return parcels.values();
    }
}