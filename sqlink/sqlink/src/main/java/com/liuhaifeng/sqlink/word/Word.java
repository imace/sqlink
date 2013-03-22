package com.liuhaifeng.sqlink.word;

import java.io.Serializable;

/**
 * Word.
 * 
 * @author LiuHaifeng <br>
 *         $Id: Word.java 46 2009-12-04 14:28:03Z LiuHaifeng $
 */
public class Word implements Serializable {
	private static final long serialVersionUID = -6274298781617954509L;

	private int line;
	private int column;
	private int type;
	private String word;

	public Word() {
	}

	public Word(int line, int column, int type, String word) {
		this.line = line;
		this.column = column;
		this.type = type;
		this.word = word;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
