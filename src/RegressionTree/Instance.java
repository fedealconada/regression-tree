package RegressionTree;

import Launcher.Application;

public class Instance {
	
	private double[] attributes;
	
	public Instance(double[] values){
		this.attributes = values;
	}
	
	public Double getAttribute(int i) {
		if(i<this.getAttributesLength()) return attributes[i];
		return null;
	}

	public double getTarget() {
		return this.attributes[this.attributes.length-1];
	}

	public void printInstance() {
		Application.println("");
		for(int i =0;i<attributes.length;i++)
			Application.print(attributes[i]+"\t");
	}
	public int getAttributesLength(){
		return this.attributes.length-1;
	}
	public int getTargetIndex(){
		return this.attributes.length-1;
	}
	public int getDataLength(){
		return this.attributes.length;
	}
	

}
