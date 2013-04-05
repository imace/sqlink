package com.liuhaifeng.sqlink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.liuhaifeng.sqlink.error.CompilerError;
import com.liuhaifeng.sqlink.word.Word;
import com.liuhaifeng.sqlink.word.WordParser;

public class TestWordParser {

	@Test
	public void test() throws IOException {
		parseSQL("select ph_archives.* from `ph_archives` "
				+ "where arc_title like '%CS%' order by arc_created desc "
				+ "limit 8");
		parseSQL("select `a`.`arc_id`, `a`.`arc_title` from ph_archives "
				+ "where `arc_rank` >= 50 and arc_status = 'OPEN' or arc_author is null or arc_source != 'http://' "
				+ "and arc_name <> 'test' or arc_rank <= 10 order by `arc_created` desc limit 0, 8");
		parseSQL("select `a`.`arc_id`, `a`.`arc_title` from ph_archives "
				+ "where `arc_rank` >= 50 and arc_status = ? or arc_author is null or arc_source != ? "
				+ "and arc_name <> 'test' or arc_rank <= ? order by `arc_created` desc limit 0, 8");
	}

	public void parseSQL(String sql) throws IOException {
		System.out.println("====================================");
		List<Word> wordList = new ArrayList<Word>();
		List<CompilerError> errorList = new ArrayList<CompilerError>();
		WordParser.parse(sql, wordList, errorList);
		dumpWordList(wordList);
		dumpErrorList(errorList);
	}

	/**
	 * 2013-03-24: Parse 1 million times costs 13652 @ MacBook pro (i7 2.2GHz Quad, 4G Ram)
	 * @throws IOException
	 * 
	 */
	@Test
	@Ignore
	public void testTimmingWordParser() throws IOException {
		String sql = "select `a`.`arc_id`, `a`.`arc_title` from ph_archives "
				+ "where `arc_rank` >= 50 and arc_status = 'OPEN' or arc_author is null or arc_source != 'http://' "
				+ "and arc_name <> 'test' or arc_rank <= 10 order by `arc_created` desc limit 0, 8";
		List<Word> wordList = new ArrayList<Word>();
		List<CompilerError> compilerErrorList = new ArrayList<CompilerError>();
		long ts = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			wordList.clear();
			compilerErrorList.clear();
			WordParser.parse(sql, wordList, compilerErrorList);
		}
		long cost = System.currentTimeMillis() - ts;
		System.out.println("Cost: " + cost);
	}

	private void dumpWordList(List<Word> wordList) {
		System.out.println("Dumping word list...");
		System.out.println();
		if (wordList == null)
			return;
		for (Word w : wordList) {
			System.out.println(w.getWord() + "\t" + w.getType());
		}
		System.out.println();
	}

	private void dumpErrorList(List<CompilerError> errorList) {
		System.out.println("Dumping error list");
		System.out.println();
		if (errorList == null)
			return;
		for (CompilerError er : errorList) {
			System.out.println(er.getErrorCode() + "\t(" + er.getLine() + ", "
					+ er.getColumn() + ")\t" + er.getMessage());
		}
		System.out.println();
	}
}
