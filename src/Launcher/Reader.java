package Launcher;

import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import RegressionTree.Instance;

import com.opencsv.CSVReader;

public class Reader {
	
	private String filepath;
	private Character separator;
	private List<Instance> instances;
	private String[] headers;
	private HashMap<Integer, HashMap<String,Double>> nominalToOrdinal;
	private boolean inputContainHeaders = true;
	
	public Reader filepath(String filepath){
		this.filepath = filepath;
		return this;
	}
	public Reader separator(Character separator){
		this.separator = separator;
		return this;
	}
	public Reader inputContainHeaders(boolean b){
		this.inputContainHeaders = b;
		return this;
	}
	public Reader read(String filepath,Character separator,boolean containHeaders) throws Exception{
		this.filepath = filepath;
		this.separator = separator;
		this.inputContainHeaders = containHeaders;
		return this.read();
	}
	public Reader nominalToOrdinalMapping(HashMap<Integer, HashMap<String,Double>> nomToOrd){
		this.nominalToOrdinal = nomToOrd;
		return this;
	}
	public Reader read() throws Exception{
		instances = new LinkedList<Instance>();
		try {
			//Read entries
			CSVReader reader = new CSVReader(new FileReader(filepath),separator);
			List<String[]> entries = reader.readAll();
			
			//Convert nominal values into ordinal
			if(nominalToOrdinal==null)
				this.parseNominals(entries);
			
	     	//Read headers if necesary
			Iterator<String[]> it = entries.iterator();
			if(it.hasNext() && inputContainHeaders){
				headers = it.next();
			}
			//Read instances
			while(it.hasNext()){
				instances.add(new Instance(parseInstance(it.next())));
			}
			reader.close();
		} catch (IOException e) {
			throw new Exception(Application.LANG.getString("fileNotFound")+filepath);
		} catch (Exception e) {
			if(e.getMessage()!=null) throw e;
			throw new Exception(Application.LANG.getString("errorLoadingData"));
		} 
		return this;
	}
	
	private Double parseDouble(String d){
		Double value = null;
 		try{
 			value = new Double(d);
 		}catch (NumberFormatException e) {
 			try {
			  NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
 			  Number number = format.parse(d);
 			  value = number.doubleValue();
 			} catch (ParseException  e2) {}
 		}
 		return value;
	}
	
	/**
	 * Convert nominal values into ordinal values.
	 * First compute the average of the class value for each instance
	 * Then, replace nominal with ordinal values corresponding to its position after average sorting
	 * @throws Exception 
	 * */
	private void parseNominals(List<String[]> entries) throws Exception{
		HashMap<Integer, HashMap<String,Pair<Integer,Double>>> memo = new HashMap<Integer, HashMap<String,Pair<Integer,Double>>>();
		nominalToOrdinal = new HashMap<Integer, HashMap<String,Double>>();
		
		Iterator<String[]> it = entries.iterator();
		if(!it.hasNext()) return;
		
		String[] instance = it.next();
		
		
		it = entries.iterator();
		//Skip headers:
		if(inputContainHeaders) headers = it.next();
		
		//Check if attribute is nominal and compute averages of its classes:
     	while(it.hasNext()){
     		instance = it.next();
     		Double target = this.parseDouble(instance[instance.length-1]);
     		if(target==null) 
     			throw new Exception(Application.LANG.getString("targetNotOrdinal")+": \""+instance[instance.length-1]+"\"");
     		
     		for(int i=0; i<instance.length-1; i++){
     			Double value = this.parseDouble(instance[i]);
     			if(value==null){	
     				//Memorize the class value and count number of instances:
					if(!memo.containsKey(i))
						memo.put(i,new HashMap<String,Pair<Integer,Double>>());
					if(!memo.get(i).containsKey(instance[i]))
						memo.get(i).put(instance[i],new Pair<Integer,Double>(0,0.0));
					
					Pair<Integer,Double> curr = memo.get(i).get(instance[i]);
					memo.get(i).put(instance[i],new Pair<Integer,Double>(curr.first+1,curr.second+target));
				}
     		}
     	}
     	//For each nominal attribute:
     	for(Integer attribute: memo.keySet()){
     		//Attach average class to each nominal attribute for sorting
     		ArrayList<Pair<Double,String>> sortedValues= new ArrayList<Pair<Double,String>>();
     		for(String value: memo.get(attribute).keySet()){
     			Pair<Integer,Double> info = memo.get(attribute).get(value);
     			Double score = info.second/info.first;
     			sortedValues.add(new Pair<Double,String>(score,value));
     		}
     		//Sort by averages
     		Collections.sort(sortedValues);
     		
     		//Convert nominal values into ordinal according to sorting position
     		nominalToOrdinal.put(attribute,new HashMap<String, Double>());
     		Double newValue = 1.0;
     		for(Pair<Double, String> value: sortedValues){
     			nominalToOrdinal.get(attribute).put(value.second,newValue);
     			newValue++;
     		}
     	}
	}
	
	/**
	 * Convert strings into doubles
	 * Map nominal to ordinal values
	 * */
	private double[] parseInstance(String[] entry) {
		double[] values = new double[entry.length];
		for(int i =0; i<entry.length;i++){
			if(this.isAttributeNominal(i)){
				values[i]= nominalToOrdinal.get(i).get(entry[i]);
			}else{
				values[i]= this.parseDouble(entry[i]);
			}
		}
		return values;
	}
	
	public String[] getHeaders() {
		return headers;
	}
	public List<Instance> getInstances(){
		return instances;
	}
	public boolean isAttributeNominal(int i){
		return nominalToOrdinal.containsKey(i);
	}
	public  HashMap<Integer, HashMap<String, Double>> getNominalToOrdinalMapping(){
		return nominalToOrdinal;
	}
	public String getNominalValueOf(int attr, double value){
		for(String s: nominalToOrdinal.get(attr).keySet()){
			if(nominalToOrdinal.get(attr).get(s) == value) return s;
		}
		return "";
	}
}
