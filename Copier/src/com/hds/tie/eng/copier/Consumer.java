package com.hds.tie.eng.copier;

public class Consumer implements Runnable {

	private int id;
	private Producer producer;
	private SynchFileWriter fileWriter;
	private DataLine current;

	// READ: ready to read data from producer.
	// WRITE: ready for writing to file.
	// DONE: producer is done and no buffered data to write to file.
	private enum STATUS {
		READ, WRITE, DONE
	};

	private STATUS state = STATUS.READ;

	private Thread me;

	public Consumer(int id, Producer producer, SynchFileWriter fileWriter) {
		this.id = id;
		this.producer = producer;
		this.fileWriter = fileWriter;
		this.me = new Thread(this);
	}

	public void start() {
		this.me.start();
	}

	@Override
	public void run() {
		while (this.state != STATUS.DONE) {
			// check on WRITE first, since if buffered data exists, write to
			// file.
			if (this.state == STATUS.WRITE) {
				System.out.println("Consumer " + id + " fetched line:");
				System.out.println("----------------------------------------------");
				System.out.println(new String(this.current.getData()));
				System.out.println("----------------------------------------------\n");
				fileWriter.submitLine(this.current.getLineNumber(), this.current.getData());

				this.state = STATUS.READ;
			}

			// only when producer is DONE, and no buffered data ready for
			// writing
			// should we set state to DONE.
			if (producer.isDone() && this.state != STATUS.WRITE) {
				this.state = STATUS.DONE;
			} else if (this.state == STATUS.READ) {
				this.current = producer.getNextLine();
				if (this.current != null) {
					this.state = STATUS.WRITE;
				}
			}
		}
	}

	public void join() {
		// This method has to block until the execution has been complete
		// After that, it should have no blocking effect
		try {
			this.me.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
}
