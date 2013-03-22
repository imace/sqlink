package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;
import java.util.List;

import com.liuhaifeng.sqlink.config.Keywords;
import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.error.SQLRuntimeException;
import com.liuhaifeng.sqlink.word.Word;

public class Pagination implements Serializable {
	private static final long serialVersionUID = -6368929169985465942L;

	private Integer start;
	private Integer size;
	
	private boolean startHolder = false;
	private boolean sizeHolder = false;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("limit ");
		if (start != null) {
			sb.append(start).append(", ");
		} else if (startHolder) {
			sb.append("?, ");
		}
		if (size != null) {
			sb.append(size);
		} else if (sizeHolder) {
			sb.append("?");
		} else {
			throw new SQLRuntimeException(
					"Limit option must occurs with size information.");
		}
		return sb.toString();
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setSizeHolder(boolean sizeHolder) {
		this.sizeHolder = sizeHolder;
	}

	public boolean isSizeHolder() {
		return sizeHolder;
	}

	public void setStartHolder(boolean startHolder) {
		this.startHolder = startHolder;
	}

	public boolean isStartHolder() {
		return startHolder;
	}

	public int parse(int index, List<Word> wordList)
			throws SQLCompilerException {
		if (index >= wordList.size())
			throw new SQLCompilerException(wordList.get(wordList.size() - 1),
					ErrorCode.ERROR, ErrorCode.SP_INVALID_SENTENCE,
					"Unexpected end.");

		Word w = wordList.get(index);
		if (w.getType() != WordDef.KEYWORD
				|| !Keywords.LIMIT.equalsIgnoreCase(w.getWord()))
			return index;

		index++;
		if (index >= wordList.size())
			throw new SQLCompilerException(w, ErrorCode.ERROR,
					ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

		w = wordList.get(index);

		if (w.getType() != WordDef.IN && w.getType() != WordDef.LO
				&& w.getType() != WordDef.HOLDER)
			throw new SQLCompilerException(w, ErrorCode.ERROR,
					ErrorCode.CG_UNEXPECTED_WORD, "Only expects number here.");

		if (w.getType() == WordDef.HOLDER) {
			setSize(null);
			setSizeHolder(true);
		} else {
			setSize(Integer.parseInt(w.getWord()));
			setSizeHolder(false);
		}

		index++;
		if (index < wordList.size()
				&& wordList.get(index).getType() == WordDef.DE
				&& Keywords.COMMA.equals(wordList.get(index).getWord())) {
			setStart(getSize());
			setStartHolder(isSizeHolder());
			index++;
			if (index >= wordList.size())
				throw new SQLCompilerException(wordList.get(index - 1),
						ErrorCode.ERROR, ErrorCode.SP_INVALID_SENTENCE,
						"Unexpected end.");
			w = wordList.get(index);
			if (w.getType() != WordDef.IN && w.getType() != WordDef.LO
					&& w.getType() != WordDef.HOLDER)
				throw new SQLCompilerException(w, ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE, "Expects number here.");

			if (w.getType() == WordDef.HOLDER) {
				setSize(null);
				setSizeHolder(true);
			} else {
				setSize(Integer.parseInt(w.getWord()));
				setSizeHolder(false);
			}
			index++;
		}
		return index;
	}
}
