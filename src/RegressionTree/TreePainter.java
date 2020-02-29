package RegressionTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

import scala.util.parsing.input.StreamReader;



public class TreePainter implements Runnable{
	
	
		private RegressionNode root;
		public TreePainter(RegressionNode root) {
			this.root = root;
		}
		
		private HashMap<Integer,List<Node>> nodesByLevel = new HashMap<Integer,List<Node>>();
		
		private void initPositions(){
			int Xmargin=150,Ymargin=120;
			int y=0;
			int xmid=0;
			for(Integer level: nodesByLevel.keySet()){
				int n = nodesByLevel.get(level).size();
				int x = xmid-(Xmargin*n)/2; 
				for(Node node:nodesByLevel.get(level)){
					node.setAttribute("x",x);
					node.setAttribute("y",y);
					x+=Xmargin;
				}
				y-=Ymargin;
			}
		}
		
		private Node fillGraph(int level, RegressionNode root, Graph g){
			if(!nodesByLevel.containsKey(level)) nodesByLevel.put(level,new LinkedList<Node>());
			Node node = g.addNode(root.getId());
			node.addAttribute("ui.label",root.toString());
			
			nodesByLevel.get(level).add(node);
			
			for(RegressionNode child: root.getChildren()){
				fillGraph(level+1, child, g);
				g.addEdge(System.currentTimeMillis()+"-"+Math.random(),root.getId(),child.getId(),true)
					.addAttribute("ui.label",child.getBranchTag());
			}
			return node;
		}
		
		
		private Viewer printNode(RegressionNode root){
			StringBuilder stylesheet =  new StringBuilder();
			try {
				/*Read stylesheet*/
				InputStream stream = TreePainter.class.getResourceAsStream("stylesheet.css");
				BufferedReader bf = new BufferedReader(new InputStreamReader(stream));
				String line = bf.readLine();
				while(line!=null){
					stylesheet.append(line);
					line = bf.readLine();
				}
			} catch (IOException e) {}
			
			System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
			
			Graph graph = new SingleGraph("Regression Tree");
    	    //graph.addAttribute("ui.stylesheet", "url('file://"+System.getProperty("user.dir")+"/stylesheet.css')");
			graph.addAttribute("ui.stylesheet", stylesheet.toString());
    	    graph.addAttribute("ui.quality");
    	    graph.addAttribute("ui.antialias");
    	    
    	    fillGraph(0,root,graph).setAttribute("ui.class", "marked");
    	    initPositions();
    	    return graph.display(false);
		}
		
		public Viewer print(){
			return printNode(this.root);
		}
		
		@Override
		public void run() {
			printNode(this.root);
		}

	    public static void printTree(RegressionNode root) {
	    	new Thread(new TreePainter(root), "DisplayTree").start();
        }

}
