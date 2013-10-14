package com.blackware.joinc;


public class SystemCallHandler 
{
	private Computer computer;
	private Processor processor;
	private Memory memory;
	public SystemCallHandler(Computer computer)
	{
		this.computer=computer;
		processor=computer.processor;
		memory=computer.memory;
	}
	public void handleInterrupt80()
	{
		switch(processor.eax.getValue())
		{
		case 1:
			System.out.println("sys_exit");
			sys_exit(processor.ebx.getValue());
			break;
		case 3: case 145:
			System.out.println("sys_read");
			sys_read(processor.ebx.getValue(),processor.ecx.getValue(),processor.edx.getValue());
			break;
		case 4: case 146:
			System.out.println("sys_write");
			sys_write(processor.ebx.getValue(),processor.ecx.getValue(),processor.edx.getValue());
			break;			
		case 5:
			System.out.println("sys_open");
			sys_open(processor.ebx.getValue(),processor.ecx.getValue());
//			new java.util.Scanner(System.in).next();
			break;
		case 6:
			System.out.println("sys_close");
			processor.eax.setValue(0);
			break;
		case 0x7a:
			System.out.println("sys_newuname");
			break;
		case 0x2d:
			System.out.println("sys_brk");
			processor.eax.setValue(0);
			break;
		case 243:
			System.out.println("sys_set_thread_area");
			System.out.println("ebx="+Integer.toHexString(processor.ebx.getValue()));
			System.out.println("*ebx="+Integer.toHexString(processor.ds.loadDoubleWord(processor.ebx.getValue())));
			System.out.println("*ebx+4="+Integer.toHexString(processor.ds.loadDoubleWord(processor.ebx.getValue()+4)));
			System.out.println("*ebx+8="+Integer.toHexString(processor.ds.loadDoubleWord(processor.ebx.getValue()+8)));
			System.out.println("*ebx+12="+Integer.toHexString(processor.ds.loadDoubleWord(processor.ebx.getValue()+12)));
			System.out.println("*ebx+16="+Integer.toHexString(processor.ds.loadDoubleWord(processor.ebx.getValue()+16)));
			System.out.println("*ebx+20="+Integer.toHexString(processor.ds.loadDoubleWord(processor.ebx.getValue()+20)));
			System.out.println("*ebx+24="+Integer.toHexString(processor.ds.loadDoubleWord(processor.ebx.getValue()+24)));
			System.out.println("*ebx+28="+Integer.toHexString(processor.ds.loadDoubleWord(processor.ebx.getValue()+28)));
			System.out.println("*ebx+32="+Integer.toHexString(processor.ds.loadDoubleWord(processor.ebx.getValue()+32)));
			
			//set the gdt entry to 0
			int user_desc=processor.ebx.getValue();
			processor.ds.storeDoubleWord(user_desc, 0);
			int base=processor.ds.loadDoubleWord(user_desc+4);
//base=0x80d0830;
			int limit=processor.ds.loadDoubleWord(user_desc+8);
			
			//make a gdt entry
			long descriptor=0;
			descriptor|=limit&0xffffl;
			descriptor|=(base&0xffffffl)<<16;
			descriptor|=(limit&0xf0000l)<<48;
			descriptor|=(base&0xffffffl)<<16;
			descriptor|=(base&0xff000000l)<<56;
			descriptor|=(1l<<54);	//32 bit size
			descriptor|=(1l<<47);	//is present
			descriptor|=(3l<<45);	//lowest privilege level
			descriptor|=(12<<40);	//other access settings: r/w data segment
			base=(int)((0xffffffl & (descriptor>>16))|((descriptor>>32)&0xffffffffff000000l));
			processor.gdtr.storeQuadWord(0, descriptor);
			
			System.out.println("Base="+Integer.toHexString(base)+" limit="+Integer.toHexString(limit)+" descriptor="+Long.toHexString(descriptor));
			
			processor.eax.setValue(0);
			break;
		case 201:
			System.out.println("sys_geteuid");
			processor.eax.setValue(0);
			break;
		case 199:
			System.out.println("sys_getuid");
			processor.eax.setValue(0);
			break;
		case 202:
			System.out.println("sys_getegid");
			processor.eax.setValue(0);
			break;
		case 200:
			System.out.println("sys_getgid");
			processor.eax.setValue(0);
			break;
		case 192:
			System.out.println("sys_mmap_pgoff");
			if (processor.ebp.getValue()==0)
				processor.eax.setValue(computer.heapPtr);
			computer.heapPtr+=processor.ecx.getValue();
			System.out.println(processor.eax.getValue());
//			System.exit(0);
//			processor.eax.setValue(0);
			break;
		case 240:
			System.out.println("sys_futex");
//			processor.eax.setValue(0);
			break;
		case 252:
			System.out.println("sys_exit_group");
			processor.eax.setValue(0);
			break;
		case 221:
			System.out.println("sys_fcntl64");
			processor.eax.setValue(0);
			break;
		case 117:
			System.out.println("sys_ipc");
			processor.eax.setValue(0);
			break;
		case 120:
			System.out.println("sys_clone");
			processor.eax.setValue(1);
			break;
		case 125:
			System.out.println("sys_mprotect");
			processor.eax.setValue(0);
			break;
		case 104:
			System.out.println("sys_setitimer");
			processor.eax.setValue(0);
			break;
		case 118:
			System.out.println("sys_fsync");
			processor.eax.setValue(0);
			break;
		case 13:
			System.out.println("sys_time");
			processor.eax.setValue(0);
			break;
		case 140:
			System.out.println("sys_llseek");
			//TODO
			processor.eax.setValue(0);
			break;
		case 195:
			System.out.println("sys_stat64");
			sys_stat();
			break;
		case 197:
			System.out.println("sys_fstat");
			sys_stat();
			break;
		case 196:
			System.out.println("sys_lstat64");
			sys_stat();
			break;
		case 258:
			System.out.println("sys_set_tid_address");
			processor.eax.setValue(0);
			break;
		case 311:
			System.out.println("sys_set_robust_list");
			processor.eax.setValue(0);
			break;
		case 174:
			System.out.println("sys_rt_sigaction");
			processor.eax.setValue(0);
			break;
		case 175:
			System.out.println("sys_rt_sigprocmask");
			processor.eax.setValue(0);
			break;
		case 191:
			System.out.println("sys_getrlimit");
			processor.eax.setValue(0);
			break;
		case 78:
			System.out.println("sys_gettimeofday");
			break;
		case 20:
			System.out.println("sys_getpid");
			processor.eax.setValue(0);
			break;
		case 97:
			System.out.println("sys_setpriority");
			processor.eax.setValue(0);
			break;
		case 91:
			System.out.println("sys_munmap");
			processor.eax.setValue(0);
			break;
		case 10:
			System.out.println("sys_unlink");
			//TODO: delete file
			processor.eax.setValue(0);
			break;
		default:
			System.out.println("Not handled");
			new java.util.Scanner(System.in).next();
			break;
		}
	}

