package com.liuhaifeng.sqlink;

import com.liuhaifeng.sqlink.beans.SQL;
import com.liuhaifeng.sqlink.word.Token;
import com.liuhaifeng.sqlink.word.Tokenizer;

public class SQLink {

	public static SQL parse(String sql) {
		Tokenizer tokenizer = new Tokenizer(sql);
		Token token = tokenizer.nextToken();
		return null;
	}

}
