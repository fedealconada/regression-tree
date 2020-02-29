package RegressionTree;

import java.util.Collection;


public class DivisionCriteria {

	private double standardDeviationReduction;
	private int attributeIndex;
	private boolean nominal=false;
	private double ordinalSplitValue;
	private Collection<Double> nominalValues;

	
	public DivisionCriteria standardDeviationReduction(double sdr) {
		this.standardDeviationReduction = sdr;
		return this;
	}
	public double getStandardDeviationReduction() {
		return this.standardDeviationReduction;
	}

	public DivisionCriteria attributeIndex(int attr) {
		this.attributeIndex = attr;
		return this;
	}
	public int getAttributeIndex(){
		return this.attributeIndex;
	}
	
	public DivisionCriteria setAsOrdinal(double splitValue){
		this.nominal=false;
		this.ordinalSplitValue= splitValue;
		return this;
	}
	
	public DivisionCriteria setAsNominal(Collection<Double> values){
		this.nominal = true;
		this.nominalValues = values;
		return this;
	}
	public boolean isNominal(){
		return this.nominal;
	}
	public String getConditionAsString(String[] headers ) {
		if(this.isNominal()){
			return headers[this.attributeIndex];
		}else{
			return headers[this.attributeIndex]+"["+this.ordinalSplitValue+"]";
		}
	}
	public Collection<Double> getNominalValues(){
		return nominalValues;
	}
	public Double getOrdinalSplitValue(){
		return ordinalSplitValue;
	}

}
