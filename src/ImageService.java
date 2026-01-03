import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;




public class ImageService {

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

    
    public static ArrayList<BufferedImage> naloziSlikeIzMape(File[] datoteke) {
        ArrayList<BufferedImage> slikeSeznam = new ArrayList<>();

        if (datoteke != null) {
            for (File datoteka : datoteke) {
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

        return slikeSeznam;
    }


    public static void obdelajSliko(ArrayList<BufferedImage> slike, ArrayList<String> imenaKernelov) throws IOException {

        if (slike == null || slike.isEmpty()) {
            System.out.println("Nalaganje slik ni uspelo. Poskusite z drugo izbiro.");
            return;
        }

        System.out.println("-------------------------");


        ArrayList<float[][]> kerneli = Kernel.izbiraKernelov(imenaKernelov);
        // čas merimo izključno za izvedbo konvolucije
        // tukaj ne vključimo notri čas branja slika čas write na disk..
        long zacetniCas = System.currentTimeMillis();

        ArrayList<BufferedImage> rezultati =
                Konvolucija.konvolucijaRGBVecSlik(slike, kerneli);

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


}
