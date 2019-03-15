package mmu.stu.ac.coursework.transport_finder_app.model;

public class TransportLocation {

    private Location location;

    private String country;

    private String city;

    private String timezone;

    private String name;

    private String type;

    public TransportLocation(Location location, String country, String city, String timezone, String name, String type){
        this.location = location;
        this.country = country;
        this.city = city;
        this.timezone = timezone;
        this.name = name;
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
