package com.theprophet31337.springprojectcreator.main;

import java.io.*;

import com.theprophet31337.springprojectcreator.config.ConfigFileReader;
import com.theprophet31337.springprojectcreator.ir.Config;
import com.theprophet31337.springprojectcreator.ir.PathNode;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		/*
		ST hello = new ST("Hello, <name>");
		hello.add("name", "World");
		System.out.println(hello.render());
		*/
		
		if(args.length!=1||args[0].equals("-h"))
		{
			printHelp();
			return;
		}
		
		final String configFilePath=args[0];
		ConfigFileReader configReader=new ConfigFileReader(new File(configFilePath));
		Config config=configReader.parse();
		
		//asd
	}
	
	private static void printHelp()
	{
		System.out.println("springProjectCreator [options] configFile");
		System.out.println("options:");
		System.out.println("    -h        help. print this help information.");
	}
}
