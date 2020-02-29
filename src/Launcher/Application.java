package Launcher;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import Gui.Callback;
import Gui.GUI;
import RegressionTree.Instance;
import RegressionTree.RegressionTree;
import RegressionTree.TreePainter;


public class Application implements Callback {
	
	public static GUI     WINDOW;
	public static ResourceBundle LANG;
	
	
	public static void print(String str){
		System.out.print(str);
		WINDOW.print(str);
	}
	public static void println(String str){
		System.out.println(str);
		WINDOW.println(str);
	}
	public static void printData(Reader r){
		String[] headers = r.getHeaders();
		
		/*
		//Print headers
		println(LANG.getString("headers"));
		for(String h: headers)
			print(h+"|\t");
		
		//Print values
		println("\n\n\n"+LANG.getString("values"));
		for(Instance i: r.getInstances()) i.printInstance();
		*/
		
		HashMap<Integer, HashMap<String,Double>> mapping =  r.getNominalToOrdinalMapping();
		if(mapping.size()>0) println("\n\n\n"+LANG.getString("references"));
		for(Integer i: mapping.keySet()){
			System.out.println(headers[i]);
			for(String s: mapping.get(i).keySet()){
				println("\t"+s+": "+mapping.get(i).get(s));
			}
		}
	}
	
	public static void execute(){
		try{		
			println(LANG.getString("loading_training_data"));
			Reader trainingReader = (new Reader())
								.inputContainHeaders(WINDOW.trainingSetHasHeaders())
								.filepath(WINDOW.getTrainingFilepath())
								.separator(WINDOW.getTrainingSeparator())
								.read();
			
			List<Instance> trainingInstances = trainingReader.getInstances();
			println(LANG.getString("instances_loaded")+" "+trainingInstances.size());
			List<Instance> testInstances = null;
			if(WINDOW.createTestFromTrainingData()){
				println("\n"+LANG.getString("creating_test_data"));
				testInstances = new LinkedList<Instance>();
				int testSize = trainingInstances.size()*20/100;
				Iterator<Instance> it = trainingInstances.iterator();
				while(testSize>0){
					if(!it.hasNext()) it = trainingInstances.iterator();
					Instance i = it.next();
					if(Math.random()<0.5){
						testInstances.add(i);
						it.remove();
						testSize--;
					}
				}
			}
			
			RegressionTree tree = (new RegressionTree(trainingInstances))
									.setNominalMapping(trainingReader.getNominalToOrdinalMapping())
									.setHeaders(trainingReader.getHeaders())
									.treatNominalAsOrdinal(WINDOW.treatNominalsAsOrdinals())
									.minimumNumberOfInstances(WINDOW.numberOfInstancesThreshold())
									.minimumPercentageOfStandardDeviation(WINDOW.percentageOfStandarDeviationThreshold())
									.build();
			
			if(testInstances == null && WINDOW.getTestFilepath().length()>0){
				println("\n"+LANG.getString("loading_test_data"));
				Reader testReader = (new Reader())
						.inputContainHeaders(WINDOW.testSetHasHeaders())
						.filepath(WINDOW.getTestFilepath())
						.separator(WINDOW.getTestSeparator())
						.read();
				testInstances = testReader.getInstances();
				println(LANG.getString("instances_loaded")+" "+testInstances.size());
			}
			
			println("\n"+LANG.getString("training_instances_size")+" "+trainingInstances.size());
			
			if(testInstances!=null){
				println(LANG.getString("test_instances_size")+" "+testInstances.size());
					
				println("\n"+LANG.getString("runing_test"));
				double avError = tree.test(testInstances);
				println(LANG.getString("average_error")+" "+avError);
			}
			

			if(WINDOW.treatNominalsAsOrdinals()){
				printData(trainingReader);
			}
			
			//tree.printTree();
			WINDOW.setViewer((new TreePainter(tree.getRoot())).print());
			
			System.out.println();
		}catch(Exception e){
			println("\n"+LANG.getString("error"));
			println(e.getMessage());
		}
	}


	@Override
	public void callback() {
		WINDOW.clearConsole();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				execute();
			}
		});
		t.run();
	}
	
	private static Application appInstance = null;
	public static Application getInstace(){
		if(appInstance == null){
			appInstance = new Application();
		}
		return appInstance;
	}
	public static void main(String[] args) {
		
		Locale defaultLocale = Locale.getDefault();
		LANG = ResourceBundle.getBundle("Language", defaultLocale);
		
		//Locale englishLocale = new Locale("en");
		//LANG = ResourceBundle.getBundle("Language", englishLocale);
		
		WINDOW = new GUI();
		WINDOW.setCallback(getInstace());
	}
}
