package com.theprophet31337.springprojectcreator.ir;

import java.io.File;

public class PathNode
{
	private String path;
	private File templateFile;
	
	public PathNode(String path)
	{
		this(path, null);
	}
	
	public PathNode(String path, File templateFile)
	{
		this.path=path;
		this.templateFile=templateFile;
	}
	
	public boolean isDirectory()
	{
		return path.endsWith(File.separator);
	}
	
	public boolean isFile()
	{
		return !isDirectory();
	}
	
	public String getPath()
	{
		return path;
	}
	
	public File getTemplateFile()
	{
		return templateFile;
	}
}
