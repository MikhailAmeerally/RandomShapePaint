package ca.utoronto.utm.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JPanel;

class PaintPanel extends JPanel {
	private static final long serialVersionUID = 3277442988868869424L;
	public ArrayList<PaintCommand> commands = new ArrayList<PaintCommand>();
	
	public PaintPanel(){
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(300,300));
	}
	
	public void setCommands(ArrayList<PaintCommand> commands){
		this.commands=commands;
	}
	public void reset(){
		this.commands.clear();
		this.repaint();
	}
	
	public void addCommand(PaintCommand command){
		this.commands.add(command);
	}
	public void save(PrintWriter writer){
		writer.write("PaintSaveFileVersion1.0\n");
		for(PaintCommand pc: commands)
			writer.write(pc.toString());
		writer.write("EndPaintSaveFile");
		writer.close();
	}
	public void paintComponent(Graphics g) {
        super.paintComponent(g); //paint background
        Graphics2D g2d = (Graphics2D) g;		
		for(PaintCommand c: this.commands){
			c.execute(g2d);
		}
		g2d.dispose();
	}
}
