package medicalsecure;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.zip.ZipFile;

public final class FileSecurityAnalyzer {
    public static final class Result {
        public final boolean suspicious; public final int score; public final String sha256; public final List<String> indicators;
        Result(boolean s,int score,String h,List<String> i){this.suspicious=s;this.score=score;this.sha256=h;this.indicators=i;}
    }
    private static final String[] BAD={"ransomware","malware","trojan","payload","powershell","cmd.exe","wscript","cscript","eval(","<script","javascript:","base64_decode","dropper","cryptominer"};
    public static Result analyze(Path file) throws Exception {
        byte[] data=Files.readAllBytes(file); MessageDigest md=MessageDigest.getInstance("SHA-256"); String hash=CryptoEngine.hex(md.digest(data));
        String text=extractScanText(file,data).toLowerCase(Locale.ROOT); List<String> hits=new ArrayList<>(); int score=0;
        String name=file.getFileName().toString().toLowerCase(Locale.ROOT);
        for(String k:BAD) if(text.contains(k)){hits.add("Suspicious pattern: "+k);score+=24;}
        if(name.contains("suspicious")||name.contains("threat")||name.contains("malware")){hits.add("Suspicious filename indicator");score+=18;}
        if(data.length==0){hits.add("Empty file");score+=10;}
        if(data.length>50L*1024*1024){hits.add("Oversized file for demo policy");score+=20;}
        if(isExecutableSignature(data)){hits.add("Executable file signature detected");score+=70;}
        score=Math.min(100,score); return new Result(score>=35,score,hash,hits);
    }
    private static String extractScanText(Path file,byte[] data) throws Exception {
        String name=file.getFileName().toString().toLowerCase(Locale.ROOT);
        if(name.endsWith(".docx")||name.endsWith(".xlsx")||name.endsWith(".pptx")){
            StringBuilder s=new StringBuilder(); try(ZipFile z=new ZipFile(file.toFile())){z.stream().filter(e->e.getName().endsWith(".xml")||e.getName().endsWith(".rels")).limit(80).forEach(e->{try(InputStream in=z.getInputStream(e)){s.append(new String(in.readAllBytes(),StandardCharsets.UTF_8)).append('\n');}catch(Exception ignored){}});} return s.toString();
        }
        int limit=Math.min(data.length,2_000_000); StringBuilder s=new StringBuilder(limit);
        for(int i=0;i<limit;i++){int c=data[i]&255;s.append((c>=32&&c<=126)||c=='\n'||c=='\r'||c=='\t'?(char)c:' ');} return s.toString();
    }
    private static boolean isExecutableSignature(byte[] b){if(b.length>=2&&b[0]=='M'&&b[1]=='Z')return true;return b.length>=4&&b[0]==0x7f&&b[1]=='E'&&b[2]=='L'&&b[3]=='F';}
}
