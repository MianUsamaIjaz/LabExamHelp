package sim;

import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class SimulatorOutp extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population;
    private FieldView fieldView;
    
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    
    static Color[] pickColors = { Color.blue, Color.red, Color.green };
    static int used_colors = 0;
    
    // A statistics object computing and storing simulation information
    
    private Simulator sim;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorOutp(Simulator sim)
    {
        colors = new LinkedHashMap<>();
        this.sim = sim;

        setTitle("Fox and Rabbit Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        
        fieldView = new FieldView(sim.getField().getDepth(), 
                                    sim.getField().getWidth());

        Container contents = getContentPane();

        contents.add(stepLabel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        pack();
        setVisible(true);
        setResizable(false);
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null) {
            // no color defined for this class
            if(used_colors >= pickColors.length) used_colors = 0;
            if( used_colors < pickColors.length ){
                colors.put(animalClass, pickColors[used_colors++]);
                return getColor(animalClass);
            }
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(String message)
    {
        int step = sim.getStep();
        Field field = sim.getField();
        
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);


        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {

                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
      

        population.setText(POPULATION_PREFIX + message);
        fieldView.doText();
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JTextArea
    {
        

        private int gridWidth, gridHeight;
        
        char[] fieldSymbols;
        
        /**
         * Create a new FieldView component.
         * Rows=height, Cols = Width
         */
        public FieldView(int height, int width)
        {
            super(height, width+1);
            gridHeight = height;
            gridWidth = width;
            // size = new Dimension(0, 0);
            fieldSymbols = new char[height*width];
            this.setLineWrap(true);
            this.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 6));
            this.setPreferredSize(new Dimension(height,width+1));
            this.setMaximumSize(new Dimension(height,width+1));
            
            
        }

       private char charForColor(Color color)
       {
            if( color == Color.RED ) return '+';
            if( color == Color.BLUE ) return '*';
            if( color == Color.GREEN ) return 'x';
            return ' ';
       }
        
        /**
         * Paint on grid location on this field in a given color. Substitute a character for a color.
         */
        public void drawMark(int x, int y, Color color)
        {
                fieldSymbols[y*gridWidth + x] = charForColor(color);
        }
        
        public void doText(){
            
            this.setText(String.valueOf(fieldSymbols));
        }
    }

}



//color enum (POSSIBLE)

import java.awt.Color;

/**
 * An enumeration class to hold a set of colors.
 * This is used to provide a list of colors to the user.
 * The name is used to identify the color in the list.
 * The color is used to display the color in the list.
 * The color is also used to display the color in the simulation.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public enum ColorEnum
{
    RED("Red", Color.RED),
    BLUE("Blue", Color.BLUE),
    GREEN("Green", Color.GREEN),
    YELLOW("Yellow", Color.YELLOW),
    BLACK("Black", Color.BLACK),
    WHITE("White", Color.WHITE),
    GRAY("Gray", Color.GRAY),

    // The name of the color.
    private String name;
    // The color.
    private Color color;

    /**
     * Create a ColorEnum with the given name and color.
     * @param name The name of the color.
     * @param color The color.
     */
    private ColorEnum(String name, Color color)
    {
        this.name = name;
        this.color = color;
    }

    /**
     * @return The name of the color.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return The color.
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * @return A string representation of this object.
     */
    public String toString()
    {
        return name;
    }
}

package sim;

