package com.theprophet31337.filestructurecreator.creator;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.stringtemplate.v4.ST;

import com.theprophet31337.filestructurecreator.exception.TemplateFileNotFoundException;
import com.theprophet31337.filestructurecreator.ir.Config;
import com.theprophet31337.filestructurecreator.ir.PathNode;
import com.theprophet31337.filestructurecreator.util.SharedUtil;
import com.theprophet31337.filestructurecreator.util.SharedUtil.VariableForDelegate;

public class StructureCreator
{
	private class ResolvedPath
	{
		private String resolvedPath;
		private Map<String, String> multivarContext;
		
		public ResolvedPath()
		{
			multivarContext=new HashMap<String, String>();
		}
		
		public void setResolvedPath(String resolvedPath)
		{
			this.resolvedPath=resolvedPath;
		}
		
		public String getResoledPath()
		{
			return resolvedPath;
		}
		
		public void addContextVar(String varname, String value)
		{
			multivarContext.put(varname, value);
		}
		
		public Set<String> getContextVarNames()
		{
			return multivarContext.keySet();
		}
		
		public String getContextVarValue(String varname)
		{
			return multivarContext.get(varname);
		}
	}
	
	private Config config;
	private char startDelimiter;
	private char endDelimiter;
	
	public StructureCreator(Config config)
	{
		this.config=config;
		
		startDelimiter=config.getStartVariableDelimiter();
		endDelimiter=config.getEndVariableDelimiter();
	}
	
	public void createStructure() throws IOException
	{
		for(PathNode node: config.getPaths())
			createPath(node);
	}
	
	private void createPath(PathNode node) throws IOException
	{
		String nodePath=node.getPath();
		List<ResolvedPath> resolvedPaths=new LinkedList<ResolvedPath>();
		
		nodePath=resolveStaticVariables(nodePath);
		
		resolvedPaths=resolveMultiValueVariables(nodePath);
		
		//Create paths
		try
		{
			if(node.isDirectory())
			{
				for(ResolvedPath resolvedPath: resolvedPaths)
				{
					Files.createDirectories(new File(resolvedPath.getResoledPath()).toPath());
					
					System.out.println(new File(resolvedPath.getResoledPath()).getAbsolutePath());
				}
			}
			else
			{
				for(ResolvedPath resolvedPath: resolvedPaths)
				{
					createFileNode(node, resolvedPath);
				}
			}
		}
		catch(FileAlreadyExistsException e)
		{
			System.out.println("Error: file already exists: \""+e.getFile()+"\"");
		}
	}
	
	private void createFileNode(PathNode fileNode, ResolvedPath resolvedPath) throws TemplateFileNotFoundException, IOException
	{
		Path resolvedFilePath=new File(resolvedPath.getResoledPath()).toPath();
		
		//Create parent directory if it does not exists
		Files.createDirectories(new File(resolvedPath.getResoledPath()).getParentFile().toPath());
		
		//Create file
		Files.createFile(resolvedFilePath);
		
		System.out.println(new File(resolvedPath.getResoledPath()).getAbsolutePath());
		
		//Create content from template is any template was provided.
		if(fileNode.getTemplateFile()!=null)
		{
			try
			{
				String templateString=new String(Files.readAllBytes(fileNode.getTemplateFile().toPath()));
				
				ST template = new ST(templateString, startDelimiter, endDelimiter);
				
				//Add global variables to template
				for(String varname: config.getGlobalVarNames())
					if(config.getGlobalVarValues(varname).size()==1)
						template.add(varname, config.getGlobalVarValues(varname).get(0));
				
				//Add context variables (current values of multivalue var for this path) to template
				for(String varname: resolvedPath.getContextVarNames())
					template.add(varname, resolvedPath.getContextVarValue(varname));
				
				Files.write(resolvedFilePath, template.render().getBytes());
			}
			catch(FileNotFoundException e)
			{
				throw new TemplateFileNotFoundException("Template for file node with path \""+
						fileNode.getPath()+"\" was not found: template file path: \""+fileNode.getTemplateFile().getAbsolutePath()+"\"");
			}
		}
	}
	
	/**
	 * Resolve the multivalue variables in the path; create a new path for each value of each variable.
	 * 
	 * @param path
	 * 			Node path where all the static variables already resolved.
	 */
	private List<ResolvedPath> resolveMultiValueVariables(final String path)
	{
		final String startDelStr="\\Q"+startDelimiter+"\\E";
		final String endDelStr="\\Q"+endDelimiter+"\\E";
		
		final Pattern varRegx=Pattern.compile(startDelStr+"([^"+endDelStr+"]+)"+endDelStr);
		
		final List<ResolvedPath> paths=new LinkedList<ResolvedPath>();
		final List<String> multiValueVarNames=new LinkedList<String>();
		Matcher matcher=varRegx.matcher(path);
		
		while(matcher.find())
			multiValueVarNames.add(matcher.group(1));
		
		//No multivalue variable detected.
		if(multiValueVarNames.size()==0)
			return paths;
		
		List<Integer> counterInitValues=new LinkedList<Integer>();
		List<Integer> counterMaxValues=new LinkedList<Integer>();
		
		for(int i=0; i<multiValueVarNames.size(); i++)
		{
			String varName=multiValueVarNames.get(i);
			counterInitValues.add(0);
			counterMaxValues.add(config.getGlobalVarValues(varName).size());
		}
		
		SharedUtil.variableFor(SharedUtil.toPrimitiveArray(counterMaxValues), new VariableForDelegate()
		{
			@Override
			public void doAction(int[] counters)
			{
				String tmpPath=path;
				ResolvedPath resolvedPath=new ResolvedPath();
				
				for(int i=0; i<counters.length; i++)
				{
					String varname=multiValueVarNames.get(i);
					String varValue=config.getGlobalVarValues(varname).get(counters[i]);
					tmpPath=tmpPath.replace(startDelimiter+varname+endDelimiter, varValue);
					
					resolvedPath.addContextVar(varname, varValue);
				}
				
				resolvedPath.setResolvedPath(tmpPath);
				
				paths.add(resolvedPath);
			}
		});
		
		return paths;
	}
	
	/**
	 * Resolve static variables (variables with only one value) in the specified node path.
	 */
	private String resolveStaticVariables(String path)
	{
		for(String varName: config.getGlobalVarNames())
		{
			if(config.getGlobalVarValues(varName).size()!=1)
				continue;
			
			path=path.replace(startDelimiter+varName+endDelimiter, config.getGlobalVarValues(varName).get(0));
		}
		
		return path;
	}
}
