package ca.utoronto.utm.paint;

import java.awt.Color;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about its effort
 * to parse a file. After a successful parse, an instance will have an ArrayList
 * of PaintCommand suitable for rendering. If there is an error in the parse,
 * the instance stores information about the error. For more on the format of
 * Version 1.0 of the paint save file format, see the associated documentation.
 * 
 * @author
 *
 */
public class PaintSaveFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage = ""; // error encountered during parse
	private ArrayList<PaintCommand> commands; // created as a result of the
												// parse

	/**
	 * Below are Patterns used in parsing
	 */
	private Pattern pFileStart = Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd = Pattern.compile("^EndPaintSaveFile$");

	private Pattern pCircleStart = Pattern.compile("^Circle$");
	private Pattern pCircleEnd = Pattern.compile("^EndCircle$");
	private Pattern pRectangleStart = Pattern.compile("^Rectangle$");
	private Pattern pRectangleEnd = Pattern.compile("^EndRectangle$");
	private Pattern pSquiggleStart = Pattern.compile("^Squiggle");
	private Pattern pSquiggleEnd = Pattern.compile("^EndSquiggle$");
	private Pattern pPointStart = Pattern.compile("^points$");
	private Pattern pPointEnd = Pattern.compile("^endpoints$");
	private Pattern pfill = Pattern.compile("^filled:");
	private Pattern pColor = Pattern.compile("^color:");
	private Pattern pCenter = Pattern.compile("^center:");
	private Pattern pRadius = Pattern.compile("^radius:");
	private Pattern pPoint = Pattern.compile("^point:");
	private Pattern pP1 = Pattern.compile("^p1:");
	private Pattern pP2 = Pattern.compile("^p2:");
	private Pattern[] patternShapeStart = { pCircleStart, pRectangleStart, pSquiggleStart };
	

	/**
	 * Store an appropriate error message in this, including lineNumber where
	 * the error occurred.
	 * 
	 * @param mesg
	 */
	private void error(String mesg) {
		this.errorMessage = "Error in line " + lineNumber + " " + mesg;
	}

	/**
	 * 
	 * @return the PaintCommands resulting from the parse
	 */
	public ArrayList<PaintCommand> getCommands() {
		return this.commands;
	}

	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * Parse the inputStream as a Paint Save File Format file. The result of the
	 * parse is stored as an ArrayList of Paint command. If the parse was not
	 * successful, this.errorMessage is appropriately set, with a useful error
	 * message.
	 * 
	 * @param inputStream
	 *            the open file to parse
	 * @return whether the complete file was successfully parsed
	 */
	/**
	 * @param inputStream
	 * @return
	 */
	public boolean parse(BufferedReader inputStream) {
		this.commands = new ArrayList<PaintCommand>();
		this.errorMessage = "";
		// During the parse, we will be building one of the
		// following shapes. As we parse the file, we modify
		// the appropriate shape.

		try {
			int state = 0;
			Matcher m=null;
			String l;
			Shape s = null;
			this.lineNumber = 0;
			outerloop:
			while ((l = inputStream.readLine()) != null) {
				this.lineNumber++;
				l = l.replaceAll("\\s+", "");
				if(l.isEmpty()) continue;
				switch (state) {
				case 0:
					m = pFileStart.matcher(l);
					if (m.find()) {
						state = 1;
						continue outerloop;
					}
					error("Expected Start of Paint Save File");
					return false;
				case 1: // Looking for the start of a new object or end of the
						// save file
					for (int i = 0; i < patternShapeStart.length; i++) {
						if (patternShapeStart[i].matcher(l).find()) {
							if (i==0) s = new Circle();
							else if (i == 1) s = new Rectangle();
							else if (i == 2) s = new Squiggle();
							state = 4;
							continue outerloop;
						}
					}
					if (pFileEnd.matcher(l).find()){
						lineNumber++;
						if((l = inputStream.readLine()) == null) return true;
						else error("Expecting end of file.");
					}else error("Expected either end of file or new Shape.");
					return false;
				case 2:
						if(s instanceof Circle)
							m = pCircleEnd.matcher(l);
						else if(s instanceof Rectangle)
							m = pRectangleEnd.matcher(l);
						else if(s instanceof Squiggle)
							m = pSquiggleEnd.matcher(l);
						if(m.find()){
							if(s instanceof Circle)
								commands.add(new CircleCommand((Circle) s));
							else if(s instanceof Rectangle)
								commands.add(new RectangleCommand((Rectangle) s));
							else if(s instanceof Squiggle)
								commands.add(new SquiggleCommand((Squiggle) s));
							state = 1;
							s = null;
							continue outerloop;
						}else error("Expected End of Shape");
						return false;
				case 3: // Points
					if (pPoint.matcher(l).find()) {
						Pattern values = Pattern.compile("^point:\\(([0-9]{1,3}),([0-9]{1,3})\\)$");
						m = values.matcher(l);
						if (!m.matches()) {
							error("Property \"point\" expects two integer values.");
							return false;
						}
						int x = Integer.parseInt(m.group(1));
						int y = Integer.parseInt(m.group(2));
						Point p = new Point(x, y);
						((Squiggle) s).add(p);
						continue outerloop;
					} else if (pPointEnd.matcher(l).find()) {
						state = 2;
						continue outerloop;
					}
					continue outerloop;
				case 4: // color
					if (pColor.matcher(l).find()) {
						Pattern values = Pattern.compile("^\\s*color:([0-9]{1,3}),([0-9]{1,3}),([0-9]{1,3})\\s*$");
						m = values.matcher(l);
						if (!m.matches()) {
							error("Property \"color\" expects three integer values.");
							return false;
						}
						int r = Integer.parseInt(m.group(1));
						int g = Integer.parseInt(m.group(2));
						int b = Integer.parseInt(m.group(3));
						if(r > 255 || g > 255 || b > 255){ 
							error("RGB values must be between 0 and 255.");
							return false;
						}
						s.setColor(new Color(r,g,b));
						state = 5;
						continue outerloop;
					}else{
						error("Expected property 'color'");
						return false;
					}
				case 5: //isFilled
					if (pfill.matcher(l).find()) {
						Pattern value = Pattern.compile("^\\s*filled:(true|false)\\s*$");
						m = value.matcher(l);
						if (!m.matches()) {
							error("Property \"filled\" expects boolean value.");
							return false;
						}
						s.setFill(Boolean.parseBoolean(m.group(1)));
						if(s instanceof Circle) state = 6;
						else if(s instanceof Rectangle) state = 8;
						else if(s instanceof Squiggle) state = 10;
						continue outerloop;
					}else{
						error("Expected property 'filled'");
						return false;
					}
				case 6: // center point for circle
					if (pCenter.matcher(l).find()) {
						Pattern value = Pattern.compile("^center:\\(([0-9]{1,3}),([0-9]{1,3})\\)$");
						m = value.matcher(l);
						if (!m.matches()) {
							error("property \"center\" expects two integer values.");
							return false;
						}
						int x = Integer.parseInt(m.group(1));
						int y = Integer.parseInt(m.group(2));
						Point center = new Point(x, y);
						((Circle) s).setCentre(center);
						state = 7;
						continue outerloop;
					}
					else{
						error("Expected property 'center'");
						return false;
					}
				case 7://radius for circle
					if (pRadius.matcher(l).find()) {
						Pattern value = Pattern.compile("^radius:([0-9]{1,3})$");
						m = value.matcher(l);
						if (!m.matches()) {
							error("property \"radius\" expects an integer value.");
							return false;
						}
						int radius = Integer.parseInt(m.group(1));
						((Circle) s).setRadius(radius);
						state = 2;
						continue outerloop;
					}
					else {
						error("Expected property 'radius'");
						return false;
					}
				case 8: //Rectangle P1
					if (pP1.matcher(l).find()) {
						Pattern values = Pattern.compile("^p1:\\(([0-9]{1,3}),([0-9]{1,3})\\)$");
						m = values.matcher(l);
						if (!m.matches()) {
							error("Property \"p1\" expects two integer values.");
							return false;
						}
						int x = Integer.parseInt(m.group(1));
						int y = Integer.parseInt(m.group(2));
						Point p1 = new Point(x, y);
						((Rectangle) s).setP1(p1);
						state = 9;
						continue outerloop;
					}
					else{
						error("Expected property 'p1'");
						return false;
					}
				case 9: //Rectangle P2
					if (pP2.matcher(l).find()) {
						Pattern values = Pattern.compile("^p2:\\(([0-9]{1,3}),([0-9]{1,3})\\)$");
						m = values.matcher(l);
						if (!m.matches()) {
							error("Property \"p2\" expects two integer values.");
							return false;
						}
						int x = Integer.parseInt(m.group(1));
						int y = Integer.parseInt(m.group(2));
						Point p2 = new Point(x, y);
						((Rectangle) s).setP2(p2);
						state = 2;
						continue outerloop;
					}
					else{
						error("Expected property 'p2'");
						return false;
					}
				case 10: //Squiggle start points
					if (pPointStart.matcher(l).find()) {
						state = 3;
						continue outerloop;
					}else{
						error("Expected property 'points'");
						return false;
					}
				}
				
			}

		} catch (Exception e) {

		}

		return true;
	}
}
