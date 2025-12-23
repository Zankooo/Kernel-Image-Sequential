import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;


public class ImageProcessor {

    public static void main(String[] args) throws IOException {
        ustvariGui();
    }
    
    public static void ustvariGui() {

        JFrame frame = new JFrame("Image Processing");
        frame.setSize(600, 380);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // gumbi
        JButton gumbEnaSlika = new JButton("Obdelaj izbrano sliko");
        JButton gumbMapa = new JButton("Obdelaj mapo slik");

        // imena slik (v mapi "slike/")
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

        // dropdown z imeni slik
        JComboBox<String> comboSlike = new JComboBox<>(slike);

        // check box z imeni kernelov
        JCheckBox cbBlur = new JCheckBox("Blur");
        JCheckBox cbSharpen = new JCheckBox("Sharpen");
        JCheckBox cbSobelX = new JCheckBox("SobelX");
        JCheckBox cbGaussian = new JCheckBox("Gaussian");
        JCheckBox cbEdge = new JCheckBox("EdgeDetection");

        // ta koda skrbi da je pravilen vrstni red kernelov
        // kot jih izbiramo
        // skupni seznam kernelov (vrstni red = vrstni red klikanja)
        ArrayList<String> imenaKernelov = new ArrayList<>();

        // listener, ki fair vzdržuje vrstni red v imenaKernelov
        ActionListener fairListener = event -> {
            JCheckBox source = (JCheckBox) event.getSource();
            String ime = source.getText();

            if (source.isSelected()) {
                if (!imenaKernelov.contains(ime)) {
                    imenaKernelov.add(ime);  // doda na konec
                }
            } else {
                imenaKernelov.remove(ime);  // odstrani, ostali se pomaknejo
            }

            System.out.println("Trenutni vrstni red kernelov: " + imenaKernelov);
        };

        // povežemo checkboxe na fairListener
        cbBlur.addActionListener(fairListener);
        cbSharpen.addActionListener(fairListener);
        cbSobelX.addActionListener(fairListener);
        cbGaussian.addActionListener(fairListener);
        cbEdge.addActionListener(fairListener);




        // 1️⃣ OBDELAVA ENE SLIKE
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
                BufferedImage slika = naloziSliko("slike/" + imeSlike);
                // tukaj teoreticno en bi rabil arraylist ker je samo ena slika
                // ampak sem dal da lahko uporabim isto funkcijo kot 
                // kot pri načinu ko uporabnik nalozi sliko/i/e notri
                ArrayList<BufferedImage> slikeSeznam = new ArrayList<>();
                slikeSeznam.add(slika);

                System.out.println("-------------------------");
                System.out.println("Izbrana slika: " + imeSlike);
                System.out.println("Izbrani kerneli (zaporedje): " + imenaKernelov);

                // pošljemo kopijo seznama kernelov
                obdelajSliko(slikeSeznam, new ArrayList<>(imenaKernelov));

            } catch (IOException e) {
                System.out.println("Napaka pri obdelavi slike.");
            }
        });

        // 2️⃣ OBDELAVA MAPE SLIK
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

            ArrayList<BufferedImage> slikeSeznam = new ArrayList<>();
            
            // ce so datoteke oziroma slike ki so v mapi
            if (datoteke != null) {
                for (File datoteka : datoteke) {
                    // pogledamo za vsako če je pravi format!
                    try {
                        String ime = datoteka.getName().toLowerCase();
                        if (ime.endsWith(".png") || ime.endsWith(".jpg") || ime.endsWith(".jpeg")) {
                            slikeSeznam.add(ImageIO.read(datoteka));
                        }
                    } catch (IOException e) {
                        System.out.println("Napaka pri datoteki: " + datoteka.getName());
                    }
                }
            }
            // če ni pravi format je to konec
            if (slikeSeznam.isEmpty()) {
                System.out.println("V mapi ni veljavnih slik.");
                return;
            }

            System.out.println("-------------------------");
            System.out.println("Izbrana mapa: " + mapa.getAbsolutePath());
            System.out.println("Število najdenih slik v mapi: " + slikeSeznam.size());
            System.out.println("Izbrani kerneli (zaporedje): " + imenaKernelov);

            try {
                obdelajSliko(slikeSeznam, new ArrayList<>(imenaKernelov));
            } catch (IOException e) {
                System.out.println("Napaka pri obdelavi mape.");
            }
        });

        // postavitev gui
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


    public static void obdelajSliko(ArrayList<BufferedImage> slike, ArrayList<String> imenaKernelov) throws IOException {

        if (slike == null || slike.isEmpty()) {
            System.out.println("Nalaganje slik ni uspelo. Poskusite z drugo izbiro.");
            return;
        }

        System.out.println("-------------------------");


        ArrayList<float[][]> kerneli = izbiraKernelov(imenaKernelov);
        // čas merimo izključno za izvedbo konvolucije
        // tukaj ne vključimo notri čas branja slika čas write na disk..
        long zacetniCas = System.currentTimeMillis();

        ArrayList<BufferedImage> rezultati =
                konvolucijaRGBVecSlik(slike, kerneli);

        long koncaniCas = System.currentTimeMillis();
        double kolikoCasaJeTrajaloSek = (koncaniCas - zacetniCas) / 1000.0;

        System.out.println("Čas za izvedbo konvolucije" + slike.size() + "je trajal: "
                + kolikoCasaJeTrajaloSek + " sekund");

        // shranimo vsako sliko posebej
        for (int i = 0; i < rezultati.size(); i++) {
            BufferedImage rezultat = rezultati.get(i);
            ImageIO.write(
                    rezultat,
                    "png",
                    new File("ustvarjeneSlike/rezultat_" + i + ".png")
            );
        }

        System.out.println("Ustvarjene slike so na voljo v mapi: ustvarjeneSlike");
    }



    /**
     * Funkcija ki vrne izbran kernel, s katerim bomo manipulirali sliko
     * @return izbran kernel
     */
    public static ArrayList<float[][]> izbiraKernelov(ArrayList<String> izbraniKerneli) {

        ArrayList<float[][]> seznamKernelov = new ArrayList<>();

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

        for (String imeKernela : izbraniKerneli) {
            switch (imeKernela) {
                case "Blur" -> seznamKernelov.add(Blur);
                case "Sharpen" -> seznamKernelov.add(Sharpen);
                case "SobelX" -> seznamKernelov.add(SobelX);
                case "Gaussian" -> seznamKernelov.add(Gaussian);
                case "EdgeDetection" -> seznamKernelov.add(EdgeDetection);
                default -> {
                    System.out.println("Neznan kernel: " + imeKernela + " – uporabljen Blur.");
                    seznamKernelov.add(Blur);
                }
            }
        }

    return seznamKernelov;
}


    /**
     * Funkcija, ki izvede konvolucijo/e nad sliko,slikami zaporedno.
    
     * Za vsako vhodno sliko se izvede zaporedje vseh podanih kernelov.
     * Rezultat ene konvolucije se uporabi kot vhod v naslednjo,
     * zato se na vsaki sliki izvede celotna sekvenca operacij.
     
     * Na koncu funkcija vrne seznam končnih slik, kjer je
     * za vsako vhodno sliko ustvarjena natanko ena izhodna slika.
     
     * @param slike Seznam vhodnih slik (BufferedImage), nad katerimi se izvede obdelava.
     * @param kerneli Seznam kernelov (float[][]), ki se izvedejo zaporedno na vsaki sliki.
     * @return Seznam BufferedImage objektov, ki predstavljajo končne rezultate obdelave.
     */

    public static ArrayList<BufferedImage> konvolucijaRGBVecSlik(
        ArrayList<BufferedImage> slike,
        ArrayList<float[][]> kerneli) 
        {
        // kamor bomo shranjevali rezultate slik po konvoluciji
        ArrayList<BufferedImage> rezultatiSlik = new ArrayList<>();
        // vzamemo vsako sliko posebej
        for (int i = 0; i < slike.size(); i++) {
            BufferedImage trenutnaSlika = slike.get(i);
            // in na njen naredimo sekvenco vseh izbranih kernelov
            for (int j = 0; j < kerneli.size(); j++) {
                float[][] kernel = kerneli.get(j);
                // kličemo logično funkcijo
                trenutnaSlika = konvolucijaRGB(trenutnaSlika, kernel);
            }

            rezultatiSlik.add(trenutnaSlika);
        }

        return rezultatiSlik;
    }


    /**
     * Funkcija izvede 2D konvolucijo nad barvno sliko (RGB) z uporabo podanega kernela.
     *
     * Za vsak piksel vhodne slike izračuna novo vrednost barvnih komponent (R, G, B)
     * tako, da uporabi uteži iz kernela nad sosednjimi piksli.
     * Robovi slike so obravnavani s pomočjo omejevanja indeksov (clamp),
     * kar prepreči dostop izven meja slike.
     *
     * Alpha kanal (prosojnost) se ohrani iz izvornega piksla.
     * Rezultat konvolucije je nova slika enakih dimenzij kot vhodna slika.
     *
     * @param slika Vhodna slika tipa BufferedImage, nad katero se izvede konvolucija.
     * @param kernel 2D matrika uteži (float[][]), ki predstavlja konvolucijski kernel.
     *               Dimenzije kernela morajo biti lihe (npr. 3x3, 5x5).
     * @return Nova BufferedImage slika, ki predstavlja rezultat konvolucije.
     * @throws IllegalArgumentException Če ima kernel sodo širino ali višino.
     */

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

    /**
     * Funkcija omeji podano celo število na določen interval.
     *
     * Če je vrednost manjša od spodnje meje, se vrne spodnja meja.
     * Če je vrednost večja od zgornje meje, se vrne zgornja meja.
     * V nasprotnem primeru se vrne originalna vrednost.
     *
     * Funkcija se uporablja za preprečevanje dostopa izven
     * meja slike (npr. pri obdelavi robnih pikslov).
     *
     * @param v Vrednost, ki jo želimo omejiti.
     * @param lo Spodnja dovoljena meja.
     * @param hi Zgornja dovoljena meja.
     * @return Omejena vrednost znotraj intervala [lo, hi].
     */
    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }



}