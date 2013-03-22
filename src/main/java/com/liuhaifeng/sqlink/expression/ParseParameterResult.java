package com.liuhaifeng.sqlink.expression;

import java.io.Serializable;
import java.util.List;

import com.liuhaifeng.sqlink.beans.Expression;

/**
 * Hold result information of parameter parsing.
 * 
 * @author LiuHaifeng <br>
 *         $Id: ParseParameterResult.java 52 2009-12-16 12:03:57Z LiuHaifeng $
 */
public class ParseParameterResult implements Serializable {
	private static final long serialVersionUID = -8501866744539542403L;

	private int index;
	private List<Expression> parameterList;

	public ParseParameterResult() {
	}

	public ParseParameterResult(int index, List<Expression> parameterList) {
		this.index = index;
		this.parameterList = parameterList;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<Expression> getParameterList() {
		return parameterList;
	}

	public void setParameterList(List<Expression> parameterList) {
		this.parameterList = parameterList;
	}
}
