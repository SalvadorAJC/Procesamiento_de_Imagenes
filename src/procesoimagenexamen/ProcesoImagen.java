package procesoimagenexamen;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ProcesoImagen extends javax.swing.JFrame {

    private BufferedImage foto;
    private String img = "c:/imagen/beat.jpeg";
    private String rutaCreada = "c:/imagen/beate.jpeg"; 
    private int col, fil;
    private Color color;
    private int[][] ima, aux;
    private int fils, cols;
    InputStream impu;
    ImageInputStream image1;
    BufferedImage imagen;
    String ruta, ruta2, ruta3, ruta4, ruta5;
    int mayor, vuelta = 0;
    private int histograma[][] = new int[256][2];
    private double Sk[][] = new double[256][2];
    private int imags[][], repetidos[][] = new int[256][2];
    private long S[][] = new long[256][2];

    public ProcesoImagen() {
        initComponents();
    }

    public void abirImagen() {
        JFileChooser abrirIma = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("jpg, bmp, gif, jpeg, jfif, tif", "jpg", "bmp", "jfif", "gif", "jpeg","tif");
        abrirIma.setFileFilter(filtro);
        int res = abrirIma.showOpenDialog(this);

        if (res == 0) {
            ruta = abrirIma.getSelectedFile().getPath();
            Image imaOriginal = new ImageIcon(ruta).getImage();
            ImageIcon oriIcon = new ImageIcon(imaOriginal.getScaledInstance(lblOriginal.getWidth(), lblOriginal.getHeight(), Image.SCALE_SMOOTH));
            lblOriginal.setIcon(oriIcon);
        }
    }

    public void mostrarMatriz() {
        DefaultTableModel modelo = (DefaultTableModel) Matriz.getModel();
        modelo.setRowCount(fil);
        modelo.setColumnCount(col);
        for (int x = 0; x < fil; x++) {
            for (int y = 0; y < col; y++) {
                modelo.setValueAt(ima[y][x], x, y);
            }
        }
    }

    public void matrizDatosBase() {
        try {
            ruta = ruta.replace("\\", "/");
            lblTransformacion.setText(ruta);
            impu = new FileInputStream(ruta);
            image1 = ImageIO.createImageInputStream(impu);
            imagen = ImageIO.read(image1);
            fil = imagen.getHeight();
            col = imagen.getWidth();
            ima = new int[fil][col];
            aux = new int[fil][col];
            for (int y = 0; y < col; y++) {
                for (int x = 0; x < fil; x++) {
                    int pixel = imagen.getRGB(y, x);
                    color = new Color(pixel);
                    ima[x][y] = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void matrizDatos() {
        try {
            ruta = ruta.replace("\\", "/");
            lblTransformacion.setText(ruta);
            impu = new FileInputStream(ruta);
            image1 = ImageIO.createImageInputStream(impu);
            imagen = ImageIO.read(image1);
            fil = imagen.getHeight();
            col = imagen.getWidth();
            ima = new int[col][fil];
            aux = new int[col][fil];
            for (int y = 0; y < col; y++) {
                for (int x = 0; x < fil; x++) {
                    int pixel = imagen.getRGB(y, x);
                    color = new Color(pixel);
                    ima[y][x] = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                    if (ima[y][x] >= 0 && ima[y][x] < 138) {
                        aux[y][x] = 0;
                    } else {
                        aux[y][x] = 255;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void calcularMayor() {
        int posx = 0, posy = 0;
        int inicial = ima[0][0], segundo = 0, fin = 0;
        for (int y = 0; y < col; y++) {
            for (int x = 0; x < fils; x++) {
                segundo = ima[x][y];
                if (inicial < segundo) {
                    inicial = ima[x][y];
                    posx = x;
                    posy = y;
                }
            }
        }
        mayor = ima[posx][posy];
        System.out.println("Mayor: " + mayor);
        calcularPr();
    }

    public void calcularPr() {
        System.out.println("CALCULAR Pr");
        double nk, MN = fils * col;
        double pk, suma = 0, s;
        for (int j = 0; j < 256; j++) {
            nk = repetidos[j][1];
            pk = nk / MN;
            s = pk * mayor;
            suma += s;
            S[j][1] = Math.round(suma);
        }
        construirImagen();
    }

    public void calcularRepetidos() {
        int sumaRepetidos = 0;
        for (int y = 0; y < col; y++) {
            for (int x = 0; x < fils; x++) {

                for (int i = 0; i < 256; i++) {
                    if (ima[x][y] == repetidos[i][0]) {
                        repetidos[i][1] += 1;
                    }
                }
            }
        }
        ecualizar();
    }

    public void ecualizar() {
        double histogramaAcomulado = 0, L = 256, num = 0;
        int MN = (fils * col);
        for (int x = 0; x < 256; x++) {
            histogramaAcomulado += repetidos[x][1];
            Sk[x][1] = (histogramaAcomulado * ((L - 1) / MN));
        }
        histogramaEcualizado();
    }

    public void construirImagen() {
        System.out.println("EQALZD");
        Color color_c;
        int color;
        BufferedImage imgEcualizada = new BufferedImage(fils, col, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < col; y++) {
            for (int x = 0; x < fils; x++) {
                for (int i = 0; i < 256; i++) {
                    if (ima[x][y] == S[i][0]) {
                        color = (int) S[i][1];
                        color_c = new Color(color, color, color);
                        imgEcualizada.setRGB(x, y, color_c.getRGB());
                    }
                }
            }
        }
        try {
            ImageIO.write(imgEcualizada, "jpg", new File(rutaCreada));
            System.out.println("La imagen ha sido guardada con éxito");
        } catch (Exception e) {
            System.out.println("error " + e);
        }
        histogramaEcualizado();
    }

    public void leerNueva() {
        try {
            impu = new FileInputStream(rutaCreada);
            image1 = ImageIO.createImageInputStream(impu);

            BufferedImage image = ImageIO.read(image1);
            fils = image.getWidth();
            col = image.getHeight();

            ima = new int[fils][col];
            System.out.println(fils);
            System.out.println(col);

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = image.getRGB(x, y);
                    Color color1 = new Color(pixel);
                    ima[x][y] = (color1.getRed() + color1.getGreen() + color1.getBlue()) / 3;
                }
            }
        } catch (IOException ex) {
            System.out.println("Error");
        }
        vuelta = 1;
    }

    public void histogramaEcualizado() {
        XYSeries series = new XYSeries("Escala de grises");
        XYSeriesCollection dataSet = new XYSeriesCollection();
        for (int i = 0; i < 256; i++) {
            series.add(i, Sk[i][1]);
        }
        dataSet.addSeries(series);
        JFreeChart jfc = ChartFactory.createXYLineChart("Histograma Ecualizado", "Intensidad", "Frecuencia", dataSet, PlotOrientation.VERTICAL, true, false, false);
        ChartPanel cp = new ChartPanel(jfc);
        if (vuelta == 0) {
            jPanelGrafica jpg = new jPanelGrafica();
            jpg.jPanelOri.setLayout(new java.awt.BorderLayout());
            jpg.jPanelOri.add(cp);
            jpg.jPanelOri.validate();
            jpg.setVisible(true);
            leerNueva();
        } else {
            jPanelGrafica2 jpg = new jPanelGrafica2();
            jpg.jPanelEcu.setLayout(new java.awt.BorderLayout());
            jpg.jPanelEcu.add(cp);
            jpg.jPanelEcu.validate();
            jpg.setVisible(true);
        }
    }

    public void ecualizarImagen() {
        try {
            impu = new FileInputStream(img);
            image1 = ImageIO.createImageInputStream(impu);
            BufferedImage image = ImageIO.read(image1);
            fils = image.getWidth();
            col = image.getHeight();
            ima = new int[fils][col];
            System.out.println(fils);
            System.out.println(col);

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = image.getRGB(x, y);
                    Color color1 = new Color(pixel);
                    ima[x][y] = (color1.getRed() + color1.getGreen() + color1.getBlue()) / 3;
                }
            }
        } catch (IOException ex) {
        }
        guardarImaBaseEcua();
    }

    public void guardarImaBaseEcua() {
        Color color_c;
        BufferedImage gris = new BufferedImage(fil, col, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < col; x++) {
            for (int y = 0; y < fil; y++) {
                color_c = new Color(ima[x][y], ima[x][y], ima[x][y]);
                gris.setRGB(x, y, color_c.getRGB());
            }
        }
        try {
            ruta5 = "c:/imagen/" + "Imagen Ecualizada.jpg";
            ImageIO.write(gris, "jpg", new File(ruta5));
            System.out.println("Imagen creada");
            histogramaEcualizado();
            histogramaOriginalEcualizado();
        } catch (Exception e) {
            System.out.println(e + "Error al guardar imagen");
        }
    }

    public void guardarImaBase() {
        Color color_c;
        BufferedImage gris = new BufferedImage(fil, col, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < col; x++) {
            for (int y = 0; y < fil; y++) {
                color_c = new Color(ima[x][y], ima[x][y], ima[x][y]);
                gris.setRGB(x, y, color_c.getRGB());
            }

        }
        try {
            ruta2 = "c:/imagen/beat.jpeg" + "NuevoMetodo103Base.jpg";
            ImageIO.write(gris, "jpg", new File(ruta2));
            System.out.println("Imagen creada");
        } catch (Exception e) {
            System.out.println(e + "Error al guardar imagen");
        }
    }

    public void imagen_negativa() {
        BufferedImage imagen_negativo = this.imagen;
        for (int i = 0; i < imagen_negativo.getWidth(); i++) {
            for (int j = 0; j < imagen_negativo.getHeight(); j++) {
                int color = imagen_negativo.getRGB(i, j);
                Color color_obtenido = new Color(color);
                int color_rojo = 255 - color_obtenido.getRed();
                int color_verde = 255 - color_obtenido.getGreen();
                int color_azul = 255 - color_obtenido.getBlue();
                Color color_convertido = new Color(color_rojo, color_verde, color_azul);
                imagen_negativo.setRGB(i, j, color_convertido.getRGB());
            }
        }
        try {
            ruta4 = "c:/imagen/beat.jpeg" + "TheBeast.jpg";
            ImageIO.write(imagen_negativo, "jpg", new File(ruta2));
            System.out.println("Imagen creada");
        } catch (Exception e) {
            System.out.println(e + "Error al guardar imagen");
        }
    }

    public void histogramaOriginal() {
        XYSeries series = new XYSeries("Escala de grises");
        XYSeriesCollection dataSet = new XYSeriesCollection();
        for (int i = 0; i < 256; i++) {
            series.add(i, ima[i][1]);
        }
        dataSet.addSeries(series);
        JFreeChart jfc = ChartFactory.createXYLineChart("Histograma Resultante", "Intensidad", "Frecuencia", dataSet, PlotOrientation.VERTICAL, true, false, false);
        ChartPanel cp = new ChartPanel(jfc);
        jPanelGrafica jpg = new jPanelGrafica();
        jpg.setVisible(true);
        jpg.setLayout(new java.awt.BorderLayout());
        jpg.add(cp);
        jpg.validate();
    }

    public void histogramaOriginalEcualizado() {
        XYSeries series = new XYSeries("Escala de grises");
        XYSeriesCollection dataSet = new XYSeriesCollection();

        for (int i = 0; i < 256; i++) {

            series.add(i, histograma[i][1]);
        }
        dataSet.addSeries(series);

        JFreeChart jfc = ChartFactory.createXYLineChart("Histograma Original", "Intensidad", "Frecuencia", dataSet, PlotOrientation.VERTICAL, true, false, false);

        ChartPanel cp = new ChartPanel(jfc);
        jPanelGrafica jpg = new jPanelGrafica();
        jpg.setVisible(true);
        jpg.jPanelOri.setLayout(new java.awt.BorderLayout());
        jpg.jPanelOri.add(cp);
        jpg.jPanelOri.validate();
    }

    public void guardarIma() {
        Color color1;
        BufferedImage gris = new BufferedImage(col, fil, BufferedImage.TYPE_3BYTE_BGR);
        for (int x = 0; x < fil; x++) {
            for (int y = 0; y < col; y++) {
                color1 = new Color(aux[y][x], aux[y][x], aux[y][x]);
                gris.setRGB(y, x, color1.getRGB());
            }
        }
        try {
            ruta2 = "c:/imagen/beatE.jpeg";
            ImageIO.write(gris, "jpg", new File(ruta2));
            System.out.println("Imagen creada");
        } catch (Exception e) {
            System.out.println(e + "Error al guardar imagen");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTransformacion = new javax.swing.JLabel();
        lblOriginal = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Matriz = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        Matriz.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        Matriz.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(Matriz);

        jMenu1.setText("Archivo");

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ima16/abrir_icon.png"))); // NOI18N
        jMenuItem1.setText("Abrir imagen");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ima16/guardar_icon.png"))); // NOI18N
        jMenuItem2.setText("Guardar imagen");
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu5.setText("Transformación");
        jMenu5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu5ActionPerformed(evt);
            }
        });

        jMenuItem3.setText("Matriz de datos");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem3);

        jMenuItem4.setText("Imagen binarizada");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem4);

        jMenuItem5.setText("Imagen gris");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem5);

        jMenuItem6.setText("Negativo");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem6);

        jMenuItem8.setText("Ecualizacion");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem8);

        jMenuItem7.setText("Histograma");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem7);

        jMenuBar1.add(jMenu5);

        jMenu4.setText("Ayuda");
        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1158, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblOriginal, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTransformacion, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(78, 78, 78))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTransformacion, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOriginal, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(75, 75, 75)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        abirImagen();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        matrizDatos();
        mostrarMatriz();

    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenu5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu5ActionPerformed

    }//GEN-LAST:event_jMenu5ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        matrizDatos();
        guardarIma();
        Image imaOri = new ImageIcon(ruta2).getImage();
        ImageIcon oriIco = new ImageIcon(imaOri.getScaledInstance(lblTransformacion.getWidth(), lblTransformacion.getHeight(), Image.SCALE_SMOOTH));
        lblTransformacion.setIcon(oriIco);
        System.out.println("Ejecucion");
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        matrizDatosBase();
        guardarImaBase();
        Image imaOri = new ImageIcon(ruta2).getImage();
        ImageIcon oriIco = new ImageIcon(imaOri.getScaledInstance(lblTransformacion.getWidth(), lblTransformacion.getHeight(), Image.SCALE_SMOOTH));
        lblTransformacion.setIcon(oriIco);
        System.out.println("Ejecucion");
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        matrizDatos();
        imagen_negativa();
        Image imaOri = new ImageIcon(ruta2).getImage();
        ImageIcon oriIco = new ImageIcon(imaOri.getScaledInstance(lblTransformacion.getWidth(), lblTransformacion.getHeight(), Image.SCALE_SMOOTH));
        lblTransformacion.setIcon(oriIco);
        System.out.println("Ejecucion");
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        histogramaOriginal();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        matrizDatos();
        ecualizarImagen();        
        Image imaOri = new ImageIcon(ruta5).getImage();
        ImageIcon oriIco = new ImageIcon(imaOri.getScaledInstance(lblTransformacion.getWidth(), lblTransformacion.getHeight(), Image.SCALE_SMOOTH));
        lblTransformacion.setIcon(oriIco);
        System.out.println("Ejecucion");
    }//GEN-LAST:event_jMenuItem8ActionPerformed

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
            java.util.logging.Logger.getLogger(ProcesoImagen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProcesoImagen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProcesoImagen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProcesoImagen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProcesoImagen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Matriz;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblOriginal;
    private javax.swing.JLabel lblTransformacion;
    // End of variables declaration//GEN-END:variables
}
