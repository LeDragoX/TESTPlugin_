
import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Trab6_1_SomarImagens implements PlugIn, DialogListener {

	ImagePlus imgOriginal1 = IJ.getImage();
	ImageProcessor processadorOriginal1 = imgOriginal1.getProcessor();

	@Override
	public void run(String arg) {

		GenericDialog janela = new GenericDialog("Adjust Image");
		janela.addDialogListener(this);
		String[] strategies = { "Truncamento", "Normalização", "Wrapping" };

		for (int i = 0; i < WindowManager.getIDList().length; i++) {
			System.out.println(
					"Img[%d] ID[%d]: %s".formatted(i, WindowManager.getIDList()[i], WindowManager.getImageTitles()[i]));
		}
		System.out.println("\n");

		janela.addMessage("Descrição: esse PlugIn irá Somar 2 imagens em 8-bit.");
		janela.addRadioButtonGroup("Tratamento de overflow\n", strategies, 3, 1, strategies[0]);
		janela.showDialog();

		if (janela.wasCanceled()) {
			System.out.println("Cancelled");
			processadorOriginal1.reset();
			imgOriginal1.updateAndDraw();
		} else {
			System.out.println("Ok");
			String selectedStrategy = janela.getNextRadioButton();
			if (selectedStrategy.equals(strategies[0])) {
				System.out.println("Botão %s selecionado".formatted(selectedStrategy));
			} else if (selectedStrategy.equals(strategies[1])) {
				System.out.println("Botão %s selecionado".formatted(selectedStrategy));
			} else if (selectedStrategy.equals(strategies[2])) {
				System.out.println("Botão %s selecionado".formatted(selectedStrategy));
			}
			adjustImage(strategies, selectedStrategy);
		}
	}

	@Override
	public boolean dialogItemChanged(GenericDialog janela, AWTEvent e) {
		if (janela.wasCanceled()) {
			return false;
		}

		processadorOriginal1.reset();

		return true;
	}

	private void adjustImage(String[] strategies, String selectedStrategy) {
		int[] grayPixels = new int[2];
		float lowerPixel = 256; // Inicializar maior número possível
		float higherPixel = -1; // Inicializar menor número possível

		WindowManager.putBehind();
		ImagePlus imgOriginal2 = IJ.getImage();
		ImageProcessor processadorOriginal2 = imgOriginal2.getProcessor();

		ImagePlus imgSum = IJ.createImage("Processed image", "8-bit", imgOriginal1.getWidth(), imgOriginal1.getHeight(), 1);
		ImageProcessor processorSum = imgSum.getProcessor();
		
		
		// Encontrar os Pixeis Low e High
		for (int x = 0; x < imgOriginal1.getWidth(); x++) {
			for (int y = 0; y < imgOriginal1.getHeight(); y++) {
				grayPixels[0] = processadorOriginal1.getPixel(x, y);
				grayPixels[1] = processadorOriginal2.getPixel(x, y);
				
				if (grayPixels[0] + grayPixels[1] < lowerPixel) {
					lowerPixel = grayPixels[0]+grayPixels[1];
				}
				if (grayPixels[0] + grayPixels[1] > higherPixel) {
					higherPixel = grayPixels[0]+grayPixels[1];
				}
				// System.out.println("Pixel somado: %d Low= %f High= %f".formatted(grayPixels[0]+grayPixels[1], lowerPixel, higherPixel));
			}
		}


		for (int x = 0; x < imgOriginal1.getWidth(); x++) {
			for (int y = 0; y < imgOriginal1.getHeight(); y++) {
				grayPixels[0] = processadorOriginal1.getPixel(x, y);
				grayPixels[1] = processadorOriginal2.getPixel(x, y);

				processorSum.putPixel(x, y, convertPixel(strategies, selectedStrategy, grayPixels, lowerPixel, higherPixel));
			}
		}

		imgSum.updateAndDraw();
		imgSum.show();
	}

	private int convertPixel(String[] strategies, String selectedStrategy, int[] grayPixels, float lowerPixel, float higherPixel) {
		// Soma crua dos pixels da img1 e img2
		int newPixel = grayPixels[0] + grayPixels[1];

		if (selectedStrategy.equals(strategies[0])) { // Truncamento
			if (newPixel > 255) {
				newPixel = 255;
			} else if (newPixel < 0) {
				newPixel = (-newPixel) - 255;
			}
			
		} else if (selectedStrategy.equals(strategies[1])) { // Normalização
			// newPixel = ((255/(510-255)) * (newPixel - 255)); // Interseção
			newPixel = (int) ((255 / (higherPixel - lowerPixel)) * (newPixel - lowerPixel));
			System.out.println("Novo Pixel: %d".formatted(newPixel));
			
		} else if (selectedStrategy.equals(strategies[2])) { // Wrapping
			if (newPixel > 255) {
				newPixel = newPixel - 255;
			} else if (newPixel < 0) {
				newPixel = (-newPixel) - 255;
			}
		}

		return newPixel;
	}
}
