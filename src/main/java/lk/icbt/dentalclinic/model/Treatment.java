package lk.icbt.dentalclinic.model;

import java.math.BigDecimal;

public class Treatment {

    private int treatmentId;
    private String treatmentType;
    private BigDecimal consultationFee;

    public Treatment() {
    }

    public Treatment(int treatmentId, String treatmentType, BigDecimal consultationFee) {
        this.treatmentId = treatmentId;
        this.treatmentType = treatmentType;
        this.consultationFee = consultationFee;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public BigDecimal getFee() {
        return consultationFee;
    }

    public String getTreatmentInfo() {
        return treatmentType + " - Rs. " + consultationFee;
    }

    @Override
    public String toString() {
        return getTreatmentInfo();
    }
}