	private void sys_stat()
	{
			int strindex1=processor.ebx.getValue();
			String str1="";
			for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
				str1+=(char)processor.ds.loadByte(strindex1+i);
			System.out.println(str1);
			if (computer.fileTable.containsKey(str1))
				processor.eax.setValue(0);
			else
				processor.eax.setValue(-1);
	}
	
	private void sys_exit(int code)
	{
		System.out.println("exit");
		System.out.println("exit code: "+code);
		computer.running=false;		
	}
	
	private void sys_read(int fileId, int bufferAddress, int count)
	{
		System.out.println(fileId+" "+bufferAddress+" "+count);
		if (fileId<0 || fileId>=computer.openFiles.size()+1)
		{
			System.out.println("Not a valid file handler");
			processor.eax.setValue(0);
			return;
		}
		String name=computer.openFiles.get(fileId-1);
		int i;
		for (i=0; i<count; i++)
		{
			int b=computer.fileTable.get(name).getc();
			if (name.equals("/dev/urandom"))
				b=0;
			if (b==-1)
				break;
			System.out.println((char)b);
			processor.ds.storeByte(bufferAddress+i, (byte)b);
		}
		processor.ds.storeByte(bufferAddress+i,(byte)0);
		if (i<count)
			processor.eax.setValue(i);
		else
			processor.eax.setValue(0);
	}
	private void sys_write(int fileId, int bufferAddress, int count)
	{
		System.out.println(fileId+" "+bufferAddress+" "+count);
		if (fileId<0 || fileId>=computer.openFiles.size()+1)
		{
			System.out.println("Not a valid file handler");
			processor.eax.setValue(0);
			return;
		}
		String name=computer.openFiles.get(fileId-1);
		System.out.println("filename: "+name);
		for (int i=0; i<count; i++)
		{
			byte b=processor.ds.loadByte(bufferAddress+i);
			System.out.println((char)b+" "+Integer.toHexString(b));
			computer.fileTable.get(name).putc(b);
		}
		processor.eax.setValue(count);
		new java.util.Scanner(System.in).next();
	}
	
