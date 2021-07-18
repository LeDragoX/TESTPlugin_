
import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
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
		String[] strategies = { "Mediana", "Máximo", "Moda" };

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

		if (selectedStrategy.equals(strategies[0])) { // Mediana
			
			int[] orderedVector = new int[nearbyPixels.length * nearbyPixels.length];
			int aux = 0;
			int counter = 0;
			
			for (int i = 0; i < nearbyPixels.length; i++) {
				for (int j = 0; j < nearbyPixels.length; j++) {
					orderedVector[counter] = nearbyPixels[i][j];
					counter++;
				}
			}
			
			for (int i = 0; i < orderedVector.length; i++) {
				for (int j = 0; j < orderedVector.length; j++) {
					if (orderedVector[i] < orderedVector[j]) {
						aux = orderedVector[i];
						orderedVector[i] = orderedVector[j];
						orderedVector[j] = aux;
					}
				}
			}
			newPixel = orderedVector[(int) (orderedVector.length/2)];
			
			for (int i = 0; i < orderedVector.length; i++) {
				System.out.println("Ordered Vector[%d]: %d".formatted(i, orderedVector[i]));
			}
			
			// System.out.println("[%s] Novo Pixel[%.2f] ~> [%d]: %d".formatted(selectedStrategy, (float) orderedVector.length/2, (int) orderedVector.length/2, (int) newPixel));

		} else if (selectedStrategy.equals(strategies[1])) { // Máximo
			
			newPixel = nearbyPixels[0][0];
			
			for (int i = 0; i < nearbyPixels.length; i++) {
				for (int j = 0; j < nearbyPixels.length; j++) {
					if (nearbyPixels[i][j] > newPixel) {
						newPixel = nearbyPixels[i][j];
					}
				}
			}
						
		} else if (selectedStrategy.equals(strategies[2])) { // Moda
			
			int[] iVector = new int[nearbyPixels.length * nearbyPixels.length];
			int aux = 0;
			int counter = 0;
			
			for (int i = 0; i < nearbyPixels.length; i++) {
				for (int j = 0; j < nearbyPixels.length; j++) {
					iVector[counter] = nearbyPixels[i][j];
					counter++;
				}
			}
			
			for (int i = 0; i < iVector.length; i++) {
				counter = 0;
				for (int j = 0; j < iVector.length; j++) {
					if (iVector[i] == iVector[j]) {
						counter++;
					}
				}
				if (counter >= aux) {
					aux = counter;
					newPixel = iVector[i];
				}
			}
		}

		System.out.println("[%s] Novo Pixel: %.2f ~> %d".formatted(selectedStrategy, newPixel, (int) newPixel));
		return (int) newPixel;
	}
	
}
