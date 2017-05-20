/*
 * Copyright (c) 2013, ENGEES. All rights reserved.
 * This file is part of RCAExplore.
 * 
 *  RCAExplore is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  RCAExplore is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RCAExplore.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  Authors : 
 *  - Jean-Rémy Falleri
 *  - Xavier Dolques
 *  
 *  this file contains code covered by the following terms:
 *  Copyright 2009 Jean-Rémy Falleri
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rcaexplore.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public abstract class GenerateCode {
	
	protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

	protected Writer writer;
	protected BufferedWriter buffer;
	
	public GenerateCode(Writer buffer2) {
		this.writer=buffer2;
		buffer=new BufferedWriter(buffer2);
	}
	
	/**
	 * Generate the code.
	 */
	public abstract void generateCode() throws IOException;
	
	
	
	
	/**
	 * Returns the code buffer.
	 * @return a string buffer.
	 */
	public Writer getCodeBuffer() {
		return this.writer;
	}
	
	
	/**
	 * Appends the given string to the internal buffer.
	 * @param s a string.
	 */
	protected void append(String s) throws IOException {
		buffer.append(s);
	}
	
	/**
	 * Appends the given string to a dedicated line in the internal buffer.
	 * @param s a string.
	 * @throws IOException 
	 */
	protected void appendLine(String s) throws IOException {
		append(s + LINE_SEPARATOR);
	}
	
	/**
	 * Appends an empty line in the internal buffer.
	 * @throws IOException 
	 */
	protected void newLine() throws IOException {
		append(LINE_SEPARATOR);
	}
	
	/**
	 * Appends the given string in a dedicated line in the internal buffer. This string is preceded
	 * by a given number of tabulations.
	 * @param s a string.
	 * @param tabsNb the number of tabulations.
	 * @throws IOException 
	 */
	protected void appendWithTabs(String s,int tabsNb) throws IOException {
		for(int i = 0 ; i < tabsNb ; i++ )
			append("\t");
		append(s);
	}
	
	/**
	 * Appends the given string preceded by a tab.
	 * @param s a string.
	 * @throws IOException 
	 */
	protected void appendWithTab(String s) throws IOException {
		appendWithTabs(s,1);
	}
	
	/**
	 * Appends the given string on a dedicated line preceded by a tab.
	 * @param s a string
	 * @throws IOException 
	 */
	protected void appendLineWithTab(String s) throws IOException {
		appendLineWithTabs(s,1);
	}
	
	/**
	 * Appends the given string on a dedicated line preceded by a given number of tabs.
	 * @param s a string
	 * @param tabsNb the number of tabs.
	 * @throws IOException 
	 */
	protected void appendLineWithTabs(String s,int tabsNb) throws IOException {
		appendWithTabs(s + LINE_SEPARATOR, tabsNb);
	}
	
}