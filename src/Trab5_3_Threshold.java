
import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Trab5_3_Threshold implements PlugIn, DialogListener {

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

		janela.addMessage("Esse PlugIn irá Binarizar a imagem, deixando somente em 2 cores, Preto e Branco.");
		janela.addMessage("\nAbaixo do Threshold = Preto");
		janela.addMessage("Acima do Threshold = Branco");

		janela.addSlider("Threshold", 0, 255, 255, 1);
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

		int threshold = (int) janela.getNextNumber();

		System.out.println("Threshold: " + threshold);
		System.out.println("\n");

		AdjustImage(threshold);

		return true;
	}

	public void AdjustImage(int threshold) {
		int pixel[] = new int[3];

//		ImagePlus imgBackup = IJ.createImage("Brightness", "8-bit", processorOriginal.getWidth(), processorOriginal.getHeight(), 1);
//		ImageProcessor processorBackup = imgBackup.getProcessor();

		for (int x = 0; x < processorOriginal.getWidth(); x++) {
			for (int y = 0; y < processorOriginal.getHeight(); y++) {
				pixel = processorOriginal.getPixel(x, y, pixel);

				processorOriginal.putPixel(x, y, convertPixel(pixel, threshold));
			}
		}

		imagemOriginal.updateAndDraw();
	}

	private int[] convertPixel(int[] pixel, int t) {

		// Binarizacao
		for (int cor = 0; cor < pixel.length; cor++) {
			if (pixel[cor] >= t) {
				pixel[cor] = 0;
			} else if (pixel[cor] < t) {
				pixel[cor] = 255;
			}
		}
		// System.out.println("Threshold value: R%d G%d B%d".formatted(pixel[0], pixel[1], pixel[2]));

		return pixel;

	}
}