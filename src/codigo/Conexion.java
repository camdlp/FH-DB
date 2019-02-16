/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author carlosabia
 */
public class Conexion {

    //Como mi aplicación sólamente usará una conexión y un statement simultáneamente los declaro
    //de instancia para poder cerrarlos desde cualquier clase cuando no los necesite más.
    public Connection con;
    public Statement sta;
    public ResultSet rs;
    public PreparedStatement pst;

    public Conexion() {
        con = null;
        try {
            String url = "jdbc:mysql://localhost/fh?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            //Error de zona horaria, fix: jdbc:mysql://localhost/discografica?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
            String user = "root";
            String password = "";
            con = (Connection) DriverManager.getConnection(url, user, password);
            if (con != null) {
                System.out.println("CONECTADO a la base de datos");
            }

        } catch (SQLException ex) {
            System.out.println("ERROR en la conexión a la base de datos");
        }
    }

    public void finalizaConexion() {
        try {
            con.close();
            System.out.println("Conexión cerrada");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage()
                    + ". >>> Error de Desconexion!!");
        }

    }

    //Cierra el statement
    public void cierraStatement() throws SQLException {
        try {
            sta.close();
            System.out.println("Statement cerrado");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage()
                    + ". >>> Error de cierre de Statement!!");
        }
    }

    //Cierra el ResultSet
    public void cierraResultSet() throws SQLException {
        try {
            rs.close();
            System.out.println("ResultSet cerrado");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage()
                    + ". >>> Error de cierre de ResultSet!!");
        }
    }

    public void ejecutaQuery(String query) throws SQLException {
        con.setAutoCommit(false);
        sta = con.createStatement();
        System.out.println("Statement abierto");
        try {

            sta.executeUpdate(query);
            con.commit();

        }catch(Exception e){
            e.printStackTrace();
            con.rollback();
        
        }finally {
            sta.close();
            System.out.println("Statement cerrado");
        }

    }

    public ResultSet devuelveResultSet(String query) throws SQLException {

        sta = con.createStatement();
        System.out.println("Statement abierto");

        rs = sta.executeQuery(query);
        return rs;
    }


    public ResultSet ultimaAdicion(String tabla, int id) {
        try {
            pst = con.prepareStatement("SELECT * FROM "+ tabla +" WHERE `id` LIKE ?");
            pst.setInt(1, id);
            rs = pst.executeQuery();            
            
            
        } catch (SQLException ex) {
            System.out.println("ERROR:al consultar");
            ex.printStackTrace();
        }
        
        return rs;
    }

}
