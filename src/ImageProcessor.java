import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageProcessor {

    public static void main(String[] args) throws IOException {
        ustvariGui();
    }
    
    public static void ustvariGui() {
        JFrame frame = new JFrame("Image Processing");
        frame.setSize(420, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        // morajo biti ista imena kot spodaj da je match
        String[] kerneli = {
                "Blur",
                "Sharpen",
                "SobelX",
                "Gaussian",
                "EdgeDetection"
        };

        JComboBox<String> comboSlike = new JComboBox<>(slike);
        JComboBox<String> comboKernela = new JComboBox<>(kerneli);

        JButton gumb = new JButton("Zaženi konvolucijo");

        gumb.addActionListener(e -> {
            try {
                String imeSlike = (String) comboSlike.getSelectedItem();
                String potDoSlike = "slike/" + imeSlike;
                BufferedImage slika = naloziSliko(potDoSlike);

                String izbranKernelIme = (String) comboKernela.getSelectedItem();
                System.out.println("-------------------------");
                System.out.println("Izbran kernel ime: " + izbranKernelIme);
                obdelajSliko(slika, izbranKernelIme);
                float[][] izbranKernel = izbiraKernela(izbranKernelIme);

                konvolucijaRGB(slika, izbranKernel);

            } catch (IOException ex) {
                System.out.println("Napaka pri shranjevanju ali obdelavi slike: " + ex.getMessage());
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.GridLayout(3, 1, 10, 10));
        panel.add(comboSlike);
        panel.add(comboKernela);
        panel.add(gumb);

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Funkcija ki naloži sliko iz določene poti in jo vrne kot BufferedImage.
     * BufferedImage je klass za delo s slikami v RAMU
     * omogoča branje, risanje, spreminjanje in shranjevanje slik na nivoju posameznih pikslov.
     * @param potDoSlike Pot do slikovne datoteke.
     * @return BufferedImage ali null, če pride do napake.
     */

    public static BufferedImage naloziSliko(String potDoSlike) {
        try {
            File file = new File(potDoSlike);

            if (!file.exists()) {
                System.err.println("Napaka: Datoteka na poti " + potDoSlike + " ne obstaja.");
                return null;
            }
            // ImageIO.read(File) poskuša dešifrirati sliko in jo naložiti
            BufferedImage image = ImageIO.read(file);
            // če je poškodovana datoteka
            if (image == null) {
                System.err.println("Napaka: Datoteka je morda poškodovana ali pa ImageIO formata ne podpira.");
            }

            // vrnemo sliko
            return image;

            // če je i/o napaka
        } catch (IOException e) {
            System.err.println("Napaka pri I/O operaciji: " + e.getMessage());
            return null;
        }
    }

    public static void obdelajSliko(BufferedImage slika, String imeKernela) throws IOException {
        if (slika != null) {
            System.out.println("Širina: " + slika.getWidth() + " pikslov");
            System.out.println("Višina: " + slika.getHeight() + " pikslov");
            System.out.println("-------------------------");
            // IZBERI KERNEL FUNKCIJO KLIČEMO
            float[][] izbranKernel = izbiraKernela(imeKernela);
            // cas zacnemo merit pred zacetkom operacije konvolucija
            long zacetniCas = System.currentTimeMillis();
            // KLIČEMO FUNKCIJO KONVOLUCIJA
            BufferedImage novoUstvarjenaSlika = konvolucijaRGB(slika, izbranKernel);

            long koncaniCas = System.currentTimeMillis();
            double kolikoCasaJeTrajaloSek = (koncaniCas - zacetniCas) / 1000.0;
            System.out.println("Čas za izvedbo konvolucije je trajal; " + kolikoCasaJeTrajaloSek + " sekund");
            // sliko shranimo
            ImageIO.write(novoUstvarjenaSlika, "png", new File("ustvarjeneSlike/novoUstvarjenaSlika.jpg"));
            System.out.println("Ustvarjena slika je na voljo v mapi: ustvarjeneSlike");

        }
        // ce s sliko nekaj nu v redu
        else {
            System.out.println("Nalaganje slike ni uspelo. Poskusite z drugo sliko");
        }
    }


    /**
     * Funkcija ki vrne izbran kernel, s katerim bomo manipulirali sliko
     * @return izbran kernel
     */
    public static float[][] izbiraKernela(String izbranKernelIme) {

        float[][] Blur = {
                {1f/25, 1f/25, 1f/25, 1f/25, 1f/25},
                {1f/25, 1f/25, 1f/25, 1f/25, 1f/25},
                {1f/25, 1f/25, 1f/25, 1f/25, 1f/25},
                {1f/25, 1f/25, 1f/25, 1f/25, 1f/25},
                {1f/25, 1f/25, 1f/25, 1f/25, 1f/25}
        };

        float[][] Sharpen = {
                { 0, -1,  0},
                {-1,  5, -1},
                { 0, -1,  0}
        };

        float[][] SobelX = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };

        float[][] Gaussian = {
                {1f/16, 2f/16, 1f/16},
                {2f/16, 4f/16, 2f/16},
                {1f/16, 2f/16, 1f/16}
        };
        float[][] EdgeDetection = {
                { 0, -1,  0},
                {-1,  4, -1},
                { 0, -1,  0}
        };

        
        return switch (izbranKernelIme) {
            case "Blur" -> Blur;
            case "Sharpen" -> Sharpen;
            case "SobelX" -> SobelX;
            case "Gaussian" -> Gaussian;
            case "EdgeDetection" -> EdgeDetection;
            default -> {
                System.out.println("Napačna izbira, uporabljen bo blur.");
                yield Blur;
            }
        };

    }

    public static BufferedImage konvolucijaRGB(BufferedImage slika, float[][] kernel) {
        // koliko je stevilk oziroma dolzina prve vrstice kernela oz nulte
        // {1f/9, 1f/9, 1f/9} <- 0.vrstica
        // {1f/9, 1f/9, 1f/9} <- 1.vrstica
        // {1f/9, 1f/9, 1f/9} <- 2.vrstica

        // koliko je sirok kernel oziroma koliko elementov je v eni vrstici
        int kernelSirina = kernel[0].length;
        // koliko je visok oziroma koliko je vseh vrstic
        int kernelDolzina = kernel.length;

        //filter, če je kernel sploh v redu
        if (kernelSirina % 2 == 0 || kernelDolzina % 2 == 0) {
            throw new IllegalArgumentException("Kernel mora imeti liho širino in višino (npr. 3x3, 5x5).");
        }

        // polmer
        // pove, koliko pikslov levo/desno in gor/dol gleda kernel
        //  za 3×3 je polmer = 1
        int kernelPolmerXos = kernelSirina / 2; // ce imamo 3x3 matriko je sredinski un glavni in en piksel bo gledal levo oz desno
        int kernelPolmerYos = kernelDolzina / 2; // in en piksel bo gledal gor oziroma dol

        int sirinaSlike = slika.getWidth();
        int visinaSlike = slika.getHeight();

        // ustvarjanje nove slike
        BufferedImage novaSlika = new BufferedImage(sirinaSlike, visinaSlike, BufferedImage.TYPE_INT_ARGB);

        // prvi for loop da se premaknemo eno vrstico dol
        for (int y = 0; y < visinaSlike; y++) {
            // drugi for loop pa je da se premikamo po pikslih od leve proti desni celo vrstico
            // vsako vrstico posebi, začnemo levo zgori in piksel po piksel vrstico
            // in pol se pomaknemo v drugo vrstico
            for (int x = 0; x < sirinaSlike; x++) {

                // tukaj si pripravimo ker bomo seštevali
                float vsotaRed = 0, vsotaGreen = 0, vsotaBlue = 0;

                // vzamemo alpha vrednost centralnega piksla
                // >>> 24 premakne alpha na pravo mesto
                // & 0xFF odstrani ostale bite
                int centerARGB = slika.getRGB(x, y);
                int a = (centerARGB >>> 24) & 0xFF;

                // premikamo se po kernelu
                //
                for (int kernelY = -kernelPolmerYos; kernelY <= kernelPolmerYos; kernelY++) {
                    for (int kernelX = -kernelPolmerXos; kernelX <= kernelPolmerXos; kernelX++) {

                        int px = clamp(x + kernelX, 0, sirinaSlike - 1);
                        int py = clamp(y + kernelY, 0, visinaSlike - 1);

                        int argb = slika.getRGB(px, py);

                        int r = (argb >>> 16) & 0xFF;
                        int g = (argb >>> 8) & 0xFF;
                        int b = argb & 0xFF;

                        float weight = kernel[kernelY + kernelPolmerYos][kernelX + kernelPolmerXos];

                        vsotaRed += r * weight;
                        vsotaGreen += g * weight;
                        vsotaBlue += b * weight;
                    }
                }

                int outRed = clamp(Math.round(vsotaRed), 0, 255);
                int outGreen = clamp(Math.round(vsotaGreen), 0, 255);
                int outBlue = clamp(Math.round(vsotaBlue), 0, 255);

                int outARGB = (a << 24) | (outRed << 16) | (outGreen << 8) | outBlue;
                novaSlika.setRGB(x, y, outARGB);
            }
        }

        return novaSlika;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }



}