	int O_CREAT=00100;
	int O_TRUNC=01000;
	int O_APPEND=02000;
	
	private void sys_open(int strindex1, int flags)
	{
		String str1="";
		//extract them from memory
		for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
			str1+=(char)processor.ds.loadByte(strindex1+i);
		System.out.println(" "+str1+" "+flags);
		
		//does it exist?
		if (computer.fileTable.containsKey(str1))
		{
			//make a id for it
			computer.openFiles.add(str1);
			processor.eax.setValue(computer.openFiles.size());
			computer.fileTable.get(str1).filePointer=0;
			System.out.println("file opened with id "+computer.openFiles.size());
			if ((flags&O_TRUNC)!=0)
				computer.fileTable.put(str1,new Computer.FileObject(new byte[0]));
			if ((flags&O_APPEND)!=0)
				computer.fileTable.get(str1).filePointer=computer.fileTable.get(str1).file.length;
		}
		else if ((flags&O_CREAT)==0)
		{
			System.out.println("error: file doesn't exist");
			processor.eax.setValue(-1);
		}			
		else
		{
			//make a file entry
			computer.fileTable.put(str1,new Computer.FileObject(new byte[0]));
			computer.openFiles.add(str1);
			processor.eax.setValue(computer.openFiles.size());
			System.out.println("new file created: "+computer.openFiles.size());
		}
	}
	
