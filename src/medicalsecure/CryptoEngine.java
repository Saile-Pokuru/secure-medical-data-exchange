package medicalsecure;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public final class CryptoEngine {
    private CryptoEngine() {}
    public static final class PackageData {
        public byte[] cipherText, iv, wrappedKey, signature;
        public String hash;
        public long originalSize;
    }
    public static final class ReceiverKeys {
        public final KeyPair rsa;
        public ReceiverKeys(KeyPair rsa) { this.rsa = rsa; }
    }
    public static ReceiverKeys createReceiver() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048, SecureRandom.getInstanceStrong());
        return new ReceiverKeys(kpg.generateKeyPair());
    }
    public static PackageData protect(byte[] plain, ReceiverKeys receiver) throws Exception {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] iv = new byte[12]; random.nextBytes(iv);
        KeyGenerator kg = KeyGenerator.getInstance("AES"); kg.init(256, random);
        SecretKey aes = kg.generateKey();
        Cipher ac = Cipher.getInstance("AES/GCM/NoPadding");
        ac.init(Cipher.ENCRYPT_MODE, aes, new GCMParameterSpec(128, iv));
        byte[] encrypted = ac.doFinal(plain);
        Cipher rc = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rc.init(Cipher.ENCRYPT_MODE, receiver.rsa.getPublic());
        byte[] wrapped = rc.doFinal(aes.getEncoded());
        byte[] signed = concat(encrypted, iv, wrapped);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(receiver.rsa.getPrivate(), random); sig.update(signed);
        PackageData p = new PackageData();
        p.cipherText=encrypted; p.iv=iv; p.wrappedKey=wrapped; p.signature=sig.sign(); p.hash=hex(md.digest(signed)); p.originalSize=plain.length;
        return p;
    }
    public static byte[] recover(PackageData p, ReceiverKeys receiver) throws Exception {
        byte[] signed=concat(p.cipherText,p.iv,p.wrappedKey);
        Signature sig=Signature.getInstance("SHA256withRSA"); sig.initVerify(receiver.rsa.getPublic()); sig.update(signed);
        if(!sig.verify(p.signature)) throw new SecurityException("Digital signature verification failed");
        Cipher rc=Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding"); rc.init(Cipher.DECRYPT_MODE,receiver.rsa.getPrivate());
        SecretKey aes=new SecretKeySpec(rc.doFinal(p.wrappedKey),"AES");
        Cipher ac=Cipher.getInstance("AES/GCM/NoPadding"); ac.init(Cipher.DECRYPT_MODE,aes,new GCMParameterSpec(128,p.iv));
        return ac.doFinal(p.cipherText);
    }
    public static String hex(byte[] b){StringBuilder s=new StringBuilder();for(byte x:b)s.append(String.format("%02x",x));return s.toString();}
    public static String shortHash(String h){return h==null?"waiting":h.substring(0,Math.min(18,h.length()))+"…";}
    public static String b64(byte[] b){return Base64.getEncoder().encodeToString(b);}
    private static byte[] concat(byte[]... a){int n=0;for(byte[] x:a)n+=x.length;byte[] o=new byte[n];int p=0;for(byte[] x:a){System.arraycopy(x,0,o,p,x.length);p+=x.length;}return o;}
}
