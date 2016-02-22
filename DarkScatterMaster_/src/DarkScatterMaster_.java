import ij.IJ;
import ij.plugin.PlugIn;
 
public class DarkScatterMaster_  implements PlugIn {
	public void run(String arg) {
		IJ.error("Hello world!");
		//add prior.dll path to system path
		System.loadLibrary("Prior");
		//StageClass m_stage=new StageClass();
	}
}