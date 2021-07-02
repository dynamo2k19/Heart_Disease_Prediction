/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hdp;
import java.awt.Image;
import java.time.LocalDate;
import javax.imageio.ImageIO;
/**
 *
 * @author rishu
 */
public class HDPCheck extends javax.swing.JFrame {

    /**
     * Creates new form HDPCheck
     */
    DBC db;
    HDPUserDash obdash;
    
    public HDPCheck(HDPUserDash ob) {
        initComponents();
        setLocationRelativeTo(null);
        try
        {
            Image i = ImageIO.read(getClass().getResource("/icon.jpeg"));
            setIconImage(i);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        getContentPane().setBackground(new java.awt.Color(135, 187, 221));
        db = new DBC();
        obdash = ob;
        try
        {
            db.rs1 = db.stm.executeQuery("SELECT Age, Gender FROM hdpuser WHERE UserID = '"+LoginData.uid+"'");
            if (db.rs1.next())
            {
                tfage.setText(db.rs1.getString(1));
                String gen = db.rs1.getString(2);
                if (gen.equals("M"))
                {
                    male.setSelected(true);
                }
                else if (gen.equals("F"))
                {
                    female.setSelected(true);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        cp.addItem("Typical Angina");
        cp.addItem("Atypical Angina");
        cp.addItem("Non-Anginal Pain");
        cp.addItem("Asymptotic");
        ecg.addItem("Normal");
        ecg.addItem("Having ST-T Wave Abnormality");
        ecg.addItem("Left Ventricular Hyperthrophy");
        pest.addItem("Upsloping");
        pest.addItem("Flat");
        pest.addItem("Downsloping");
        thal.addItem("Normal");
        thal.addItem("Fixed Defect");
        thal.addItem("Reversible Defect");
        
    }

    private int getProb()
    {
        int gen = 0, age = Integer.parseInt(tfage.getText()), cptype = cp.getSelectedIndex();
        int chol = Integer.parseInt(tfchol.getText());   // <200 / 200-239 / >239
        float psum = 0;
        if (male.isSelected())
        {
            gen = 1;
        }
        
        int uage = getAgeLimit(age);
        int lage = uage - 20;
        
        int lch = 0, uch = 100;
        if (chol < 200)
        {
            uch = 200;
        }
        else if (chol > 200 && chol < 239)
        {
            lch = 200;
            uch = 239;
        }
        else
        {
            lch = 240;
            uch = 700;
        }
        
        int fbs = 0, recg = ecg.getSelectedIndex(), thalach = Integer.parseInt(tfthalach.getText());
        if (Integer.parseInt(tf_fbs.getText()) > 120)
        {
            fbs = 1;
        }
        
        int uth = getThalach(thalach, age);
        int lth = uth - 70;
        
        int angina = 0, rbp = Integer.parseInt(tfrbp.getText()), stseg = pest.getSelectedIndex();
        if (yes.isSelected())
        {
            angina = 1;
        }
        int lrbp = 0, urbp = 90;
        if (rbp < 90)
        {
            urbp = 90;
        }
        else if (rbp >= 90 && rbp < 120)
        {
            lrbp = 90;
            urbp = 120;
        }
        else if (rbp >= 120 && rbp < 140)
        {
            lrbp = 120;
            urbp = 140;
        }
        else
        {
            lrbp = 140;
            urbp = 300;
        }
        int tha = thal.getSelectedIndex() + 1;
        int ca = Integer.parseInt(tfca.getText());
        String qry = "SELECT COUNT(Age) FROM hdpfactors WHERE Age >= "+lage+" AND Age <= "+uage+" AND Target = 1";
        String qry2 = "SELECT COUNT(Age) FROM hdpfactors WHERE Age >= "+lage+" AND Age <= "+uage;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(Gender) FROM hdpfactors WHERE Gender = "+gen+" AND Target = 1";
        qry2 = "SELECT COUNT(Gender) FROM hdpfactors WHERE Gender = "+gen;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(CPType) FROM hdpfactors WHERE CPType = "+cptype+" AND Target = 1";
        qry2 = "SELECT COUNT(CPType) FROM hdpfactors WHERE CPType = "+cptype;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(Chol) FROM hdpfactors WHERE Chol > "+lch+" AND Chol < "+uch+" AND Target = 1";
        qry2 = "SELECT COUNT(Chol) FROM hdpfactors WHERE Chol > "+lch+" AND Chol < "+uch;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(FBS) FROM hdpfactors WHERE FBS = "+fbs+" AND Target = 1";
        qry2 = "SELECT COUNT(FBS) FROM hdpfactors WHERE FBS = "+fbs;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(ECG) FROM hdpfactors WHERE ECG = "+recg+" AND Target = 1";
        qry2 = "SELECT COUNT(ECG) FROM hdpfactors WHERE ECG = "+recg;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(Thalach) FROM hdpfactors WHERE Thalach > "+lth+" AND Thalach < "+uth+" AND Target = 1";
        qry2 = "SELECT COUNT(Thalach) FROM hdpfactors WHERE Thalach > "+lth+" AND Thalach < "+uth;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(ExIndAngina) FROM hdpfactors WHERE ExIndAngina = "+angina+" AND Target = 1";
        qry2 = "SELECT COUNT(ExIndAngina) FROM hdpfactors WHERE ExIndAngina = "+angina;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(RestingBP) FROM hdpfactors WHERE RestingBP > "+lrbp+" AND RestingBP < "+urbp+" AND Target = 1";
        qry2 = "SELECT COUNT(RestingBP) FROM hdpfactors WHERE RestingBP > "+lrbp+" AND RestingBP < "+urbp;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(PeakExSTSegment) FROM hdpfactors WHERE PeakExSTSegment = "+stseg+" AND Target = 1";
        qry2 = "SELECT COUNT(PeakExSTSegment) FROM hdpfactors WHERE PeakExSTSegment = "+stseg;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(Thal) FROM hdpfactors WHERE Thal = "+tha+" AND Target = 1";
        qry2 = "SELECT COUNT(Thal) FROM hdpfactors WHERE Thal = "+tha;
        psum = psum + getRes(qry, qry2);
        qry = "SELECT COUNT(CA) FROM hdpfactors WHERE CA = "+ca+" AND Target = 1";
        qry2 = "SELECT COUNT(CA) FROM hdpfactors WHERE CA = "+ca;
        psum = psum + getRes(qry, qry2);
        float prob = psum / 12;
        System.out.println(psum);
        System.out.println("Your Heart Disiease Probability is " + prob);
        int p = (int)(prob * 100);
        System.out.println("Prob % = " + p);
        return p;
    }
    
    private float getRes(String qry, String qry2)
    {
        //System.out.println(qry);
        //System.out.println(qry2);
        float sum = 0;
        int x = 1, y = 1;
        try
        {
            db.rs1 = db.stm.executeQuery(qry);
            
            if (db.rs1.next())
            {
                x = db.rs1.getInt(1);
                System.out.println("x"+x);
            }
            db.rs1.close();
            db.rs1 = db.stm.executeQuery(qry2);
            if (db.rs1.next())
            {
                y = db.rs1.getInt(1);
                System.out.println("y"+y);
            }
            db.rs1.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        sum = (float)x / y;
        System.out.println(sum);
        return sum;
    }
    private int getThalach(int thalach, int age)
    {
        int th = 0;
        if (age > 0 && age <= 20)
        {
	    if (thalach > 170)
            {
                th = 170;
            }
            else if (thalach < 100)
            {
                th = 100;
            }
            else
            {
                th = thalach;
            }
        }
        else if (age > 20 && age <= 35)
        {
            if (thalach > 162)
            {
                th = 162;
            }
            else if (thalach < 93)
            {
                th = 93;
            }
            else
            {
                th = thalach;
            }
	} 
        else if (age > 35 && age <= 45) 
        {
	    if (thalach > 157)
            {
                th = 157;
            }
            else if (thalach < 88)
            {
                th = 88;
            }
            else
            {
                th = thalach;
            }
	}
        else if (age > 45 && age <= 60) 
        {
	    if (thalach > 149)
            {
                th = 149;
            }
            else if (thalach < 80)
            {
                th = 80;
            }
            else
            {
                th = thalach;
            }
	}
        else if (age > 60)
        {
	    if (thalach > 138)
            {
                th = 138;
            }
            else if (thalach < 75)
            {
                th = 75;
            }
            else
            {
                th = thalach;
            }
	}
        return th;
    }
    private int getAgeLimit(int age)
    {
        int uage = 120;
        if (age <= 20)
        {
            uage = 20;
        }
        else if (age <= 40)
        {
            uage = 40;
        }
        else if (age <= 60)
        {
            uage = 60;
        }
        else if (age <= 80)
        {
            uage = 80;
        }
        else if (age <= 100)
        {
            uage = 100;
        }
        return uage;
    }
    
    private void saveData(int p)
    {
        String g = "F";
        if (male.isSelected())
        {
            g = "M";
        }
        String angina = "No";
        if (yes.isSelected())
        {
            angina = "Yes";
        }
        
        String qry = "INSERT INTO hdpchkuphistory (UserID,Age,Gender,CPType,Chol,FBS,ECG,Thalach,ExIndAngina,RestingBP,PeakExSTSegment,Thal,CA,Probability,Date) VALUES('"+LoginData.uid+"',"+tfage.getText()+",'"+g+"','"+cp.getSelectedItem()+"',"+tfchol.getText()+","+
                tf_fbs.getText()+",'"+ecg.getSelectedItem()+"',"+tfthalach.getText()+",'"+angina+"',"+tfrbp.getText()+",'"+pest.getSelectedItem()+"','"+(thal.getSelectedItem())+"',"
                +tfca.getText()+","+p+",'"+LocalDate.now()+"')";
        System.out.println(qry);
        try
        {
            db.stm.executeUpdate(qry);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        tfage = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cp = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        tfrbp = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        female = new javax.swing.JRadioButton();
        male = new javax.swing.JRadioButton();
        tfchol = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tf_fbs = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tfthalach = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        no = new javax.swing.JRadioButton();
        yes = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        ecg = new javax.swing.JComboBox();
        pest = new javax.swing.JComboBox();
        thal = new javax.swing.JComboBox();
        tfca = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        btnchk = new javax.swing.JButton();
        btnback = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Age:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Gender:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Fasting Blood Sugar:");

        cp.setFont(new java.awt.Font("Tahoma", 1, 18));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setText("Chest Pain Type:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Serum Cholestrol:");

        buttonGroup1.add(female);
        female.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        female.setText("Female");

        buttonGroup1.add(male);
        male.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        male.setText("Male");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Resting Blood Pressure:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setText("Resting ECG:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setText("Exercise Induced Angina:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setText("Maximum Heart Rate Achieved (thalach)");

        buttonGroup2.add(no);
        no.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        no.setText("No");

        buttonGroup2.add(yes);
        yes.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        yes.setText("Yes");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setText("Peak Exercise ST Segment:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setText("Thal:");

        ecg.setFont(new java.awt.Font("Tahoma", 1, 18));

        pest.setFont(new java.awt.Font("Tahoma", 1, 18));

        thal.setFont(new java.awt.Font("Tahoma", 1, 18));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setText("No. of major vessels colored by fluoroscopy (ca)");

        btnchk.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        btnchk.setText("Check");
        btnchk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnchkActionPerformed(evt);
            }
        });

        btnback.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        btnback.setText("<- GO BACK ");
        btnback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(tfchol, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel10))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tfage, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(male)
                                                .addGap(18, 18, 18)
                                                .addComponent(female))
                                            .addComponent(cp, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel11)))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(tfthalach, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(123, 123, 123))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(yes)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(no)
                                .addGap(203, 203, 203))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tfrbp, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pest, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(thal, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tf_fbs, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ecg, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addGap(26, 26, 26)
                        .addComponent(tfca, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(47, 47, 47))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(483, 483, 483)
                        .addComponent(btnchk, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnback)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btnback, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfage, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfthalach, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(female, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(male, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(no, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(yes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cp, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfrbp, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfchol, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pest, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tf_fbs, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(thal, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ecg, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfca, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(btnchk, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnchkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnchkActionPerformed
        int pr = getProb();
        saveData(pr);
        HDPRes ob = new HDPRes(pr, obdash);
        ob.setLocationRelativeTo(this);
        ob.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnchkActionPerformed

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
        obdash.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnbackActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HDPCheck.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HDPCheck.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HDPCheck.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HDPCheck.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HDPCheck(new HDPUserDash()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnback;
    private javax.swing.JButton btnchk;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<String> cp;
    private javax.swing.JComboBox<String> ecg;
    private javax.swing.JRadioButton female;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JRadioButton male;
    private javax.swing.JRadioButton no;
    private javax.swing.JComboBox<String> pest;
    private javax.swing.JTextField tf_fbs;
    private javax.swing.JTextField tfage;
    private javax.swing.JTextField tfca;
    private javax.swing.JTextField tfchol;
    private javax.swing.JTextField tfrbp;
    private javax.swing.JTextField tfthalach;
    private javax.swing.JComboBox<String> thal;
    private javax.swing.JRadioButton yes;
    // End of variables declaration//GEN-END:variables
}