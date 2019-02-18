/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.awt.Color;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Carlos
 */
public class Ventana extends javax.swing.JFrame {

    /**
     * Creates new form Ventana
     */
    public Ventana() throws SQLException {        
        initComponents();
        getContentPane().setBackground( Color.WHITE );
        //centro el jframe
        setLocationRelativeTo(null);

        //Customizo el header del jtable
        jTableResultados.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
       
        
        //Empiezan los clientes clickados
        jToggleButtonClientes.doClick();
        creaTabla(c.devuelveResultSet("SELECT * FROM clientes"), tabla);
    }

    Conexion c = new Conexion();
    String tabla = "clientes";


    /*
    ================================                ================================
    ================================    MÉTODOS     ================================
    ================================                ================================
    
     */
    public void creaTabla(ResultSet rs, String tabla) throws SQLException {

        //Reinicio la tabla
        DefaultTableModel model = (DefaultTableModel) jTableResultados.getModel();
        model.setRowCount(0);

        //Creo el resultsetmetadata y el modelo de la tabla, necesarios para la creación
        //de la tabla dinámica.
        ResultSetMetaData rsmd = rs.getMetaData();
        DefaultTableModel tm = new DefaultTableModel();

        //Pongo los nombres de cada columna
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            tm.addColumn(rsmd.getColumnName(i));

        }
        //Recorro el resultset
        //Números mágicos en j para arreglar el desfase entre el resultset, que empieza en 0
        //y el array Object que empieza en 0.
        while (rs.next()) {
            Object[] o = new Object[rsmd.getColumnCount()];
            for (int j = 1; j <= rsmd.getColumnCount(); j++) {
                o[j - 1] = rs.getObject(j);
            }
            tm.addRow(o);
        }

        //Añado el modelo a la tabla
        jTableResultados.setModel(tm);
        //Habilito el textinput para afinar búsqueda
        jTextFieldBusqueda.setEnabled(true);
        //Reinicio el filtro de la tabla.
        filtrar("");

