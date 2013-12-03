package com.cr7.model;

import java.io.File;

public interface Model {
	public void init();
	public void train(File [] files);
	public String predict(File file);
}
