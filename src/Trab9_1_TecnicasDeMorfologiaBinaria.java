
import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Trab9_1_TecnicasDeMorfologiaBinaria implements PlugIn, DialogListener {

	ImagePlus imgOriginal = IJ.getImage();
	ImageProcessor processadorBackup = imgOriginal.getProcessor().duplicate();

	@Override
	public void run(String arg) {
		GenericDialog janela = new GenericDialog("Adjust Image");
		janela.addDialogListener(this);
		String[] strategies = { "Dilatação", "Erosão", "Fechamento (Dilatação + Erosão)", "Abertura (Erosão + Dilatação)", "Borda (outline)" };

		janela.addMessage("Descrição: esse PlugIn irá realizar Técnicas de Morfologia em imagens 8-bit binarizadas.");
		janela.addRadioButtonGroup("Tipo\n", strategies, 5, 1, strategies[0]);
		janela.showDialog();

		if (janela.wasCanceled()) {
			System.out.println("Cancelled");
			
			imgOriginal.setProcessor(processadorBackup.duplicate());
			imgOriginal.updateAndDraw();
		} else {
			System.out.println("Ok");
			String selectedStrategy = janela.getNextRadioButton();
			ImagePlus imgModificada = IJ.createImage("Result of %s".formatted(selectedStrategy), "8-bit", imgOriginal.getWidth(), imgOriginal.getHeight(), 1);
			
			if (selectedStrategy.equals(strategies[0])) {
				System.out.println("Botão '%s' selecionado".formatted(selectedStrategy));

				adjustImage(strategies, selectedStrategy, imgModificada);

			} else if (selectedStrategy.equals(strategies[1])) {
				System.out.println("Botão '%s' selecionado".formatted(selectedStrategy));

				adjustImage(strategies, selectedStrategy, imgModificada);

			} else if (selectedStrategy.equals(strategies[2])) {
				System.out.println("Botão '%s' selecionado".formatted(selectedStrategy));

				adjustImage(strategies, strategies[0], imgModificada); // Dilatação
				adjustImage(strategies, strategies[1], imgModificada); // Erosão

			} else if (selectedStrategy.equals(strategies[3])) {
				System.out.println("Botão '%s' selecionado".formatted(selectedStrategy));

				adjustImage(strategies, strategies[1], imgModificada); // Erosão
				adjustImage(strategies, strategies[0], imgModificada); // Dilatação

			} else if (selectedStrategy.equals(strategies[4])) {
				System.out.println("Botão '%s' selecionado".formatted(selectedStrategy));
				
				adjustImage(strategies, strategies[1], imgModificada); // Erosão
				adjustImage(strategies, selectedStrategy, imgModificada); // Borda (outline)

			}
			// imgModificada.show(); // Só usaria caso usasse a Imagem Modificada para mostrar as mudanças
		}
	}

	@Override
	public boolean dialogItemChanged(GenericDialog janela, AWTEvent e) {
		if (janela.wasCanceled()) {
			return false;
		}

		imgOriginal.getProcessor().reset();

		return true;
	}

	private void adjustImage(String[] strategies, String selectedStrategy, ImagePlus imgModificada) {
		System.out.println("Entrou a função de: %s".formatted(selectedStrategy));
		
		int grayPixel;
		int size = 3;
		int[][] nearbyPixels = new int[size][size];

		ImageProcessor processadorOriginalAtual = imgOriginal.getProcessor();
		ImageProcessor processadorModificado = imgModificada.getProcessor();

		
		for (int x = 0; x < imgOriginal.getWidth(); x++) {
			for (int y = 0; y < imgOriginal.getHeight(); y++) {
				grayPixel = processadorOriginalAtual.getPixel(x, y);
				nearbyPixels = initNearbyPixels(processadorOriginalAtual ,nearbyPixels, x, y);
				
				// System.out.println("\nW: %d H: %d\n".formatted(x+1, y+1));
				manualPutPixel(processadorModificado, strategies, selectedStrategy, grayPixel, nearbyPixels, x, y);
			}
		}

		imgModificada.updateAndDraw();
		imgOriginal.setProcessor(processadorModificado.duplicate()); // ATENÇÃO: A IMAGEM ORIGINAL PEGA A DÚPLICA DO PROCESSADOR MODIFICADO
	}

	public int[][] initNearbyPixels(ImageProcessor processadorOriginal, int[][] nearbyPixels, int x, int y) {

		if (x > 0 && y > 0 && x < imgOriginal.getWidth() && y < imgOriginal.getHeight()) {
			int counter = 0;
			
			for (int i = x-1; i <= x+1 ; i++) {
				for (int j = y-1; j <= y+1 ; j++) {
					counter = counter + 1;
					nearbyPixels[i-(x-1)][j-(y-1)] = processadorOriginal.getPixel(i, j);
					// System.out.println("(%d, %d) nearbyPixels[%d][%d] = %d | Counter = %d".formatted(x+1, y+1, i-(x-1), j-(y-1), nearbyPixels[i-(x-1)][j-(y-1)], counter));
				}
			}
		}
		
		return nearbyPixels;
	}
	
	private void manualPutPixel(ImageProcessor processadorModificado, String[] strategies, String selectedStrategy, int grayPixel, int[][] nearbyPixels, int x, int y) {

		if (selectedStrategy.equals(strategies[0])) { // Dilatação
			
			if (x > 0 && y > 0 && x < imgOriginal.getWidth() && y < imgOriginal.getHeight()) {

				if (grayPixel == 0) {
					for (int i = x-1; i <= x+1; i++) {
						for (int j = y-1; j <= y+1; j++) {
							processadorModificado.putPixel(i, j, 0);
						}
					}
				}
			}
			
		} else if (selectedStrategy.equals(strategies[1])) { // Erosão

			if (x > 0 && y > 0 && x < imgOriginal.getWidth() && y < imgOriginal.getHeight()) {
				if (nearbyPixels[0][1] == 0 && nearbyPixels[1][0] == 0 && nearbyPixels[1][2] == 0 && nearbyPixels[2][1] == 0) {
					processadorModificado.putPixel(x, y, 0);
				} else {
					processadorModificado.putPixel(x, y, 255);
				}
			}
			
		} else if (selectedStrategy.equals(strategies[2])) { // Fechamento (PARTE ESTÉTICA)

		} else if (selectedStrategy.equals(strategies[3])) { // Abertura (PARTE ESTÉTICA)
			
		} else if (selectedStrategy.equals(strategies[4])) { // Borda (outline)
			
			processadorModificado.putPixel(x, y, processadorBackup.getPixel(x, y));
			if (x > 0 && y > 0 && x < imgOriginal.getWidth() && y < imgOriginal.getHeight()) {
				if (grayPixel == 0) {
					processadorModificado.putPixel(x, y, 255);
				}
			}
			
		}

	}
	
}
