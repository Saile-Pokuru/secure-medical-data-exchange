package medicalsecure;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.Executors;

public class Dashboard extends JFrame {
    private final JTextArea log=new JTextArea(); private final JLabel status=new JLabel("SYSTEM READY");
    private final JLabel selectedLabel=new JLabel("No medical document selected"); private final JLabel threatLabel=new JLabel("ANALYSIS: WAITING");
    private final JLabel hashLabel=new JLabel("SHA-256: waiting"); private final JLabel scoreLabel=new JLabel("RISK SCORE: --/100");
    private final JProgressBar progress=new JProgressBar(0,100); private final SecureExchange exchange;
    private Path selectedFile; private byte[] selectedBytes; private FileSecurityAnalyzer.Result analysis;
    public Dashboard(){try{exchange=new SecureExchange();}catch(Exception e){throw new RuntimeException(e);}setTitle("MEDCRYPT SENTINEL | Secure Medical Data Exchange");setDefaultCloseOperation(EXIT_ON_CLOSE);setSize(1380,820);setLocationRelativeTo(null);build();}
    private void build(){
        JPanel root=new JPanel(new BorderLayout(16,16));root.setBorder(new EmptyBorder(16,16,16,16));root.setBackground(new Color(6,12,24));
        JPanel top=new JPanel(new BorderLayout());top.setOpaque(false);JLabel title=new JLabel("MEDCRYPT SENTINEL  //  HEALTHCARE SECURITY OPERATIONS CENTER");title.setForeground(Color.WHITE);title.setFont(new Font("SansSerif",Font.BOLD,25));status.setForeground(new Color(50,230,165));status.setFont(new Font("SansSerif",Font.BOLD,15));top.add(title,BorderLayout.WEST);top.add(status,BorderLayout.EAST);root.add(top,BorderLayout.NORTH);
        JPanel metrics=new JPanel(new GridLayout(1,5,10,10));metrics.setOpaque(false);metrics.add(metric("ENCRYPTION","AES-256-GCM",new Color(65,175,255)));metrics.add(metric("KEY PROTECTION","RSA-2048 / OAEP",new Color(190,120,255)));metrics.add(metric("INTEGRITY","SHA-256",new Color(255,195,70)));metrics.add(metric("AUTHENTICITY","RSA SIGNATURE",new Color(55,225,160)));metrics.add(metric("REPLAY DEFENSE","TIMESTAMP + NONCE",new Color(90,205,220)));
        JPanel main=new JPanel(new BorderLayout(12,12));main.setOpaque(false);main.add(metrics,BorderLayout.NORTH);
        JPanel left=panel();left.setLayout(new BorderLayout(10,10));JLabel lh=heading("FILE SECURITY + SECURE EXCHANGE");left.add(lh,BorderLayout.NORTH);
        log.setEditable(false);log.setForeground(new Color(215,230,245));log.setBackground(new Color(9,19,34));log.setFont(new Font(Font.MONOSPACED,Font.PLAIN,13));log.setBorder(new EmptyBorder(12,12,12,12));left.add(new JScrollPane(log),BorderLayout.CENTER);
        JPanel actions=new JPanel(new BorderLayout(8,8));actions.setOpaque(false);JButton choose=bigButton("📁  SELECT MEDICAL FILE");choose.addActionListener(e->selectFile());actions.add(choose,BorderLayout.NORTH);
        selectedLabel.setHorizontalAlignment(SwingConstants.CENTER);selectedLabel.setForeground(new Color(120,225,190));selectedLabel.setFont(new Font("SansSerif",Font.BOLD,14));threatLabel.setHorizontalAlignment(SwingConstants.CENTER);threatLabel.setFont(new Font("SansSerif",Font.BOLD,14));scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);scoreLabel.setForeground(Color.WHITE);JPanel info=new JPanel(new GridLayout(3,1));info.setOpaque(false);info.add(selectedLabel);info.add(threatLabel);info.add(scoreLabel);actions.add(info,BorderLayout.CENTER);
        JPanel btns=new JPanel(new GridLayout(1,4,8,8));btns.setOpaque(false);JButton protect=button("PROTECT + EXCHANGE");protect.addActionListener(e->protect());JButton quarantine=button("QUARANTINE");quarantine.addActionListener(e->quarantine());JButton save=button("SAVE PACKAGE");save.addActionListener(e->savePackage());JButton video=button("GENERATE 2–3 MIN VIDEO");video.addActionListener(e->video());btns.add(protect);btns.add(quarantine);btns.add(save);btns.add(video);actions.add(btns,BorderLayout.SOUTH);left.add(actions,BorderLayout.SOUTH);
        JPanel right=panel();right.setLayout(new BoxLayout(right,BoxLayout.Y_AXIS));right.add(heading("LIVE SECURITY TELEMETRY"));right.add(Box.createVerticalStrut(14));right.add(infoBlock("SELECTED FILE",selectedLabel));right.add(Box.createVerticalStrut(12));right.add(infoBlock("RISK SCORE",scoreLabel));right.add(Box.createVerticalStrut(12));right.add(infoBlock("FILE HASH",hashLabel));right.add(Box.createVerticalStrut(18));right.add(flowPanel());right.add(Box.createVerticalGlue());JLabel note=new JLabel("Use synthetic medical records for classroom demos.");note.setForeground(new Color(150,165,185));right.add(note);
        JPanel body=new JPanel(new GridLayout(1,2,12,12));body.setOpaque(false);body.add(left);body.add(right);main.add(body,BorderLayout.CENTER);progress.setStringPainted(true);progress.setString("VIDEO RENDERER IDLE");main.add(progress,BorderLayout.SOUTH);root.add(main,BorderLayout.CENTER);setContentPane(root);append("SYSTEM ONLINE — cryptographic receiver initialized.");append("Click SELECT MEDICAL FILE to choose a document from your Documents folder.");
    }
    private JPanel metric(String a,String b,Color c){JPanel p=panel();JLabel x=new JLabel(a);x.setForeground(new Color(145,165,190));x.setFont(new Font("SansSerif",Font.BOLD,11));JLabel y=new JLabel(b);y.setForeground(c);y.setFont(new Font("SansSerif",Font.BOLD,16));p.add(x,BorderLayout.NORTH);p.add(y,BorderLayout.SOUTH);return p;}
    private JPanel infoBlock(String a,JLabel b){JPanel p=new JPanel(new BorderLayout());p.setOpaque(false);JLabel x=new JLabel(a);x.setForeground(new Color(140,160,185));x.setFont(new Font("SansSerif",Font.BOLD,11));b.setFont(new Font("SansSerif",Font.BOLD,14));p.add(x,BorderLayout.NORTH);p.add(b,BorderLayout.SOUTH);return p;}
    private JPanel flowPanel(){JPanel p=panel();p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));JLabel h=heading("SECURE FLOW");p.add(h);String[] s={"1  FILE ANALYSIS","2  THREAT DECISION","3  AES-256-GCM","4  RSA KEY PROTECTION","5  SECURE EXCHANGE","6  VERIFY + DECRYPT"};for(String x:s){JLabel l=new JLabel("●  "+x);l.setForeground(new Color(75,220,180));l.setBorder(new EmptyBorder(6,0,6,0));p.add(l);}return p;}
    private JLabel heading(String s){JLabel l=new JLabel(s);l.setForeground(Color.WHITE);l.setFont(new Font("SansSerif",Font.BOLD,16));return l;}
    private JPanel panel(){JPanel p=new JPanel();p.setBackground(new Color(13,25,43));p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(40,65,92)),new EmptyBorder(14,14,14,14)));return p;}
    private JButton button(String s){JButton b=new JButton(s);b.setForeground(Color.WHITE);b.setBackground(new Color(23,47,73));b.setFocusPainted(false);b.setFont(new Font("SansSerif",Font.BOLD,11));return b;}
    private JButton bigButton(String s){JButton b=button(s);b.setPreferredSize(new Dimension(0,62));b.setFont(new Font("SansSerif",Font.BOLD,19));b.setBackground(new Color(20,115,215));b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(90,195,255),2),new EmptyBorder(10,16,10,16)));return b;}
    private void selectFile(){
        JFileChooser fc=new JFileChooser();fc.setDialogTitle("Select Medical Document from Documents");Path docs=Paths.get(System.getProperty("user.home"),"Documents");if(Files.isDirectory(docs))fc.setCurrentDirectory(docs.toFile());fc.setFileSelectionMode(JFileChooser.FILES_ONLY);fc.setAcceptAllFileFilterUsed(true);
        if(fc.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION)return;try{selectedFile=fc.getSelectedFile().toPath();selectedBytes=Files.readAllBytes(selectedFile);analysis=FileSecurityAnalyzer.analyze(selectedFile);selectedLabel.setText("SELECTED: "+selectedFile.getFileName()+"  |  "+human(selectedBytes.length));hashLabel.setText("SHA-256: "+CryptoEngine.shortHash(analysis.sha256));scoreLabel.setText("RISK SCORE: "+analysis.score+"/100");append("FILE SELECTED: "+selectedFile.toAbsolutePath());append("SHA-256: "+analysis.sha256);append("RISK SCORE: "+analysis.score+"/100");if(analysis.suspicious){threatLabel.setText("🔴 SUSPICIOUS — EXCHANGE BLOCKED");threatLabel.setForeground(new Color(255,85,95));status.setText("THREAT DETECTED");for(String i:analysis.indicators)append("INDICATOR: "+i);append("RESPONSE: quarantine before any exchange.");}else{threatLabel.setText("🟢 SAFE — READY FOR CRYPTOGRAPHIC PROTECTION");threatLabel.setForeground(new Color(55,230,165));status.setText("FILE READY");append("ANALYSIS: no configured threat indicators detected.");append("READY: AES encryption + RSA key protection + verification.");}}catch(Exception ex){selectedFile=null;selectedBytes=null;analysis=null;showError(ex);}}
    private boolean require(){if(selectedFile==null||selectedBytes==null){JOptionPane.showMessageDialog(this,"Click SELECT MEDICAL FILE and choose a document from Documents first.","Medical File Required",JOptionPane.INFORMATION_MESSAGE);return false;}return true;}
    private void protect(){if(!require())return;if(analysis.suspicious){status.setText("EXCHANGE BLOCKED");append("BLOCKED: suspicious file is not encrypted or exchanged.");quarantine();return;}try{boolean ok=exchange.run(selectedBytes);hashLabel.setText("SHA-256: "+CryptoEngine.shortHash(exchange.packageData.hash));append("AES-256-GCM: encrypted.");append("RSA-OAEP: session key protected.");append("SHA-256: fingerprint recorded.");append("RSA signature: verified.");append("Receiver decryption: "+(ok?"MATCH ✓":"MISMATCH ✗"));status.setText(ok?"VERIFIED • SECURE":"VERIFICATION FAILED");}catch(Exception ex){showError(ex);}}
    private void savePackage(){if(!require())return;if(analysis.suspicious){quarantine();return;}try{if(exchange.packageData==null)protect();if(exchange.packageData==null)return;Path out=exchange.saveEncryptedPackage(Paths.get("secure_output"),selectedFile.getFileName().toString());append("SECURE PACKAGE SAVED: "+out.toAbsolutePath());}catch(Exception ex){showError(ex);}}
    private void quarantine(){if(!require())return;try{Path dir=Paths.get("quarantine");Files.createDirectories(dir);Path out=dir.resolve(selectedFile.getFileName().toString());Files.copy(selectedFile,out,StandardCopyOption.REPLACE_EXISTING);append("QUARANTINED: "+out.toAbsolutePath());status.setText("QUARANTINED");JOptionPane.showMessageDialog(this,"Suspicious file blocked and copied to quarantine.\nNo secure exchange was performed.","Security Alert",JOptionPane.WARNING_MESSAGE);}catch(Exception ex){showError(ex);}}
    private void video(){
        if(!require()) return;
        Path out=Paths.get("output","MedCrypt_Secure_Medical_Exchange.mp4");
        try{
            Files.createDirectories(out.getParent());
            progress.setValue(0);
            progress.setString("RENDERING VIDEO...");
            append("VIDEO: rendering 2–3 minute animated security demonstration.");
            String fileName=selectedFile.getFileName().toString();
            boolean bad=analysis.suspicious;
            Executors.newSingleThreadExecutor().submit(()->{
                try{
                    VideoRenderer.render(out,fileName,bad,(f,total,scene)->
                        SwingUtilities.invokeLater(()->{
                            progress.setValue((int)(f*100.0/total));
                            progress.setString(scene+"  |  "+progress.getValue()+"%");
                        })
                    );
                    SwingUtilities.invokeLater(()->{
                        append("VIDEO READY: "+out.toAbsolutePath());
                        progress.setString("VIDEO READY");
                        status.setText("VIDEO READY");
                    });
                }catch(Exception ex){
                    SwingUtilities.invokeLater(()->{
                        progress.setString("VIDEO ERROR — check lib/ JCodec JARs");
                        showError(ex);
                    });
                }
            });
        }catch(Exception ex){showError(ex);}
    }
    private void append(String s){log.append("[MEDCRYPT] "+s+"\n");log.setCaretPosition(log.getDocument().getLength());}
    private void showError(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"MEDCRYPT",JOptionPane.ERROR_MESSAGE);}
    private String human(long n){if(n<1024)return n+" B";if(n<1024*1024)return String.format("%.1f KB",n/1024.0);return String.format("%.1f MB",n/1048576.0);}
}
