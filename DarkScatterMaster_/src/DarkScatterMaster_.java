import ij.IJ;
import ij.plugin.PlugIn;
import ij.gui.GenericDialog; 
import ij.ImagePlus;
import ij.io.OpenDialog;
import java.io.File;
import ij.process.*; 
import ij.plugin.ChannelSplitter;
import ij.plugin.ImageCalculator;
import ij.plugin.Selection;
import ij.plugin.frame.RoiManager;
import ij.util.Tools;
import ij.measure.*;
import ij.macro.Interpreter;
import ij.*;
import ij.gui.*;
//import ij.Macro; 
 
public class DarkScatterMaster_  implements PlugIn {
	private boolean bROI; //disable ROI selection
	private int iCt,iCs; //Contour threshold and scale for ROI option
	private int iType; //quantification type: 0 red, 1 green, 2 enhanced
	private boolean bDSM; //enable ROI selection
	private int iLt,iHt; //Contour threshold and scale for ROI option
	private boolean bFo; //enable Images output (Fo) 
	private boolean bSm; //single mode
	
	public void run(String arg) {

		this.SettingDialog();
		if (!this.InputCheck()){
			IJ.error("Input Error! Please run again");
			return;
		}
		
		if (bSm) {
		    // Then the image wasn't loaded...
			SingleProc();
		} else {
			BatchProc();
		}
		
		//IJ.error("Checked");
		
	}
	public void SettingDialog(){
		GenericDialog gd = new GenericDialog("Configuration");
        
		//ROI
		gd.addMessage("ROI Configuration");
		gd.addCheckbox("Diable ROI", false);
		gd.addNumericField("Contour Threshold", -130423.00,0); // contour threshold
		gd.addNumericField("Center Scale", 0.8,0);//trim diameter scale
		
		//Quantification
		gd.addMessage("Quantification Configuration");
		gd.addCheckbox("Diable DSM", false);
		String[] type={"Red", "Green", "Enhanced"};
		gd.addChoice("Scatter Type:", type, type[0]);
		gd.addNumericField("Low limit", 15,0);//threshold setting lth
		gd.addNumericField("High limit", 62,0);// hth
		
		
		//Output
		gd.addMessage("Output Configuration");
		gd.addCheckbox("Output Filtered Images", false);
		// Running mode: batch folder mode and single image mode
		gd.addMessage("Mode Configuration");
		gd.addCheckbox("Single mode", false);
		gd.showDialog();
		if (gd.wasCanceled()) {
			IJ.error("PlugIn canceled!");
			return;
		}else{
			//Get input
			bROI=gd.getNextBoolean();
			iCt=(int)gd.getNextNumber();
			iCs = (int) gd.getNextNumber();
			bDSM=gd.getNextBoolean();
			iType = gd.getNextChoiceIndex();
			iLt= (int) gd.getNextNumber();
			iHt = (int) gd.getNextNumber();	
			bFo=gd.getNextBoolean();
			bSm=gd.getNextBoolean();
			return;
		}
	}
	public boolean InputCheck(){
		boolean rtn=true;
		
		return rtn;
	}
	
	public void Proc(String fn,String dir){
		String fp=dir+fn;
		ImagePlus imgPlus=new ImagePlus(fp);
		//ColorProcessor processor = (ColorProcessor) imgPlus.getProcessor();
		ImagePlus imgPlus_cp=new ImagePlus();
		imgPlus_cp=imgPlus.duplicate();
		
	    ImagePlus imgPlusCh[] =ChannelSplitter.split(imgPlus_cp);
	    ImagePlus imgR= imgPlusCh[0];
	    ImagePlus imgG= imgPlusCh[1];
	    ImagePlus imgB= imgPlusCh[2];
	    
	    ImageCalculator ic = new ImageCalculator();
	    ImagePlus Fi=new ImagePlus();
	    
	    switch (iType) {
		    case 0:Fi = ic.run("Subtract create", imgR, imgB); break;
		    case 1:Fi = ic.run("Subtract create", imgG, imgB);;break;
		    case 2:Fi = ic.run("AND create", ic.run("Subtract create", imgR, imgB), ic.run("Subtract create", imgG, imgB)) ;break;
		    default:break;
	    }
	    if(!bROI){
	    	RoiSel(Fi);
	    }
	    Fi.show();

		
		//if (img==null) return;
		
	}
	//batch process for folder
	public void BatchProc(){
		//setBatchMode(true); 
		OpenDialog od = new OpenDialog("Open image folder...", "");
		String dir = od.getDirectory();
		File f = new File(dir);
		String[] FileList= f.list();
		String fn;
		for (int k=0;k<FileList.length;k++){
			fn=FileList[k];
			if (fn==null) return;
			Proc(fn,dir);
		}
	}
	//single process
	public void SingleProc(){
		OpenDialog od = new OpenDialog("Open image ...", "");
		String dir = od.getDirectory();
		String fn = od.getFileName();
		if (fn==null) return;
		Proc(fn,dir);
	}
	private void RoiSel(ImagePlus imp){
		IJ.run("Fit Circle to Image", "threshold="+iCt); //fit Ct
		IJ.run("Scale... ", "x="+iCs+" y="+iCs+" centered");
		RoiManager rm = RoiManager.getInstance();
		rm.addRoi(imp.getRoi());
	}
}