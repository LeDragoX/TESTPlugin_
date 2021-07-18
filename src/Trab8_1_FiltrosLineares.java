
import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
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

		janela.addMessage("Descrição: esse PlugIn irá realizar filtros lineares em imagens 8-bit.");
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
				nearbyPixels = initNearbyPixels(nearbyPixels, size, x, y);
				
				System.out.println("\nW: %d H: %d\n".formatted(x+1, y+1));
				processadorModificado.putPixel(x, y, convertPixel(strategies, selectedStrategy, grayPixel, nearbyPixels));
			}
		}

		imgModificada.updateAndDraw();
		imgModificada.show();
	}

	public int[][] initNearbyPixels(int[][] nearbyPixels, int size, int x, int y) {
		int counter = 0;
		
		for (int i = x-1; i <= x+1 ; i++) {
			for (int j = y-1; j <= y+1 ; j++) {
				counter = counter + 1;
				nearbyPixels[i-(x-1)][j-(y-1)] = processadorOriginal1.getPixel(i, j);
				// System.out.println("(%d, %d) nearbyPixels[%d][%d] = %d | Counter = %d".formatted(x+1, y+1, i-(x-1), j-(y-1), nearbyPixels[i-(x-1)][j-(y-1)], counter));
			}
		}
		
		return nearbyPixels;
	}
	
	private int convertPixel(String[] strategies, String selectedStrategy, int grayPixel, int[][] nearbyPixels) {
		float newPixel = 0;
//		 int[][] convolutionMatrix = new int[nearbyPixels.length][nearbyPixels.length];

		if (selectedStrategy.equals(strategies[0])) { // Passa-baixas de média
			
			for (int i = 0; i < nearbyPixels.length; i++) {
				for (int j = 0; j < nearbyPixels.length; j++) {
					newPixel += nearbyPixels[i][j];
				}
			}
			newPixel = newPixel / (nearbyPixels.length*nearbyPixels.length); // avg/(3*3)

		} else if (selectedStrategy.equals(strategies[1])) { // Passa-altas
			
			int[][] convolutionMatrix = {
										{0, -2, 0}, 
										{-2, 9, -2}, 
										{0, -2, 0}
										};
			
//			for (int i = 0; i < nearbyPixels.length; i++) {
//				for (int j = 0; j < nearbyPixels.length; j++) {
//					convolutionMatrix[i][j] = 0;
//				}
//			}
//			convolutionMatrix[0][0] = 4;
//			convolutionMatrix[nearbyPixels.length - 1][nearbyPixels.length - 1] = -4;
			
			for (int i = 0; i < nearbyPixels.length; i++) {
				for (int j = 0; j < nearbyPixels.length; j++) {
					newPixel += convolutionMatrix[i][j] * nearbyPixels[i][j];
				}
			}
			
			if (newPixel < 0) {
				newPixel = 0;
			}
						
		} else if (selectedStrategy.equals(strategies[2])) { // Filtros de borda
			int[][] convolutionMatrix = {
										{0, -2, 0}, 
										{-2, 8, -2}, 
										{0, -2, 0}
										};
						
			for (int i = 0; i < nearbyPixels.length; i++) {
				for (int j = 0; j < nearbyPixels.length; j++) {
					newPixel += convolutionMatrix[i][j] * nearbyPixels[i][j];
				}
			}
			
			if (newPixel < 0)
				newPixel = 0;
		}

		System.out.println("[%s] Novo Pixel: %.2f ~> %d".formatted(selectedStrategy, newPixel, (int) newPixel));
		return (int) newPixel;
	}
	
}
