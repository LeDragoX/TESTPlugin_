
import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Trab5_1_Dessaturacao implements PlugIn, DialogListener {

	ImagePlus imagemOriginal = IJ.getImage();
	ImageProcessor processorOriginal = imagemOriginal.getProcessor();

	@Override
	public void run(String arg) {

		GenerateWindow();
	}

	public void GenerateWindow() {
		GenericDialog janela = new GenericDialog("Adjust Image");
		janela.addDialogListener(this);

		processorOriginal.snapshot();

		janela.addMessage("Esse PlugIn irá ajustar o Brilho, Contraste, Solarização e Dessaturação da imagem.");
		janela.addSlider("Brightness: ", -255, 255, 0, 1);
		janela.addSlider("Contrast: ", -255, 255, 0, 1);
		janela.addMessage("Solarize");
		janela.addSlider("Min. Threshold: ", 0, 255, 255, 1);
		janela.addSlider("Max. Threshold: ", 0, 255, 0, 1);
		janela.addSlider("Dessaturate: ", 0.00, 1.00, 1.00, 0.01);
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

		int brilho = (int) janela.getNextNumber();
		float contraste = (float) janela.getNextNumber();
		int[] solarizacao = { (int) janela.getNextNumber(), (int) janela.getNextNumber() };
		float dessaturacao = (float) janela.getNextNumber();

		System.out.println("Brightness: " + brilho);
		System.out.println("Contrast: " + contraste);
		System.out.println("Solarize Min.: " + solarizacao[0]);
		System.out.println("Solarize Max.: " + solarizacao[1]);
		System.out.println("Dessaturation : " + dessaturacao);

		System.out.println("\n");

		AdjustImage(brilho, contraste, solarizacao, dessaturacao);

		return true;
	}

	public void AdjustImage(int brilho, float contraste, int[] solarizacao, float dessaturacao) {
		int pixel[] = new int[3];

//		ImagePlus imgBackup = IJ.createImage("Brightness", "8-bit", processorOriginal.getWidth(), processorOriginal.getHeight(), 1);
//		ImageProcessor processorBackup = imgBackup.getProcessor();

		for (int x = 0; x < processorOriginal.getWidth(); x++) {
			for (int y = 0; y < processorOriginal.getHeight(); y++) {
				pixel = processorOriginal.getPixel(x, y, pixel);

				processorOriginal.putPixel(x, y, convertPixel(pixel, brilho, contraste, solarizacao));
				processorOriginal.putPixel(x, y, dessaturate(pixel, dessaturacao));
			}
		}

		imagemOriginal.updateAndDraw();
	}

	private int[] convertPixel(int[] pixel, int b, float c, int[] solarizacao) {

		// Brilho
		for (int cor = 0; cor < pixel.length; cor++) {
			if (pixel[cor] + b > 255) {
				pixel[cor] = 255;
			} else if (pixel[cor] + b < 0) {
				pixel[cor] = 0;
			} else {
				pixel[cor] = (int) (pixel[cor] + b);
			}
		}
		// System.out.println("Brightnesses value: R%d G%d B%d".formatted(pixel[0],
		// pixel[1], pixel[2]));

		// Contraste
		float factor = (259 * (c + 255)) / (255 * (259 - c));

		for (int cor = 0; cor < pixel.length; cor++) {
			int pixel_validator = (int) (factor * (pixel[cor] - 128) + 128);

			if (pixel_validator > 255) {
				pixel[cor] = 255;
			} else if (pixel_validator < 0) {
				pixel[cor] = 0;
			} else {
				pixel[cor] = pixel_validator;
			}
		}
		// System.out.println("Contrasted value: Factor(%.2f) R%d G%d
		// B%d".formatted(factor, pixel[0], pixel[1], pixel[2]));

		// Solarizacao
		for (int cor = 0; cor < pixel.length; cor++) {

			if (pixel[cor] >= solarizacao[0] && pixel[cor] <= solarizacao[1]) {
				pixel[cor] = 255 - pixel[cor];
			}
		}
		// System.out.println("Solarized value: R%d G%d B%d".formatted(pixel[0],
		// pixel[1], pixel[2]));

		return pixel;
	}

	public int[] dessaturate(int[] pixel, float dessaturacao) {
		// Dessaturacao

		float avg = ((pixel[0] + pixel[1] + pixel[2]) / 3);

		for (int cor = 0; cor < pixel.length; cor++) {
			pixel[cor] = (int) (avg + dessaturacao * (pixel[cor] - avg));
		}

		// System.out.println("Dessaturated value: avg(%.2f), R%d G%d
		// B%d".formatted(avg, pixel[0], pixel[1], pixel[2]));
		return pixel;
	}
}