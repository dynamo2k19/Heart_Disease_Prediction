/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hdp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author rishu
 */
public class DBC {
    Connection con;
    Statement stm;
    ResultSet rs1, rs2;
    
    public DBC()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/hdp","root","rootwdp");        
            stm = con.createStatement();

        }
        catch(Exception e)
        {
            e.printStackTrace();
            //System.out.println(".............." + e);
        }
    }
        
    
}
