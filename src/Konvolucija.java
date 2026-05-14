import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JCheckBox;

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

    public static ArrayList<BufferedImage> izvediOperacije(ArrayList<BufferedImage> slike, ArrayList<float[][]> kerneli, JCheckBox cbMirror) {
        
        // kamor bomo shranjevali rezultate slik po konvoluciji
        ArrayList<BufferedImage> rezultatiSlik = new ArrayList<>();
        
        // merimo čas
        long zacetniCas = System.currentTimeMillis();

        // vzamemo vsako sliko posebej
        for (int i = 0; i < slike.size(); i++) {
            BufferedImage trenutnaSlika = slike.get(i);
            // in na njen naredimo sekvenco vseh izbranih kernelov
            for (int j = 0; j < kerneli.size(); j++) {
                
                System.out.println("Za " + (i + 1) + ". sliko je končana operacija: " + Arrays.deepToString(kerneli.get(j)) );
                
                float[][] kernel = kerneli.get(j);
                // kličemo logično funkcijo
                trenutnaSlika = konvolucijaRGB(trenutnaSlika, kernel);
            }
            // OBRAT SLIKE OZ MIRROR SE NAREDI VEDNO NA KONCU!
            if (cbMirror.isSelected()) {
                trenutnaSlika = mirrorFunkcija(trenutnaSlika);
                System.out.println("Operacija Mirror je bila narejena!");
}
            System.out.println();
            rezultatiSlik.add(trenutnaSlika);
        }

        long koncaniCas = System.currentTimeMillis();
        double kolikoCasaJeTrajaloSek = (koncaniCas - zacetniCas) / 1000.0;

        System.out.println();
        System.out.println("Čas za izvedbo vsega zgoraj je trajal: " + kolikoCasaJeTrajaloSek + " sekund");
        

        return rezultatiSlik;
    }


    public static BufferedImage mirrorFunkcija(BufferedImage slika) {
        int sirina = slika.getWidth();
        int visina = slika.getHeight();

        BufferedImage out = new BufferedImage(sirina, visina, slika.getType());

        for (int y = 0; y < visina; y++) {
            for (int x = 0; x < sirina; x++) {
                int rgb = slika.getRGB(x, y);
                out.setRGB(sirina - 1 - x, y, rgb);
            }
        }

        return out;
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

        //filter za vsak slucaj, če je kernel sploh v redu. More bit liho 
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

        // ustvarjanje nove slike ki je sprva prazna in je velikosti toliko kot je slika ki jo obravnavamo
        BufferedImage novaSlika = new BufferedImage(sirinaSlike, visinaSlike, BufferedImage.TYPE_INT_ARGB);

        // prvi for loop da se premikamo po vrstici dol  
        for (int poStolpcuDol = 0; poStolpcuDol < visinaSlike; poStolpcuDol++) {
            // drugi for loop pa je da se premikamo po pikslih od leve proti desni celo vrstico
            for (int poVrsticiDesno = 0; poVrsticiDesno < sirinaSlike; poVrsticiDesno++) {

                // tukaj si pripravimo ker bomo seštevali
                float vsotaRed = 0;
                float vsotaGreen = 0; 
                float vsotaBlue = 0;

                // vzamemo alpha vrednost centralnega piksla
                // >>> 24 premakne alpha na pravo mesto
                // & 0xFF odstrani ostale bite
                // vzamemo prosojnost piksla
                int centerARGB = slika.getRGB(poVrsticiDesno, poStolpcuDol);
                int prosojnost = (centerARGB >>> 24) & 0xFF;

                
                // te dve zanki dolocata katere sosede bomo pogledali
                // te dva for loopa dolocata katere okoliske sosede bomo vprasali za naso novo barvo
                // te dve zanki dolocata katere sosede bomo vprasali
                // ce je 3x3 kernel, je polmer 1 in for loopa bosta sla od -1 do 1
                // ta vrstica gre po y osi gor dol
                for (int kernelY = -kernelPolmerYos; kernelY <= kernelPolmerYos; kernelY++) {
                    // // ta vrstica gre po x levo desno
                    for (int kernelX = -kernelPolmerXos; kernelX <= kernelPolmerXos; kernelX++) {

                        int px = omejimoRobnePiksle(poVrsticiDesno + kernelX, 0, sirinaSlike - 1);
                        int py = omejimoRobnePiksle(poStolpcuDol + kernelY, 0, visinaSlike - 1);
                        
                        int argb = slika.getRGB(px, py);
                        
                        // pridobimo rgb iz sosedov
                        int rdeca = (argb >>> 16) & 0xFF;
                        int zelena = (argb >>> 8) & 0xFF;
                        int modra = argb & 0xFF;

                        float weight = kernel[kernelY + kernelPolmerYos][kernelX + kernelPolmerXos];

                        vsotaRed += rdeca * weight;
                        vsotaGreen += zelena * weight;
                        vsotaBlue += modra * weight;
                    }
                }

                int outRed = omejimoRobnePiksle(Math.round(vsotaRed), 0, 255);
                int outGreen = omejimoRobnePiksle(Math.round(vsotaGreen), 0, 255);
                int outBlue = omejimoRobnePiksle(Math.round(vsotaBlue), 0, 255);
                // zapakirmo v eno stevilko
                int outARGB = (prosojnost << 24) | (outRed << 16) | (outGreen << 8) | outBlue;
                // na lokaciji pobarvamo piksel 
                novaSlika.setRGB(poVrsticiDesno, poStolpcuDol, outARGB);
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
     * Če smo na piksli (10, 10) in filter želi pogledati soseda levo zgoraj, 
     * je kernelX = -1 in kernelY = -1. Nova lokacija bi bila (9, 9).
     * Zakaj clamp: Če bi bili na pikslu (0, 0), bi moral izračun upoštevati barvo na pikslu (-1, -1). Brez clampa bi program puko
     * clamp pa ga prisili, da pogleda spet piksel (0, 0). In to uposteva
     * @param stevilkaKiJoOmejimo Vrednost, ki jo želimo omejiti.
     * @param minimalno Spodnja dovoljena meja. ce bi morali iti na 
     * @param maksimalno Zgornja dovoljena meja. 
     * @return Omejena vrednost znotraj intervala [lo, hi].
     */
    private static int omejimoRobnePiksle(int stevilkaKiJoOmejimo, int minimalno, int maksimalno) {
        return Math.max(minimalno, Math.min(maksimalno, stevilkaKiJoOmejimo));
    }

}
