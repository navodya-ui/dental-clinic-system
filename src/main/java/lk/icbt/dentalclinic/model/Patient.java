package lk.icbt.dentalclinic.model;

public class Patient {

    private int patientId;
    private String name;
    private String address;
    private String contactNumber;

    public Patient() {
    }

    public Patient(String name, String address, String contactNumber) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public Patient(int patientId, String name, String address, String contactNumber) {
        this.patientId = patientId;
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPatientDetails() {
        return "Patient{id=" + patientId + ", name='" + name + "', address='" + address
                + "', contact='" + contactNumber + "'}";
    }

    public void updateDetails(String address, String contactNumber) {
        this.address = address;
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return getPatientDetails();
    }
}