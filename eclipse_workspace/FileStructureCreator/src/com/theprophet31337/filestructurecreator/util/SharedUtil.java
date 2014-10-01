package com.theprophet31337.filestructurecreator.util;

import java.util.*;

public class SharedUtil
{
	public static interface VariableForDelegate
	{
		public void doAction(int[] counters);
	}
	
	public static int[] toPrimitiveArray(List<Integer> integerList)
	{
		int[] array=new int[integerList.size()];
		
		for(int i=0; i<array.length; i++)
			array[i]=integerList.get(i);
		
		return array;
	}
	
	public static void variableFor(int[] counterMaxValues, VariableForDelegate delegate)
	{
		//"Initialize" init values to 0
		int[] counterInitValues=new int[counterMaxValues.length];
		
		variableFor(counterMaxValues, counterInitValues, delegate);
	}
	
	public static void variableFor(int[] counterMaxValues, int[] counterInitValues, VariableForDelegate delegate)
	{
		int[] values=new int[counterMaxValues.length];
		
		rCount(counterInitValues, counterMaxValues, values, 0, delegate);
	}
	
	private static void rCount(int[] initValues, int[] counterMaxValues, int[] values, int curDimension, VariableForDelegate delegate)
	{
		for(int i=initValues[curDimension]; i<counterMaxValues[curDimension]; i++)
		{
			values[curDimension]=i;
			
			if(curDimension+1==values.length)
				delegate.doAction(values);
			else
				rCount(initValues, counterMaxValues, values, curDimension+1, delegate);
		}
	}
}
