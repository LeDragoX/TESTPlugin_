
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.LUT;

public class Trab4_1_LUT implements PlugIn {

	public void run(String arg) {
		ImagePlus imagemOriginal = IJ.getImage();
		ImageProcessor processadorOriginal = imagemOriginal.getProcessor();
		int pixel[] = new int[3];
		byte[] r = new byte[256];
		byte[] g = new byte[256];
		byte[] b = new byte[256];

		ImagePlus imagemR = IJ.createImage("Image 1", "8-bit", processadorOriginal.getWidth(), processadorOriginal.getHeight(), 1);
		ImageProcessor processadorR = imagemR.getProcessor();

		ImagePlus imagemG = IJ.createImage("Image 2", "8-bit", processadorOriginal.getWidth(), processadorOriginal.getHeight(), 1);
		ImageProcessor processadorG = imagemG.getProcessor();

		ImagePlus imagemB = IJ.createImage("Image 3", "8-bit", processadorOriginal.getWidth(), processadorOriginal.getHeight(), 1);
		ImageProcessor processadorB = imagemB.getProcessor();

		for (int w = 0; w < processadorOriginal.getWidth(); w++) {
			for (int h = 0; h < processadorOriginal.getHeight(); h++) {
				pixel = processadorOriginal.getPixel(w, h, pixel);

				processadorR.putPixel(w, h, pixel[0]);
				processadorG.putPixel(w, h, pixel[1]);
				processadorB.putPixel(w, h, pixel[2]);
			}
		}

		SepararCanaisRGBemLUT(processadorR, 'r', r, g, b);
		SepararCanaisRGBemLUT(processadorG, 'g', r, g, b);
		SepararCanaisRGBemLUT(processadorB, 'b', r, g, b);

		imagemR.show();
		imagemG.show();
		imagemB.show();
	}

	public void SepararCanaisRGBemLUT(ImageProcessor processador, char canal_RGB, byte[] r, byte[] g, byte[] b) {

		if (canal_RGB == 'r') {
			for (int cor = 0; cor < 256; cor++) {
				r[cor] = (byte) cor;
				g[cor] = 0;
				b[cor] = 0;
			}
			LUT red = new LUT(r, g, b);
			processador.setLut(red);

		} else if (canal_RGB == 'g') {
			for (int cor = 0; cor < 256; cor++) {
				r[cor] = 0;
				g[cor] = (byte) cor;
				b[cor] = 0;
			}
			LUT green = new LUT(r, g, b);
			processador.setLut(green);

		} else if (canal_RGB == 'b') {
			for (int cor = 0; cor < 256; cor++) {
				r[cor] = 0;
				g[cor] = 0;
				b[cor] = (byte) cor;
			}
			LUT blue = new LUT(r, g, b);
			processador.setLut(blue);
		}
	}
}
