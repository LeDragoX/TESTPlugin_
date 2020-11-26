
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.ImagePlus;
import ij.IJ;

public class Trab4_1_RGB implements PlugIn {
	public void run(String arg) {
		ImagePlus imagemOriginal = IJ.getImage();

		ImagePlus imagemR = imagemOriginal.duplicate();
		imagemR.setTitle("Image 1");
		modificar_imagem(imagemR, 255, 'r');

		ImagePlus imagemG = imagemOriginal.duplicate();
		imagemG.setTitle("Image 2");
		modificar_imagem(imagemG, 255, 'g');

		ImagePlus imagemB = imagemOriginal.duplicate();
		imagemB.setTitle("Image 3");
		modificar_imagem(imagemB, 255, 'b');

	}

	public void modificar_imagem(ImagePlus imagem, int valorRGB, char canal_RGB) {
		ImageProcessor processador = imagem.getProcessor();

		separar_canais_RGB(processador, imagem.getWidth(), imagem.getHeight(), valorRGB, canal_RGB);

		imagem.show();
	}

	public void separar_canais_RGB(ImageProcessor processador, int larg, int alt, int valorRGB, char canal_RGB) {
		int x, y, valorPixel[] = { 0, 0, 0 };

		for (x = 0; x < larg; x++) {
			for (y = 0; y < alt; y++) {
				valorPixel = processador.getPixel(x, y, valorPixel);

				if (canal_RGB == 'r') {
					// valorPixel[0] = valorRGB - valorPixel[0];
					valorPixel[1] = 0;
					valorPixel[2] = 0;
					processador.putPixel(x, y, valorPixel);
				}

				if (canal_RGB == 'g') {
					valorPixel[0] = 0;
					// valorPixel[1] = valorRGB - valorPixel[1];
					valorPixel[2] = 0;
					processador.putPixel(x, y, valorPixel);
				}

				if (canal_RGB == 'b') {
					valorPixel[0] = 0;
					valorPixel[1] = 0;
					// valorPixel[2] = valorRGB - valorPixel[2];
					processador.putPixel(x, y, valorPixel);
				}
			}
		}
	}
}
