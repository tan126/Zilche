package com.zilche.zilche;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class BarChart extends Activity {

    private View mChart;
    private Poll poll;
    private String[] options;
    private int[] votes;
    private int optNum;
    private int maxVote;
    public int ylable;
    private String question;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        // Getting reference to the button btn_chart
        Button btnChart = (Button) findViewById(R.id.btn_chart);

        ImageButton cancel = (ImageButton) findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Defining click event listener for the button btn_chart
        /*OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Draw the Income vs Expense Chart
                openChart();
            }
        };*/

        Bundle extras = getIntent().getExtras();
        final Poll poll = extras.getParcelable("poll");
        String tmpOptions[] = poll.getOptions();
        for(int i = 0; i < tmpOptions.length; i ++){
            String [] extract = tmpOptions[i].split(" ");
            if (extract.length > 1)
                tmpOptions[i] = extract[0] + "...";
            else
                tmpOptions[i] = extract[0];
        }
        options = tmpOptions;
        votes = poll.getVotes();
        optNum = poll.getCount();
        question = poll.getQuestion();

        // Setting event click listener for the button btn_chart of the MainActivity layout
        //btnChart.setOnClickListener(clickListener);
        //Log.d("kkk", ""+votes[0]);
        openChart();

    }


    private void openChart(){
        //int[] x = { 0,1,2,3,4,5,6,7, 8, 9, 10, 11 };
        //int[] income = { 2000,2500,2700,3000,2800,3500,3700,3800, 0,0,0,0};
        //int[] expense = {2200, 2700, 2900, 2800, 2600, 3000, 3300, 3400, 0, 0, 0, 0 };

        // Creating an XYSeries for Income
        //XYSeries incomeSeries = new XYSeries("Income");
        // Creating an XYSeries for Expense
        //XYSeries expenseSeries = new XYSeries("Expense");
        XYSeries votesSeries = new XYSeries("Votes");
        // Adding data to Income and Expense Series
        /*for(int i=0;i<x.length;i++){
            incomeSeries.add(i,income[i]);
            expenseSeries.add(i,expense[i]);
        }*/

        maxVote = 0;
        for(int i = 0; i < optNum; i++ ){
            votesSeries.add(i, votes[i]);
            if(votes[i] > maxVote)
                maxVote = votes[i];
        }
        ylable = 1;
        if (maxVote >= 5)
            ylable = 5;
        else
            ylable = maxVote;

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Income Series to the dataset
        //dataset.addSeries(incomeSeries);
        // Adding Expense Series to dataset
        dataset.addSeries(votesSeries);

        // Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer votesRenderer = new XYSeriesRenderer();
        votesRenderer.setColor(Color.parseColor("#76A7FA")); //color of the graph set to cyan
        votesRenderer.setFillPoints(true);
        votesRenderer.setLineWidth(0);
        votesRenderer.setDisplayChartValues(true);
        votesRenderer.setChartValuesTextSize(28);
        //votesRenderer.setDisplayChartValuesDistance(10); //setting chart value distance
        votesRenderer.setChartValuesTextAlign(Align.CENTER);

        // Creating XYSeriesRenderer to customize expenseSeries
        /*XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.GREEN);
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(2);
        expenseRenderer.setDisplayChartValues(true);*/

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        //multiRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        multiRenderer.setXLabels(0);
        //multiRenderer.setYLabels(0);
        multiRenderer.setChartTitle(question);
        //multiRenderer.setXTitle("Options");
        //multiRenderer.setYTitle("Votes");
        multiRenderer.setYLabelsColor(0, Color.TRANSPARENT);
        multiRenderer.setGridColor(Color.GRAY);
        multiRenderer.setShowAxes(false);
        multiRenderer.setLabelsColor(Color.BLACK);
        multiRenderer.setXLabelsColor(Color.BLACK);


        /***
         * Customizing graphs
         */
//setting text size of the title
        multiRenderer.setChartTitleTextSize(32);
        //setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(24);
        //setting text size of the graph lable
        multiRenderer.setLabelsTextSize(24);
        //setting zoom buttons visiblity
        multiRenderer.setZoomButtonsVisible(false);
        //setting pan enablity which uses graph to move on both axis
        multiRenderer.setPanEnabled(false, false);
        //setting click false on graph
        multiRenderer.setClickEnabled(false);
        //setting zoom to false on both axis
        multiRenderer.setZoomEnabled(false, false);
        //setting lines to display on y axis
        multiRenderer.setShowGridY(false);
        //setting lines to display on x axis
        multiRenderer.setShowGridX(true);
        //setting legend to fit the screen size
        multiRenderer.setFitLegend(true);
        //setting displaying line on grid
        //multiRenderer.setShowGrid(true);
        //setting zoom to false
        multiRenderer.setZoomEnabled(false);
        //setting external zoom functions to false
        multiRenderer.setExternalZoomEnabled(false);
        //setting displaying lines on graph to be formatted(like using graphics)
        multiRenderer.setAntialiasing(true);
        //setting to in scroll to false
        multiRenderer.setInScroll(false);
        //setting to set legend height of the graph
        multiRenderer.setLegendHeight(30);
        //multiRenderer.setShowLegend(false);
        //setting x axis label align
        multiRenderer.setXLabelsAlign(Align.CENTER);
        //setting y axis label to align
        multiRenderer.setYLabelsAlign(Align.LEFT);
        //setting text style
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
        //setting no of values to display in y axis
        multiRenderer.setYLabels(ylable);
        // setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
        // if you use dynamic values then get the max y value and set here
        multiRenderer.setYAxisMax(maxVote);
        multiRenderer.setYAxisMin(0);
        //setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMin(-1);
//setting max values to be display in x axis
        multiRenderer.setXAxisMax(optNum);
        //setting bar size or space between two bars
        multiRenderer.setBarSpacing(0.5);
        //Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
        //Setting margin color of the graph to transparent
        multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent_background));
        //multiRenderer.setMarginsColor(Color.GRAY);
        multiRenderer.setApplyBackgroundColor(true);

        //setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{120, 80, 120, 80});

        for(int i=0; i< optNum;i++){
            multiRenderer.addXTextLabel(i, options[i]);
        }

        // Adding incomeRenderer and expenseRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(votesRenderer);
        //multiRenderer.addSeriesRenderer(expenseRenderer);

        //this part is used to display graph on the xml
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart);
        //remove any views before u paint the chart
        chartContainer.removeAllViews();
        //drawing bar chart
        mChart = ChartFactory.getBarChartView(BarChart.this, dataset, multiRenderer,Type.DEFAULT);
        //adding the view to the linearlayout
        chartContainer.addView(mChart);

    }

}
