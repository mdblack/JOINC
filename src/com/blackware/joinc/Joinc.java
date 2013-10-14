package com.blackware.joinc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class Joinc extends Activity 
{
	BoincServerLink boincServerLink=null;
	public Handler handler;
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        handler=new Handler();
        System.out.println(Runtime.getRuntime().freeMemory()+" "+Runtime.getRuntime().maxMemory()+" "+Runtime.getRuntime().totalMemory());
        new ProjectChooser(this);
    }
    
    public void login(String url, String userName, String password)
    {
    	if (url.contains("http://"))
    		url=url.substring(7,url.length());
    	String projectID=url.substring(url.indexOf("/")+1,url.length());
    	String address=url.substring(0,url.indexOf("/"));
    	
		boincServerLink = new BoincServerLink(address,projectID,true);
		
		String error = boincServerLink.loginToAccount(userName, password);
		
		if (!error.equals(""))
		{
			errorBox(error);
			return;
		}
		startProject();
    }
    public void createAccount(String url, String userName, String password)
    {
    	if (url.contains("http://"))
    		url=url.substring(7,url.length());
    	String address=url.substring(0,url.indexOf("/"));
    	String projectID=url.substring(url.indexOf("/"),url.length());
		boincServerLink = new BoincServerLink(address,projectID,true);
		
		String error = boincServerLink.createAccount(userName, password, userName);
		if (!error.equals(""))
		{
			errorBox(error);
			return;
		}
		startProject();
    }
    private void errorBox(String message)
    {
    	AlertDialog.Builder adb=new AlertDialog.Builder(this);
    	adb.setTitle("Error");
    	adb.setMessage(message);
    	adb.setNeutralButton("Exit", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				System.exit(0);
			}});
    	adb.show();
    }
    private Button pauseResumeButton, exitButton;
    private EditText[] infoText;
    private static final int INFOTEXTSIZE=7;
    private int infoTextNum=0;
    private void createProjectView()
    {
        infoText=new EditText[INFOTEXTSIZE];
		LinearLayout layout=new LinearLayout(this);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);

		pauseResumeButton=new Button(this);
		exitButton=new Button(this);
		pauseResumeButton.setText("Pause");
		pauseResumeButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				if (pauseResumeButton.getText().equals("Pause"))
				{
					pauseResumeButton.setText("Continue");
				}
				else
				{
					pauseResumeButton.setText("Pause");
				}
			}});
		exitButton.setText("Close");
		exitButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				System.exit(0);
			}});
		layout.addView(pauseResumeButton);
		layout.addView(exitButton);

		for (int i=0; i<INFOTEXTSIZE; i++)
    	{
    		infoText[i]=new EditText(this);
     		infoText[i].setText("");
    		layout.addView(infoText[i]);
    	}
		setContentView(layout);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }
    public void clearInfoText()
    {
    	handler.post(new Runnable(){public void run(){
		for (int i=0; i<INFOTEXTSIZE; i++)
    	{
     		infoText[i].setText(" ");
    	}
		infoTextNum=0;
    	}});
    }
    public void setInfoText(final String message)
    {
       	handler.post(new Runnable(){public void run(){
		if (infoTextNum<INFOTEXTSIZE)
			infoText[infoTextNum++].setText(message);
    	}});
    }
    public void startProject()
    {
    	createProjectView();
    	new Thread(new Runnable()
    	{
    		public void run()
    		{
    			Looper.prepare();
    			while(true)
    				runProjectCycle();
    		}
    	}).start();
    }
    public void runProjectCycle()
    {
    	clearInfoText();
		setInfoText("Attached to "+boincServerLink.url);
		setInfoText("Requesting work from scheduler");
		String error=boincServerLink.schedulerRequest();
		if (!error.equals(""))
			{
				errorBox(error);
				return;
			}

		String name=boincServerLink.executableName().substring(boincServerLink.executableName().lastIndexOf("/"),boincServerLink.executableName().length());
		
		setInfoText("Downloading executable "+name);
    	
		byte[] executable = boincServerLink.downloadExecutable();
		if (executable==null)
		{
	       	handler.post(new Runnable(){public void run(){
			errorBox("Couldn't download executable");
	       	}});
			return;
		}

    	
		name=boincServerLink.workloadUrl().substring(boincServerLink.workloadUrl().lastIndexOf("/"),boincServerLink.workloadUrl().length());
		setInfoText("Downloading workunit "+name);

		byte[] workunit = boincServerLink.downloadWorkUnit();
		if (workunit==null)
		{
	       	handler.post(new Runnable(){public void run(){
			errorBox("Couldn't download workunit");
	       	}});
			return;
		}

		setInfoText("Creating virtual machine");

		Computer computer=new Computer();
		computer.loadElf(executable);
		computer.loadInitData(boincServerLink.generate_init_data_xml());
		computer.loadWorkUnit(workunit);
		
		setInfoText("Starting processing");
		
		computer.execute();
		
		setInfoText("Uploading result");
		
		boincServerLink.uploadResult(computer.getOutputFile());

		clearInfoText();
		setInfoText("Done!");
    }
}