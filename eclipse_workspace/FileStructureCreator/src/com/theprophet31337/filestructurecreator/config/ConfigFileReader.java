package com.theprophet31337.filestructurecreator.config;

import java.io.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.*;
import com.theprophet31337.filestructurecreator.ir.Config;

public class ConfigFileReader
{
	private File configFile;
	
	public ConfigFileReader(File configFile)
	{
		this.configFile=configFile;
	}
	
	/**
	 * Reads the configuration file and produce an internal representation of it.
	 * 
	 * This creates a key-value pair for each path variables, a key-values pair for each global variable and
	 * generate the flat representation of the structure with
	 * only the path variables already substituted (not the global variables).
	 * 
	 * @throws IOException
	 */
	public Config parse() throws IOException
	{
		try
		(
				FileReader reader=new FileReader(configFile);
		)
		{
			Config config=new Config();
			
			JsonElement rootElement=new JsonParser().parse(reader);
			
			if(!validate(rootElement))
				throw new IOException("Invalid configuration file");
			
			JsonObject root=(JsonObject)rootElement;
			
			//Get structure file path (to parse later)
			
			String structureFilePath=root.get("structure").getAsString();
			
			//Get variable delimiters
			
			if(root.has("startVariableDelimiter"))
				config.setStartVariableDelimiter(root.get("startVariableDelimiter").getAsCharacter());
			else
				config.setStartVariableDelimiter('<');
			
			if(root.has("endVariableDelimiter"))
				config.setEndVariableDelimiter(root.get("endVariableDelimiter").getAsCharacter());
			else
				config.setEndVariableDelimiter('>');
			
			//asd
			
			/* Get path variables. Each variable has a name and a value (both string).
			 * Replace the slash '/' in the path by the platform specific path seperator.
			 */
			
			for(Entry<String, JsonElement> entry: root.get("pathVariables").getAsJsonObject().entrySet())
				config.addPathVar(entry.getKey(), entry.getValue().getAsString().replace('/', File.separatorChar));
			
			JsonElement entryElement;
			JsonArray arrayEntry;
			
			/* Get global variables. Each variable has a name (string) and a single value (string) or
			 * an array of string values.
			 */
			
			for(Entry<String, JsonElement> entry: root.get("globalVariables").getAsJsonObject().entrySet())
			{
				entryElement=entry.getValue();
				
				if(entryElement.isJsonPrimitive())
					config.addGlobalVar(entry.getKey(), entry.getValue().getAsString());
				else
				{
					arrayEntry=(JsonArray)entryElement;
					
					for(int i=0; i<arrayEntry.size(); i++)
					{
						config.addGlobalVar(entry.getKey(), arrayEntry.get(i).getAsString());
					}
				}
			}
			
			//Parse structure file
			StructureFileReader structureFileReader=new StructureFileReader(config, new File(structureFilePath));
			structureFileReader.parse();
			
			return config;
		}
	}
	
	/**
	 * Validate root properties in the configuration file.
	 */
	private boolean validate(JsonElement rootElement)
	{
		if(!(rootElement instanceof JsonObject))
			return false;
		
		JsonObject root=(JsonObject)rootElement;
		
		if(!root.has("structure")||!root.has("pathVariables")||!root.has("globalVariables"))
			return false;
		
		return true;
	}
}