import java.util.Random; 
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.HashMap;
import javax.swing.JFrame;
import java.io.IOException;
import java.nio.file.*;
import java.io.BufferedWriter;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 * @version Mods 2022 E Brown
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;




    // List of animals in the field.
    private List<Animal> animals;

    // The current state of the field.
    private Field field;

    // The current step of the simulation.
    private int step;

    
    // Update views of the simulation.
    boolean viewOn = true;
    boolean outpOn = false;
    //new
    boolean logOn = false;
 
    private SimulatorView view = null;
    private SimulatorOutp outp = null;
    //new
    private SimulatorLog log = null;


    private FieldStats stats;
    //new
    private int rabbitCount;
    private int foxCount;
    
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);


    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;

        }
        
        animals = new ArrayList<>();
        field = new Field(depth, width);
        //new
        stats = new FieldStats();
        rabbitCount = 0;
        foxCount = 0;



        // Create a view of the state of each location in the field.
        createViews();
        
        // Setup a valid starting point.
        reset();

    }
    
    /** The following methods help decouple views from the simulator and stats methods.
     * If you want to alter the views, turn them on or off in these methods.
     *
     * Create the stats object and views 
     */
    private void createViews(){
        // Create a view of the state of each location in the field.
        stats = new FieldStats();
        if(viewOn) view = new SimulatorView(this);
        if(outpOn) outp = new SimulatorOutp(this);

        //new
        if(logOn) log = new SimulatorLog(this);

    }
    
    /** 
     * update the views
     */
    private void updateViews(){ 
        stats.reset();
        if(view != null) view.showStatus(stats.getPopulationDetails(field));
        if(outp != null) outp.showStatus(stats.getPopulationDetails(field));
        //new
        if(log != null) log.showStatus(stats.getPopulationDetails(field));

    }
    
    /**
     * delete the views
     */
    public void endSimulation(){
        if(view != null) view.setVisible(false);
        if(outp != null) outp.setVisible(false);
        if(view != null) view.dispose();
        if(outp != null) outp.dispose();
        //new
        if(log != null) log.setVisible(false);
        if(log != null) log.dispose();

    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);

    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && stats.isViable(field); step++) {
            simulateOneStep();
        }


    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;


        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>(); 

        // Let all rabbits act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }

        }
               
        // Add the newly born foxes and rabbits to the main lists.
        animals.addAll(newAnimals);

        updateViews();

    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        Randomizer.reset();
        populate();
        
        // Show the starting state in the view.
        updateViews();


    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        field.clear();

        //new
        //Random rand = Randomizer.getRandom();
        // for(int row = 0; row < field.getDepth(); row++) {
        //     for(int col = 0; col < field.getWidth(); col++) {
        //         Location location = new Location(row, col);
        //         Object animal = Randomizer.getRandomAnimal();
        //         if(animal instanceof Rabbit) {
        //             Rabbit rabbit = (Rabbit) animal;
        //             boolean rabbitPlaced = rabbit.place(field, location);
        //             if(rabbitPlaced) {
        //                 animals.add(rabbit);
        //             }

        //         }
        //         else if(animal instanceof Fox) {
        //             Fox fox = (Fox) animal;
        //             boolean foxPlaced = fox.place(field, location);
        //             if(foxPlaced) {
        //                 animals.add(fox);
        //             }

        //         }
        //         // else leave the location empty.
        //     }

        // }


        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Animal a = AnimalCollector.randAnimal(field);
                if( a != null ){
                    Location location = new Location(row, col);
                    field.place(a, row,col);
                    a.setLocation(location);
                    animals.add(a);
                }
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Accessor for private field.
     */
    public Field getField(){
        return field;
    }
    
    /**
     * Accessor for private step.
     */
    public int getStep(){
        return step;
    }
    
    /**
     * Accessor for field stats details.
     */
    public String getDetails() {
        return stats.getPopulationDetails(field);
    }
    
    public boolean log(String fname){
        // Create a log file (POSSIBLE)
        try{
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(fname));
            writer.write(getDetails());
            writer.close();
            return true;
        } catch (IOException e){
            System.out.println("Error writing to file: " + e);
            return false;
        }

        //END

        return false;


    }

    /**
     * Return the current population of rabbits.
     * @return The number of rabbits currently alive.
     */
    public int getRabbitPopulation()
    {
        int rabbits = 0;
        for(Animal animal : animals) {
            if(animal instanceof Rabbit) {
                rabbits++;
            }
        }
        return rabbits;
    }

}

