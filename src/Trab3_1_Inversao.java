
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.ImagePlus;
import ij.IJ;

public class Trab3_1_Inversao implements PlugIn {
	public void run(String arg) {
		ImagePlus imgOriginal = IJ.getImage();

		ImagePlus img1 = imgOriginal.duplicate();
		img1.setTitle("Image 1");
		modificar_imagem(img1);

		ImagePlus img2 = imgOriginal.duplicate();
		img2.setTitle("Image 2");
		modificar_imagem(img2);

		ImagePlus img3 = imgOriginal.duplicate();
		img3.setTitle("Image 3");
		modificar_imagem(img3);
	}

	public void modificar_imagem(ImagePlus imagem) {
		ImageProcessor processador = imagem.getProcessor();

		inverter_pixels_RGB(processador, imagem.getWidth(), imagem.getHeight());

		imagem.show();
	}

	public void inverter_pixels_RGB(ImageProcessor processador, int larg, int alt) {
		int x, y, valorPixel[] = { 0, 0, 0 };

		for (x = 0; x < larg; x++) {
			for (y = 0; y < alt; y++) {
				valorPixel = processador.getPixel(x, y, valorPixel);
				valorPixel[0] = 255 - valorPixel[0];
				valorPixel[1] = 205 - valorPixel[1];
				valorPixel[2] = 155 - valorPixel[2];
				processador.putPixel(x, y, valorPixel);
			}
		}
	}
}
