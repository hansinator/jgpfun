/*
 */
package jgpfun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 * A convenience singleton container for properties.
 * This is to be replaced by a more complex mechanism
 * as soon as this application can run multiple simulations
 * in parallel.
 *
 * @author Hansinator
 */
public final class Settings {

    private static final Settings instance = new Settings();

    private final Properties properties = new Properties();


    private Settings() {
    }


    public synchronized static Settings getInstance() {
        return instance;
    }


    public static String getProperty(String key) {
        return instance.properties.getProperty(key);
    }


    public static String setProperty(String key, String value) {
        return (String) instance.properties.setProperty(key, value);
    }


    public static void load(File file) {
        Reader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            instance.properties.load(reader);
        } catch (FileNotFoundException ex) {
            // TODO: log
            // wrap and rethrow
            throw new RuntimeException("Properties file not found", ex);
        } catch (IOException ex) {
            // TODO: log
            // wrap and rethrow
            throw new RuntimeException("Error while reading properties", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // TODO: log
                    // wrap and rethrow
                    throw new RuntimeException("Error closing properties file", ex);
                }
            }
        }
    }


    public static void store(File file, String comments) {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            instance.properties.store(writer, comments);
        } catch (FileNotFoundException ex) {
            // TODO: log
            // wrap and rethrow
            throw new RuntimeException("Properties file not found", ex);
        } catch (IOException ex) {
            // TODO: log
            // wrap and rethrow
            throw new RuntimeException("Error while reading properties", ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    // TODO: log
                    // wrap and rethrow
                    throw new RuntimeException("Error closing properties file", ex);
                }
            }
        }
    }

}
