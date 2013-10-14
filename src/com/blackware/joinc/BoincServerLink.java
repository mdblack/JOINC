package com.blackware.joinc;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;


public class BoincServerLink 
{
	String url,projectName,schedulerReply,authenticator,masterurl;
	boolean projectNameInPHPPath;
	
	public BoincServerLink(String url, String projectName, boolean projectNameInPHPPath)
	{
		this.url=url;
		this.projectName=projectName;
		this.projectNameInPHPPath=projectNameInPHPPath;
	}
	
	public String createAccount(String email, String password, String username)
	{
		String fullurl="http://"+url+"/"+projectName+"/create_account.php?email_addr="+email+"&passwd_hash=";
	
		if (!projectNameInPHPPath)
			fullurl="http://"+url+"/create_account.php?email_addr="+email+"&passwd_hash=";
		
		HttpURLConnection connection=null;

		String reply="";
		try
		{
			MessageDigest md=MessageDigest.getInstance("MD5");
			byte[] md5=md.digest((password+email).getBytes());
			for (int i=0; i<md5.length; i++)
			{
				String m=Integer.toHexString(md5[i]);
				if (m.length()<2)
					m="0"+m;
				else if (m.length()>2)
					m=m.substring(m.length()-2,m.length());
				fullurl+=m;
			}
			fullurl+="&user_name="+username;
			System.out.println(fullurl);
			connection=(HttpURLConnection)(new URL(fullurl).openConnection());
			connection.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while (true)
			{
				int c=br.read();
				if (c==-1) break;
				reply+=(char)c;
			}
		} 
		catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
			e.printStackTrace();
			return "Bad URL";
		}
		catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
			return "IO Exception";
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No MD5 algorithm");
			e.printStackTrace();
			return "No MD5 algorithm";
		}
		finally
		{
			if (connection!=null) connection.disconnect();
		}
		System.out.println(reply);		

		authenticator=reply.substring(reply.indexOf("<authenticator>")+"<authenticator>".length(),reply.indexOf("</authenticator>"));
		return "";
	}
	
	public String loginToAccount(String email, String password)
	{
		String fullurl="http://"+url+"/"+projectName+"/lookup_account.php?email_addr="+email+"&passwd_hash=";
		
		if (!projectNameInPHPPath)
			fullurl="http://"+url+"/lookup_account.php?email_addr="+email+"&passwd_hash=";

		HttpURLConnection connection=null;

		String reply="";
		try
		{
			MessageDigest md=MessageDigest.getInstance("MD5");
			byte[] md5=md.digest((password+email).getBytes());
			for (int i=0; i<md5.length; i++)
			{
				String m=Integer.toHexString(md5[i]);
				if (m.length()<2)
					m="0"+m;
				else if (m.length()>2)
					m=m.substring(m.length()-2,m.length());
				fullurl+=m;
			}
			System.out.println(fullurl);
			connection=(HttpURLConnection)(new URL(fullurl).openConnection());
			connection.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while (true)
			{
				int c=br.read();
				if (c==-1) break;
				reply+=(char)c;
			}
		} 
		catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
			e.printStackTrace();
			return "bad url";
		}
		catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
			return "io exception";
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No MD5 algorithm");
			e.printStackTrace();
			return "No md5 algorithm";
		}
		finally
		{
			if (connection!=null) connection.disconnect();
		}
		System.out.println(reply);
		
		authenticator=reply.substring(reply.indexOf("<authenticator>")+"<authenticator>".length(),reply.indexOf("</authenticator>"));
		System.out.println(authenticator);
		return "";
	}
	
	public String schedulerRequest()
	{
		String request = generateSchedulerRequest();
		System.out.println("Request:\n"+request+"\n\n");
		String reply="";
		HttpURLConnection connection=null;
		try 
		{
			String fullurl="http://"+url+"/"+projectName+"_cgi/cgi";
			if (!this.projectNameInPHPPath)
				fullurl="http://"+url+"_cgi/cgi";
			System.out.println(fullurl);
			connection=(HttpURLConnection)(new URL(fullurl).openConnection());
			connection.setDoOutput(true);
			connection.connect();
			connection.getOutputStream().write(request.getBytes());
			connection.getOutputStream().close();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while (true)
			{
				int c=br.read();
				if (c==-1) break;
				reply+=(char)c;
			}
		} 
		catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
			e.printStackTrace();
			return "Bad URL";
		}
		catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
			return "IO exception";
		}
		finally
		{
			if (connection!=null) connection.disconnect();
		}
		
		System.out.println("Reply:\n"+reply);
		schedulerReply=reply;
		masterurl=reply.substring(reply.indexOf("<master_url>")+"<master_url>".length(),reply.indexOf("</master_url>"));
		System.out.println("masterurl: "+masterurl);
		return "";
	}
	
	public String generateSchedulerRequest()
	{
	//	String authenticator="548456c52dda3f1c99e32d33625a345d",code_sign_key="1024\nbecc7c5e972597404ce59f630075496e80e08e8d9b85ad60dcc97fef5d2473b7748f92579e7a17e20a42a527beeb126b92a57bbfe86fd2f63fe23f4af6ac9330d3be8d6d1e8fe3603f83d78bf15da5d300ad2e6b5feb0d6b1e59a281b984af69787832e92cdedc5a3c5b11876a2380edcc35b638e94b6ec97eb1c8617ef3c9c30000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010001.";
		
		String xmlrequest="";
		xmlrequest+="<scheduler_request>\n";
		xmlrequest+="    <authenticator>"+authenticator+"</authenticator>\n";
		xmlrequest+="    <hostid>"+1+"</hostid>\n";
		xmlrequest+="    <rpc_seqno>"+1+"</rpc_seqno>\n";
		xmlrequest+="    <platform_name>i686-pc-linux-gnu</platform_name>\n";
		xmlrequest+="    <core_client_major_version>"+4+"</core_client_major_version>\n";
		xmlrequest+="    <core_client_minor_version>"+43+"</core_client_minor_version>\n";
		xmlrequest+="    <core_client_release>"+"</core_client_release>\n";
		xmlrequest+="    <work_req_seconds>100000</work_req_seconds>\n";
		xmlrequest+="    <resource_share_fraction>"+"0.50"+"</resource_share_fraction>\n";
		xmlrequest+="    <estimated_delay>0.000000</estimated_delay>\n";
//		xmlrequest+="    <code_sign_key>"+code_sign_key+"</code_sign_key>\n";
		xmlrequest+="    <global_preferences>\n";
		xmlrequest+="     <source_project>"+"http://"+url+"/"+projectName+"/</source_project>\n";
		xmlrequest+="     <source_scheduler>"+"http://"+url+"/"+projectName+"/cgi/</source_scheduler>\n";
		xmlrequest+="      <mod_time>0</mod_time>\n";
		xmlrequest+="      <run_on_batteries/>\n";
		xmlrequest+="      <run_if_user_active/>\n";
		xmlrequest+="      <idle_time_to_run>3</idle_time_to_run>\n";
		xmlrequest+="      <leave_apps_in_memory/>\n";
		xmlrequest+="      <cpu_scheduling_period_minutes>60</cpu_scheduling_period_minutes>\n";
		xmlrequest+="      <work_buf_min_days>3.0</work_buf_min_days>\n";
		xmlrequest+="      <max_cpus>1</max_cpus>\n";
		xmlrequest+="      <disk_interval>60</disk_interval>\n";
		xmlrequest+="      <disk_max_used_gb>100</disk_max_used_gb>\n";
		xmlrequest+="      <disk_max_used_pct>50</disk_max_used_pct>\n";
		xmlrequest+="      <disk_min_free_gb>0.1</disk_min_free_gb>\n";
		xmlrequest+="      <vm_max_used_pct>75</vm_max_used_pct>\n";
		xmlrequest+="      <max_bytes_sec_down>0</max_bytes_sec_down>\n";
		xmlrequest+="      <max_bytes_sec_up>0</max_bytes_sec_up>\n";
		xmlrequest+="    </global_preferences>\n";
		xmlrequest+="    <time_stats>\n";
		xmlrequest+="     <on_frac>0.988982</on_frac>\n";
		xmlrequest+="     <connected_frac>-1.000000</connected_frac>\n";
		xmlrequest+="     <active_frac>0.998568</active_frac>\n";
		xmlrequest+="    </time_stats>\n";
		xmlrequest+="    <net_stats>\n";
		xmlrequest+="     <bwup>100000</bwup>\n";
		xmlrequest+="     <bwdown>1000000</bwdown>\n";
		xmlrequest+="    </net_stats>\n";
		xmlrequest+="    <host_info>\n";
		xmlrequest+="     <timezone>28800</timezone>\n";
		xmlrequest+="     <domain_name>"+url+"</domain_name>\n";
		xmlrequest+="     <ip_addr>"+url+"</ip_addr>\n";
		xmlrequest+="     <p_ncpus>1</p_ncpus>\n";
		xmlrequest+="     <m_nbytes>197427200.000000</m_nbytes>\n";
		xmlrequest+="     <m_cache>131072.000000</m_cache>\n";
		xmlrequest+="     <m_swap>178012160.000000</m_swap>\n";
		xmlrequest+="     <d_total>62621513472.000000</d_total>\n";
		xmlrequest+="     <d_free>33745464320.000000</d_free>\n";
		xmlrequest+="    </host_info>\n";
		xmlrequest+="</scheduler_request>\n";

		return xmlrequest;
	}
	
	public byte[] loadLocalExecutable(String name)
	{
		ArrayList<Byte> fileList=new ArrayList<Byte>();
		try
		{
			FileInputStream br = new FileInputStream(name);
			while (true)
			{
				int c=br.read();
				if (c==-1) break;
				fileList.add(new Byte((byte)c));
			}
		}
		catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		byte[] file=new byte[fileList.size()];
		int i=0;
		for (Byte b:fileList)
			file[i++]=b.byteValue();
		return file;
	}
	
	public String executableName()
	{
		return schedulerReply.substring(schedulerReply.indexOf("<url>",schedulerReply.indexOf("<file_info>"))+"<url>".length(),schedulerReply.indexOf("</url>",schedulerReply.indexOf("<file_info>")));
	}

	public String workloadUrl()
	{
		return schedulerReply.substring(schedulerReply.indexOf("<url>",schedulerReply.indexOf("<file_info>",schedulerReply.indexOf("<file_info>")+1))+"<url>".length(),schedulerReply.indexOf("</url>",schedulerReply.indexOf("<file_info>",schedulerReply.indexOf("<file_info>")+1)));
	}
	
	public byte[] downloadExecutable()
	{
//		String fileUrl=schedulerReply.substring(schedulerReply.indexOf("<url>",schedulerReply.indexOf("<file_info>"))+"<url>".length(),schedulerReply.indexOf("</url>",schedulerReply.indexOf("<file_info>")));
		String fileUrl=masterurl+schedulerReply.substring(schedulerReply.indexOf("download/",schedulerReply.indexOf("<url>",schedulerReply.indexOf("<file_info>"))),schedulerReply.indexOf("</url>",schedulerReply.indexOf("<file_info>")));
		
		HttpURLConnection connection=null;
		ArrayList<Byte> fileList=new ArrayList<Byte>();
		try 
		{
			System.out.println(fileUrl);
			connection=(HttpURLConnection)(new URL(fileUrl).openConnection());
			connection.connect();
			InputStream br = connection.getInputStream();
			while (true)
			{
				int c=br.read();
				if (c==-1) break;
				fileList.add(new Byte((byte)(c&0xff)));
			}
		} 
		catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		finally
		{
			if (connection!=null) connection.disconnect();
		}
		byte[] file=new byte[fileList.size()];
		int i=0;
		for (Byte b:fileList)
			file[i++]=b.byteValue();
		return file;
	}

	public byte[] downloadWorkUnit()
	{
//		String fileUrl=schedulerReply.substring(schedulerReply.indexOf("<url>",schedulerReply.indexOf("<file_info>",schedulerReply.indexOf("<file_info>")+1))+"<url>".length(),schedulerReply.indexOf("</url>",schedulerReply.indexOf("<file_info>",schedulerReply.indexOf("<file_info>")+1)));
		String fileUrl=masterurl+schedulerReply.substring(schedulerReply.indexOf("download/",schedulerReply.indexOf("<url>",schedulerReply.indexOf("<file_info>",schedulerReply.indexOf("<file_info>")+1))),schedulerReply.indexOf("</url>",schedulerReply.indexOf("<file_info>",schedulerReply.indexOf("<file_info>")+1)));
		
		HttpURLConnection connection=null;
		ArrayList<Byte> fileList=new ArrayList<Byte>();
		try 
		{
			System.out.println(fileUrl);
			connection=(HttpURLConnection)(new URL(fileUrl).openConnection());
			connection.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while (true)
			{
				int c=br.read();
				if (c==-1) break;
				fileList.add(new Byte((byte)c));
			}
		} 
		catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		finally
		{
			if (connection!=null) connection.disconnect();
		}
		byte[] file=new byte[fileList.size()];
		int i=0;
		for (Byte b:fileList)
			file[i++]=b.byteValue();
		return file;
	}

	
	public void uploadResult(byte[] output)
	{
		String outputData=new String(output);
		String request = generateDataUploadRequest();
		System.out.println("Request:\n"+request+"\n\n");
		String reply="";
		HttpURLConnection connection=null;
		try 
		{
			String fullurl="http://"+url+"/"+projectName+"_cgi/file_upload_handler";
			System.out.println(fullurl);
			connection=(HttpURLConnection)(new URL(fullurl).openConnection());
			connection.setDoOutput(true);
			connection.connect();
			connection.getOutputStream().write(request.getBytes());
			connection.getOutputStream().close();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while (true)
			{
				int c=br.read();
				if (c==-1) break;
				reply+=(char)c;
			}
		} 
		catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		finally
		{
			if (connection!=null) connection.disconnect();
		}
		
		System.out.println("Reply:\n"+reply);

		
		request = generateDataUpload(outputData);
		System.out.println("Request:\n"+request+"\n\n");
		reply="";
		connection=null;
		try 
		{
			String fullurl="http://"+url+"/"+projectName+"_cgi/file_upload_handler";
			System.out.println(fullurl);
			connection=(HttpURLConnection)(new URL(fullurl).openConnection());
			connection.setDoOutput(true);
			connection.connect();
			connection.getOutputStream().write(request.getBytes());
			connection.getOutputStream().flush();
			connection.getOutputStream().close();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while (true)
			{
				int c=br.read();
				if (c==-1) break;
				reply+=(char)c;
			}
		} 
		catch (MalformedURLException e) {
			System.out.println("MalformedURLException");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		finally
		{
			if (connection!=null) connection.disconnect();
		}
		
		System.out.println("Reply:\n"+reply);
	}
	
	private String generateDataUploadRequest()
	{
		String wuname=schedulerReply.substring(schedulerReply.indexOf("<wu_name>")+"<wu_name>".length(),schedulerReply.indexOf("</wu_name>",schedulerReply.indexOf("<wu_name>")));
		String r=	"<data_server_request>\n";
		r+=			"    <core_client_major_version>1</core_client_major_version>\n";
	    r+=			"    <core_client_minor_version>1</core_client_minor_version>\n";
	    r+=			"    <core_client_release>1</core_client_release>\n";
	    r+=			"    <get_file_size>"+wuname+"</get_file_size>\n";
	    r+=			"</data_server_request>\n";
	    return r;
	}
	
	private String generateDataUpload(String outputData)
	{
		String xmlsignature=schedulerReply.substring(schedulerReply.indexOf("<xml_signature>")+"<xml_signature>".length(),schedulerReply.indexOf("</xml_signature>",schedulerReply.indexOf("<xml_signature>")));
		String fileinfo=schedulerReply.substring(schedulerReply.indexOf("<file_info>",schedulerReply.indexOf("</workunit>"))+"<file_info>".length(),schedulerReply.indexOf("<xml_signature>"));
		String r=	"<data_server_request>\n";
		r+=			"   <core_client_major_version>1</core_client_major_version>\n";
		r+=			"	<core_client_minor_version>1</core_client_minor_version>\n";
		r+=			"   <core_client_release>1</core_client_release>\n";
		r+=			"   <file_upload>\n";
		r+=			"   <file_info>"+fileinfo+"<xml_signature>"+xmlsignature+"</xml_signature>\n";
		r+=			"   </file_info>\n";
		r+=			"   <nbytes>"+outputData.length()+"</nbytes>\n";
		r+=			"   <md5_cksum>"+"</md5_cksum>\n";
		r+=			"   <offset>0</offset>\n";
		r+=			"   <data>\n";
		r+=outputData;
		r+="</data></data_server_request>\n";
		return r;
	}
	public byte[] generate_init_data_xml()
	{
		String r="";
		r+="<app_init_data>";
/*		r+="<major_version>0</major_version>\n";
		r+="<minor_version>0</minor_version>\n";
		r+="<release>0</release>\n";
		r+="<app_version>0</app_version>\n";
		r+="<hostid>0</hostid>\n";
		r+="<app_name>"+projectName+"</app_name>\n";
		r+="<wu_name>workunit</wu_name>\n";
        r+="<slot>0</slot>\n";
        r+="<wu_cpu_time>0.0</wu_cpu_time>\n";
        r+="<starting_elapsed_time>0.0</starting_elapsed_time>\n";
        r+="<user_total_credit>0.0</user_total_credit>\n";
        r+="<user_expavg_credit>0.0</user_expavg_credit>\n";
        r+="<host_total_credit>0.0</host_total_credit>\n";
        r+="<host_expavg_credit>0.0</host_expavg_credit>\n";
        r+="<resource_share_fraction>0.0</resource_share_fraction>\n";
        r+="<checkpoint_period>0.0</checkpoint_period>\n";
        r+="<fraction_done_start>0.0</fraction_done_start>\n";
        r+="<fraction_done_end>1.0</fraction_done_end>\n";
        r+="<rsc_fpops_est>0.0</rsc_fpops_est>\n";
        r+="<rsc_fpops_bound>0.0</rsc_fpops_bound>\n";
        r+="<rsc_memory_bound>0.0</rsc_memory_bound>\n";
        r+="<rsc_disk_bound>0.0</rsc_disk_bound>\n";
        r+="<computation_deadline>0.0</computation_deadline>\n";*/
        r+="</app_init_data>";
		
		return r.getBytes();
	}
}
