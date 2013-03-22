package com.liuhaifeng.sqlink.config;

import java.io.Serializable;

/**
 * Word define.
 * 
 * @author LiuHaifeng <br>
 *         $Id: WordDef.java 67 2009-12-29 12:29:10Z LiuHaifeng $
 */
public class WordDef implements Serializable {
	private static final long serialVersionUID = -6586461652145434767L;

	// state
	public static final int BE = 0; // begin
	public static final int UN = 1; // unknown

	public static final int ID = 2; // identification
	public static final int IN = 3; // integer
	public static final int DO = 4; // double
	public static final int ST = 5; // string
	public static final int SY = 6; // symbol
	public static final int DC = 7; // float candidate
	public static final int LC = 8; // line comment
	public static final int BC = 9; // block comment
	public static final int CS = 10; // comment start candidate
	public static final int CE = 11; // comment end candidate
	public static final int LO = 12; // long
	public static final int FL = 13; // float
	public static final int RF = 14; // refer string

	public static final int NU = 100; // null
	public static final int DE = 101; // delimiter
	public static final int OP = 102; // operator
	public static final int BO = 103; // boolean (true/false)

	public static final int KEYWORD = 201; // keyword
	public static final int FUNCTION = 202; // function
	public static final int HOLDER = 203; // "?" parameters

	public static final int EN = 1000; // end

	// action
	public static final int SPAC = 0; // space
	public static final int LETT = 1; // letter
	public static final int NUMB = 2; // number
	public static final int DOT_ = 3; // dot
	public static final int UNDE = 4; // underline
	public static final int QUOT = 5; // quote
	public static final int RQUO = 6; // reverse quote
	public static final int BIAS = 7; // bias
	public static final int STAR = 8; // star
	public static final int SYMB = 9; // symbol
	public static final int LE_L = 10; // l/L (e.g. 1000L)
	public static final int LE_F = 11; // f/F (e.g. 0.01f)
	public static final int UNKN = 12; // unknown
	public static final int TERM = 13; // terminate

	// transition
	public static final int[][] transition = {
			// a \ s--> BE, UN, ID, IN, DO, ST, SY, DC, LC, BC, CS, CE, LO, FL, RF
			/* SPAC */{ BE, BE, BE, BE, BE, ST, BE, BE, LC, BC, BE, BC, BE, BE, UN },
			/* LETT */{ ID, UN, ID, UN, UN, ST, ID, ID, LC, BC, ID, BC, UN, UN, RF },
			/* NUMB */{ IN, UN, ID, IN, DO, ST, IN, DO, LC, BC, IN, BC, UN, UN, RF },
			/* DOT_ */{ DC, SY, SY, DO, UN, ST, DC, SY, LC, BC, DC, BC, UN, UN, UN },
			/* UNDE */{ ID, UN, ID, UN, UN, ST, ID, ID, LC, BC, ID, BC, UN, UN, RF },
			/* QUOT */{ ST, UN, UN, UN, UN, BE, ST, ST, LC, BC, ST, BC, UN, UN, UN },
			/* RQUO */{ RF, UN, UN, UN, UN, BE, RF, RF, LC, BC, RF, BC, UN, UN, BE },
			/* BIAS */{ CS, CS, CS, CS, CS, ST, CS, CS, LC, BC, LC, BE, CS, CS, UN },
			/* STAR */{ SY, SY, SY, SY, SY, ST, SY, SY, LC, CE, BC, CE, SY, SY, UN },
			/* SYMB */{ SY, SY, SY, SY, SY, ST, SY, SY, LC, BC, SY, BC, SY, SY, UN },
			/* LE_L */{ ID, UN, ID, LO, UN, ST, ID, ID, LC, BC, ID, BC, UN, UN, UN },
			/* LE_F */{ ID, UN, ID, UN, FL, ST, ID, ID, LC, BC, ID, BC, UN, UN, UN },
			/* UNKN */{ UN, UN, UN, UN, UN, ST, UN, UN, LC, BC, UN, BC, UN, UN, UN },
			/* TERM */{ EN, EN, EN, EN, EN, EN, EN, EN, EN, BC, EN, BC, EN, EN, EN } };

	/* long delimiters in front */
	public static final String[] delimiters = { "{", "}", "(", ")", ",", "#",
			";" };

	/*
	 * long operators in front(real used operators are defined in class
	 * Keywords: operators and it's alias map.)
	 */
	public static final String[] operators = { "++", "--", "+=", "-=", "*=",
			"/=", "%=", "&=", "|=", ">=", "<=", "==", "!=", "<>", "&&", "||",
			"[]", "~", "`", "!", "@", "#", "$", "%", "^", "&", "*", "-", "+",
			"=", "|", "/", ">", "<", ":", ".", "?", "[", "]" };

	/**
	 * Get action by char.
	 * 
	 * @param ch
	 * @return
	 */
	public static int getAction(char ch) {
		if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
			if (ch == 'l' || ch == 'L')
				return LE_L;
			else if (ch == 'f' || ch == 'F')
				return LE_F;
			else
				return LETT;
		else if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n')
			return SPAC;
		else if (ch >= '0' && ch <= '9')
			return NUMB;
		else if (ch == '_')
			return UNDE;
		else if (ch == '.')
			return DOT_;
		else if (ch == '"' || ch == '\'')
			return QUOT;
		else if (ch == '`')
			return RQUO;
		else if (ch == '/')
			return BIAS;
		else if (ch == '*')
			return STAR;
		else if (ch == 0)
			return TERM;
		else
			return SYMB;
	}

}
