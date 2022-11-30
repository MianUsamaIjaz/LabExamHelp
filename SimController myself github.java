package sim;

import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import javax.swing.JLabel;


/**
 * A graphical view of a rectangular field. This is a view of the field
 * occupied by foxes and rabbits. It is responsible for displaying the
 * field, a legend, and some statistics.
 * 
 * This view uses colors to indicate the life forms. Empty locations
 * are white. Rabbits are orange. Foxes are blue. 
 * 
 * The simulation can be stepped through one step at a time or it
 * can run continuously.
 * 
 * @author Mark Truran
 * @version 2011.11.30
 */



public class SimController extends JFrame
{
    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 400;
    
    private static final String TITLE = "Sim Control";
    private Simulator sim;
    

    private JButton stepButton;
    private JButton runButton;
    private JButton stopButton;
    private JButton resetButton;
    private JButton runToButton;
    private JButton slowerButton;
    private JButton fasterButton;
    private JTextField runToField;
    private JTextField timerField;
    private JTextField speedField;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem loadItem;
    private JMenuItem saveItem;
    private JMenuItem quitItem;

    private boolean running;
    private boolean runTo;
    private int runToStep;
    private int timer;
    private int speed;
    private Timer t;
    
    private JLabel status;
    private JLabel mbar;
    
    private JFileChooser chooser = new JFileChooser();
    /**
     * Create a view of the given width and height.
     * @param width The simulation's width.
     * @param height The simulation's height.
     */
    public SimController()
    {
        super(TITLE);
        sim = new Simulator();
        running = false;
        runTo = false;
        runToStep = 0;
        timer = 0;
        speed = 100;
        createComponents();
        setLayout(new BorderLayout());
        add(menuBar, BorderLayout.NORTH);
        
        add(createControlPlane(), BorderLayout.SOUTH);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Create the control plane.
     */
    private JPanel createControlPlane()
    {
        JPanel controlPlane = new JPanel();
        controlPlane.setLayout(new GridLayout(4, 8));
        controlPlane.add(status);
        controlPlane.add(stepButton);
        controlPlane.add(runButton);
        controlPlane.add(stopButton);

        controlPlane.add(slowerButton);
        //controlPlane.add(timerField);
        controlPlane.add(speedField);
        controlPlane.add(fasterButton);
        
        controlPlane.add(runToButton);
        controlPlane.add(runToField);

        controlPlane.add(resetButton);
        controlPlane.add(mbar);
        
        
        return controlPlane;
    }

    /**
     * Create the menu bar.
     */
    private void createComponents()
    {
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        loadItem = new JMenuItem("Load");
        saveItem = new JMenuItem("Save");
        quitItem = new JMenuItem("Quit");
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.add(quitItem);
        menuBar.add(fileMenu);
        stepButton = new JButton("Step");
        runButton = new JButton("Run");
        stopButton = new JButton("Stop");
        resetButton = new JButton("Reset");
        runToButton = new JButton("Run To:");
        slowerButton = new JButton("Slower");
        fasterButton = new JButton("Faster");
        runToField = new JTextField(5);
        timerField = new JTextField(5);
        speedField = new JTextField(5);
        status = new JLabel("SIM NOT RUNNING");
        mbar = new JLabel("STATUS BAR");

        stepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                step();
            }
        });
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                run();
            }
        });
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        runToButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runTo();
            }
        });
        slowerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                slower();
            }
        });
        fasterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                faster();
            }
        });
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });
        
        loadItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooser.showSaveDialog(null);
            }
        });
    }

    /**
     * Run the simulation.
     */
    public void run()
    {
        running = true;
        runTo = false;
        runToStep = 0;
        speedField.setText(Integer.toString(speed));
        status.setText("SIM RUNNING");
        
        t = new Timer(speed, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                step();
                mbar.setText("["+sim.getStep()+"]" + sim.getDetails());
            }
        });
        runButton.setEnabled(false);
        t.start();
    }

    /**
     * Run the simulation to the given step.
     */
    public void runTo()
    {
        running = true;
        runTo = true;
        runToStep = Integer.parseInt(runToField.getText());
        status.setText("Sim Running");
        t = new Timer(speed, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                step();
                if (sim.getStep()==runToStep){
                    stop();
                }
            }
        });
        
        t.start();
    }

    /**
     * Stop the simulation.
     */
    public void stop()
    {
        running = false;
        runTo = false;
        runToStep = 0;
        status.setText("SIM NOT RUNNING");
        runButton.setEnabled(true);
        t.stop();
    }

    /**
     * Reset the simulation.
     */
    public void reset()
    {
        running = false;
        runTo = false;
        runToStep = 0;
        sim.reset();
        timer = 0;
        speed = 100;
        status.setText("SIM NOT RUNNING");
        mbar.setText("STATUS BAR");
        runButton.setEnabled(true);
        t.stop();
    }

    /**
     * Step the simulation.
     */
    public void step()
    {
        sim.simulateOneStep();
        timer++;
        timerField.setText("" + timer);
        mbar.setText("["+sim.getStep()+"]" + sim.getDetails());
    }

    /**
     * Make the simulation run slower.
     */
    public void slower()
    {
        if (speed != 0 && (speed*2) < 1025 ) {speed = speed *2 ;}
        else if(speed == 0){speed+=1;}
        else{speed=speed;}
        t.setDelay(speed);
        speedField.setText(Integer.toString(speed));
    }

    /**
     * Make the simulation run faster.
     */
    public void faster()
    {
        if ((speed / 2) != 0 ) {speed = speed / 2;}
        t.setDelay(speed);
        speedField.setText(Integer.toString(speed));
    }

    /**
     * Quit the application.
     */
     public void quit()
    {
        t.stop();
        this.dispose();
        sim.endSimulation();
        this.setVisible(false);
        this.dispose();
        //System.exit(0);
    }

    /**
     * Main method.
     */
    public static void main(String[] args)
    {
        new Simulator();
    }
    
    public Simulator getSimulator()
    {
        return sim;
    }
    
}