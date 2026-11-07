package medicalsecure;

public class MedicalRecord {
    public final String patientId;
    public final String patientName;
    public final String diagnosis;
    public final String doctor;
    public final String notes;

    public MedicalRecord(String patientId, String patientName, String diagnosis, String doctor, String notes) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.diagnosis = diagnosis;
        this.doctor = doctor;
        this.notes = notes;
    }

    public String toText() {
        return "PATIENT ID: " + patientId + "\nPATIENT: " + patientName + "\nDIAGNOSIS: " + diagnosis
                + "\nDOCTOR: " + doctor + "\nNOTES: " + notes + "\n";
    }
}
