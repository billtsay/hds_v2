package com.hds.tie.eng.copier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.Condition;

public class SynchFileWriter {

	private FileOutputStream output;
	private int lineNumber;
	private Exception reason = null;

	/* package */ SynchFileWriter(File output) {
		try {
			this.output = new FileOutputStream(output);
		} catch (FileNotFoundException e) {
			reason = e;
		}

		this.lineNumber = 0;
	}

	public synchronized void submitLine(int lno, byte[] lineBuffer) {
		// This is the method that makes sure lines are
		// written to the file in the right order
		try {
			while (lno != this.lineNumber + 1) {
				wait();  // for client with wrong line number, wait ...
			}
			
			this.output.write(lineBuffer);
			this.lineNumber = this.lineNumber + 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.reason = e;
		} catch (InterruptedException e) {
			// do nothing
		} finally {
			notifyAll();
		}
	}

	public int lineNumber() {
		return this.lineNumber;
	}

	public String getReason() {
		return this.reason.getClass().getName() + " : " + this.reason.getMessage();
	}

	public void close() {
		// Close file
		try {
			this.output.close();
		} catch (IOException e) {
			// don't care.
		}
	}
}
