package com.theprophet31337.filestructurecreator.config;

import java.io.*;
import java.util.Map.Entry;

import com.google.gson.*;
import com.theprophet31337.filestructurecreator.ir.Config;
import com.theprophet31337.filestructurecreator.ir.PathNode;

public class StructureFileReader
{
	private File structureFile;
	private Config config;
	
	public StructureFileReader(Config config, String configFilePath)
	{
		this(config, new File(configFilePath));
	}
	
	public StructureFileReader(Config config, File configFile)
	{
		this.config=config;
		this.structureFile=configFile;
	}
	
	/**
	 * This reads from a partially constructed internal representation of the configuration file and structure file.
	 * The internal representation needs to have at least the path variables defined.
	 * 
	 * @throws IOException
	 */
	public void parse() throws IOException
	{
		try
		(
				FileReader reader=new FileReader(structureFile);
		)
		{
			JsonObject root=(JsonObject)new JsonParser().parse(reader);
			
			rcalcPath(".", root);
		}
	}
	
	private void rcalcPath(String prefix, JsonElement node)
	{
		prefix=replacePathVar(prefix);
		
		if(node instanceof JsonObject)
		{
			JsonObject folder=(JsonObject)node;
			
			if(!prefix.endsWith(File.separator))
				prefix+=File.separator;
			
			if(!prefix.trim().equals(File.separator))
				config.addPath(new PathNode(prefix));
			
			for(Entry<String, JsonElement> entry: folder.entrySet())
				rcalcPath(prefix+entry.getKey(), entry.getValue());
		}
		else
		{
			if(!prefix.trim().equals(File.separator))
			{
				if(node.isJsonNull())
					config.addPath(new PathNode(prefix));
				else
					config.addPath(new PathNode(prefix, new File(node.getAsString())));
			}
		}
	}
	
	private String replacePathVar(String path)
	{
		final char startDelimiter=config.getStartVariableDelimiter();
		final char endDelimiter=config.getEndVariableDelimiter();
		
		for(String pathVarName: config.getPathVarNames())
			path=path.replace(startDelimiter+"path:"+pathVarName+endDelimiter, config.getPathVarValue(pathVarName));
		
		return path;
	}
}
