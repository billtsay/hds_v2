package com.hds.tie.eng.copier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class Producer implements Runnable {

	private FileInputStream input;

	private byte[] lineBuffer;
	private int dataLength;
	private int lastReadLineNumber = 0;

	private Exception reason = null;
	private ReentrantLock lock;

	// READ: should be ready for reading from file.
	// GET: ready for getting data.
	// DONE: no longer be able to read from file. reach the end of file or error occurs.
	private enum STATUS {
		READ, GET, DONE
	};

	private STATUS state = STATUS.READ;
	private Thread me;

	/* package */ Producer(File input) {
		this.state = STATUS.READ;
		this.lock = new ReentrantLock();
		try {
			this.input = new FileInputStream(input);
			this.lineBuffer = new byte[Configuration.getBufferSize()];
		} catch (FileNotFoundException e) {
			this.reason = e;
			this.state = STATUS.DONE;
		}

		this.me = new Thread(this);
	}

	public String getReason() {
		return this.reason.getClass().getName() + " : " + this.reason.getMessage();
	}

	public boolean isAvailable() {
		return this.state == STATUS.GET;
	}

	public boolean isDone() {
		return this.state == STATUS.DONE;
	}

	public void start() {
		this.me.start();
	}
	
	// Use java.util.concurrent.Lock to keep synchronization for this method.
	// Producer return data and line number. So it is called DataLine.
	public DataLine getNextLine() {
		// This method returns the currently buffered line to the caller
		// Make sure to make this method thread-safe
		try {
			lock.lock();
			if (this.state == STATUS.GET) {
				this.lastReadLineNumber = this.lastReadLineNumber + 1;
				DataLine dl = new DataLine(this.lastReadLineNumber, this.lineBuffer, this.dataLength);
				this.state = STATUS.READ;
				return dl;
			} else {
				// just not ready for reading data from clients.
				return null;
			}
		} finally {
			lock.unlock();
		}
	}

	public int getLastReadLineNumber() {
		return this.lastReadLineNumber;
	}

	@Override
	public void run() {
		// This method reads one line of the inmput file at a time
		// and make it available for consumers to grab using the getNextLine()
		// method
		// When the file is over, consumers fetching lines should be informed
		// that
		// no more lines are available
		while (this.state != STATUS.DONE) {
			if (this.state == STATUS.READ) {
				try {
					this.dataLength = input.read(lineBuffer);

					if (this.dataLength > 0) {
						this.state = STATUS.GET;
					}
					else {
						this.state = STATUS.DONE;
					}
				} catch (IOException e) {
					this.reason = e;
					this.state = STATUS.DONE;
				}
			}
		}

		try {
			input.close();
		} catch (IOException e) {
			// do nothing.
		}
	}

	public void join() {
		// This method has to block until the execution has been complete
		// After that, it should have no blocking effect
		try {
			this.me.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
