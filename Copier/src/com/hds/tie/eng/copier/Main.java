package com.hds.tie.eng.copier;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		// args[0]: number of consumers.
		// args[1]: working folder for import/export files.
		if (args.length < 1) {
			System.err.println("The number of consumers should be specified");
			return;
		}

		int consumerCount = Integer.parseInt(args[0]);
		File workFolder = null;

		if (args.length > 1) {
			workFolder = new File(args[1]);
			if (!workFolder.isDirectory()) {
				System.err.println(args[1] + " isn't a directory");
				return;
			}
		}

		// Define input and output files
		File input = new File(workFolder, "input.txt");
		if (!input.exists()) {
			System.err.println("Couldn't find input file " + input.getAbsolutePath());
			return;
		}
		File output = new File(workFolder, "output.txt");
		if (output.exists()) {
			output.delete();
		}

		// Instantiate and start components
		Producer producer = new Producer(input);
		Consumer[] consumers = new Consumer[consumerCount];
		SynchFileWriter fileWriter = new SynchFileWriter(output);
		for (int i = 0; i < consumerCount; i++) {
			consumers[i] = new Consumer(i, producer, fileWriter);
			consumers[i].start();
		}

		producer.start();

		// Wait for producer to end

		producer.join();
		System.out.println("Producer finished execution.");

		// wait for consumers to end
		for (int i = 0; i < consumerCount; i++) {
			consumers[i].join();
			System.out.println("Consumer " + i + " finished execution.");
		}
	}
}
