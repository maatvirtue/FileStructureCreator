package com.theprophet31337.filestructurecreator.main;

import java.io.*;

import com.theprophet31337.filestructurecreator.config.ConfigFileReader;
import com.theprophet31337.filestructurecreator.creator.StructureCreator;
import com.theprophet31337.filestructurecreator.ir.Config;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		if(args.length!=1||args[0].equals("-h"))
		{
			printHelp();
			return;
		}
		
		final String configFilePath=args[0];
		ConfigFileReader configReader=new ConfigFileReader(new File(configFilePath));
		Config config=configReader.parse();
		
		StructureCreator creator=new StructureCreator(config);
		creator.createStructure();
	}
	
	private static void printHelp()
	{
		System.out.println("fileStructureCreator [options] configFile");
		System.out.println("options:");
		System.out.println("    -h        help. print this help information.");
	}
}
