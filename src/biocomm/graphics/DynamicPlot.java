package biocomm.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;



import biocomm.host.*;
import biocomm.networking.*;

/**
 * A demonstration application showing a time series chart where you can dynamically add
 * (random) data by clicking on a button.
 *
 */
public class DynamicPlot extends ApplicationFrame implements ActionListener {

    /** The number of subplots. */
    public static final int SUBPLOT_COUNT = 8;
    
    /** Toogle switch to plot all channels*/
    public static Boolean[] plotChannel =  {false, false, false, false, false, false, false, false} ;

    private int incrementTracker = 0;
    
    /** The datasets. */
    private TimeSeriesCollection[] datasets;
    
    /** The most recent value added to series 1. */
    private double[] lastValue = new double[SUBPLOT_COUNT];
    
    /** Timer to refresh graph after every 1/4th of a second */
    private Timer timer = new Timer(1, this);

    /**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    public DynamicPlot(final String title) {

        super(title);
        
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis("Time"));
        this.datasets = new TimeSeriesCollection[SUBPLOT_COUNT];
        
        
        
        for (int i = 0; i < SUBPLOT_COUNT; i++) {
            this.lastValue[i] = 100.0;
            final TimeSeries series = new TimeSeries("CHANNEL " + i, Millisecond.class);
            this.datasets[i] = new TimeSeriesCollection(series);
            final NumberAxis rangeAxis = new NumberAxis("CH" + i);
            rangeAxis.setAutoRangeIncludesZero(false);
            final XYPlot subplot = new XYPlot(
                    this.datasets[i], null, rangeAxis, new StandardXYItemRenderer()
            );
            subplot.setBackgroundPaint(Color.black);
            subplot.setDomainGridlinePaint(Color.white);
            subplot.setRangeGridlinePaint(Color.white);
            plot.add(subplot);
        }

        final JFreeChart chart = new JFreeChart("BIOCOMM RECEIVER", plot);
                
        timer.setInitialDelay(1000);
       
        
//        chart.getLegend().setAnchor(Legend.EAST);
        chart.setBorderPaint(Color.black);
        chart.setBorderVisible(true);
        chart.setBackgroundPaint(Color.darkGray);
        
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
  //      plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 4, 4, 4, 4));
        final ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);  // 60 seconds
        
        final JPanel content = new JPanel(new BorderLayout());

        final ChartPanel chartPanel = new ChartPanel(chart);
        content.add(chartPanel);

        final JPanel buttonPanel = new JPanel(new FlowLayout());
        

        
        
        for (incrementTracker = 0; incrementTracker < SUBPLOT_COUNT; incrementTracker++) {
            final JToggleButton button = new JToggleButton("CH " + incrementTracker);
            button.setActionCommand("TOGGLE_CH_" + incrementTracker);
            button.addActionListener(buttonActionHold(incrementTracker));
            buttonPanel.add(button);
        }
        final JToggleButton buttonAll = new JToggleButton("START/STOP");
        buttonAll.setActionCommand("DISABLE_ALL");
        buttonAll.addActionListener(actionStart);
        buttonPanel.add(buttonAll);
        
        content.add(buttonPanel, BorderLayout.SOUTH);
        chartPanel.setPreferredSize(new java.awt.Dimension(900, 580));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(content);

        
        timer.start();
    }

    /**
     * Handles the automatic refresh that happens on input
     *
     * @param ActionEvent e  the action event.
     */
    public void actionPerformed(final ActionEvent e) {
    	
    	String currentInputSummaryDisplay = null;
   	
    	// Compute the data to be passed to graph
            final Millisecond now = new Millisecond();
            
            currentInputSummaryDisplay = "Current Time in Milliseconds = " + now.toString();
            logGenerator.addLogEntry(new File("biocomm.log"), currentInputSummaryDisplay);
            
            for (int i = 0; i < SUBPLOT_COUNT; i++) {
            	

            	if(plotChannel[i] == true){
            		
            		
            		this.lastValue[i] = DesktopActingServer.iCurrentADCValue[i];//this.lastValue[i] * (0.90 + 0.2 * Math.random());
            		this.datasets[i].getSeries(0).add(new Millisecond(), this.lastValue[i]); 
                
            		currentInputSummaryDisplay = DesktopActingServer.sCurrentSensorName + " | CHANNEL " + i + " | Current Value : "+ this.lastValue[i];
            		logGenerator.addLogEntry(new File("biocomm.log"), currentInputSummaryDisplay);
            	} else {
            		
                    currentInputSummaryDisplay = "NO DATA TO PLOT FOR CHANNEL " + i +" | INPUT DISABLED";
                    logGenerator.addLogEntry(new File("biocomm.log"), currentInputSummaryDisplay);  
            		
            	}
        
            }

    }

    /*
     * Action Listener for START/STOP BUTTON
     */

    ActionListener actionStart = new ActionListener(){
    
    	public void actionPerformed(ActionEvent e){
    	
        JToggleButton tBtn = (JToggleButton)e.getSource();
        if (tBtn.isSelected()) {
           System.out.println("HOLDING ALL CHANNELS");
           
           
           for(int i=0; i < plotChannel.length ; i++){
        	   
        	   plotChannel[i] = false;
        	   
           }
           tBtn.setText("STOPPED");
           tBtn.setForeground(Color.red);
           
           
           
        } else {
           System.out.println("PLOT ALL CHANNELS");

           for(int i=0; i < plotChannel.length ; i++){
        	   
        	   plotChannel[i] = true;
           }
           tBtn.setText("STARTED");
           tBtn.setForeground(Color.black);
           tBtn.setBackground(Color.green);
           
        }
    
    }


    };
    
    
    /*
     * PERFORM Hold action on buttons
     * @param int
     * @return ActionListener
     */
    
    public ActionListener buttonActionHold(int incrementTracker){
    
    ActionListener actionHold = new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		
            JToggleButton tBtn = (JToggleButton)e.getSource();
            if (tBtn.isSelected()) {
            	
            			
                                      
                	   System.out.println("HOLDING CHANNEL" + 0);
                	   plotChannel[incrementTracker] = false; 
                	   tBtn.setForeground(Color.red);

            } else {
            	
                		System.out.println("PLOT CHANNEL" + 0);
                		plotChannel[incrementTracker] = true;
                		tBtn.setForeground(Color.black);
                		
                } 
            }
    };
    
    return actionHold;
    }
    
    
    /*
     * TO RELEASE CH i Buttons
     */
    ActionListener actionStop = new ActionListener(){
    	public void actionPerformed(ActionEvent e){
    		
    		JToggleButton tBtn = (JToggleButton)e.getSource();
    		tBtn.setSelected(false);
    		
    	}
    	};
    
}