	public void doLibFunction(String functionName)
	{
		if (functionName==null)
		{
			System.out.println("Unhandled jump to 0");
			new java.util.Scanner(System.in).next();
		}
		else if (functionName.equals("__libc_start_main"))
		{
			System.out.println("libc_start_main");
			//remove the return address.  main is the next on the stack.
			processor.esp.setValue(processor.esp.getValue()+4);
			//set argc and argv to 0.  we'll have no command line arguments.
			processor.ss.storeDoubleWord(processor.esp.getValue()+8, 0);
			processor.ss.storeDoubleWord(processor.esp.getValue()+12, 0);
		}
		else if (functionName.equals("exit"))
		{
			sys_exit(processor.ss.loadDoubleWord(processor.esp.getValue()+4));
		}
		else if (functionName.equals("toupper"))
		{
			System.out.println("toupper");
			byte b=processor.ss.loadByte(processor.esp.getValue()+4);
			if (b>='a' && b<='z')
				b=(byte)(b+'A'-'a');
			processor.eax.setValue(b&0xff);			
		}
		else if (functionName.equals("fgetc"))
		{
			System.out.println("fgetc");
			//get the id
			int id=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			//get the name
			String name=computer.openFiles.get(id-1);
			int b=computer.fileTable.get(name).getc();
			processor.eax.setValue(b);
			System.out.println(id+" "+name+" "+(char)b);
		}
		else if (functionName.equals("fgets"))
		{
			System.out.println("fgets");
			//get the buffer location
			int buffer=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			//get the max size
			int maxsize=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			//get the id
			int id=processor.ss.loadDoubleWord(processor.esp.getValue()+12);
			//get the name
			String name=computer.openFiles.get(id-1);
			System.out.print(Integer.toHexString(buffer)+" "+maxsize+" "+name+": ");
			//start copying
			byte b;
			int i=0;
			do
			{
				if (computer.fileTable.get(name).eof())
					break;
				b=(byte)computer.fileTable.get(name).getc();
				processor.ds.storeByte(buffer+i, b);
				i++;
				System.out.print((char)b);
			} while(b!=(byte)'\n' && i!=maxsize-1);
			processor.ds.storeByte(buffer+i, (byte)0);
			System.out.println();
			processor.eax.setValue(buffer);
		}
		else if (functionName.equals("ungetc"))
		{
			System.out.println("ungetc");
			//get the id
			int id=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			byte c=(byte)processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			//get the name
			String name=computer.openFiles.get(id-1);
			computer.fileTable.get(name).ungetc(c);
			processor.eax.setValue(c);
		}
		else if (functionName.equals("isspace"))
		{
			System.out.println("isspace");
			char c=(char)processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			if (c==' '||c=='\n'||c=='\t'||c=='\f')
				processor.eax.setValue(1);
			else
				processor.eax.setValue(0);
		}
		else if (functionName.equals("strlen"))
		{
			System.out.println("strlen");
			int strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			String str1="";
			for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
				str1+=(char)processor.ds.loadByte(strindex1+i);
			System.out.println(str1);
			processor.eax.setValue(str1.length());
		}
		else if (functionName.equals("strcpy")||functionName.equals("__strcpy_chk"))
		{
			System.out.println("strcpy");
			//get two strings:
			int dest=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			int src=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			System.out.println(Integer.toHexString(dest)+" "+Integer.toHexString(src));
			for (int i=0; processor.ds.loadByte(src+i)!=0; i++)
				processor.ds.storeByte(dest+i, processor.ds.loadByte(src+i));
			processor.eax.setValue(dest);
		}
		else if (functionName.equals("memcpy"))
		{
			System.out.println("memcpy");
			//get two strings:
			int dest=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			int src=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			int quantity=processor.ss.loadDoubleWord(processor.esp.getValue()+12);
			System.out.println(Integer.toHexString(dest)+" "+Integer.toHexString(src));
			for (int i=0; i<quantity; i++)
				processor.ds.storeByte(dest+i, processor.ds.loadByte(src+i));
			processor.eax.setValue(dest);
		}
		else if (functionName.equals("strstr"))
		{
			System.out.println("strstr");
			//get two strings:
			int strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			int strindex2=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			System.out.println(Integer.toHexString(strindex1)+" "+Integer.toHexString(strindex2));
			//extract them from memory
			String haystack="",needle="";
			for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
				haystack+=(char)processor.ds.loadByte(strindex1+i);
			for (int i=0; processor.ds.loadByte(strindex2+i)!=0; i++)
				needle+=(char)processor.ds.loadByte(strindex2+i);
			if (!haystack.contains(needle))
				processor.eax.setValue(0);
			else
				processor.eax.setValue(haystack.indexOf(needle)+strindex1);
			System.out.println(haystack+" "+needle+" "+processor.eax.getValue());
		}
		else if (functionName.equals("getchar"))
		{
/*			System.out.println("getchar");
			System.out.println("Enter a char: ");
			char c = 		new java.util.Scanner(System.in).next().charAt(0);
			if (c=='\\')
				c='\n';
			processor.eax.setValue(c);
*/
		}
		else if (functionName.equals("printf"))
		{
			System.out.println("printf");
			//get two strings:
			int strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			System.out.println(Integer.toHexString(strindex1));
			//extract them from memory
			String str1="",str2="";
			for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
				str1+=(char)processor.ds.loadByte(strindex1+i);

			System.out.println(str1);
	//		new java.util.Scanner(System.in).next();
		}
		else if (functionName.equals("gets"))
		{
			System.out.println("gets");
			//get a buffer location
			int strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			System.out.println(Integer.toHexString(strindex1));
			
/*			System.out.println("Type your string here: ");
			String str=new java.util.Scanner(System.in).next();
			int i;
			for (i=0; i<str.length(); i++)
				processor.ds.storeByte(strindex1+i, (byte)str.charAt(i));
			processor.ds.storeByte(strindex1+i,(byte)0);
			processor.ds.storeByte(strindex1+i+1,(byte)0);
	*/		
		}
		else if (functionName.equals("puts"))
		{
			System.out.println("puts");
			//get two strings:
			int strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			System.out.println(Integer.toHexString(strindex1));
			//extract them from memory
			String str1="",str2="";
			for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
				str1+=(char)processor.ds.loadByte(strindex1+i);

			System.out.println(str1);
	//		new java.util.Scanner(System.in).next();
		}
		else if (functionName.equals("strcmp"))
		{
			System.out.println("strcmp");
			//get two strings:
			int strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			int strindex2=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			System.out.println(Integer.toHexString(strindex1)+" "+Integer.toHexString(strindex2));
			//extract them from memory
			String str1="",str2="";
			for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
				str1+=(char)processor.ds.loadByte(strindex1+i);
			for (int i=0; processor.ds.loadByte(strindex2+i)!=0; i++)
				str2+=(char)processor.ds.loadByte(strindex2+i);
			int result=str1.compareTo(str2);
			System.out.println(" "+str1+" "+str2+" "+result);
			processor.eax.setValue(result);
//			String str=new java.util.Scanner(System.in).next();
		}
		else if (functionName.equals("fwrite"))
		{
			System.out.println("fscanf");
			int ptr=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			int size=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			int nmemb=processor.ss.loadDoubleWord(processor.esp.getValue()+12);

			//get the id
			int id=processor.ss.loadDoubleWord(processor.esp.getValue()+16);
			//get the name
			String name=computer.openFiles.get(id-1);	
			
			System.out.println(ptr+" "+size+" "+nmemb+" "+name);
			
			for (int i=0; i<nmemb*size; i++)
				computer.fileTable.get(name).putc(processor.ds.loadByte(ptr+i));
			processor.eax.setValue(nmemb);
			
//			new java.util.Scanner(System.in).next();
		}
		else if (functionName.equals("fflush"))
		{
			System.out.println("fflush");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("fsync"))
		{
			System.out.println("fsync");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("fscanf"))
		{
			System.out.println("fscanf");
			//get the id
			int id=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			//get the name
			String name=computer.openFiles.get(id-1);
			int strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			String str1="";
			for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
				str1+=(char)processor.ds.loadByte(strindex1+i);
			System.out.println(" "+name+" "+str1);
//			processor.eax.setValue(result);
//			new java.util.Scanner(System.in).next();
		}
		else if (functionName.equals("__stack_chk_fail"))
		{
			System.out.println("__stack_chk_fail");
		}		
		else if (functionName.equals("_Unwind_Resume"))
		{
			System.out.println("_Unwind_Resume");
		}
		else if (functionName.equals("shmget"))
		{
			System.out.println("shmget");
			processor.eax.setValue(-1);
		}
		else if (functionName.equals("perror"))
		{
			System.out.println("perror");
			int strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			String str1="";
			for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
				str1+=(char)processor.ds.loadByte(strindex1+i);
			System.out.println(str1);
		}
		else if (functionName.equals("rand"))
		{
			System.out.println("rand");
			processor.eax.setValue((int)(Math.random()*32767));
		}
		else if (functionName.equals("time"))
		{
			System.out.println("time");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("gettimeofday"))
		{
			System.out.println("gettimeofday");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("sleep"))
		{
			System.out.println("sleep");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("__errno_location"))
		{
			System.out.println("__errno_location");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("localtime"))
		{
			System.out.println("localtime");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("strftime"))
		{
			System.out.println("time");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("getpid"))
		{
			System.out.println("getpid");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("fprintf"))
		{
			//TODO
			System.out.println("fprintf");
		}		
		else if (functionName.equals("__sprintf_chk"))
		{
			System.out.println("__sprintf_chk");
		}
		else if (functionName.equals("__vfprintf_chk"))
		{
			System.out.println("__vfprintf_chk");
		}
		else if (functionName.equals("__snprintf_chk"))
		{
			System.out.println("__snprintf_chk");
		}
		else if (functionName.equals("freopen64"))
		{
			System.out.println("freopen64");
		}
		else if (functionName.equals("setbuf"))
		{
			System.out.println("setbuf");
		}
		else if (functionName.equals("__fprintf_chk"))
		{
			System.out.println("__fprintf_chk");
		}
		else if (functionName.equals("pthread_attr_init"))
		{
			System.out.println("pthread_attr_init");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("pthread_attr_setstacksize"))
		{
			System.out.println("pthread_attr_setstacksize");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("pthread_create"))
		{
			System.out.println("pthread_create");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("sigemptyset"))
		{
			System.out.println("sigemptyset");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("sigaction"))
		{
			System.out.println("sigaction");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("setitimer"))
		{
			System.out.println("setitimer");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("malloc"))
		{
			System.out.println("malloc");
			processor.eax.setValue(computer.heapPtr);
			computer.heapPtr+=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			System.out.println(Integer.toHexString(computer.heapPtr));
		}
		else if (functionName.equals("calloc"))
		{
			System.out.println("malloc");
			processor.eax.setValue(computer.heapPtr);
			computer.heapPtr+=processor.ss.loadDoubleWord((processor.esp.getValue()+4)*(processor.esp.getValue()+8));
			System.out.println(Integer.toHexString(computer.heapPtr));
		}
		else if (functionName.equals("realloc"))
		{
			System.out.println("realloc");
			int oldbuffer=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			int newbuffer=computer.heapPtr;
			int size=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			processor.eax.setValue(newbuffer);
			computer.heapPtr+=size;
			if (oldbuffer!=0)
			{
				for (int i=0; i<size; i++)
					processor.ds.storeByte(newbuffer+i,processor.ds.loadByte(oldbuffer+i));
			}
			System.out.println(Integer.toHexString(computer.heapPtr));
		}
		else if (functionName.equals("fileno"))
		{
			//reset file pointer
			System.out.println("fileno");
			//get the id
			int id=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			//get the name
			String name=computer.openFiles.get(id-1);
			System.out.println(id+" "+name);
			computer.fileTable.get(name).filePointer=0;
			processor.eax.setValue(id);
		}
		else if (functionName.equals("fopen64") || functionName.equals("fopen")|| functionName.equals("open64"))
		{
			int strindex1;
			String str1="";
			int val=0;
			if (functionName.equals("fopen")||functionName.equals("fopen64"))
			{
				String str2="";
				System.out.println("fopen64");
				//extract params
				strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
				int strindex2=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
				//extract them from memory
				for (int i=0; processor.ds.loadByte(strindex2+i)!=0; i++)
					str2+=(char)processor.ds.loadByte(strindex2+i);
				System.out.println(" "+str1+" "+str2);
				if (str2.charAt(0)=='r')
					val=0;
				else if (str2.charAt(0)=='w')
				{
					val+=O_CREAT;
					val+=O_TRUNC;
				}
				else if (str2.charAt(0)=='a')
				{
					val+=O_CREAT;
					val+=O_APPEND;
				}
				else
				{
					System.out.println("error: operator isn't r or w");
					processor.eax.setValue(0);
				}
			}
			else
			{
				System.out.println("open64");
				//extract params
				strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
				val=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
				//extract them from memory
				for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
					str1+=(char)processor.ds.loadByte(strindex1+i);
				System.out.println(" "+str1+" "+val);
			}
			sys_open(strindex1,val);
		}
		else if (functionName.equals("fcntl"))
		{
			//TODO
			System.out.println("fcntl");
			//ignore and return 0 for success
			processor.eax.setValue(0);
		}
		else if (functionName.equals("fclose"))
		{
			System.out.println("fclose");
			//ignore and return 0 for success
			processor.eax.setValue(0);
		}
		else if (functionName.equals("free"))
		{
			//ignore frees, let the garbage pile up
			System.out.println("free");
		}
		else if (functionName.equals("memset"))
		{
			System.out.println("memset");
			//get params: pointer, int, int
			int ptr=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			int c=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			int size=processor.ss.loadDoubleWord(processor.esp.getValue()+12);
			System.out.println(Integer.toHexString(ptr)+" "+c+" "+size);
			
			//fill memory with a constant byte
			for (int i=0; i<size; i++)
				processor.ds.storeByte(ptr+i, (byte)c);
		}
		else if (functionName.equals("__fxstat64"))
		{
			System.out.println("fxstat64");
			processor.eax.setValue(-1);
		}
		else if (functionName.equals("close"))
		{
			System.out.println("close");
			processor.eax.setValue(0);
		}
		else if (functionName.equals("__xstat64")||functionName.equals("__lxstat64")||functionName.equals("stat"))
		{
			System.out.println("stat");
			int strindex1=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			String str1="";
			for (int i=0; processor.ds.loadByte(strindex1+i)!=0; i++)
				str1+=(char)processor.ds.loadByte(strindex1+i);
			System.out.println(str1);
			if (computer.fileTable.containsKey(str1))
				processor.eax.setValue(0);
			else
				processor.eax.setValue(-1);
			
			//TODO
			//sure it exists!
//			processor.eax.setValue(0);
		}
		else if (functionName.equals("strchr"))
		{
			System.out.println("strchr");
			int ptr=processor.ss.loadDoubleWord(processor.esp.getValue()+4);
			int c=processor.ss.loadDoubleWord(processor.esp.getValue()+8);
			System.out.print(Integer.toHexString(ptr)+" "+c+" ");
			//find first c in *ptr
			int i;
			for (i=ptr; processor.ds.loadByte(i)!=0 && processor.ds.loadByte(i)!=(byte)c; i++);
			if (processor.ds.loadByte(i)==(byte)c)
				processor.eax.setValue(i);
			else
				processor.eax.setValue(0);
			System.out.println(processor.eax.getValue());
		}
		else
		{
			System.out.println("Unhandled library call "+functionName);
//			new java.util.Scanner(System.in).next();
		}
		
		//return
		processor.eip.setValue(processor.ss.loadDoubleWord(processor.esp.getValue()));
		processor.esp.setValue(processor.esp.getValue()+4);
	}
}
