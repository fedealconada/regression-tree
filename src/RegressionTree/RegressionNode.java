package RegressionTree;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RegressionNode{
	private String id;
	private Set<Instance> instances = null;
	private RegressionTree owner;
	private List<RegressionNode> subsets = null;
	private Double targetStandardDeviation;
	private DivisionCriteria splitCriteria;
	private Double predictionAverage;
	private LinkedHashMap<Double,RegressionNode> childNodes;
	private String branchTag;
	
	private static final String noInstanceMsg = "ERROR: RegressionNode with no instances has been created!\nProgram will be aborted";
					  
	public RegressionNode(RegressionTree owner,Collection<Instance> instances) {
		this.owner = owner;
		if(instances.size()==0) {
			System.err.println(noInstanceMsg);
			System.exit(1);	
		}
		this.instances = new HashSet<Instance>(instances);
	}
	
	public RegressionTree getOwner(){
		return this.owner;
	}
	
	public RegressionNode setBranchTag(String tag){
		branchTag = tag;
		return this;
	}
	public Set<Instance> getInstances() {
		return instances;
	}
	public void setInstances(Collection<Instance> instances) {
		this.instances = new HashSet<Instance>(instances);
	}
	
	private Instance getAnyInstance(){
		if(!this.getInstances().iterator().hasNext()) return null;
		return this.getInstances().iterator().next();
	}
	public boolean isSplittable(){
		return this.divide().size() > 0;
	}
	public boolean isLeaf(){
		return !this.isSplittable();
	}
	
	private DivisionCriteria getSplitCriteria() {
		return splitCriteria;
	}
	private void setSplitCriteria(DivisionCriteria splitCriteria) {
		this.splitCriteria = splitCriteria;
	}
	/**
	 * @return standard deviation of values in array
	 * StandardDeviation = sqrt( sumattory( Math.pow(  (attr[i]-average[i]) ,2) )/set.size() );
	 * */
	private double standardDeviation(double[] values,int start, int end){
		int size  = end-start;
		double average=0,sum=0;
		
		//Compute average
		for(int i=start;i<end;i++) average+=values[i];
		average/=size;
		
		//Compute summattory
		for(int i=start;i<end;i++) sum+= Math.pow(values[i]-average,2);
		
		//Return standard deviation
		return Math.sqrt(sum/size);
	}
	
	/** 
	 * @return the standard deviation of the target class
	 * */
	public double getTargetStandardDeviation(){
		if(this.getAnyInstance()==null) return 0;
		   
		if(targetStandardDeviation==null){
			int nInstances  = instances.size();
			double[] values = new double[nInstances];
			int i=0;
			for(Instance instance: instances){
				values[i]=instance.getTarget();
				i++;
			}
			targetStandardDeviation = this.standardDeviation(values,0,values.length);
		}
		return targetStandardDeviation;
	}
	
	/**
	 * Computes the standard deviation of two attributes: the selected attr for split and the target value
	 * @param the target values divided into subsets according to the attr split criteria
	 * */
	private double standardDeviationAfterAttrSplit(double[][] subsets){
		int nSubsets = subsets.length;
		int nInstances = 0;
		for(int i=0;i<nSubsets;i++) nInstances+= subsets[i].length;
		
		double finalStandardDeviation = 0; //Save the final standard deviation
		for(int i=0;i<nSubsets;i++){
			//Compute standard deviation of each partition:
			double standardDeviationOfPartition = this.standardDeviation(subsets[i],0,subsets[i].length);
			
			finalStandardDeviation+= (subsets[i].length/(double)nInstances)*standardDeviationOfPartition;
		}
		
		return finalStandardDeviation;
	}

	/**
	 * @return the standard deviation reduction after partition.
	 * */
	private double standardDeviationReduction(double[][] subsets){
		return this.getTargetStandardDeviation() - this.standardDeviationAfterAttrSplit(subsets);
	}
	
	/**
	 * For ordinal attributes, the best partition point is where the standardDeviationReduction is maximal
	 * This function finds the best split index where standardDeviationReduction is optimal for a given attribute.
	 * @return SplitCandidate
	 * */
	private DivisionCriteria getBestPartitionsForOrdinal(int attr){
		
		/* First sort the values*/
		Instance[] sortedInstances = this.sortInstancesByAttr(attr);
		double[] targetValues = new double[sortedInstances.length];
		for(int i=0;i<sortedInstances.length;i++)
			targetValues[i]= sortedInstances[i].getTarget();
		/* Now, targetValues contains the target class values sorted by the attribute values */
		
		double bestReduction = 0.0;
		int    bestSplitIndex = 0;
		double [][] bestPartition = null;
		/*Start at 1, at least 1 element must be on the left side*/
		for(int splitPoint=1; splitPoint<targetValues.length-1;splitPoint++){
			double lastLeft = sortedInstances[(splitPoint-1)].getAttribute(attr);
			double firstRight = sortedInstances[splitPoint].getAttribute(attr);
			
			/*keep equals elements into the same subset*/
			if(lastLeft == firstRight) continue;
			
			/*Create subsets*/
			double [] left  = new double[splitPoint];
			double [] right = new double[targetValues.length-splitPoint];
			System.arraycopy(targetValues, 0, left,0,splitPoint);
			System.arraycopy(targetValues, splitPoint, right,0,targetValues.length-splitPoint);
			double[][] split = {left,right};
			
			/*Find best standard deviation reduction*/
			double sdr = standardDeviationReduction(split);
			if(sdr > bestReduction){
				bestReduction=sdr;
				bestSplitIndex=splitPoint;
				bestPartition=split;
			}
		}
		if(bestPartition==null)return null;
		/*Return best candidate found*/
		return new DivisionCriteria()
				.standardDeviationReduction(bestReduction)
				.attributeIndex(attr)
				.setAsOrdinal(sortedInstances[bestSplitIndex-1].getAttribute(attr));
	}
	
	/**
	 * For nominal attributes, the best partition is creating a branch for each possible nominal value
	 * */
	private DivisionCriteria getBestPartitionsForNominal(int attr){
		HashMap<Double,LinkedList<Instance>> map = new HashMap<Double,LinkedList<Instance>>();
		Instance[] instances = this.getInstancesAsArray();
		
		for(Instance inst: instances){
			if(!map.containsKey(inst.getAttribute(attr))) 
				map.put(inst.getAttribute(attr),new LinkedList<Instance>());
			map.get(inst.getAttribute(attr)).add(inst);
		}
		double[][] subsets = new double[map.size()][];
		int i=0;
		for(LinkedList<Instance> subset: map.values()){
			subsets[i] = new double[subset.size()];
			int j=0;
			for(Instance inst:subset){
				subsets[i][j]=inst.getTarget();
				j++;
			}
			i++;
		}
		
		/* Now instaces are grouped by equal values in map */
		/* And target values for each subset are stored in subsets */
		
		/* Compute standard deviation reduction */
		double sdr = standardDeviationReduction(subsets);
		
		/*Return best candidate found*/
		return new DivisionCriteria()
				.standardDeviationReduction(sdr)
				.attributeIndex(attr)
				.setAsNominal(map.keySet());
	}
	
	
	private Instance[] getInstancesAsArray(){
		Instance[] instances = new Instance[this.getInstances().size()];
		int i=0;
		for(Instance instance: this.getInstances()){
			instances[i]=instance;
			i++;
		}
		return instances;
	}
	
	/**
	 * @param  index of the attribute for sorting
	 * @return an array of instances sorted by the values of the attribute
	 * */
	private Instance[] sortInstancesByAttr(int attr) {
		Instance[] instances = this.getInstancesAsArray();
		Arrays.sort(instances,new Comparator<Instance>() {
			@Override
			public int compare(Instance i1, Instance i2) {
				return Double.compare(i1.getAttribute(attr), i2.getAttribute(attr));
			}
		});
		return instances;
	}
	
	
	/**
	 * Try to reduce the set of instances into 2 or more subsets by the best attribute candidate found.
	 * If no subset is returned, the reduction for this set is over.
	 * @return a list for each subset created.
	 * */
	public List<RegressionNode> divide() {
		if(subsets!=null)return subsets;
		//Compute splits sets:
		subsets = new LinkedList<RegressionNode>();
		//Check 1º stop split threshold
		if(this.getTargetStandardDeviation() <= this.getOwner().getMinStandardDeviationThreshold() ) return subsets;
		//Check 2º stop split threshold
		if(this.getInstances().size() <= this.getOwner().getMinInstancesThreshold()) return subsets;
		
		//Now split must be done, find best attribute candidate for split instance's set.
		DivisionCriteria bestCandidate = null;
		double bestSdr = 0.0; //Best standard deviation reduction
		for(int i=0;i<this.getAnyInstance().getAttributesLength();i++){
			DivisionCriteria candidate;
			
			if(this.getOwner().treatingNominalAsOrdinal() || !this.getOwner().isAttributeNominal(i))
				candidate =  getBestPartitionsForOrdinal(i);
			else 
				candidate =  getBestPartitionsForNominal(i);
			
			if(candidate!=null && candidate.getStandardDeviationReduction() > bestSdr){
				bestSdr = candidate.getStandardDeviationReduction();
				bestCandidate = candidate;
			}
		}
		
		if(bestCandidate!=null) subsets = this.splitUsingCriteria(bestCandidate);
		return subsets;
	}

	private List<RegressionNode>  splitUsingCriteria(DivisionCriteria criteria) {
		this.setSplitCriteria(criteria);
		childNodes = new LinkedHashMap<Double,RegressionNode>();
		if(criteria.isNominal()){
			//Split set by nominal values
			
			//Initialize map
			LinkedHashMap<Double,List<Instance>> map = new LinkedHashMap<Double,List<Instance>>();
			for(Double i:criteria.getNominalValues()) map.put(i,new LinkedList<Instance>());
			//Group instances by nominal value
			for(Instance i:instances) map.get(i.getAttribute(criteria.getAttributeIndex())).add(i);
			//For each group, create new Regression node and add group's instances.
			for(Double key:map.keySet()){
				RegressionNode child = new RegressionNode(this.getOwner(),map.get(key));
				child.setBranchTag(this.getOwner().getNominalValueOf(criteria.getAttributeIndex(), key));
				childNodes.put(key,child);
			}
		}else{
			//Split set into 2 subsets by ordinal value
			List<Instance>left=new LinkedList<Instance>();
			List<Instance>right=new LinkedList<Instance>();
			for(Instance i:instances){
				if(i.getAttribute(criteria.getAttributeIndex()) <= criteria.getOrdinalSplitValue())
					left.add(i);
				else 
					right.add(i);	
			}
			//Create left node
			RegressionNode leftChild = new RegressionNode(this.getOwner(),left);
			leftChild.setBranchTag("<=");
			childNodes.put(0.0,leftChild);
			//Create right node
			RegressionNode rightChild = new RegressionNode(this.getOwner(),right); 
			rightChild.setBranchTag(">");
			childNodes.put(1.0, rightChild);			
		}
		return new LinkedList<RegressionNode>(childNodes.values());
	}

	public RegressionNode getNextNode(Instance instance) {
		DivisionCriteria criteria= this.getSplitCriteria();
		if(criteria.isNominal()) return childNodes.get(instance.getAttribute(criteria.getAttributeIndex()));
		else{
			if(instance.getAttribute(criteria.getAttributeIndex())<=criteria.getOrdinalSplitValue())
				return childNodes.get(0.0); //Go left
			else return childNodes.get(1.0);//Go right
		}
	}

	public Double getPrediction() {
		if(this.predictionAverage == null){
			double sum = 0;
			for(Instance i: this.getInstances()) sum+= i.getTarget();
			this.predictionAverage = sum/(double)this.getInstances().size();
		}
		return this.predictionAverage;
	}	
	
	public List<RegressionNode> getChildren() {
		List<RegressionNode> children = new LinkedList<RegressionNode>();
		for(RegressionNode s: this.divide()) children.add(s);
		return children;
	}
	
	public String getId() {
		if(this.id==null){
			id= System.currentTimeMillis()+"-"+Math.random();
		}
		return this.id;
	}

	public String getBranchTag() {
		return branchTag;
	}
	
	@Override
	public String toString() {
		if(this.getSplitCriteria()==null){
			String pred = this.getPrediction().toString();
			if(pred.length()>6) return pred.substring(0,6);
			return pred;	
		}
		return this.getSplitCriteria().getConditionAsString(this.getOwner().getHeaders());
	}

}
