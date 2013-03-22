package com.liuhaifeng.sqlink.global;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.liuhaifeng.sqlink.beans.SQL;
import com.liuhaifeng.sqlink.error.CompilerError;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.optimize.Optimize;
import com.liuhaifeng.sqlink.semantic.SemanticParser;
import com.liuhaifeng.sqlink.word.Word;
import com.liuhaifeng.sqlink.word.WordParser;

/**
 * Compiler of Logic & Action Script.
 * 
 * @author LiuHaifeng <br>
 *         $Id: SQLParser.java 51 2009-12-16 07:55:46Z LiuHaifeng $
 */
public class SQLParser {

	private List<Word> wordList;
	private List<CompilerError> compilerErrorList;
	private List<SQL> sqlStatements;

	/**
	 * Constructor
	 * 
	 * @param nativeRunnable
	 */
	public SQLParser() {
		wordList = new ArrayList<Word>();
		compilerErrorList = new ArrayList<CompilerError>();
		sqlStatements = new ArrayList<SQL>();
	}

	/**
	 * Compile SQL.
	 * 
	 * @param sqlStr
	 * @throws SQLCompilerException
	 * @throws IOException
	 */
	public void compile(String sqlStr) throws SQLCompilerException, IOException {
		compile(sqlStr, Optimize.DEFAULT_OPTIMIZE_LEVEL);
	}

	/**
	 * Compile SQL.
	 * 
	 * @param sqlStr
	 * @param optimizeLevel
	 * @throws SQLCompilerException
	 * @throws IOException
	 */
	public void compile(String sqlStr, int optimizeLevel)
			throws SQLCompilerException, IOException {
		wordList.clear();
		compilerErrorList.clear();
		sqlStatements.clear();

		try {
			// parse word
			WordParser.parse(sqlStr, wordList, compilerErrorList);

			// semantic parse
			SemanticParser.parse(wordList, sqlStatements, compilerErrorList);

			// optimize
			Optimize.optimize(sqlStatements, optimizeLevel);
		} catch (SQLCompilerException e) {
			e.getCompilerError().setSourceName(sqlStr);
			compilerErrorList.add(e.getCompilerError());
			throw e;
		}
	}

	/**
	 * Get a copy of the whole word list.
	 * 
	 * @return
	 */
	public List<Word> getWordList() {
		if (wordList == null)
			return null;

		List<Word> copiedList = new ArrayList<Word>();
		Iterator<Word> it = wordList.iterator();
		Word w = null;
		Word w2 = null;
		while (it.hasNext()) {
			w = it.next();
			w2 = new Word(w.getLine(), w.getColumn(), w.getType(), new String(w
					.getWord()));
			copiedList.add(w2);
		}
		return copiedList;
	}

	/**
	 * Get compiler error list. You can invoke this method after compile a
	 * source file to see error information. <b>NOTE</b> that what you get is
	 * not a copy, in another word, if you have modified the error list, you
	 * will get a modified error list in the future until you compiled another
	 * source file.
	 * 
	 * @return
	 */
	public List<CompilerError> getCompilerErrorList() {
		return compilerErrorList;
	}

	/**
	 * Get the root domain. This domain actually contains all the data of the
	 * compiled script.
	 * 
	 * @return
	 */
	public List<SQL> getSQLStatements() {
		return sqlStatements;
	}

	@Override
	protected void finalize() throws Throwable {
		wordList.clear();
		wordList = null;
		compilerErrorList.clear();
		compilerErrorList = null;
		sqlStatements.clear();
		sqlStatements = null;
		super.finalize();
	}
}
