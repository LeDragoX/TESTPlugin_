
import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Trab4_2_EscalaCinza implements PlugIn, DialogListener {

	public void run(String arg) {
		ImagePlus imagemOriginal = IJ.getImage();
		ImageProcessor processorOriginal = imagemOriginal.getProcessor();

		String[] estrategia = { "1", "2", "3" };
		GerarDialogo(processorOriginal, estrategia);
	}

	public void GerarDialogo(ImageProcessor processor, String[] estrategia) {
		GenericDialog janela = new GenericDialog("Escalador de Cinza");
		janela.addDialogListener(this);
		janela.addMessage("Esse PlugIn irá converter a imagem em escala de cinza.");
		janela.addRadioButtonGroup("Selecione uma estratégia: ", estrategia, 1, 3, "1");
		janela.showDialog();

		if (janela.wasCanceled()) {
			System.out.println("Cancelled");
		}
		if (janela.wasOKed()) {
			String strategy = janela.getNextRadioButton();
			if (strategy == "1") {
				System.out.println("Botão 1 selecionado");
				Grayscale(processor, strategy);
			} else if (strategy == "2") {
				System.out.println("Botão 2 selecionado");
				Grayscale(processor, strategy);
			} else if (strategy == "3") {
				System.out.println("Botão 3 selecionado");
				Grayscale(processor, strategy);
			}
			System.out.println("Ok");
		}
	}

	public void Grayscale(ImageProcessor processor, String strategy) {
		int pixel[] = new int[3];

		ImagePlus imgSample = IJ.createImage("Grayscale", "8-bit", processor.getWidth(), processor.getHeight(), 1);
		ImageProcessor processorCopy = imgSample.getProcessor();

		for (int x = 0; x < processor.getWidth(); x++) {
			for (int y = 0; y < processor.getHeight(); y++) {
				pixel = processor.getPixel(x, y, pixel);

				processorCopy.putPixel(x, y, convertPixel(pixel, strategy));
			}
		}

		imgSample.show();
	}

	public int convertPixel(int[] pixel, String strategy) {
		int value;
		if (strategy == "1") {
			value = (int) ((pixel[0] + pixel[1] + pixel[2]) / 3);
			return value;

		} else if (strategy == "2") {
			double wr = 0.299;
			double wg = 0.587;
			double wb = 0.114;
			value = (int) (((pixel[0] * wr) + (pixel[1] * wg) + (pixel[2] * wb)));
			return value;

		} else if (strategy == "3") {
			double wr = 0.2125;
			double wg = 0.7154;
			double wb = 0.072;
			value = (int) (((pixel[0] * wr) + (pixel[1] * wg) + (pixel[2] * wb)));
			return value;
		}
		value = (int) pixel[0] + pixel[1] + pixel[2];
		// System.out.println("[debug] value: "+value);
		return value;
	}

	@Override
	public boolean dialogItemChanged(GenericDialog janela, AWTEvent e) {
		if (janela.wasCanceled()) {
			return false;
		}
		return true;
	}

}