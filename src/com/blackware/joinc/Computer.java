package com.blackware.joinc;
import java.util.ArrayList;
import java.util.Hashtable;

public class Computer 
{
	public Processor processor;
	public Memory memory;
	private SystemCallHandler systemCallHandler;
	public boolean debugMode=false, printregMode=false;
	public long icount=0;
	private int entryPoint;
	private Hashtable<Integer,String> symbolTable,dllFunctionTable;
	private int heapStart=0xc000000;
	int heapPtr=heapStart;
	
//	private byte[] inputFile;
//	public String outputFile="";
//	private int inputFilePointer;
	boolean running;
	public boolean dllPresent=false,symbolPresent=false;
	
	//stores all boinc files
	public Hashtable<String,FileObject> fileTable;
	//store open file descriptors
	public ArrayList<String> openFiles;
	
	public Computer()
	{
		memory=new Memory();
		processor=new Processor(this);
		systemCallHandler=new SystemCallHandler(this);
		fileTable=new Hashtable<String,FileObject>();
		openFiles=new ArrayList<String>();
		
		fileTable.put("/dev/tty", new FileObject(new byte[0]));
		fileTable.put("/dev/urandom", new FileObject(new byte[0]));
		fileTable.put("/proc/sys/kernel/osrelease", new FileObject("2.6.32-32-generic"));
		
		fileTable.put("in", new FileObject("Hello Michael"));
		}
	
	public void execute()
	{
		running=true;
		int ic=0;
		while(running)
		{
			processor.executeAnInstruction();

			if (printregMode)
			{
			System.out.println(Integer.toHexString(processor.eip.getValue())+" "+processor.processorGUICode.constructName());
//			System.out.println(Integer.toHexString(processor.eax.getValue())+" "+Integer.toHexString(processor.edx.getValue()));
			processor.printRegisters();
			new java.util.Scanner(System.in).next();
			}
			//check for jump to dll (ip==0)
			if (processor.eip.getValue()==0)
				libcCall();
			
			//check for known symbols
			if (symbolTable.containsKey(new Integer(processor.eip.getValue())))
				boincCall();

		}
	}
	
	//arrive here if we landed at an address in the symbol table
	private void boincCall()
	{
		String symbol=symbolTable.get(new Integer(processor.eip.getValue()));
		if (symbol.equals("")) return;
		System.out.println("--- Arrived at local function call "+symbol+"---");

		
		if (!this.dllPresent)
		{
//			if (symbol.equals("__libc_start_main"))
//			systemCallHandler.doLibFunction(symbol);
			if (symbol.equals("toupper"))
			{
				System.out.println("toupper bypassed");
//				systemCallHandler.doLibFunction("toupper");
//				System.out.println(processor.eax.getValue());
//				this.printregMode=true;
			}
		}
		
//		if (symbol.equals("main"))
//		new java.util.Scanner(System.in).next();
	}
	
	//arrive here if interrupt 80 happened
	public void handleInterrupt80()
	{
		System.out.println("Interrupt 80: EAX="+processor.eax.getValue());
		systemCallHandler.handleInterrupt80();
//		new java.util.Scanner(System.in).next();
	}
	
	//arrive here if we jump to 0
	private void libcCall()
	{
		//remove four bytes of 0s
		processor.esp.setValue(processor.esp.getValue()+4);
		//next four bytes is the function
		int callNumber=processor.ss.loadDoubleWord(processor.esp.getValue());
		processor.esp.setValue(processor.esp.getValue()+4);
		String functionName=dllFunctionTable.get(new Integer(callNumber));
		
		System.out.println("--- Arrived at libc call "+functionName+"---");
		
		systemCallHandler.doLibFunction(functionName);
//		new java.util.Scanner(System.in).next();
	}

	public static class FileObject
	{
		byte[] file;
		int filePointer;
		public FileObject(byte[] file)
		{
			this.file=file;
			filePointer=0;
		}
		public FileObject(String contents)
		{
			file=contents.getBytes();
			filePointer=0;
		}
		public boolean eof()
		{
			return (filePointer>=file.length);
		}
		public int getc()
		{
			if (eof())
				return -1;
			return file[filePointer++];
		}
		public void ungetc(byte b)
		{
			file[--filePointer]=b;
		}
		public void putc(byte b)
		{
			byte[] newfile=new byte[file.length+1];
			for (int i=0; i<file.length; i++)
				newfile[i]=file[i];
			newfile[file.length]=b;
			file=newfile;
		}
	}
	
