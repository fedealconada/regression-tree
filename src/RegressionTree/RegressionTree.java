package RegressionTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RegressionTree {
	
	private RegressionNode root = null;
	private boolean treatNominalAsOrdinal = false;
	private double 	minSDPer = 0.05;
	private int 	minNumInstances = 10;
	private String[] headers;
	private HashMap<Integer, HashMap<String, Double>> nominalMapping;
	public RegressionNode getRoot() {
		return root;
	}

	public void setRoot(RegressionNode root) {
		this.root = root;
	}

	public RegressionTree(Collection<Instance> trainingSet){
		this.setRoot(new RegressionNode(this,trainingSet));
	}
	
	public RegressionTree minimumPercentageOfStandardDeviation(double per){
		this.minSDPer = per;
		return this;
	}
	public RegressionTree minimumNumberOfInstances(int n){
		this.minNumInstances = n;
		return this;
	}
	public int getMinInstancesThreshold(){
		return minNumInstances;
	}
	public double getMinStandardDeviationThreshold(){
		if(this.minSDPer<=0) this.minSDPer = 0.01;
		return this.getRoot().getTargetStandardDeviation() * this.minSDPer;
	}
	public RegressionTree treatNominalAsOrdinal(boolean b){
		this.treatNominalAsOrdinal = b;
		return this;
	}
	public boolean treatingNominalAsOrdinal(){
		return this.treatNominalAsOrdinal;
	}
	public String[] getHeaders() {
		return headers;
	}
	public RegressionTree setHeaders(String[] headers) {
		this.headers = headers;
		return this;
	}

	public RegressionTree build(){
		this.buildHelper(this.getRoot());
		return this;
	}
	private void buildHelper(RegressionNode root){
		if(root.isSplittable()){
			List<RegressionNode> children = root.divide();
			for(RegressionNode child: children)
				buildHelper(child);
		}
	}
	
	public void printTree(){
		TreePainter.printTree(this.getRoot());
	}

	public double predict(Instance instance){
		RegressionNode node = this.getRoot();
		while(!node.isLeaf()){
			node = node.getNextNode(instance);
		}
		return node.getPrediction();
	}	
	
	public double test(Collection<Instance> instances) {
		double errorSum = 0.0;
		for(Instance inst: instances){
			double prediction = predict(inst);
			double err = Math.abs(prediction - inst.getTarget());
			errorSum+=err;
		}
		return (errorSum/instances.size());
	}

	public boolean isAttributeNominal(int i) {
		return nominalMapping.containsKey(i);
	}

	public String getNominalValueOf(int attrIndex, double value) {
		for(String s: nominalMapping.get(attrIndex).keySet()){
			if(nominalMapping.get(attrIndex).get(s) == value) return s;
		}
		return "";
	}

	public RegressionTree setNominalMapping(HashMap<Integer, HashMap<String, Double>> nominalToOrdinalMapping) {
		this.nominalMapping = nominalToOrdinalMapping;
		return this;
	}
}
