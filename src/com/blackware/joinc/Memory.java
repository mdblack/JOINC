package com.blackware.joinc;

import java.util.Scanner;

public class Memory
{
	//memory is handled in units of BLOCK_SIZE
	public static final int BLOCK_SIZE=0x10000, BLOCK_SIZE_BITS=16, BLOCK_SIZE_OFFSET_MASK=0xffff;
	public static final int MEMORY_BLOCKS=0x10000;
	
	//pointers to memory blocks
	private MemoryBlock[] memoryBlock;
	
	public String saveState()
	{
		StringBuilder state=new StringBuilder();
		
		for (int i=0; i<memoryBlock.length; i++)
		{
			if (memoryBlock[i].initialized)
			{
				System.out.println("saving base block "+i);
				StringBuilder s=new StringBuilder();
				s.append(i+" "+(memoryBlock[i].writeable?1:0)+" ");
				for (int j=0; j<memoryBlock[i].rambyte.length; j++)
					s.append(memoryBlock[i].rambyte[j]+" ");
				state.append(s);
			}
		}
		return state.toString();
	}
	
	public void loadState(String state)
	{
		Scanner loader=new Scanner(state);
		while(loader.hasNextInt())
		{
			int index=loader.nextInt();
			memoryBlock[index].writeable=loader.nextInt()==1;
			memoryBlock[index].initialize();
			for (int j=0; j<memoryBlock[index].rambyte.length; j++)
			{
				memoryBlock[index].rambyte[j]=loader.nextByte();
			}
			System.out.println("loaded block "+index);
		}
	}
	
	public Memory()
	{
		//initialize memory array
		memoryBlock=new MemoryBlock[MEMORY_BLOCKS];
		for (int i=0; i<MEMORY_BLOCKS; i++)
			memoryBlock[i]=new MemoryBlock(BLOCK_SIZE);
	}

	public byte getByte(int address)
	{
		return memoryBlock[address>>>BLOCK_SIZE_BITS].getByte(address&BLOCK_SIZE_OFFSET_MASK);
	}
	public void setByte(int address, byte value)
	{
		memoryBlock[address>>>BLOCK_SIZE_BITS].setByte(address&BLOCK_SIZE_OFFSET_MASK,value);
	}

	public short getWord(int address)
	{
		return (short)((getByte(address)&0xff)|((getByte(address+1)<<8)&0xff00));
	}
	public int getDoubleWord(int address)
	{
		return (getWord(address)&0xffff)|((getWord(address+2)<<16)&0xffff0000);
        }
	public long getQuadWord(int address)
	{
		return (getDoubleWord(address)&0xffffffffl)|((((long)getDoubleWord(address+4))<<32)&0xffffffff00000000l);
	}
	public void setWord(int address, short value)
	{
		setByte(address,(byte)value);
		setByte(address+1,(byte)(value>>8));
	}
	public void setDoubleWord(int address, int value)
	{
		setByte(address,(byte)value);
		setByte(address+1,(byte)(value>>8));
		setByte(address+2,(byte)(value>>16));
		setByte(address+3,(byte)(value>>24));
	}
	public void setQuadWord(int address, long value)
	{
		setByte(address,(byte)value);
		setByte(address+1,(byte)(value>>8));
		setByte(address+2,(byte)(value>>16));
		setByte(address+3,(byte)(value>>24));
		setByte(address+4,(byte)(value>>32));
		setByte(address+5,(byte)(value>>40));
		setByte(address+6,(byte)(value>>48));
		setByte(address+7,(byte)(value>>56));
	}

	public boolean isInitialized(int address)
	{
		return memoryBlock[address>>>BLOCK_SIZE_BITS].initialized;
	}

	public void dumpMemory(int address, int quantity)
	{
		for (int i=0; i<quantity; i++)
		{
			if (i%24==0)
				System.out.println();
			System.out.printf("%x ",getByte(address+i));
		}
	}

	private class MemoryBlock
	{
		byte[] rambyte;
		boolean initialized=false;
		boolean writeable=false;

		int BLOCK_SIZE;
		
		public MemoryBlock(int BLOCK_SIZE)
		{
			this.BLOCK_SIZE=BLOCK_SIZE;
		}

		//make the block active
		public void initialize()
		{
			rambyte=new byte[BLOCK_SIZE];
			for (int i=0; i<BLOCK_SIZE; i++)
				rambyte[i]=0;
			initialized=true;
		}

		public byte getByte(int offset)
		{
			if(!initialized)
				return (byte)(-1);
//					initialize();
			return rambyte[offset];
		}
		public void setByte(int offset, byte value)
		{
//			if (!writeable) return;
			if (!initialized)
				initialize();
			rambyte[offset]=value;
		}
	}
}
