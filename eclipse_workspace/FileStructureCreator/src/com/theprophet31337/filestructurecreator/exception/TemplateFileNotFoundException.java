package com.theprophet31337.filestructurecreator.exception;

import java.io.FileNotFoundException;

public class TemplateFileNotFoundException extends FileNotFoundException
{
	private static final long serialVersionUID=-3472519318919932813L;

	public TemplateFileNotFoundException()
	{
		//Do nothing
	}
	
	public TemplateFileNotFoundException(String message)
	{
		super(message);
	}
}
