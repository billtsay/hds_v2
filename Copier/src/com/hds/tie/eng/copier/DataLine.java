package com.hds.tie.eng.copier;

import java.util.Arrays;

public class DataLine {
	private byte[] lineBuffer;
	private int lineNumber;
	
	public DataLine(int n, byte[] line, int len) {
		this.lineNumber = n;
		this.lineBuffer = Arrays.copyOf(line, len);
	}
	
	public byte[] getData() {
		return this.lineBuffer;
	}

	public int getLineNumber() {
		return this.lineNumber;
	}
}
