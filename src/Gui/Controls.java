/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.Viewer;

import Launcher.Application;

public class Controls extends JFrame {
	
	private  Viewer viewer;
	private  GUI parent;

	
	private final static String	MOVES			= Application.LANG.getString("move");
	private final static String	ZOOM			= Application.LANG.getString("zoom");
	private final static String	CLOSE			= Application.LANG.getString("close");
	private final static int	MOVE_DIST		= 50;
	private final static double	ZOOM_INTERVAL	= 0.2;
	
	
    public Controls(GUI parent,Viewer viewer) {
        super(Application.LANG.getString("controls"));
        this.parent = parent;
        this.viewer = viewer;
        init();
        setVisible(true);
    }
    
    public void init() {
		//Viewer controls
		JButton zoomIn		= new JButton("+");
		JButton zoomOut		= new JButton("-");
		JButton up			= new JButton("^");
		JButton down		= new JButton("v");
		JButton left		= new JButton("<");
		JButton right		= new JButton(">");
		JLabel  movesLabel	= new JLabel(MOVES);
		JLabel  zoomLabel	= new JLabel(ZOOM);
		JSeparator separator = new JSeparator(1);
		JSeparator separator2 = new JSeparator(1);
		JButton close		=	new JButton(CLOSE);
		
    	zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { zoomIn(); }
		});
    	zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { zoomOut();}
		});
    	up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { moveView(0,MOVE_DIST); }
		});
    	down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { moveView(0,-MOVE_DIST); }
		});
    	left.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { moveView(-MOVE_DIST,0); }
		});
    	right.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { moveView(MOVE_DIST,0); }
		});
    	close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { 
				parent.closeTreeView();
			}
		});
    	
    	
    	
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(left)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(up)
						.addComponent(movesLabel)
						.addComponent(down)
				)
				.addComponent(right)
				.addComponent(separator)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(zoomLabel)
						.addComponent(zoomOut)
				)
				.addComponent(zoomIn)
				.addComponent(separator2)
				.addComponent(close)
		);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(up)
						.addComponent(zoomLabel)
				)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(left)
						.addComponent(movesLabel)
						.addComponent(right)
						.addComponent(separator)
						.addComponent(zoomIn)
						.addComponent(zoomOut)
						.addComponent(separator2)
						.addComponent(close)
				)
				.addComponent(down)
		);
		pack();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
       
    private Camera getViewCamera(){
    	if(viewer==null) return null;
    	return viewer.getDefaultView().getCamera();
    }
    private void moveView(int x,int y){
    	Camera cam = this.getViewCamera();
    	if(cam == null)return;
    	Point3 curr = cam.getViewCenter();
    	cam.setViewCenter(curr.x+x,curr.y+y,curr.z);
    }
    private void zoomIn(){
    	Camera cam = this.getViewCamera();
    	if(cam == null)return;
    	cam.setViewPercent(cam.getViewPercent()-ZOOM_INTERVAL);
    }
    private void zoomOut(){
    	Camera cam = this.getViewCamera();
    	if(cam == null)return;
    	cam.setViewPercent(cam.getViewPercent()+ZOOM_INTERVAL);
    }

	public void setViewer(Viewer treeViewer) {
		viewer = treeViewer;
	}
}
