package mmu.stu.ac.coursework.transport_finder_app.model;

public class Location {

    private String longitude;

    private String latitude;

    public String getLongitude() {
        return longitude;
    }

    public Location setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getLatitude() {
        return latitude;
    }

    public Location setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }
}