        //Cierro el resultset y el statement ya que nos los voy a necesitar más
        //Resultset usado en este método
        rs.close();
        //ResultSet y statement de la clase conexion
        c.cierraResultSet();
        c.cierraStatement();

    }

    /*
    Método de filtrado de la tabla mediante TableRowSorter con regexfilter.
     */
    public void filtrar(String query) {
        DefaultTableModel dm = (DefaultTableModel) jTableResultados.getModel();
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(dm);
        jTableResultados.setRowSorter(tr);

        tr.setRowFilter(RowFilter.regexFilter(query));
    }

    public void rellenaComboBox() {

        //Vacío el ComboBox
        jComboBoxBusqueda.removeAllItems();

        //Añado el nombre del atributo al comboBox para poder hacer las búsquedas después
        for (int i = 0; i < jTableResultados.getColumnCount(); i++) {
            jComboBoxBusqueda.addItem(jTableResultados.getColumnName(i));
        }

    }

    public Object[] addClientes(String pasa_alias, String pasa_pass, String pasa_correo, String pasa_staff) {

        JTextField alias = new JTextField(pasa_alias);
        JTextField correo = new JTextField(pasa_correo);
        JPasswordField pass = new JPasswordField(pasa_pass);
        Object[] mensaje = new Object[8];
        JComboBox jc = new JComboBox();

        jc.addItem("0");
        jc.addItem("1");

        jc.setSelectedItem(pasa_staff);

        mensaje[0] = "Alias";
        mensaje[1] = alias;
        mensaje[2] = "Contraseña";
        mensaje[3] = pass;
        mensaje[4] = "Correo";
        mensaje[5] = correo;
        mensaje[6] = "Miembro del staff";
        mensaje[7] = jc;

        return mensaje;

    }

    public Object[] addPedidos(int articulos, String pasa_alias_clientes, String pasa_fecha) throws SQLException {

        JComboBox alias_clientes = new JComboBox();
        JComboBox nombre_platos = new JComboBox();
        JTextField fecha = new JTextField(LocalDateTime.now().toString());
        Object[] mensaje = new Object[6 + articulos];
        //Modelo del combobox
        ArrayList<String> modeloCombo = new ArrayList<String>();

        //Relleno los comboBox de alias_clientes y nombre_platos
        ResultSet rs;

        if (pasa_fecha != "") {
            fecha = new JTextField(pasa_fecha);
        } else {
            fecha = new JTextField(LocalDateTime.now().toString());
        }
        //Order by necesario para que los elementos del jcombobox salgan por orden
        rs = c.devuelveResultSet("SELECT id, alias FROM clientes ORDER BY id");

        while (rs.next()) {
            String cliente = "";
            cliente += rs.getString("id") + "-";
            cliente += rs.getString("alias");

            alias_clientes.addItem(cliente);
        }

        rs = c.devuelveResultSet("SELECT id, nombre FROM platos ORDER BY id");

        modeloCombo.add("");
        while (rs.next()) {
            String plato = "";
            plato += rs.getString("id") + "-";
            plato += rs.getString("nombre");
            modeloCombo.add(plato);
        }

        mensaje[0] = "Alias del Cliente";
        mensaje[1] = alias_clientes;
        mensaje[2] = "Fecha del Pedido. (Formato año/mes/dia)";
        mensaje[3] = fecha;
        mensaje[4] = "Platos pedidos";
        mensaje[5] = nombre_platos;

        //Si he pasado valores, es decir, si estoy en modificar, hago la consulta
        if (!"".equals(pasa_alias_clientes)) {

            String query = "SELECT id_plato FROM pedidos_platos WHERE id_pedido='" + jTableResultados.getModel().getValueAt(jTableResultados.getSelectedRow(), 0) + "'";
            System.out.println(query);
            rs = c.devuelveResultSet(query);
        }

        for (int i = 5; i < articulos + 5; i++) {
            JComboBox jc = new JComboBox(modeloCombo.toArray());

            if (rs.next()) {

                //Guardo el id del objeto existente si lo hubiera
                int str = rs.getInt(1);
                System.out.println(str);
                jc.setSelectedIndex(str);
            }

            mensaje[i] = jc;
        }
        rs.close();
        return mensaje;

    }

    public Object[] addPlatos(int ingredientes, String pasa_nombre) throws SQLException {

        JTextField nombre = new JTextField(pasa_nombre);
        //JComboBox nombre_ingredientes = new JComboBox();
        Object[] mensaje = new Object[3 + ingredientes];
        ArrayList<String> modeloCombo = new ArrayList<String>();
        //Relleno los comboBox de alias_clientes y nombre_platos
        ResultSet rs;
        rs = c.devuelveResultSet("SELECT id, nombre FROM ingredientes  ORDER BY id");

        modeloCombo.add("");
        while (rs.next()) {
            String ingrediente = "";
            ingrediente += rs.getString("id") + "-";
            ingrediente += rs.getString("nombre");
            modeloCombo.add(ingrediente);
        }

        mensaje[0] = "Nombre del plato";
        mensaje[1] = nombre;
        mensaje[2] = "Ingredientes que lleva";

        if (!"".equals(pasa_nombre)) {

            String query = "SELECT id_ingrediente FROM platos_ingredientes WHERE id_plato='" + jTableResultados.getModel().getValueAt(jTableResultados.getSelectedRow(), 0) + "'";
            System.out.println(query);
            rs = c.devuelveResultSet(query);
        }

        for (int i = 3; i < ingredientes + 3; i++) {
            JComboBox jc = new JComboBox(modeloCombo.toArray());

            if (rs.next()) {

                //Guardo el id del objeto existente si lo hubiera
                int str = rs.getInt(1);
                System.out.println(str);
                jc.setSelectedIndex(str);
            }

            mensaje[i] = jc;
        }
        rs.close();
        return mensaje;

    }

    public Object[] addIngredientes(String pasa_nombre, String pasa_stock) {
        JTextField nombre = new JTextField(pasa_nombre);
        JComboBox stock = new JComboBox();
        Object[] mensaje = new Object[4];

        stock.addItem("1");
        stock.addItem("0");

        stock.setSelectedItem(pasa_stock);

        mensaje[0] = "Nombre";
        mensaje[1] = nombre;
        mensaje[2] = "Stock";
        mensaje[3] = stock;

        return mensaje;

    }

    public void inserta(Object[] campos) throws SQLException, InterruptedException {
        ArrayList<String> output = new ArrayList<>();
        String query = "";
        if (tabla == "clientes") {
            query = "INSERT INTO `clientes` (`alias`, `pass`, `correo`, `staff`) VALUES(";
        } else {
            query = "INSERT INTO `ingredientes` (`nombre`, `stock`) VALUES(";
        }

        for (int i = 0; i < campos.length; i++) {
            if (campos[i].getClass() != String.class) {
                if (campos[i].getClass() != JComboBox.class) {
                    JTextField jt = (JTextField) campos[i];
                    query += "'" + jt.getText() + "'";

                    query += ", ";

                } else {
                    JComboBox jc = (JComboBox) campos[i];
                    query += jc.getSelectedItem().toString();
                    query += ");";
                }
            }
        }

        System.out.println(query);
        c.ejecutaQuery(query);
        //Muestro el campo añadido en la tabla
        creaTabla(c.ultimaAdicion(tabla, dameUltimoID()), tabla);

    }

    public void insertaPedido(Object[] campos) throws SQLException {
        ArrayList<String> filtrado = new ArrayList<>();
        String insertaEnPedido = "";
        String insertaEnPedidosPlato = "";

        for (int i = 0; i < campos.length; i++) {
            if (!(campos[i] instanceof String)) {
                if (campos[i] instanceof JTextField) {

                    JTextField jtf = (JTextField) campos[i];
                    filtrado.add(jtf.getText());

                } else if (campos[i] instanceof JComboBox) {

                    JComboBox jcb = (JComboBox) campos[i];
                    filtrado.add(jcb.getSelectedItem().toString());
                }
            }
        }
        String[] f = filtrado.get(0).split("-");
        insertaEnPedido = "INSERT INTO pedidos (alias_clientes, fecha) VALUES ('" + f[1] + "', '" + filtrado.get(1) + "')";
        System.out.println(insertaEnPedido);
        c.ejecutaQuery(insertaEnPedido);

        //Guardo el ID del pedido insertado
        int id = dameUltimoID();
        for (int j = 2; j < filtrado.size(); j++) {
            insertaEnPedidosPlato = "INSERT INTO pedidos_platos (id_pedido, id_plato) VALUES(";

            //Divido el campo del plato para sacar el id
            String[] idPlato = filtrado.get(j).split("-");
            insertaEnPedidosPlato += "'" + id + "', '" + idPlato[0] + "'";

            insertaEnPedidosPlato += ")";

            System.out.println(insertaEnPedidosPlato);
            c.ejecutaQuery(insertaEnPedidosPlato);
        }

    }

    public void insertaPlato(Object[] campos) throws SQLException {
        ArrayList<String> filtrado = new ArrayList<>();
        String insertaEnPlatos = "";
        String insertaEnPlatosIngredientes = "";

        for (int i = 0; i < campos.length; i++) {
            if (!(campos[i] instanceof String)) {
                if (campos[i] instanceof JTextField) {

                    JTextField jtf = (JTextField) campos[i];
                    filtrado.add(jtf.getText());

                } else if (campos[i] instanceof JComboBox) {

                    JComboBox jcb = (JComboBox) campos[i];
                    filtrado.add(jcb.getSelectedItem().toString());
                }
            }
        }

        insertaEnPlatos = "INSERT INTO platos (nombre) VALUES ('" + filtrado.get(0) + "')";
        System.out.println(insertaEnPlatos);
        c.ejecutaQuery(insertaEnPlatos);

        //Guardo el ID del pedido insertado
        int id = dameUltimoID();
        for (int j = 1; j < filtrado.size(); j++) {
            insertaEnPlatosIngredientes = "INSERT INTO platos_ingredientes (id_plato, id_ingrediente) VALUES(";

            //Divido el campo del plato para sacar el id
            String[] idIngrediente = filtrado.get(j).split("-");
            insertaEnPlatosIngredientes += "'" + id + "', '" + idIngrediente[0] + "'";

            insertaEnPlatosIngredientes += ")";

            System.out.println(insertaEnPlatosIngredientes);
            c.ejecutaQuery(insertaEnPlatosIngredientes);
        }

    }

    public int dameUltimoID() throws SQLException {
        ResultSet rs = c.devuelveResultSet("SELECT LAST_INSERT_ID()");
        int id = 0;
        while (rs.next()) {
            id = rs.getInt(1);
        }
        return id;
    }

    public void update(Object[] campos) throws SQLException {
        ArrayList<String> filtrado = new ArrayList<>();
        String query = "UPDATE " + tabla + " SET ";

        //Elimino los campos de los 'labels'
        for (int i = 0; i < campos.length; i++) {
            if (!(campos[i] instanceof String)) {
                if (campos[i] instanceof JTextField) {

                    JTextField jtf = (JTextField) campos[i];
                    filtrado.add(jtf.getText());

                } else if (campos[i] instanceof JComboBox) {

                    JComboBox jcb = (JComboBox) campos[i];
                    filtrado.add(jcb.getSelectedItem().toString());
                }

            }
        }

        for (int i = 0; i < filtrado.size(); i++) {

            query += jTableResultados.getColumnName(i + 1) + " = '" + filtrado.get(i) + "'";

            if (i != filtrado.size() - 1) {
                query += " , ";
            } else {
                query += " WHERE id = " + jTableResultados.getModel().getValueAt(jTableResultados.getSelectedRow(), 0);
            }
        }

        System.out.println(query);
        c.ejecutaQuery(query);

    }

    public void updatePedidos(Object[] campos) throws SQLException {
        ArrayList<String> filtrado = new ArrayList<>();
        String query1 = "";
        String query2 = "";

        //Elimino los campos de los 'labels'
        for (int i = 0; i < campos.length; i++) {
            if (!(campos[i] instanceof String)) {
                if (campos[i] instanceof JTextField) {

                    JTextField jtf = (JTextField) campos[i];
                    filtrado.add(jtf.getText());

                } else if (campos[i] instanceof JComboBox) {

                    JComboBox jcb = (JComboBox) campos[i];
                    filtrado.add(jcb.getSelectedItem().toString());
                }

            }
        }
        String id = jTableResultados.getModel().getValueAt(jTableResultados.getSelectedRow(), 0).toString();

        //Separo el alias del id para guardar el alias
        String[] separado = filtrado.get(0).split("-");

        query1 += "UPDATE pedidos SET  alias_clientes = '" + separado[1] + "'" + ", fecha = '" + filtrado.get(1) + "' WHERE id = " + id + ";";
        System.out.println(query1);
        c.ejecutaQuery(query1);

        query2 = "DELETE FROM pedidos_platos WHERE id_pedido = " + id + ";\n";

        System.out.println(query2);
        c.ejecutaQuery(query2);
        for (int i = 2; i < filtrado.size(); i++) {
            query2 = "INSERT INTO pedidos_platos (id_pedido, id_plato) VALUES ('";

            separado = filtrado.get(i).split("-");
            query2 += id + "' , '" + separado[0] + "');\n";
            System.out.println(query2);
            c.ejecutaQuery(query2);

        }

    }

    public void updatePlatos(Object[] campos) throws SQLException {
        ArrayList<String> filtrado = new ArrayList<>();
        String query1 = "";
        String query2 = "";

        //Elimino los campos de los 'labels'
        for (int i = 0; i < campos.length; i++) {
            if (!(campos[i] instanceof String)) {
                if (campos[i] instanceof JTextField) {

                    JTextField jtf = (JTextField) campos[i];
                    filtrado.add(jtf.getText());

                } else if (campos[i] instanceof JComboBox) {

                    JComboBox jcb = (JComboBox) campos[i];
                    filtrado.add(jcb.getSelectedItem().toString());
                }

            }
        }
        String id = jTableResultados.getModel().getValueAt(jTableResultados.getSelectedRow(), 0).toString();
        

        query1 += "UPDATE platos SET  `nombre` = '" + filtrado.get(0) + "'" + " WHERE `id` = '" + id + "';\n";
        System.out.println(query1);
        c.ejecutaQuery(query1);

        query2 = "DELETE FROM platos_ingredientes WHERE `id_plato` = '" + id + "';\n";
        System.out.println(query2);
        c.ejecutaQuery(query2);
        
        
        //c.ejecutaQuery(query2);
        for (int i = 1; i < filtrado.size(); i++) {
            query2 = "INSERT INTO platos_ingredientes (`id_plato`, `id_ingrediente`) VALUES ('";

            String[] separado = filtrado.get(i).split("-");
            query2 += id + "' , '" + separado[0] + "');\n";
            System.out.println(query2);
            c.ejecutaQuery(query2);

        }

        

    }

    /*
    ================================                    ================================
    ================================    FIN MÉTODOS     ================================
    ================================                    ================================
    
     */
 /*
    ================================                ================================
    ================================    EVENTOS     ================================
    ================================                ================================
    
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelApp = new javax.swing.JPanel();
        jToggleButtonClientes = new javax.swing.JToggleButton();
        jToggleButtonPedidos = new javax.swing.JToggleButton();
        jPanelCocina = new javax.swing.JPanel();
        jToggleButtonPlatos = new javax.swing.JToggleButton();
        jToggleButtonIngredientes = new javax.swing.JToggleButton();
        jPanelFondo = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableResultados = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldBusqueda = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldBusqueda1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxBusqueda = new javax.swing.JComboBox<>();
        jButtonModificar = new javax.swing.JButton();
        jButtonAdd = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane.setForeground(new java.awt.Color(51, 51, 51));
        jTabbedPane.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jTabbedPane.setRequestFocusEnabled(false);
        jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });
        jTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTabbedPaneMousePressed(evt);
            }
        });

        jPanelApp.setBackground(new java.awt.Color(193, 102, 107));
        jPanelApp.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jToggleButtonClientes.setBackground(new java.awt.Color(52, 62, 61));
        jToggleButtonClientes.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jToggleButtonClientes.setForeground(new java.awt.Color(255, 255, 255));
        jToggleButtonClientes.setText("CLIENTES");
        jToggleButtonClientes.setBorder(null);
        jToggleButtonClientes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jToggleButtonClientesStateChanged(evt);
            }
        });
        jToggleButtonClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jToggleButtonClientesMousePressed(evt);
            }
        });
        jToggleButtonClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonClientesActionPerformed(evt);
            }
        });

        jToggleButtonPedidos.setBackground(new java.awt.Color(52, 62, 61));
        jToggleButtonPedidos.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jToggleButtonPedidos.setForeground(new java.awt.Color(255, 255, 255));
        jToggleButtonPedidos.setText("PEDIDOS");
        jToggleButtonPedidos.setBorder(null);
        jToggleButtonPedidos.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jToggleButtonPedidosStateChanged(evt);
            }
        });
        jToggleButtonPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jToggleButtonPedidosMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanelAppLayout = new javax.swing.GroupLayout(jPanelApp);
        jPanelApp.setLayout(jPanelAppLayout);
        jPanelAppLayout.setHorizontalGroup(
            jPanelAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAppLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToggleButtonClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jToggleButtonPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelAppLayout.setVerticalGroup(
            jPanelAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAppLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanelAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButtonClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButtonPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        jTabbedPane.addTab("     App     ", jPanelApp);

        jPanelCocina.setBackground(new java.awt.Color(99, 163, 117));

        jToggleButtonPlatos.setBackground(new java.awt.Color(52, 62, 61));
        jToggleButtonPlatos.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jToggleButtonPlatos.setForeground(new java.awt.Color(255, 255, 255));
        jToggleButtonPlatos.setText("PLATOS");
        jToggleButtonPlatos.setBorder(null);
        jToggleButtonPlatos.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jToggleButtonPlatosStateChanged(evt);
            }
        });
        jToggleButtonPlatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jToggleButtonPlatosMousePressed(evt);
            }
        });

        jToggleButtonIngredientes.setBackground(new java.awt.Color(52, 62, 61));
        jToggleButtonIngredientes.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jToggleButtonIngredientes.setForeground(new java.awt.Color(255, 255, 255));
        jToggleButtonIngredientes.setText("INGREDIENTES");
        jToggleButtonIngredientes.setBorder(null);
        jToggleButtonIngredientes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jToggleButtonIngredientesStateChanged(evt);
            }
        });
        jToggleButtonIngredientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jToggleButtonIngredientesMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCocinaLayout = new javax.swing.GroupLayout(jPanelCocina);
        jPanelCocina.setLayout(jPanelCocinaLayout);
        jPanelCocinaLayout.setHorizontalGroup(
            jPanelCocinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCocinaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToggleButtonPlatos, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(jToggleButtonIngredientes, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelCocinaLayout.setVerticalGroup(
            jPanelCocinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCocinaLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanelCocinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButtonIngredientes, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButtonPlatos, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        jTabbedPane.addTab("     Cocina     ", jPanelCocina);

        jPanelFondo.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane3.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPane3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jTableResultados.setBackground(new java.awt.Color(255, 255, 255));
        jTableResultados.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jTableResultados.setForeground(new java.awt.Color(52, 62, 61));
        jTableResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableResultados.setGridColor(new java.awt.Color(255, 255, 255));
        jTableResultados.setOpaque(false);
        jTableResultados.setSelectionBackground(new java.awt.Color(29, 132, 181));
        jTableResultados.setShowVerticalLines(false);
        jScrollPane3.setViewportView(jTableResultados);

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("Afinar Búsqueda");

        jTextFieldBusqueda.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldBusqueda.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextFieldBusqueda.setEnabled(false);
        jTextFieldBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBusquedaKeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("Buscar");

        jTextFieldBusqueda1.setBackground(new java.awt.Color(255, 255, 255));
        jTextFieldBusqueda1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextFieldBusqueda1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldBusqueda1KeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("Por:");

        jComboBoxBusqueda.setBackground(new java.awt.Color(255, 255, 255));
        jComboBoxBusqueda.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanelFondoLayout = new javax.swing.GroupLayout(jPanelFondo);
        jPanelFondo.setLayout(jPanelFondoLayout);
        jPanelFondoLayout.setHorizontalGroup(
            jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFondoLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelFondoLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldBusqueda1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1192, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelFondoLayout.setVerticalGroup(
            jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFondoLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldBusqueda1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBoxBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addGap(16, 16, 16)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jButtonModificar.setBackground(new java.awt.Color(255, 255, 255));
        jButtonModificar.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonModificar.setText("Modificar");
        jButtonModificar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonModificarMousePressed(evt);
            }
        });

        jButtonAdd.setBackground(new java.awt.Color(255, 255, 255));
        jButtonAdd.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jButtonAdd.setText("Añadir");
        jButtonAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonAddMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelFondo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(467, 467, 467))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jPanelFondo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAdd)
                    .addComponent(jButtonModificar))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPaneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPaneMousePressed
        if (jTabbedPane.getSelectedIndex() == 0) {
            jToggleButtonPedidos.setSelected(false);
            jToggleButtonClientes.setSelected(true);

        } else if (jTabbedPane.getSelectedIndex() == 1) {
            jToggleButtonPlatos.setSelected(true);
            jToggleButtonIngredientes.setSelected(false);
        }
    }//GEN-LAST:event_jTabbedPaneMousePressed

    private void jTextFieldBusquedaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBusquedaKeyReleased
        String texto = jTextFieldBusqueda.getText();
        filtrar(texto);
    }//GEN-LAST:event_jTextFieldBusquedaKeyReleased

    private void jToggleButtonClientesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButtonClientesMousePressed

        if (jToggleButtonClientes.isSelected()) {
            jToggleButtonClientes.doClick();
            jToggleButtonClientes.doClick();
        } else {
            jToggleButtonClientes.doClick();
        }

        jToggleButtonPedidos.setSelected(false);
    }//GEN-LAST:event_jToggleButtonClientesMousePressed

    private void jToggleButtonPedidosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButtonPedidosMousePressed
        if (jToggleButtonPedidos.isSelected()) {
            jToggleButtonPedidos.doClick();
            jToggleButtonPedidos.doClick();
        } else {
            jToggleButtonPedidos.doClick();
        }

        jToggleButtonClientes.setSelected(false);
    }//GEN-LAST:event_jToggleButtonPedidosMousePressed

    private void jToggleButtonPlatosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButtonPlatosMousePressed
        if (jToggleButtonPlatos.isSelected()) {
            jToggleButtonPlatos.doClick();
            jToggleButtonPlatos.doClick();
        } else {
            jToggleButtonPlatos.doClick();
        }

        jToggleButtonIngredientes.setSelected(false);
    }//GEN-LAST:event_jToggleButtonPlatosMousePressed

    private void jToggleButtonIngredientesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButtonIngredientesMousePressed
        if (jToggleButtonIngredientes.isSelected()) {
            jToggleButtonIngredientes.doClick();
            jToggleButtonIngredientes.doClick();
        } else {
            jToggleButtonIngredientes.doClick();
        }

        jToggleButtonPlatos.setSelected(false);
    }//GEN-LAST:event_jToggleButtonIngredientesMousePressed

    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneStateChanged

    }//GEN-LAST:event_jTabbedPaneStateChanged

    private void jToggleButtonPedidosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jToggleButtonPedidosStateChanged
        if (jToggleButtonPedidos.isSelected()) {
            try {
                tabla = "pedidos";
                creaTabla(c.devuelveResultSet("SELECT p.*, pl.nombre, pp.cantidad FROM pedidos p, pedidos_platos pp, platos pl WHERE p.id = pp.id_pedido AND pl.id = pp.id_plato"), tabla);
                rellenaComboBox();
            } catch (SQLException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jToggleButtonPedidosStateChanged

    private void jToggleButtonIngredientesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jToggleButtonIngredientesStateChanged
        if (jToggleButtonIngredientes.isSelected()) {
            try {
                tabla = "ingredientes";
                creaTabla(c.devuelveResultSet("SELECT * FROM ingredientes"), tabla);
                rellenaComboBox();

            } catch (SQLException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jToggleButtonIngredientesStateChanged

    /*
    Cada vez que se pulsa una tecla realiza una búsqueda de acuerdo con el atributo elegido en el comboBox
    y con lo escrito en el jtextfield
    
     */
    private void jTextFieldBusqueda1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldBusqueda1KeyReleased
        try {
            creaTabla(c.devuelveResultSet("SELECT * FROM " + tabla + " WHERE " + jComboBoxBusqueda.getSelectedItem().toString() + " LIKE '%" + jTextFieldBusqueda1.getText() + "%';"), tabla);
        } catch (SQLException ex) {
            Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTextFieldBusqueda1KeyReleased

    private void jToggleButtonPlatosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jToggleButtonPlatosStateChanged
        if (jToggleButtonPlatos.isSelected()) {
            try {
                tabla = "platos";
                creaTabla(c.devuelveResultSet("SELECT p.*, i.nombre, pi.cantidad FROM platos p, platos_ingredientes pi, ingredientes i WHERE p.id = pi.id_plato AND i.id = pi.id_ingrediente"), tabla);
                rellenaComboBox();

            } catch (SQLException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jToggleButtonPlatosStateChanged

    private void jToggleButtonClientesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jToggleButtonClientesStateChanged
        if (jToggleButtonClientes.isSelected()) {
            try {
                tabla = "clientes";
                creaTabla(c.devuelveResultSet("SELECT * FROM clientes"), tabla);
                rellenaComboBox();
            } catch (SQLException ex) {
                Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jToggleButtonClientesStateChanged


    private void jButtonAddMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonAddMousePressed
        Object[] mensaje = null;

        try {

            if (tabla == "clientes") {
                mensaje = addClientes("", "", "", "");
            } else if (tabla == "pedidos") {
                mensaje = addPedidos(Integer.parseInt(JOptionPane.showInputDialog("¿Cuántos artículos tendrá?")), "", "");
            } else if (tabla == "ingredientes") {
                mensaje = addIngredientes("", "");
            } else if (tabla == "platos") {
                //Pregunto cuánto ingredientes tendrá
                mensaje = addPlatos(Integer.parseInt(JOptionPane.showInputDialog("¿Cuántos ingredientes tendrá?")), "");
            } else {
                System.out.println("Tabla desconocida o no seleccionada");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        int opcion = JOptionPane.showConfirmDialog(null, mensaje, "Añadir " + tabla, JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {

            try {

                if (tabla == "clientes") {
                    inserta(mensaje);
                } else if (tabla == "pedidos") {
                    insertaPedido(mensaje);
                } else if (tabla == "ingredientes") {
                    inserta(mensaje);
                } else if (tabla == "platos") {
                    insertaPlato(mensaje);
                } else {
                    System.out.println("Tabla desconocida o no seleccionada");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }//GEN-LAST:event_jButtonAddMousePressed

    private void jButtonModificarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonModificarMousePressed
        if (jTableResultados.getSelectedRow() != -1) {
            String[] columnas = new String[jTableResultados.getColumnCount()];

            for (int i = 0; i < columnas.length; i++) {

                columnas[i] = jTableResultados.getModel().getValueAt(jTableResultados.getSelectedRow(), i).toString();

            }
            Object[] mensaje = null;
            switch (tabla) {
                case "clientes":
                    mensaje = addClientes(columnas[1], columnas[2], columnas[3], columnas[4]);
                    break;

                case "pedidos": {
                    try {
                        String query = "SELECT COUNT(*) FROM pedidos_platos WHERE id_pedido = '" + jTableResultados.getModel().getValueAt(jTableResultados.getSelectedRow(), 0) + "'";
                        System.out.println(query);
                        ResultSet rs = c.devuelveResultSet(query);
                        rs.next();
                        mensaje = addPedidos(rs.getInt("COUNT(*)"), columnas[1], columnas[2]);
                        rs.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

                case "ingredientes":
                    mensaje = addIngredientes(columnas[1], columnas[2]);
                    break;

                case "platos": {
                    try {
                        String query = "SELECT COUNT(*) FROM platos_ingredientes WHERE id_plato = '" + columnas[0] + "'";
                        System.out.println(query);
                        ResultSet rs = c.devuelveResultSet(query);
                        rs.next();
                        mensaje = addPlatos(rs.getInt("COUNT(*)"), columnas[1]);
                        rs.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

            int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Modifica " + tabla, JOptionPane.OK_CANCEL_OPTION);
            if (opcion == JOptionPane.OK_OPTION) {

                try {

                    if (tabla == "clientes") {
                        update(mensaje);
                    } else if (tabla == "pedidos") {
                        updatePedidos(mensaje);
                    } else if (tabla == "ingredientes") {
                        update(mensaje);
                    } else if (tabla == "platos") {
                        updatePlatos(mensaje);
                    } else {
                        System.out.println("Tabla desconocida o no seleccionada");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un campo en la tabla para modifcar");
        }
    }//GEN-LAST:event_jButtonModificarMousePressed

    private void jToggleButtonClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonClientesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButtonClientesActionPerformed

    /*
    ================================                    ================================
    ================================    FIN EVENTOS     ================================
    ================================                    ================================
    
     */
    //Método para añadir instrucciones cuando el programa cierre
    public void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                c.finalizaConexion();
            }
        });

    }

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
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Ventana.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Ventana v = new Ventana();
                    v.setVisible(true);
                    //Meto la instrucción que cerrará la conexión
                    v.attachShutDownHook();
                } catch (SQLException ex) {
                    Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonModificar;
    private javax.swing.JComboBox<String> jComboBoxBusqueda;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanelApp;
    private javax.swing.JPanel jPanelCocina;
    private javax.swing.JPanel jPanelFondo;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTableResultados;
    private javax.swing.JTextField jTextFieldBusqueda;
    private javax.swing.JTextField jTextFieldBusqueda1;
    private javax.swing.JToggleButton jToggleButtonClientes;
    private javax.swing.JToggleButton jToggleButtonIngredientes;
    private javax.swing.JToggleButton jToggleButtonPedidos;
    private javax.swing.JToggleButton jToggleButtonPlatos;
    // End of variables declaration//GEN-END:variables
}
