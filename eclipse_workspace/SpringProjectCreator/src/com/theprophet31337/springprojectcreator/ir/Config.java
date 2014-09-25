package com.theprophet31337.springprojectcreator.ir;

import java.util.*;

public class Config
{
	private Map<String, String> pathVarMap;
	private Map<String, List<String>> globalVarMap;
	private List<PathNode> paths;
	
	public Config()
	{
		pathVarMap=new HashMap<String, String>();
		globalVarMap=new HashMap<String, List<String>>();
		paths=new LinkedList<PathNode>();
	}
	
	public void addPathVar(String name, String value)
	{
		pathVarMap.put(name, value);
	}
	
	public void addGlobalVar(String name, String value)
	{
		List<String> values=globalVarMap.get(name);
		
		if(values==null)
		{
			values=new LinkedList<String>();
		}
		
		values.add(value);
		
		globalVarMap.put(name, values); 
	}
	
	public void addPath(PathNode pathNode)
	{
		paths.add(pathNode);
	}
	
	public List<PathNode> getPaths()
	{
		return paths;
	}
	
	public Set<String> getPathVarNames()
	{
		return pathVarMap.keySet();
	}
	
	public Set<String> getGlobalVarNames()
	{
		return globalVarMap.keySet();
	}
	
	public String getPathVarValue(String name)
	{
		return pathVarMap.get(name);
	}
	
	public List<String> getGlobalVarValues(String name)
	{
		return globalVarMap.get(name);
	}
}