package sim;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.Timer;
import java.nio.file.*;
import java.io.IOException;
import java.io.BufferedWriter;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;

import java.io.File;


/**
 * Write a description of class ControllerView here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class SimController
{
    // instance variables
    private Simulator sim;
    private JFrame frame;
    private JButton oneButton;
    private JButton zeroButton;
    private Timer simTimer;
    private static JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
    //new
    private static final int DELAY = 100;
    private static final int STEPS = 100;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    
    

    public SimController(){
        makeFrame();
        sim = new Simulator();
        simTimer = new Timer(100, e -> doNothing());
        //new
        simTimer.start();


    }

    private void doNothing(){
        System.out.println("This does nothing");
    }

    /**
     * Create the Swing frame and its content.
     */
    private void makeFrame()
    {
        frame = new JFrame("Sim Control");
        JPanel contentPane = (JPanel) frame.getContentPane();

        makeMenuBar(frame);

        // Specify the layout manager
        contentPane.setLayout(new FlowLayout());

        // Create the toolbar with the buttons
        JPanel toolbar = new JPanel();

        oneButton = new JButton("1");
        zeroButton = new JButton("0");

        toolbar.add(oneButton);
        toolbar.add(zeroButton);

        // Add toolbar into panel with flow layout for spacing
        JPanel flow = new JPanel();
        flow.add(toolbar);

        contentPane.add(flow );

        frame.pack();

        // place the frame at the center of the screen and show
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(d.width/2 - frame.getWidth()/2, d.height/2 - frame.getHeight()/2);
        frame.setVisible(true);
        //new
        // Add listeners to the buttons
        oneButton.addActionListener(e -> oneButtonClicked());
        zeroButton.addActionListener(e -> zeroButtonClicked());
    }

    /**
     * Create the main frame's menu bar.
     * 
     * @param frame   The frame that the menu bar should be added to.
     */
    private void makeMenuBar(JFrame frame)
    {

        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);

        JMenu menu;
        JMenuItem item;

        // create the File menu
        menu = new JMenu("File");
        menubar.add(menu);

        item = new JMenuItem("Item 1");



        menu.add(item);
        menu.addSeparator();

        item = new JMenuItem("Quit - not working");
   
        menu.add(item);
        
        //new
        item.addActionListener(e -> quit());

        // create file load menu item
        item = new JMenuItem("Load");
        item.addActionListener(e -> loadFile());
        menu.add(item);
        // create file save menu item
        item = new JMenuItem("Save");
        item.addActionListener(e -> saveFile());
        menu.add(item);

    }

    public void quit(){
        // SHUT DOWN YOUR OWN FRAME AND TIMER HERE
        simTimer.stop();
        frame.dispose();
        sim.endSimulation();
        frame.setVisible(false);
        frame.dispose();
        // System.exit(0);
    }

    public Simulator getSimulator(){ 
        return sim;
    }

    public void oneButtonClicked(){
        System.out.println("One button clicked");
        simTimer.stop();
        simTimer.setDelay(DELAY);
        simTimer.setInitialDelay(0);
        simTimer.setRepeats(true);
        simTimer.start();
    }

    public void zeroButtonClicked(){
        System.out.println("Zero button clicked");
        simTimer.stop();
    }

    public void loadFile(){
        int returnVal = fileChooser.showOpenDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            System.out.println("You chose to open this file: " +
                file.getName());
        }
        else {
            System.out.println("Open command cancelled by user.");
        }
    }

    public void saveFile(){
        int returnVal = fileChooser.showSaveDialog(frame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            System.out.println("You chose to save this file: " +
                file.getName());
        }
        else {
            System.out.println("Save command cancelled by user.");
        }
    }

    public static void main(String[] args){
        SimController controller = new SimController();
    }



}



