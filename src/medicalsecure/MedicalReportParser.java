package medicalsecure;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipFile;
import java.io.*;

public final class MedicalReportParser {
    public static final class Report {
        public String patientId="Not detected";
        public String patientName="Not detected";
        public String reportType="Medical document";
        public String department="Not detected";
        public String diagnosis="Not detected";
        public String doctor="Not detected";
        public String date="Not detected";
        public String vitals="Not detected";
        public boolean medicalLike;
    }
    private MedicalReportParser() {}
    public static Report parse(Path file) {
        Report r=new Report();
        try {
            String text=readText(file);
            String low=text.toLowerCase(Locale.ROOT);
            r.patientId=find(text,"patient id");
            if(r.patientId.equals("Not detected")) r.patientId=find(text,"record id");
            r.patientName=find(text,"patient");
            r.reportType=find(text,"report type");
            r.department=find(text,"department");
            r.diagnosis=find(text,"diagnosis");
            r.doctor=find(text,"doctor");
            if(r.doctor.equals("Not detected")) r.doctor=find(text,"physician");
            r.date=find(text,"report date");
            r.vitals=findVitals(text);
            r.medicalLike=low.contains("patient") || low.contains("diagnosis") || low.contains("doctor") || low.contains("medical") || low.contains("hospital") || low.contains("blood pressure") || low.contains("heart rate") || low.contains("spo2");
            if(!r.medicalLike) r.reportType="Document (medical structure not detected)";
        } catch(Exception ignored) {}
        return r;
    }
    private static String readText(Path file) throws Exception {
        String name=file.getFileName().toString().toLowerCase(Locale.ROOT);
        if(name.endsWith(".docx")) {
            StringBuilder s=new StringBuilder();
            try(ZipFile z=new ZipFile(file.toFile())) {
                ZipFile finalZ=z;
                z.stream().filter(e->e.getName().equals("word/document.xml")).forEach(e->{
                    try(InputStream in=finalZ.getInputStream(e)){s.append(new String(in.readAllBytes(),StandardCharsets.UTF_8));}catch(Exception ignored){}
                });
            }
            return s.toString().replaceAll("<[^>]+>"," ").replaceAll("\\s+"," ");
        }
        byte[] b=Files.readAllBytes(file); int limit=Math.min(b.length,1_500_000);
        StringBuilder s=new StringBuilder();
        for(int i=0;i<limit;i++){int c=b[i]&255;if((c>=32&&c<=126)||c=='\n'||c=='\r'||c=='\t')s.append((char)c);else s.append(' ');} return s.toString();
    }
    private static String find(String text,String key){
        String[] lines=text.split("\\R");
        for(String line:lines){String n=line.trim();String l=n.toLowerCase(Locale.ROOT);String k=key.toLowerCase(Locale.ROOT);
            if(l.startsWith(k+":")){String v=n.substring(n.indexOf(':')+1).trim();if(!v.isEmpty())return v;}
            if(l.startsWith(k+" =")){String v=n.substring(n.indexOf('=')+1).trim();if(!v.isEmpty())return v;}
        }
        return "Not detected";
    }
    private static String findVitals(String text){
        String[] keys={"blood pressure","heart rate","spo2","oxygen saturation","temperature"};
        List<String> found=new ArrayList<>(); String[] lines=text.split("\\R");
        for(String line:lines){String l=line.toLowerCase(Locale.ROOT);for(String k:keys)if(l.contains(k)){found.add(line.trim());break;}}
        return found.isEmpty()?"Not detected":String.join("  |  ",found).substring(0,Math.min(110,String.join("  |  ",found).length()));
    }
}
