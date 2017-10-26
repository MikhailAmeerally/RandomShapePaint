package ca.utoronto.utm.paint;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class SquiggleCommand implements PaintCommand {
	private Squiggle squiggle;
	public SquiggleCommand(Squiggle squiggle){
		this.squiggle = squiggle;
	}
	public void execute(Graphics2D g2d){
		ArrayList<Point> points = this.squiggle.getPoints();
		g2d.setColor(squiggle.getColor());
		for(int i=0;i<points.size()-1;i++){
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
	}
	
	public String toString(){
		String s = "Squiggle\n";
		int r = this.squiggle.getColor().getRed();
		int g = this.squiggle.getColor().getGreen();
		int b = this.squiggle.getColor().getBlue();
		s += "\tcolor:" + r + "," + g + "," + b + "\n";
		s += "\tfilled:" + this.squiggle.isFilled() + "\n";
		s += "\tpoints\n";
		for(Point p: this.squiggle.getPoints()){
			s += "\t\tpoint:" + p + "\n";
		}
		s += "\tendpoints\n";
		s += "EndSquiggle\n";
		return s;
	}
	
}
