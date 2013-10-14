package com.blackware.joinc;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProjectChooser 
{
	private Joinc joinc;
	private EditText projectText, userNameText, passwordText;
	public ProjectChooser(Joinc joinc)
	{
		this.joinc=joinc;
		lock=new Object();
		
		LinearLayout layout=new LinearLayout(joinc);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		
		TextView text=new TextView(joinc);
		text.setText("Project URL");
		layout.addView(text);
		projectText = new EditText(joinc);
		layout.addView(projectText);
		text=new TextView(joinc);
		text.setText("Email");
		layout.addView(text);
		userNameText = new EditText(joinc);
		layout.addView(userNameText);
		text=new TextView(joinc);
		text.setText("Password");
		layout.addView(text);
		passwordText = new EditText(joinc);
		layout.addView(passwordText);
		
		projectText.setText("http://69.255.108.217/example_app");
		userNameText.setText("blackmd@gmail.com");
		passwordText.setText("boincfrog");
		
		
		
		LinearLayout blayout=new LinearLayout(joinc);
		blayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		blayout.setOrientation(LinearLayout.HORIZONTAL);

		Button loginButton,createAccountButton,exitButton;
		loginButton=new Button(joinc);
		createAccountButton=new Button(joinc);
		exitButton=new Button(joinc);
		loginButton.setText("Login");
		createAccountButton.setText("Create Account");
		exitButton.setText("Quit");
		loginButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				doLogin();
			}});
		createAccountButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				doCreateAccount();
			}});
		exitButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				System.exit(0);
			}});
		blayout.addView(loginButton);
		blayout.addView(createAccountButton);
		blayout.addView(exitButton);
		layout.addView(blayout);
		
		joinc.setContentView(layout);
	}
	
	protected void doLogin() 
	{
		joinc.login(projectText.getText().toString(),userNameText.getText().toString(),passwordText.getText().toString());
	}
	protected void doCreateAccount() 
	{
		joinc.createAccount(projectText.getText().toString(),userNameText.getText().toString(),passwordText.getText().toString());
	}

	private Object lock;
    public synchronized void pause()
    {
    	try
    	{
    		lock.wait();
    	}
    	catch(InterruptedException e){}
    }
    public synchronized void resume()
    {
    	lock.notify();
    }
}
