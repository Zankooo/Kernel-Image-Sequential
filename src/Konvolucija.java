import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JCheckBox;

import java.awt.image.BufferedImage;

public class Konvolucija {

    public static ArrayList<BufferedImage> izvediOperacije(ArrayList<BufferedImage> slike, ArrayList<float[][]> kerneli, JCheckBox cbMirror) {
        ArrayList<BufferedImage> rezultatiSlik = new ArrayList<>();
        
        long zacetniCas = System.currentTimeMillis();

        for (int i = 0; i < slike.size(); i++) {
            BufferedImage trenutnaSlika = slike.get(i);
            for (int j = 0; j < kerneli.size(); j++) {
                System.out.println("Za " + (i + 1) + ". sliko je končana operacija: " + Arrays.deepToString(kerneli.get(j)) );
                
                float[][] kernel = kerneli.get(j);
                trenutnaSlika = konvolucijaRGB(trenutnaSlika, kernel);
            }
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

    public static BufferedImage konvolucijaRGB(BufferedImage slika, float[][] kernel) {
        int kernelSirina = kernel[0].length;
        int kernelDolzina = kernel.length;

        if (kernelSirina % 2 == 0 || kernelDolzina % 2 == 0) {
            throw new IllegalArgumentException("Kernel mora imeti liho širino in višino (npr. 3x3, 5x5).");
        }

        int kernelPolmerXos = kernelSirina / 2;
        int kernelPolmerYos = kernelDolzina / 2;

        int sirinaSlike = slika.getWidth();
        int visinaSlike = slika.getHeight();

        BufferedImage novaSlika = new BufferedImage(sirinaSlike, visinaSlike, BufferedImage.TYPE_INT_ARGB);

        for (int poStolpcuDol = 0; poStolpcuDol < visinaSlike; poStolpcuDol++) {
            for (int poVrsticiDesno = 0; poVrsticiDesno < sirinaSlike; poVrsticiDesno++) {
                float vsotaRed = 0;
                float vsotaGreen = 0; 
                float vsotaBlue = 0;

                int centerARGB = slika.getRGB(poVrsticiDesno, poStolpcuDol);
                int prosojnost = (centerARGB >>> 24) & 0xFF;

                for (int kernelY = -kernelPolmerYos; kernelY <= kernelPolmerYos; kernelY++) {
                    for (int kernelX = -kernelPolmerXos; kernelX <= kernelPolmerXos; kernelX++) {
                        int px = omejimoRobnePiksle(poVrsticiDesno + kernelX, 0, sirinaSlike - 1);
                        int py = omejimoRobnePiksle(poStolpcuDol + kernelY, 0, visinaSlike - 1);
                        int argb = slika.getRGB(px, py);
                        
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

                int outARGB = (prosojnost << 24) | (outRed << 16) | (outGreen << 8) | outBlue;
                novaSlika.setRGB(poVrsticiDesno, poStolpcuDol, outARGB);
            }
        }

        return novaSlika;
    }

    private static int omejimoRobnePiksle(int stevilkaKiJoOmejimo, int minimalno, int maksimalno) {
        return Math.max(minimalno, Math.min(maksimalno, stevilkaKiJoOmejimo));
    }

}
