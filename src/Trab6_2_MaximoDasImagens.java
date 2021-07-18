
import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Trab6_2_MaximoDasImagens implements PlugIn, DialogListener {

	ImagePlus imgOriginal1 = IJ.getImage();
	ImageProcessor processadorOriginal1 = imgOriginal1.getProcessor();

	@Override
	public void run(String arg) {

		GenericDialog janela = new GenericDialog("Adjust Image");
		janela.addDialogListener(this);

		for (int i = 0; i < WindowManager.getIDList().length; i++) {
			System.out.println(
					"Img[%d] ID[%d]: %s".formatted(i, WindowManager.getIDList()[i], WindowManager.getImageTitles()[i]));
		}
		System.out.println("\n");

		janela.addMessage("Descrição: esse PlugIn irá manter o pixel de maior valor entre as duas imagens.");
		janela.showDialog();

		if (janela.wasCanceled()) {
			System.out.println("Cancelled");
			processadorOriginal1.reset();
			imgOriginal1.updateAndDraw();
		} else {
			System.out.println("Ok");
			adjustImage();
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

	private void adjustImage() {
		int[] grayPixels = new int[2];

		WindowManager.putBehind();
		ImagePlus imgOriginal2 = IJ.getImage();
		ImageProcessor processadorOriginal2 = imgOriginal2.getProcessor();

		ImagePlus imgSum = IJ.createImage("Processed image", "8-bit", imgOriginal1.getWidth(), imgOriginal1.getHeight(),
				1);
		ImageProcessor processorSum = imgSum.getProcessor();

		for (int x = 0; x < imgOriginal1.getWidth(); x++) {
			for (int y = 0; y < imgOriginal1.getHeight(); y++) {
				grayPixels[0] = processadorOriginal1.getPixel(x, y);
				grayPixels[1] = processadorOriginal2.getPixel(x, y);

				processorSum.putPixel(x, y, convertPixel(grayPixels));
			}
		}

		imgSum.updateAndDraw();
		imgSum.show();
	}

	private int convertPixel(int[] grayPixels) {
		int maxPixel = 0;

		if (grayPixels[0] >= grayPixels[1]) {
			maxPixel = grayPixels[0];
		} else {
			maxPixel = grayPixels[1];
		}

		return maxPixel;
	}
}
