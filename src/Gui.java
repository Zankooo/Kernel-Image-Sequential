import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Gui {

    public static void ustvariGui() {

        JFrame frame = ustvariOsnovnoPraznoOkno();

        JButton gumbEnaSlika = new JButton("Obdelaj izbrano sliko");
        JButton gumbMapa = new JButton("Obdelaj mapo slik");

        JComboBox<String> comboSlike = ustvariDropdownSlik();

        JCheckBox cbBlur = new JCheckBox("Blur");
        JCheckBox cbSharpen = new JCheckBox("Sharpen");
        JCheckBox cbSobelX = new JCheckBox("SobelX");
        JCheckBox cbGaussian = new JCheckBox("Gaussian");
        JCheckBox cbEdge = new JCheckBox("EdgeDetection");

        ArrayList<String> imenaKernelov = new ArrayList<>();

        ActionListener fairListener = ustvariFairListener(imenaKernelov);

        poveziCheckboxe(fairListener, cbBlur, cbSharpen, cbSobelX, cbGaussian, cbEdge);

        dodajListenerZaEnaSlika(gumbEnaSlika, comboSlike, imenaKernelov);
        dodajListenerZaMapa(gumbMapa, frame, imenaKernelov);

        JPanel panel = ustvariPanel(comboSlike, cbBlur, cbSharpen, cbSobelX, cbGaussian, cbEdge, gumbEnaSlika, gumbMapa);

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

    private static JComboBox<String> ustvariDropdownSlik() {
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
            JCheckBox cbEdge
    ) {
        cbBlur.addActionListener(fairListener);
        cbSharpen.addActionListener(fairListener);
        cbSobelX.addActionListener(fairListener);
        cbGaussian.addActionListener(fairListener);
        cbEdge.addActionListener(fairListener);
    }

    private static void dodajListenerZaEnaSlika(
            JButton gumbEnaSlika,
            JComboBox<String> comboSlike,
            ArrayList<String> imenaKernelov
    ) {
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

                ImageService.obdelajSliko(slikeSeznam, new ArrayList<>(imenaKernelov));

            } catch (IOException e) {
                System.out.println("Napaka pri obdelavi slike.");
            }
        });
    }

    private static void dodajListenerZaMapa(
            JButton gumbMapa,
            JFrame frame,
            ArrayList<String> imenaKernelov
    ) {
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
                ImageService.obdelajSliko(slikeSeznam, new ArrayList<>(imenaKernelov));
            } catch (IOException e) {
                System.out.println("Napaka pri obdelavi mape.");
            }
        });
    }

    

    private static JPanel ustvariPanel(
            JComboBox<String> comboSlike,
            JCheckBox cbBlur,
            JCheckBox cbSharpen,
            JCheckBox cbSobelX,
            JCheckBox cbGaussian,
            JCheckBox cbEdge,
            JButton gumbEnaSlika,
            JButton gumbMapa
    ) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(comboSlike);
        panel.add(cbBlur);
        panel.add(cbSharpen);
        panel.add(cbSobelX);
        panel.add(cbGaussian);
        panel.add(cbEdge);
        panel.add(gumbEnaSlika);
        panel.add(gumbMapa);

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
