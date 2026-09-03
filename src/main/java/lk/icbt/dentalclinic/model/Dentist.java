package lk.icbt.dentalclinic.model;

public class Dentist {

    private int dentistId;
    private String name;
    private String specialization;

    public Dentist() {
    }

    public Dentist(int dentistId, String name, String specialization) {
        this.dentistId = dentistId;
        this.name = name;
        this.specialization = specialization;
    }

    public int getDentistId() {
        return dentistId;
    }

    public void setDentistId(int dentistId) {
        this.dentistId = dentistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDentistInfo() {
        return "Dr. " + name + " (" + specialization + ")";
    }

    @Override
    public String toString() {
        return getDentistInfo();
    }
}