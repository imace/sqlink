package com.liuhaifeng.sqlink.semantic;

import java.io.Serializable;

import com.liuhaifeng.sqlink.beans.SQL;

/**
 * Hold result information of sentence parsing.
 * 
 * @author LiuHaifeng <br>
 *         $Id: ParseSentenceResult.java 52 2009-12-16 12:03:57Z LiuHaifeng $
 */
public class ParseSentenceResult implements Serializable {
	private static final long serialVersionUID = -1485922265896824368L;

	private int index;
	private SQL sentence;

	public ParseSentenceResult() {
	}

	public ParseSentenceResult(int index, SQL sentence) {
		this.index = index;
		this.sentence = sentence;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public SQL getSentence() {
		return sentence;
	}

	public void setSentence(SQL sentence) {
		this.sentence = sentence;
	}
}
