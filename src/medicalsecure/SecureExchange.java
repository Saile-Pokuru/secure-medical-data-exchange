package medicalsecure;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;

public final class SecureExchange {
    public final CryptoEngine.ReceiverKeys receiver;
    public CryptoEngine.PackageData packageData;
    public byte[] recoveredBytes;
    public SecureExchange() throws Exception { receiver=CryptoEngine.createReceiver(); }
    public boolean run(byte[] data) throws Exception {
        packageData=CryptoEngine.protect(data,receiver);
        recoveredBytes=CryptoEngine.recover(packageData,receiver);
        return java.util.Arrays.equals(data,recoveredBytes);
    }
    public Path saveEncryptedPackage(Path folder, String originalName) throws Exception {
        Files.createDirectories(folder);
        String safe=originalName.replaceAll("[^A-Za-z0-9._-]","_");
        Path out=folder.resolve("MEDCRYPT_"+safe+"_secure_package.txt");
        String payload="MEDCRYPT SECURE PACKAGE\nTIME="+ LocalDateTime.now()+"\nORIGINAL_FILE="+originalName+"\nORIGINAL_SIZE="+packageData.originalSize+
                "\nALGORITHM=AES-256-GCM + RSA-2048-OAEP\nSHA-256="+packageData.hash+"\nIV="+CryptoEngine.b64(packageData.iv)+
                "\nWRAPPED-KEY="+CryptoEngine.b64(packageData.wrappedKey)+"\nSIGNATURE="+CryptoEngine.b64(packageData.signature)+
                "\nCIPHERTEXT="+CryptoEngine.b64(packageData.cipherText);
        Files.writeString(out,payload,StandardCharsets.UTF_8); return out;
    }
}
