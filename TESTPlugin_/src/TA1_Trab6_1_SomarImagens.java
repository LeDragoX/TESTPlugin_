import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class TA1_Trab6_1_SomarImagens implements PlugIn, DialogListener{
	
	ImagePlus imgOriginal = IJ.getImage();
	ImageProcessor processadorOriginal = imgOriginal.getProcessor();

	@Override
	public void run(String arg) {
		
		GenericDialog janela = new GenericDialog("Adjust Image");
		janela.addDialogListener(this);
		
		processadorOriginal.snapshot();
		
		WindowManager.putBehind();
		System.out.println(WindowManager.getIDList());
		
		if (janela.wasCanceled()) {
			processadorOriginal.reset();
			imgOriginal.updateAndDraw();
		} else {
			System.out.println("Ok");
		}
	}
	
	@Override
	public boolean dialogItemChanged(GenericDialog janela, AWTEvent e) {
		if (janela.wasCanceled()) {
			return false;			
		}
		
		processadorOriginal.reset();
		
		
		return true;
	}

}
