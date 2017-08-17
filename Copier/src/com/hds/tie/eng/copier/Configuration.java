package com.hds.tie.eng.copier;

import java.io.FileReader;
import java.util.Properties;

public class Configuration {
	private static final String BUFFER_SIZE = "line.buffer.size";

	private static final Properties props = new Properties();

	static {
		FileReader fd = null;
		try {
			fd = new FileReader("../conf/settings.props");
			props.load(fd);
		} catch (Exception e) {
			System.err.println("Loading settings.props failed due to [" + e.getMessage() + "].\n");
			System.out.println("No properties file available, use default values instead.\n");
		} finally {
			try {
				if (fd != null)
					fd.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	public static int getBufferSize() {
		return Integer.valueOf(props.getProperty(BUFFER_SIZE, "12"));
	}
}
