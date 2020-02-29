/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.graphstream.ui.view.Viewer;

import Launcher.Application;

public class GUI extends JFrame {
	
	  private enum Separator{
	    	COMA(",",','),TAB("\\t",'\t'),SEMICOLON(";",';');
	    	
	    	private String strVal;
	    	private Character charVal;
	    	Separator(String s,Character c){
	    		strVal = s;
	    		charVal= c;
	    	}
	    	public Character getValue(){
	    		return charVal;
	    	}
	    	public String toString(){
	    		return strVal;
	    	}
	    }
	  
	private static Viewer   viewer;
	private static Controls controls;
    
	private final  String CREATE 			= Application.LANG.getString("create_tree");
	private final  String TRAINING_FILE 	= Application.LANG.getString("training_file");
	private final  String TEST_FILE 		= Application.LANG.getString("test_file");
	private final  String SEARCH 			= Application.LANG.getString("file_search");
	private final  String SEPARATOR 		= Application.LANG.getString("csv_separator");
	private final  String MIN_INSTANCES 	= Application.LANG.getString("min_instances");
	private final  String MIN_SD 			= Application.LANG.getString("min_standard_dev");
	private final  String MIN_SD_END 		= Application.LANG.getString("min_standard_dev_end");

	private final  String FILE_HAS_HEADERS  = Application.LANG.getString("file_has_headers");
	private final  String NOMINAL_AS_ORDINAL = Application.LANG.getString("nominal_as_ordinal");
	private final  String TAKE_TEST_FROM_TRAINING = Application.LANG.getString("take_test_from_training");
	
	
	private final static JFileChooser filechooser = new JFileChooser();
	private JTextField trainingFilepath = new JTextField();
	private JTextField testFilepath 	= new JTextField();
	private JCheckBox takeTestFromTraining 	= new JCheckBox(TAKE_TEST_FROM_TRAINING);
	private JCheckBox trainingHeaders 	= new JCheckBox(FILE_HAS_HEADERS);
	private JCheckBox testHeaders 		= new JCheckBox(FILE_HAS_HEADERS);
	private JCheckBox nominalAsOrdinal 	= new JCheckBox(NOMINAL_AS_ORDINAL);
	private JComboBox<Separator> trainingSep 	= new JComboBox<Separator>();
	private JComboBox<Separator> testSep		= new JComboBox<Separator>();
	private JSpinner  minNumberOfInstances 		= new JSpinner();
	private JSlider	  minPercentageOfSD 		= new JSlider();
	
	private JTextArea console			= new JTextArea();
	
	private Callback callbackObj;
	
    public GUI() {
        super(Application.LANG.getString("title"));
        init();
        setVisible(true);
    }
    
  
    
