
import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Trab5_2_ExpansaoEqualizacao implements PlugIn, DialogListener {

	ImagePlus imagemOriginal = IJ.getImage();
	ImageProcessor processorOriginal = imagemOriginal.getProcessor();

	@Override
	public void run(String arg) {

		GenerateWindow();
	}

	public void GenerateWindow() {
		GenericDialog janela = new GenericDialog("Adjust Image");
		janela.addDialogListener(this);
		String[] checkboxLabels = { "Expand? ", "Equalize? " };
		boolean[] checkboxBools = { false, false };

		processorOriginal.snapshot();

		janela.addMessage("Esse PlugIn expande e/ou equaliza os histogramas da imagem.");
		janela.addCheckboxGroup(2, 1, checkboxLabels, checkboxBools);
		janela.showDialog();

		if (janela.wasCanceled()) {
			System.out.println("Cancelled");
			processorOriginal.reset();
			imagemOriginal.updateAndDraw();
		}
		if (janela.wasOKed()) {
			System.out.println("Ok");
		}

	}

	@Override
	public boolean dialogItemChanged(GenericDialog janela, AWTEvent e) {
		if (janela.wasCanceled()) {
			return false;
		}

		// Resetar a imagem toda vez antes de alterar ela
		processorOriginal.reset();

		boolean[] boxes = { janela.getNextBoolean(), janela.getNextBoolean() };

		System.out.println("Expand: %b\nEqualize: %b".formatted(boxes[0], boxes[1]));
		System.out.println("\n");

		AdjustImage(boxes);

		return true;
	}

	public void AdjustImage(boolean[] boxes) {
//		int pixel[] = new int[3];

//		ImagePlus imgBackup = IJ.createImage("Brightness", "8-bit", processorOriginal.getWidth(), processorOriginal.getHeight(), 1);
//		ImageProcessor processorBackup = imgBackup.getProcessor();

		if (boxes[0] == true) {
			expandHistogram();
		}

		if (boxes[1] == true) {
			equalizeHistogram();
		}

		imagemOriginal.updateAndDraw();
	}

	public void expandHistogram() {
		// Expansao

		int[] pixel = new int[3];
		int grayPixel;
		int min = 0;
		int max = 255;

		int lowerPixel = 256; // Inicializar maior número possível
		int higherPixel = -1; // Inicializar menor número possível

		// Encontrar os Pixeis Low e High
		for (int x = 0; x < processorOriginal.getWidth(); x++) {
			for (int y = 0; y < processorOriginal.getHeight(); y++) {
				grayPixel = processorOriginal.getPixel(x, y);

				if (grayPixel < lowerPixel) {
					lowerPixel = grayPixel;
				}
				if (grayPixel > higherPixel) {
					higherPixel = grayPixel;
				}
				// System.out.println("GrayPixel: %d Low= %d High= %d".formatted(pixel, lowerPixel, higherPixel));
			}
		}

		for (int x = 0; x < processorOriginal.getWidth(); x++) {
			for (int y = 0; y < processorOriginal.getHeight(); y++) {
				pixel = processorOriginal.getPixel(x, y, pixel);

				for (int cor = 0; cor < pixel.length; cor++) {
					pixel[cor] = (int) (min + (pixel[cor] - lowerPixel) * ((max - min) / (higherPixel - lowerPixel)));
				}
				processorOriginal.putPixel(x, y, pixel);
				// System.out.println("Expanded value: Low=%d High=%d R%d G%d B%d".formatted(lowerPixel, higherPixel, pixel[0], pixel[1], pixel[2]));
			}
		}

	}

	private void equalizeHistogram() {

		// Equalizacao

		int default_range = 256;
		int[] quant_pixels = new int[default_range];
		int grayPixel;

		double size = processorOriginal.getWidth() * processorOriginal.getHeight();
		double[] chance = new double[default_range];
		double[] accum_chance = new double[default_range];
		double new_range = 255;
		double[] ranged_chance = new double[default_range];
		int[] new_intensities = new int[default_range];

		// Inicializar variaveis da quantidade de intensidade dos pixels e do acumulador
		for (int i = 0; i < default_range; i++) {
			quant_pixels[i] = 0;
			accum_chance[i] = 0;
		}

		// Contando o numero de pixels de cada intensidade
		for (int x = 0; x < processorOriginal.getWidth(); x++) {
			for (int y = 0; y < processorOriginal.getHeight(); y++) {
				grayPixel = processorOriginal.getPixel(x, y);

				quant_pixels[grayPixel] = quant_pixels[grayPixel] + 1;

				// System.out.println("Pixel %d: %d".formatted(grayPixel, quant_pixels[grayPixel]));
			}
		}

		for (int i = 0; i < default_range; i++) {

			// Preenchendo as chances de encontrar cada intensidade
			chance[i] = (double) (quant_pixels[i] / size);
			// System.out.println("Chance %d: %f".formatted(i, chance[i]));

			// Preenchendo a probabilidade acumulada
			if (i < 1) {
				accum_chance[i] = accum_chance[i] + chance[i];
			} else {
				accum_chance[i] = accum_chance[i - 1] + chance[i];
			}
			// System.out.println("Chance/Accum. Chance %d: %f/%f".formatted(i, chance[i], accum_chance[i]));

			ranged_chance[i] = accum_chance[i] * new_range;
			new_intensities[i] = (int) ranged_chance[i];
			// System.out.println("Accum Chance %d: %d".formatted(i, new_intensities[i]));

		}

		for (int x = 0; x < processorOriginal.getWidth(); x++) {
			for (int y = 0; y < processorOriginal.getHeight(); y++) {
				grayPixel = processorOriginal.getPixel(x, y);

				processorOriginal.putPixel(x, y, new_intensities[grayPixel]);
			}
		}

	}
}