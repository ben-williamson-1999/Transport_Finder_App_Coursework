package mmu.stu.ac.coursework.transport_finder_app.model;

public class Location {

    private Double longitude;

    private Double latitude;

    public Double getLongitude() {
        return longitude;
    }

    public Location setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Location setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }
}