	public void loadWorkUnit(byte[] workunit)
	{
		fileTable.put("in", new FileObject(workunit));
	}
	public void loadInitData(byte[] initdata)
	{
		fileTable.put("init_data.xml", new FileObject(initdata));
	}
	public byte[] getOutputFile()
	{
		return fileTable.get("out").file;
	}
	

	public void loadElf(byte[] elffile)
	{
		byte[] dynsym=null,dynstr=null,relplt=null;
		
		System.out.println();
		if (elffile[4]!=1)
		{
			System.out.println("It's not a 32-bit i386 executable");
			return;
		}
		
		entryPoint=extractField("entry",0,elffile,ELFHEADER_NAMES,ELFHEADER_SIZES);
		System.out.println("Entry point: "+Integer.toHexString(entryPoint));
		int sections=extractField("shnum",0,elffile,ELFHEADER_NAMES,ELFHEADER_SIZES);
		System.out.println("Number of sections: "+sections);
		
		int shentsize=extractField("shentsize",0,elffile,ELFHEADER_NAMES,ELFHEADER_SIZES);
		int shoff=extractField("shoff",0,elffile,ELFHEADER_NAMES,ELFHEADER_SIZES);
		int shstrndx=extractField("shstrndx",0,elffile,ELFHEADER_NAMES,ELFHEADER_SIZES);
		System.out.println("shstrndx: "+shstrndx);
		int place=shoff;
		
		ArrayList<Integer> symbolAList=new ArrayList<Integer>();
		ArrayList<Integer> symbolNList=new ArrayList<Integer>();		
		int symbolTableNameOffset=0;
		
		//get the shstrndx first
		int shstrndx_offset=extractField("offset",place+shstrndx*shentsize,elffile,SECTIONHEADER_NAMES,SECTIONHEADER_SIZES);
		System.out.println("shstrndx_offset: "+Integer.toHexString(shstrndx_offset));
		
		for (int sec=0; sec<sections; sec++)
		{
			int nameptr=shstrndx_offset+extractField("name",place,elffile,SECTIONHEADER_NAMES,SECTIONHEADER_SIZES);
			
			String name="";
			for (int i=nameptr; elffile[i]!=0; i++)
				name+=(char)elffile[i];

			int addr=extractField("addr",place,elffile,SECTIONHEADER_NAMES,SECTIONHEADER_SIZES);
			int size=extractField("size",place,elffile,SECTIONHEADER_NAMES,SECTIONHEADER_SIZES);
			int offset=extractField("offset",place,elffile,SECTIONHEADER_NAMES,SECTIONHEADER_SIZES);
			int type=extractField("type",place,elffile,SECTIONHEADER_NAMES,SECTIONHEADER_SIZES);
			if (addr!=0 && size!=0)
			{
				System.out.println("Found segment "+name+" of type "+Integer.toHexString(type)+" at offset "+Integer.toHexString(place));
				System.out.println("The segment is "+Integer.toHexString(size)+" big and is located in the file at "+Integer.toHexString(offset));
				System.out.println("It will be placed at address "+Integer.toHexString(addr));
				System.out.println();
				
				byte[] capture=new byte[size];
				
				if (type!=8)
				{
					for (int b=offset; b<size+offset; b++)
					{
						memory.setByte(addr+b-offset, elffile[b]);
						capture[b-offset]=elffile[b];
					}
				}
				//bss gets set to 0
				else
				{
					for (int b=offset; b<size+offset; b++)
					{
						memory.setByte(addr+b-offset, (byte)0);
						capture[b-offset]=0;
					}					
				}
				
				if (name.equals(".dynsym"))
					dynsym=capture;
				if (name.equals(".dynstr"))
					dynstr=capture;
				if (name.equals(".rel.plt"))
					relplt=capture;
			}
			else if (type==2)
			{
				System.out.println("Found symbol table addresses at offset "+Integer.toHexString(place));
				System.out.println("The segment is "+Integer.toHexString(size)+" big and is located in the file at "+Integer.toHexString(offset));
				
				for (int b=offset; b<size+offset; b+=16)
				{
					int a=extractField("value",b,elffile,SYMBOL_NAMES,SYMBOL_SIZES);
					int n=extractField("name",b,elffile,SYMBOL_NAMES,SYMBOL_SIZES);
					symbolAList.add(new Integer(a));
					symbolNList.add(new Integer(n));		
				}
			}
			else if (type==3)
			{
				System.out.println("Found symbol table names at offset "+Integer.toHexString(place));
				System.out.println("The segment is "+Integer.toHexString(size)+" big and is located in the file at "+Integer.toHexString(offset));
				
				symbolTableNameOffset=offset;
			}
			place+=shentsize;
		}
		
		dllFunctionTable=new Hashtable<Integer,String>();

		if (dynsym!=null && dynstr!=null && relplt!=null)
		{
			int base=(relplt[0]&0xff)+(relplt[1]&0xff)*256+(relplt[2]&0xff)*256*256+(relplt[3]&0xff)*256*256*256;
			for (int i=0; i<relplt.length; i+=8)
			{
				int index=(relplt[i]&0xff)+(relplt[i+1]&0xff)*256+(relplt[i+2]&0xff)*256*256+(relplt[i+3]&0xff)*256*256*256;
				int dynsymindex=(relplt[i+5]&0xff)+(relplt[i+6]&0xff)*256;
				int dynstrindex=(dynsym[dynsymindex*16]&0xff)+(dynsym[dynsymindex*16+1]&0xff)*256;
				if (dynstrindex<0) continue;
				String name="";
				for (int j=0; dynstr[dynstrindex+j]!=0; j++)
					name+=(char)dynstr[dynstrindex+j];
				dllFunctionTable.put(new Integer((index-base)*2),name);
	//			System.out.println(Integer.toHexString((index-base)*2)+" "+name);
			}
			System.out.println("DLL Function Table generated: "+dllFunctionTable.size()+" entries");
			dllPresent=true;
		}
		else
		{
			System.out.println("No DLL Function Table generated");
			dllPresent=false;
		}
		symbolTable=new Hashtable<Integer,String>();
		if (symbolTableNameOffset!=0)
		{
			for (int i=0; i<symbolAList.size(); i++)
			{
				int a=symbolAList.get(i).intValue();
				int l=symbolNList.get(i).intValue();
				String n="";
				for(;elffile[symbolTableNameOffset+l]!=0;l++)
					n+=(char)elffile[symbolTableNameOffset+l];
				symbolTable.put(new Integer(a),n);
			}
			if (symbolNList.size()>0)
				symbolPresent=true;
		}
		System.out.println("Symbol table generated: "+symbolTable.size()+" entries");
		
		processor.eip.setValue(entryPoint);
		processor.esp.setValue(0xffffd4d0);
		processor.ss.storeDoubleWord(processor.esp.getValue(), 0);	//argc=0
		processor.ss.storeDoubleWord(processor.esp.getValue()+4, 0);	//argv=0
		processor.ss.storeDoubleWord(processor.esp.getValue()+8, 0);	//env=0
	}

	private static final String[] ELFHEADER_NAMES=new String[]{"id","type","machine","version","entry","phoff","shoff","flags","ehsize","phentsize","phnum","shentsize","shnum","shstrndx"};
	private static final int[] ELFHEADER_SIZES=new int[]{16,2,2,4,4,4,4,4,2,2,2,2,2,2};
	private static final String[] SECTIONHEADER_NAMES=new String[]{"name","type","flags","addr","offset","size","link","info","addalign","entsize"};
	private static final int[] SECTIONHEADER_SIZES=new int[]{4,4,4,4,4,4,4,4,4,4};
	private static final String[] SYMBOL_NAMES=new String[]{"name","value","size","info","other","shndx"};
	private static final int[] SYMBOL_SIZES=new int[]{4,4,4,1,1,2};

	private int extractField(String name, int offset, byte[] elffile, String[] names, int[] sizes)
	{
		int off=0,i;
		for (i=0; i<names.length; i++)
		{
			if (names[i].equals(name))
				break;
			off+=sizes[i];
		}
		int retval=0;
		for (int j=0; j<sizes[i]; j++)
			retval+=(0xff&(int)elffile[j+off+offset])*(int)Math.pow(256,j);
		return retval;
	}
}
