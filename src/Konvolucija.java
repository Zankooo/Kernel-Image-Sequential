import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class Konvolucija {
    
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

    public static ArrayList<BufferedImage> konvolucijaRGBVecSlik(ArrayList<BufferedImage> slike, ArrayList<float[][]> kerneli) {
        
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
