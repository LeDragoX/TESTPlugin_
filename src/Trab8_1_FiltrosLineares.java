
import java.awt.AWTEvent;
import java.awt.Font;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Trab8_1_FiltrosLineares implements PlugIn, DialogListener {

	ImagePlus imgOriginal1 = IJ.getImage();
	ImageProcessor processadorOriginal1 = imgOriginal1.getProcessor();

	@Override
	public void run(String arg) {

		GenericDialog janela = new GenericDialog("Adjust Image");
		janela.addDialogListener(this);
		String[] strategies = { "Passa-baixas de média (Suaviza)", "Passa-altas (Realça)", "Filtros de borda" };

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
		int size = 3;
		int[][] nearbyPixels = new int[size][size];

		ImagePlus imgModificada = imgOriginal1.duplicate();
		imgModificada.setTitle("Linear filtered image");
		ImageProcessor processadorModificado = imgModificada.getProcessor();		
		
		for (int x = 1; x < imgOriginal1.getWidth() - 1; x++) {
			for (int y = 1; y < imgOriginal1.getHeight() - 1; y++) {
				grayPixel = processadorOriginal1.getPixel(x, y);
				initNearbyPixels(nearbyPixels, size, x, y);
				
				System.out.println("W: %d H: %d".formatted(x+1, y+1));
			}
		}

		imgModificada.updateAndDraw();
		imgModificada.show();
	}

	private int convertPixel(String[] strategies, String selectedStrategy, int grayPixel) {
		int newPixel = 0;

		if (selectedStrategy.equals(strategies[0])) { // Passa-baixas de média
			
		} else if (selectedStrategy.equals(strategies[1])) { // Passa-altas
			System.out.println("Novo Pixel: %d".formatted(newPixel));
			
		} else if (selectedStrategy.equals(strategies[2])) { // Filtros de borda
			
		}

		return newPixel;
	}
	
	public int[][] initNearbyPixels(int[][] nearbyPixels, int size, int x, int y) {
		int counter = 0;
		
		for (int i = x-1; i <= x+1 ; i++) {
			for (int j = y-1; j <= y+1 ; j++) {
				counter = counter + 1;
				nearbyPixels[i-(x-1)][j-(y-1)] = processadorOriginal1.getPixel(i, j);
				System.out.println("(%d, %d) nearbyPixels[%d][%d] = %d | Counter = %d".formatted(x+1, y+1, i-(x-1), j-(y-1), nearbyPixels[i-(x-1)][j-(y-1)], counter));
			}
		}
				
		return nearbyPixels;
		
	}
}
