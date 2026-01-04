import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;


public class Gui {

    // basically orkestrator vsega
    public static void ustvariGui() {

        JFrame frame = ustvariOsnovnoPraznoOkno();

        JButton gumbEnaSlika = new JButton("Obdelaj izbrano sliko");
        JButton gumbMapa = new JButton("Obdelaj mapo slik");

        // vse pred izbrane slike
        JComboBox<String> comboSlike = ustvariDropdownPredIzbranihSlik();

        // checkbox za vse kernele
        JCheckBox cbBlur = new JCheckBox("Blur");
        JCheckBox cbSharpen = new JCheckBox("Sharpen");
        JCheckBox cbSobelX = new JCheckBox("SobelX");
        JCheckBox cbGaussian = new JCheckBox("Gaussian");
        JCheckBox cbEdge = new JCheckBox("EdgeDetection");
        JCheckBox cbMirror = new JCheckBox("Mirror");



        ArrayList<String> imenaKernelov = new ArrayList<>();
        // te dve funkciji sta povezani med sabo
        ActionListener fairListener = ustvariFairListener(imenaKernelov);
        poveziCheckboxe(fairListener, cbBlur, cbSharpen, cbSobelX, cbGaussian, cbEdge, cbMirror);


        // ko kliknem gumb za eno sliko
        dodajListenerZaEnaSlika(gumbEnaSlika, comboSlike, imenaKernelov);
        // ko kliknem gumb za obdelavo več slik
        dodajListenerZaMapa(gumbMapa, frame, imenaKernelov);

        // vse elemente dodamo na en panel
        JPanel panel = ustvariPanel(comboSlike, cbBlur, cbSharpen, cbSobelX, cbGaussian, cbEdge, cbMirror, gumbEnaSlika, gumbMapa);
        
        // dodamo ta panel na window. kot komponento basically
        frame.add(panel);

        prikaziOkno(frame);
    }

    /**
     * ustvarimo prazno okno
     */
    private static JFrame ustvariOsnovnoPraznoOkno() {
        JFrame frame = new JFrame("Kernel Image Processing");
        frame.setSize(600, 380);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    private static JComboBox<String> ustvariDropdownPredIzbranihSlik() {
        String[] slike = {
                "128x128-Slika.jpg",
                "256x256-Slika.jpg",
                "384x384-Slika.jpg",
                "512x512-Slika.jpg",
                "767x768-Slika.jpg",
                "1024x1024-Slika.jpg",
                "1536x1536-Slika.jpg",
                "2048x2048-Slika.jpg",
                "3072x3072-Slika.jpg",
                "4096x4096-Slika.jpg"
        };

        return new JComboBox<>(slike);
    }

    private static ActionListener ustvariFairListener(ArrayList<String> imenaKernelov) {
        return event -> {
            JCheckBox source = (JCheckBox) event.getSource();
            String ime = source.getText();

            if (source.isSelected()) {
                if (!imenaKernelov.contains(ime)) {
                    imenaKernelov.add(ime);
                }
            } else {
                imenaKernelov.remove(ime);
            }

            System.out.println("Trenutni vrstni red kernelov: " + imenaKernelov);
        };
    }

    private static void poveziCheckboxe(
            ActionListener fairListener,
            JCheckBox cbBlur,
            JCheckBox cbSharpen,
            JCheckBox cbSobelX,
            JCheckBox cbGaussian,
            JCheckBox cbEdge,
            JCheckBox cbMirror

    ) {
        cbBlur.addActionListener(fairListener);
        cbSharpen.addActionListener(fairListener);
        cbSobelX.addActionListener(fairListener);
        cbGaussian.addActionListener(fairListener);
        cbEdge.addActionListener(fairListener);
        cbMirror.addActionListener(fairListener);
    }

    private static void 
    dodajListenerZaEnaSlika(JButton gumbEnaSlika,JComboBox<String> comboSlike,ArrayList<String> imenaKernelov) {
        gumbEnaSlika.addActionListener(event -> {

            if (imenaKernelov.isEmpty()) {
                System.out.println("Noben kernel ni izbran.");
                return;
            }

            String imeSlike = (String) comboSlike.getSelectedItem();
            if (imeSlike == null) {
                System.out.println("Nobena slika ni izbrana.");
                return;
            }

            try {
                BufferedImage slika = ImageService.naloziSliko("slike/" + imeSlike);

                ArrayList<BufferedImage> slikeSeznam = new ArrayList<>();
                slikeSeznam.add(slika);

                System.out.println("-------------------------");
                System.out.println("Izbrana slika: " + imeSlike);
                System.out.println("Izbrani kerneli (zaporedje): " + imenaKernelov);

                ImageService.izvediOperacijeSlikam(slikeSeznam, new ArrayList<>(imenaKernelov));

            } catch (IOException e) {
                System.out.println("Napaka pri obdelavi slike.");
            }
        });
    }


    private static void dodajListenerZaMapa(JButton gumbMapa,JFrame frame,ArrayList<String> imenaKernelov) {
        
        gumbMapa.addActionListener(event -> {

            if (imenaKernelov.isEmpty()) {
                System.out.println("Noben kernel ni izbran.");
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int rezultat = chooser.showOpenDialog(frame);
            if (rezultat != JFileChooser.APPROVE_OPTION) return;

            File mapa = chooser.getSelectedFile();
            File[] datoteke = mapa.listFiles();

            ArrayList<BufferedImage> slikeSeznam = ImageService.naloziSlikeIzMape(datoteke);

            if (slikeSeznam.isEmpty()) {
                System.out.println("V mapi ni veljavnih slik.");
                return;
            }

            System.out.println("-------------------------");
            System.out.println("Izbrana mapa: " + mapa.getAbsolutePath());
            System.out.println("Število najdenih slik v mapi: " + slikeSeznam.size());
            System.out.println("Izbrani kerneli (zaporedje): " + imenaKernelov);

            try {
                ImageService.izvediOperacijeSlikam(slikeSeznam, new ArrayList<>(imenaKernelov));
            } catch (IOException e) {
                System.out.println("Napaka pri obdelavi mape.");
            }
        });
    }

    // funkcija ki ustvari panel in naredi kako bo izgledal vizualno
    private static JPanel ustvariPanel(
        JComboBox<String> comboSlike,
        JCheckBox cbBlur,
        JCheckBox cbSharpen,
        JCheckBox cbSobelX,
        JCheckBox cbGaussian,
        JCheckBox cbEdge,
        JCheckBox cbMirror,
        JButton gumbEnaSlika,
        JButton gumbMapa
    ) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel zgoraj = new JPanel(new BorderLayout());
        zgoraj.add(comboSlike, BorderLayout.CENTER);

        JPanel checkboxi = new JPanel(new GridLayout(0, 2, 12, 8));
        checkboxi.setBorder(BorderFactory.createTitledBorder("Kerneli (izberi vsaj enega)"));
        checkboxi.add(cbBlur);
        checkboxi.add(cbSharpen);
        checkboxi.add(cbSobelX);
        checkboxi.add(cbGaussian);
        checkboxi.add(cbEdge);
        checkboxi.add(cbMirror);

        JPanel spodaj = new JPanel(new GridLayout(1, 2, 12, 0));
        spodaj.add(gumbEnaSlika);
        spodaj.add(gumbMapa);

        panel.add(zgoraj, BorderLayout.NORTH);
        panel.add(checkboxi, BorderLayout.CENTER);
        panel.add(spodaj, BorderLayout.SOUTH);

        return panel;
    }



    /**
     * Funkcija ki prikaže okno
     */
    private static void prikaziOkno(JFrame frame) {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
