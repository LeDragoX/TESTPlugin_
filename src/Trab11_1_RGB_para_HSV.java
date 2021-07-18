
import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class Trab11_1_RGB_para_HSV implements PlugIn, DialogListener {

	ImagePlus imgOriginal = IJ.getImage();
	ImageProcessor processadorBackup = imgOriginal.getProcessor().duplicate();
	
	@Override
	public void run(String arg) {
		imgOriginal.getProcessor().snapshot();
		
		GenericDialog janela = new GenericDialog("Adjust Image");
		janela.addDialogListener(this);

		janela.addMessage("Descrição: \n"
						+ "Esse PlugIn irá converter as camadas RGB para HSV e\n"
						+ "abrir uma imagem para cada camada.");
		janela.showDialog();

		if (janela.wasCanceled()) {
			System.out.println("Cancelled");
			
			imgOriginal.getProcessor().reset();
			imgOriginal.updateAndDraw();
		} else {
			System.out.println("Ok");
			adjustImage();
		}
	}
	
	private void adjustImage() {
		int[] pixel = new int[3];

		ImageProcessor processorOriginalAtual = imgOriginal.getProcessor();
		
		ImagePlus imgHue = IJ.createImage("Hue", "8-bit", imgOriginal.getWidth(), imgOriginal.getHeight(), 1);
		ImagePlus imgSaturation = IJ.createImage("Saturation", "8-bit", imgOriginal.getWidth(), imgOriginal.getHeight(), 1);
		ImagePlus imgVBrightness = IJ.createImage("Brightness", "8-bit", imgOriginal.getWidth(), imgOriginal.getHeight(), 1);
		ImageProcessor processorHue = imgHue.getProcessor();
		ImageProcessor processorSaturation = imgSaturation.getProcessor();
		ImageProcessor processorVBrightness = imgVBrightness.getProcessor();
		
		for (int x = 0; x < processorOriginalAtual.getWidth(); x++) {
			for (int y = 0; y < processorOriginalAtual.getHeight(); y++) {
				pixel = processorOriginalAtual.getPixel(x, y, pixel);
				
				processorHue.putPixel(x, y, hue(pixel));
				processorSaturation.putPixel(x, y, saturation(pixel));
				processorVBrightness.putPixel(x, y, brightness(pixel));
				
			}
		}
		
		imgHue.show();
		imgSaturation.show();
		imgVBrightness.show();
		
	}
	
	
	
	private int hue(int[] pixel) {
		// A divisão é para limitar o valor de MIN e MAX entre 0 e 1
		float MIN = (float) pixel[0] / 255;
		float MAX = (float) pixel[0] / 255;
		
		float R = (float) pixel[0] / 255;
		float G = (float) pixel[1] / 255;
		float B = (float) pixel[2] / 255;
		
		float HueValue = 255;
		// Gatilho para entender de onde sai o IF
		int trigger = 0;
		
		for (int cor = 0; cor < pixel.length; cor++) {
			if ((float) pixel[cor] / 255 > MAX) {
				MAX = (float) pixel[cor] / 255;
			}
			if ((float) pixel[cor] / 255 < MIN) {
				MIN = (float) pixel[cor] / 255;
			}
		}
		
		if (MAX == R) {
			if (G >= B) {
				HueValue = (60 * ((G-B)/(MAX-MIN)) + 0);
				trigger = 1;
			} else {
				HueValue = (60 * ((G-B)/(MAX-MIN)) + 360);
				trigger = 2;
			}
		}
		
		if (MAX == G) {
			HueValue = (60 * ((B-R)/(MAX-MIN)) + 120);
			trigger = 3;
		}
		if (MAX == B) {
			HueValue = (60 * ((R-G)/(MAX-MIN)) + 240);
			trigger = 4;
		}
		
//		System.out.println("[Matiz | Gatilho %d] Valor de HueValue: %f | Saída: %d".formatted(trigger, HueValue, (int) (HueValue)));
		
		return (int) (HueValue);
	}

	private int saturation(int[] pixel) {
		// A divisão é para limitar o valor de MIN e MAX entre 0 e 1
		float MIN = (float) pixel[0] / 255;
		float MAX = (float) pixel[0] / 255;
		float SatValue;
		
		for (int cor = 0; cor < pixel.length; cor++) {
			if ((float) pixel[cor] / 255 > MAX) {
				MAX = (float) pixel[cor] / 255;
			}
			if ((float) pixel[cor] / 255 < MIN) {
				MIN = (float) pixel[cor] / 255;
			}
		}
		if (MAX > 0) {
			SatValue = (MAX - MIN)/MAX;
		} else {
			SatValue = 0;
		}
		
//		System.out.println("[Saturação] Valor de SatValue: %f | Saída: %d".formatted(SatValue, (int) (SatValue * 255)));
		
		// Deve retornar um valor legível para o PutPixel
		return (int) (SatValue * 255);
	}

	private int brightness(int[] pixel) {
		// A divisão é para limitar o valor de Max entre 0 e 1
		float MAX = (float) pixel[0] / 255;
		
		for (int cor = 0; cor < pixel.length; cor++) {
			if ((float) pixel[cor] / 255 > MAX) {
				MAX = (float) pixel[cor] / 255;
			}
		}
//		System.out.println("[Brilho] Valor de MAX: %f | Saída: %d".formatted(MAX, (int) (MAX * 255)));
		
		// Deve retornar um valor legível para o PutPixel
		return (int) (MAX * 255);
	}

	@Override
	public boolean dialogItemChanged(GenericDialog janela, AWTEvent e) {
		if (janela.wasCanceled()) {
			return false;
		}

		imgOriginal.getProcessor().reset();

		return true;
	}
}
