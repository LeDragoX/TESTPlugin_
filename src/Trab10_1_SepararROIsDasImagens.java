
import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

public class Trab10_1_SepararROIsDasImagens implements PlugIn, DialogListener {

	ImagePlus imgOriginal = IJ.getImage();
	ImageProcessor processadorBackup = imgOriginal.getProcessor().duplicate();
	
	public void PreProcessImage() {
		// Binarizar a imagem selecionada
		IJ.run(imgOriginal, "Convert to Mask", "");
		// Preencher os vazios da imagem selecionada
		IJ.run(imgOriginal, "Fill Holes", "");
		// Separar as ROIs da imagem selecionada
		IJ.run(imgOriginal, "Analyze Particles...", "clear add");
	}

	@Override
	public void run(String arg) {		
		PreProcessImage();
		
		GenericDialog janela = new GenericDialog("Adjust Image");
		janela.addDialogListener(this);

		janela.addMessage("Descrição: esse PlugIn irá separar todas as regiões de interesse e salvá-las em uma pasta.");
		janela.showDialog();

		if (janela.wasCanceled()) {
			System.out.println("Cancelled");
			
			imgOriginal.setProcessor(processadorBackup.duplicate());
			imgOriginal.updateAndDraw();
		} else {
			System.out.println("Ok");
			separateROIS();
		}
	}

	public void separateROIS() {
		
		for (int i = 0; i < WindowManager.getIDList().length; i++) {
			System.out.println(
					"\nImg[%d] ID[%d]: %s".formatted(i, WindowManager.getIDList()[i], WindowManager.getImageTitles()[i]));
		}
		
		// Voltar imagem ao estado original
		imgOriginal.setProcessor(processadorBackup.duplicate());
		
		RoiManager rm = RoiManager.getRoiManager();
		// Vetor com todos as ROIs presentes
		Roi[] allROIs = rm.getRoisAsArray();
		
		// Caminho para salvar as imagens
		String path = IJ.getDir("Select a Folder to save all images");
		System.out.println("Folder selected: %s".formatted(path));
		
		if (path != null) {
			for (int i = 0; i < allROIs.length; i++) {
				// Retângulo de contorno em i
				rm.select(imgOriginal,i);
				// "Duplicar a imagem" + Crop
				ImagePlus imgCrop = imgOriginal.crop();
				// Salvar
				IJ.saveAs(imgCrop, "Jpeg", "%sCropped_%s_%s".formatted(path, String.valueOf(i), WindowManager.getImageTitles()[0]));
			}
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
}
