
import java.awt.AWTEvent;
import java.awt.Font;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Trab8_2_FiltrosNaoLineares implements PlugIn, DialogListener {

	ImagePlus imgOriginal1 = IJ.getImage();
	ImageProcessor processadorOriginal1 = imgOriginal1.getProcessor();

	@Override
	public void run(String arg) {

		GenericDialog janela = new GenericDialog("Adjust Image");
		janela.addDialogListener(this);
		String[] strategies = { "Passa-baixas de média", "Passa-altas", "Filtros de borda" };

		janela.addMessage("Descrição: esse PlugIn irá realizar filtros lineares em 8-bit.");
		janela.addRadioButtonGroup("Tipo\n", strategies, 3, 1, strategies[0]);
		janela.showDialog();

		if (janela.wasCanceled()) {
			System.out.println("Cancelled");
			processadorOriginal1.reset();
			imgOriginal1.updateAndDraw();
		} else {
			System.out.println("Ok");
			String selectedStrategy = janela.getNextRadioButton();
			if (selectedStrategy.equals(strategies[0])) {
				System.out.println("Botão '%s' selecionado".formatted(selectedStrategy));
			} else if (selectedStrategy.equals(strategies[1])) {
				System.out.println("Botão '%s' selecionado".formatted(selectedStrategy));
			} else if (selectedStrategy.equals(strategies[2])) {
				System.out.println("Botão '%s' selecionado".formatted(selectedStrategy));
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
		int grayPixel;
		float lowerPixel = 256; // Inicializar maior número possível
		float higherPixel = -1; // Inicializar menor número possível

		ImagePlus imgModificada = imgOriginal1.duplicate();
		ImageProcessor processadorModificado = imgModificada.getProcessor();		
		
		// Encontrar os Pixeis Low e High
		for (int x = 0; x < imgOriginal1.getWidth(); x++) {
			for (int y = 0; y < imgOriginal1.getHeight(); y++) {
				grayPixel = processadorOriginal1.getPixel(x, y);
				
				if (grayPixel + grayPixel < lowerPixel) {
					lowerPixel = grayPixel;
				}
				if (grayPixel + grayPixel > higherPixel) {
					higherPixel = grayPixel;
				}
				// System.out.println("Pixel: %d Low= %f High= %f".formatted(grayPixels, lowerPixel, higherPixel));
			}
		}


		for (int x = 0; x < imgOriginal1.getWidth(); x++) {
			for (int y = 0; y < imgOriginal1.getHeight(); y++) {
				grayPixel = processadorOriginal1.getPixel(x, y);

			}
		}

		imgModificada.updateAndDraw();
		imgModificada.show();
	}

	private int convertPixel(String[] strategies, String selectedStrategy, int grayPixel, float lowerPixel, float higherPixel) {
		int newPixel = 0;

		if (selectedStrategy.equals(strategies[0])) { // Passa-baixas de média
			
		} else if (selectedStrategy.equals(strategies[1])) { // Passa-altas
			System.out.println("Novo Pixel: %d".formatted(newPixel));
			
		} else if (selectedStrategy.equals(strategies[2])) { // Filtros de borda
			
		}

		return newPixel;
	}
}