    public void init() {
    	JLabel trainingLabel 		= new JLabel(TRAINING_FILE);
    	JLabel testLabel 			= new JLabel(TEST_FILE);
    	JLabel trainingSepLabel 	= new JLabel(SEPARATOR);
    	JLabel testSepLabel 		= new JLabel(SEPARATOR);
    	JLabel minInstancesLabel 	= new JLabel(MIN_INSTANCES);
    	JLabel minSDLabel	 		= new JLabel(MIN_SD);
    	JLabel minSDLabelEnd 		= new JLabel(MIN_SD_END);
    	
    	JLabel minSDDisplay 		= new JLabel();
    	JButton trainingSetPicker	= new JButton(SEARCH);    
		JButton testSetPicker		= new JButton(SEARCH);
		JButton createBtn 			= new JButton(CREATE);
		JSeparator separator1 = new JSeparator();
		JSeparator separator2 = new JSeparator();
		JSeparator separator3 = new JSeparator(1);
		JSeparator separator4 = new JSeparator(1);
		JScrollPane consoleWrapper = new JScrollPane(console);
	
		
		for(Separator s: Separator.values()){
			trainingSep.addItem(s);
			testSep.addItem(s);
		}
		
		trainingSetPicker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { openFilePicker("TRAINING");}
		});
    	testSetPicker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { openFilePicker("TEST");}
		});
    	createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { execute();}
		});	
    	
    	minPercentageOfSD.setMinimum(1);
    	minPercentageOfSD.setMaximum(100);
    	
    	minNumberOfInstances.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spinner = (JSpinner) e.getSource();
				if( (Integer) spinner.getValue() < 1){
					spinner.setValue(1);
				}
			}
		});
    	minPercentageOfSD.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				minSDDisplay.setText(minPercentageOfSD.getValue()+"%");
			}
		});
    	minNumberOfInstances.setValue(5);
    	minPercentageOfSD.setValue(5);
    	
    	trainingHeaders.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    	testHeaders.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    	nominalAsOrdinal.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		console.setRows(10);
    	
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(trainingLabel)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(trainingFilepath)
								.addGroup(layout.createSequentialGroup()
										.addComponent(trainingHeaders)
										.addComponent(separator3)
										.addComponent(trainingSep)
										.addComponent(trainingSepLabel)
								)
						)
						.addComponent(trainingSetPicker)
				)
				.addComponent(separator1)
				.addComponent(testLabel)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(takeTestFromTraining)
								.addComponent(testFilepath)
								.addGroup(layout.createSequentialGroup()
										.addComponent(testHeaders)
										.addComponent(separator4)
										.addComponent(testSep)
										.addComponent(testSepLabel)
								)
						)
						.addComponent(testSetPicker)
				)
				.addComponent(separator2)
				.addComponent(nominalAsOrdinal)
				.addGroup(layout.createSequentialGroup()
						.addComponent(minInstancesLabel)
						.addComponent(minNumberOfInstances)
				)
				.addGroup(layout.createSequentialGroup()
						.addComponent(minSDLabel)
						.addComponent(minSDDisplay)
						.addComponent(minSDLabelEnd)
				)
				.addComponent(minPercentageOfSD)
				.addComponent(createBtn)
				.addComponent(consoleWrapper)
		);

		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addComponent(trainingLabel)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addGroup(layout.createSequentialGroup()
							.addComponent(trainingFilepath)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(trainingHeaders)
									.addComponent(separator3)
									.addComponent(trainingSep)
									.addComponent(trainingSepLabel)
							)
					)
					.addComponent(trainingSetPicker)
			)
			.addComponent(separator1)
			.addComponent(testLabel)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addGroup(layout.createSequentialGroup()
							.addComponent(takeTestFromTraining)
							.addComponent(testFilepath)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(testHeaders)
									.addComponent(separator4)
									.addComponent(testSep)
									.addComponent(testSepLabel)
							)
							
					)
					.addComponent(testSetPicker)
			)
			.addComponent(separator2)
			.addComponent(nominalAsOrdinal)
			
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(minInstancesLabel)
					.addComponent(minNumberOfInstances)
			)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(minSDLabel)
					.addComponent(minSDDisplay)
					.addComponent(minSDLabelEnd)
			)
			.addComponent(minPercentageOfSD)
			.addComponent(createBtn)
			.addComponent(consoleWrapper)
		);
		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    
    
    private void openFilePicker(String openAs) {
        //Handle open button action.
            int returnVal = filechooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = filechooser.getSelectedFile();
                if(openAs.compareTo("TRAINING")==0){
                	trainingFilepath.setText(file.getAbsolutePath());
                }else{
                	testFilepath.setText(file.getAbsolutePath());
                }
            }
    }
    
    public void setCallback(Callback object){
    	this.callbackObj = object;
    }
    public void execute(){
    	this.callbackObj.callback();
    }
    public String getTrainingFilepath(){
    	return this.trainingFilepath.getText();
    }
    public String getTestFilepath(){
    	return this.testFilepath.getText();
    }
    public Character getTrainingSeparator(){
    	return ((Separator)trainingSep.getSelectedItem()).getValue();
    }
    public Character getTestSeparator(){
    	return ((Separator)testSep.getSelectedItem()).getValue();
    }
    public boolean trainingSetHasHeaders(){
    	return this.trainingHeaders.isSelected();
    }
    public boolean testSetHasHeaders(){
    	return this.testHeaders.isSelected();
    }
    public boolean treatNominalsAsOrdinals(){
    	return this.nominalAsOrdinal.isSelected();
    }
    public boolean createTestFromTrainingData(){
    	return this.takeTestFromTraining.isSelected();
    }
    public int numberOfInstancesThreshold(){
    	return ((Integer)this.minNumberOfInstances.getValue());
    }
    public double percentageOfStandarDeviationThreshold(){
    	return this.minPercentageOfSD.getValue()/(double)100;
    }
    public void print(String str){
    	console.append(str);
    }
    public void println(String str){
    	console.append(str);
    	console.append("\n");
    }
    public void clearConsole(){
    	console.setText("");
    }

	public void setViewer(Viewer treeViewer) {
		if(viewer!=null) viewer.close();
		viewer = treeViewer;
		if(treeViewer!=null){
			if(controls==null) controls = new Controls(this,viewer);
			else controls.setViewer(treeViewer);
		}
			
	}

	public void closeTreeView() {
		controls.setVisible(false);
		controls.dispose();
		controls = null;
		this.setViewer(null);
	}
    
}
