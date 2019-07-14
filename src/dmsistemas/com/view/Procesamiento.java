package dmsistemas.com.view;

import dmsistemas.com.dao.FacturaDao;
import dmsistemas.com.dao.FacturaDaoImp;
import dmsistemas.com.model.Factura;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Procesamiento extends javax.swing.JInternalFrame implements Runnable {

    private Thread proceso1;
    private Thread proceso2;
    volatile boolean ejecutar = true;
    private String txtRuta;
    private final String buscarUUID = "<span style=\"display:inline-block;width:385px;\">";
    private final String buscarRFC = "</span></td><td style=\"WORD-BREAK:BREAK-ALL;\"><span style=\"display:inline-block;width:150px;\">";
    private final String buscarRa = "</span></td><td style=\"WORD-BREAK:BREAK-ALL;\"><span style=\"display:inline-block;width:350px;\">";
    private final String buscarF1 = "</span></td><td style=\"WORD-BREAK:BREAK-ALL;\"><span style=\"display:inline-block;width:200px;\">";
    private final String buscarF2 = "</span></td><td style=\"WORD-BREAK:BREAK-ALL;\"><span style=\"display:inline-block;width:240px;\">";
    private final String nuevoLink = "https://portalcfdi.facturaelectronica.sat.gob.mx/RecuperaCfdi.aspx?Datos=";
    private String cadenaLink;
    private String nuevaCadenaLink;
    List<Factura> listaFoliosFactura;
    private Factura factura;

    public Procesamiento() {
        initComponents();
        factura = new Factura();
    }

    public Thread getProceso1() {
        return proceso1;
    }

    public void setProceso1(Thread proceso1) {
        this.proceso1 = proceso1;
    }

    public Thread getProceso2() {
        return proceso2;
    }

    public void setProceso2(Thread proceso2) {
        this.proceso2 = proceso2;
    }

    public String getTxtRuta() {
        return txtRuta;
    }

    public void setTxtRuta(String txtRuta) {
        this.txtRuta = txtRuta;
    }

    public String getNuevaCadenaLink() {
        return nuevaCadenaLink;
    }

    public void setNuevaCadenaLink(String nuevaCadenaLink) {
        this.nuevaCadenaLink = nuevaCadenaLink;
    }

    public JTextArea getjTextArea1() {
        return jTextArea1;
    }

    public void setjTextArea1(JTextArea jTextArea1) {
        this.jTextArea1 = jTextArea1;
    }

    public JTextArea getjTextArea2() {
        return jTextArea2;
    }

    public void setjTextArea2(JTextArea jTextArea2) {
        this.jTextArea2 = jTextArea2;
    }

    public String getCadenaLink() {
        return cadenaLink;
    }

    public void setCadenaLink(String cadenaLink) {
        this.cadenaLink = cadenaLink;
    }

    public List<Factura> getListaFoliosFactura() {
        return listaFoliosFactura;
    }

    public void setListaFoliosFactura(List<Factura> listaFoliosFactura) {
        this.listaFoliosFactura = listaFoliosFactura;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        Thread hilos = Thread.currentThread();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Procesamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
//        while (hilos == proceso2 && ejecutar == true) {
//            
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Procesamiento.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        while (hilos == proceso1 && ejecutar == true) {
            try {
                this.txtRuta = jTextArea1.getText().replace("file://", "").trim();
                //this.txtRuta = "/home/dmsistemas/Escritorio/noviembreAromitalia.txt";
                procesar();
                //ejecutar = false;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        ejecutar = false;
    }

    public void procesar() {
        int contador = 1;
        listaFoliosFactura = new ArrayList<>();
        if (this.txtRuta != null) {
            try {
                FileReader fr = new FileReader(String.valueOf(this.txtRuta));
                BufferedReader br = new BufferedReader(fr);

                while ((this.cadenaLink = br.readLine()) != null) {
                    String rfc = null;
                    String pintar = null;
                    while (this.cadenaLink.contains(this.buscarUUID)) {
                        this.cadenaLink = this.cadenaLink.substring(this.cadenaLink.indexOf(this.buscarUUID) + this.buscarUUID.length(), this.cadenaLink.length());
                        if (this.cadenaLink.contains(this.buscarRFC)) {
                            rfc = this.cadenaLink;
                            pintar = rfc
                                    .replace(this.buscarRFC, "%")
                                    .replace(this.buscarRa, "%")
                                    .replace(this.buscarF1, "%")
                                    .replace(this.buscarF2, "").replace("+</span></td></tr><tr><td><div style=\"width:150px;", "");
                            String[] datos = pintar.split("%");
                            FacturaDao fDao = new FacturaDaoImp();
                            String dato = datos[0];
                            listaFoliosFactura = fDao.listaFacturas(dato);
                            String p0 = "";
                            String p1 = "";//WCXP
                            String p2 = "";
                            String p3 = "";
                            String p4 = "";
                            String p5 = "";//fecha de recepción
                            String p6 = "";//fecha de factura
                            for (int i = 0; i < listaFoliosFactura.size(); i++) {
                                p0 = listaFoliosFactura.get(i).getReferencia();
                                p1 = listaFoliosFactura.get(i).getEstatusSat();
                                p2 = listaFoliosFactura.get(i).getFoliowcxp().toString();
                                p3 = listaFoliosFactura.get(i).getFactura();
                                p4 = listaFoliosFactura.get(i).getImporteCouta().toString();
                                p5 = listaFoliosFactura.get(i).getFecha();
                                p6 = listaFoliosFactura.get(i).getFechaRecepcion();
                                factura.setId(listaFoliosFactura.get(i).getId());
                                if (p0.isEmpty()) {
                                    p0 = "No encontrado";
                                }
                                if (p1.isEmpty()) {
                                    p1 = "No encontrado";
                                }
                                if (p2.isEmpty()) {
                                    p2 = "No encontrado";
                                }
                                if (p3.isEmpty()) {
                                    p3 = "No encontrado";
                                }

                            }
                            try {
                                if (!p2.isEmpty()) {
                                    factura.setUuid(datos[0]);
                                    factura.setFoliowcxp(Integer.parseInt(p2));
                                    factura.setEstatusSat(datos[10]);
                                    FacturaDao facDao = new FacturaDaoImp();
                                    facDao.updateEstatusFactura(factura);
                                    factura = new Factura();
                                }

                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }

                            try {

                                jTextArea2.append("".concat(datos[0].concat("|").concat(datos[10]).concat("|").concat(p1).concat("|")
                                        .concat(datos[10]).concat("|").concat(p2).concat("|").concat(p0).concat("|").concat(datos[1])
                                        .concat("|").concat(datos[2]).concat("|").concat(datos[8]).concat("|$").concat(p4).concat("|").concat(p3).concat("|").concat(p5).concat(p6) concat("\n"))
                                        .replace("Cancelado sin aceptacin", "")
                                        .replace("Cancelado con aceptacin", "").replace("En proceso", "").replace("Plazo vencido", ""));
//                            System.out.println(datos[0]);
//                            System.out.println(datos[10]);

                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }

                        }

                        contador++;
                        System.out.println(contador);
                    }
                    ejecutar = false;
                }
                JOptionPane.showMessageDialog(null, "Se han procesado... " + (contador - 1), "Información", JOptionPane.WARNING_MESSAGE);

            } catch (HeadlessException | IOException ex) {
                System.err.println(ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor selecciona el archivo .txt", "Información", JOptionPane.WARNING_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("PROCESAR ARCHIVO");
        setToolTipText("PROCESAR ARCHIVO");

        jTextArea1.setBackground(new java.awt.Color(204, 204, 204));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Bitstream Vera Sans", 2, 14)); // NOI18N
        jTextArea1.setRows(1);
        jTextArea1.setToolTipText("ARRASTRE Y SUELTE AQUÍ SU ARCHIVO DE TEXTO");
        jScrollPane1.setViewportView(jTextArea1);

        jTextArea2.setBackground(new java.awt.Color(0, 51, 51));
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jTextArea2.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dmsistemas/com/view/gears1.png"))); // NOI18N
        jButton1.setText("Procesar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dmsistemas/com/view/draganddrop.png"))); // NOI18N
        jLabel2.setText("RUTA DEL ARCHIVO");

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 153, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("UUID");
        jLabel3.setOpaque(true);

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 204, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("ESTATUS SAT");
        jLabel4.setOpaque(true);

        jLabel6.setBackground(new java.awt.Color(0, 0, 0));
        jLabel6.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 204, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("CFDI PORTAL");
        jLabel6.setOpaque(true);

        jLabel8.setBackground(new java.awt.Color(0, 0, 0));
        jLabel8.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 204, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("CFDI PORTAL");
        jLabel8.setOpaque(true);

        jLabel9.setBackground(new java.awt.Color(0, 0, 0));
        jLabel9.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(102, 153, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("WCXP");
        jLabel9.setOpaque(true);

        jLabel10.setBackground(new java.awt.Color(0, 0, 0));
        jLabel10.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(102, 153, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("# RECEPCIÓN");
        jLabel10.setOpaque(true);

        jLabel11.setBackground(new java.awt.Color(0, 0, 0));
        jLabel11.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(102, 153, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("RFC");
        jLabel11.setOpaque(true);

        jInternalFrame1.setClosable(true);
        jInternalFrame1.setIconifiable(true);
        jInternalFrame1.setMaximizable(true);
        jInternalFrame1.setResizable(true);
        jInternalFrame1.setTitle("PROCESAR ARCHIVO");
        jInternalFrame1.setToolTipText("PROCESAR ARCHIVO");

        jTextArea3.setBackground(new java.awt.Color(204, 204, 204));
        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Bitstream Vera Sans", 2, 18)); // NOI18N
        jTextArea3.setRows(1);
        jTextArea3.setToolTipText("ARRASTRE Y SUELTE AQUÍ SU ARCHIVO DE TEXTO");
        jScrollPane3.setViewportView(jTextArea3);

        jTextArea4.setBackground(new java.awt.Color(0, 51, 51));
        jTextArea4.setColumns(20);
        jTextArea4.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jTextArea4.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea4.setRows(5);
        jScrollPane4.setViewportView(jTextArea4);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dmsistemas/com/view/cogs.png"))); // NOI18N
        jButton2.setText("Procesar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dmsistemas/com/view/enlarge.png"))); // NOI18N
        jLabel5.setText("RUTA DEL ARCHIVO");

        jLabel7.setBackground(new java.awt.Color(0, 0, 0));
        jLabel7.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 153, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("UUID");
        jLabel7.setOpaque(true);

        jLabel12.setBackground(new java.awt.Color(0, 0, 0));
        jLabel12.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 204, 0));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("ESTATUS SAT");
        jLabel12.setOpaque(true);

        jLabel13.setBackground(new java.awt.Color(0, 0, 0));
        jLabel13.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 204, 51));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("CFDI PORTAL");
        jLabel13.setOpaque(true);

        jLabel14.setBackground(new java.awt.Color(0, 0, 0));
        jLabel14.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 204, 0));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("CFDI PORTAL");
        jLabel14.setOpaque(true);

        jLabel15.setBackground(new java.awt.Color(0, 0, 0));
        jLabel15.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(102, 153, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("WCXP");
        jLabel15.setOpaque(true);

        jLabel16.setBackground(new java.awt.Color(0, 0, 0));
        jLabel16.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(102, 153, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("# RECEPCIÓN");
        jLabel16.setOpaque(true);

        jLabel17.setBackground(new java.awt.Color(0, 0, 0));
        jLabel17.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(102, 153, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("RFC");
        jLabel17.setOpaque(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1210, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jInternalFrame1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jInternalFrame1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel18.setBackground(new java.awt.Color(0, 0, 0));
        jLabel18.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 153, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("NO. FACTURA");
        jLabel18.setOpaque(true);

        jLabel19.setBackground(new java.awt.Color(0, 0, 0));
        jLabel19.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(102, 153, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("PROVEEDOR");
        jLabel19.setOpaque(true);

        jLabel20.setBackground(new java.awt.Color(0, 0, 0));
        jLabel20.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(102, 153, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("MONTO");
        jLabel20.setOpaque(true);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dmsistemas/com/view/excel.png"))); // NOI18N
        jButton3.setText("Exportar Excel");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dmsistemas/com/view/search.png"))); // NOI18N
        jButton4.setText("BUSCAR ARCHIVO .TXT");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1210, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 12, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addGap(454, 454, 454))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jButton4)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jInternalFrame1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        proceso1 = new Thread(this);
        proceso2 = new Thread(this);
        //proceso2.start();
        proceso1.start();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        Exportar();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            this.jTextArea1.setText(String.valueOf(file));

        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void Exportar() {
        try {
            JFileChooser archivo = new JFileChooser(System.getProperty("user.dir"));
            archivo.showSaveDialog(this);
            if (archivo.getSelectedFile() != null) {
                try (FileWriter guardado = new FileWriter(archivo.getSelectedFile())) {
                    guardado.write(jTextArea2.getText());
                    JOptionPane.showMessageDialog(rootPane, "El archivo fue guardado con éxito en la ruta establecida");
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    // End of variables declaration//GEN-END:variables
}
