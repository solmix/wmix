package org.solmix.rest.util.properties;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.rest.Constant;

/**
 * Prop. Prop can load properties file from CLASSPATH or File object.
 */
public class Prop {
    private final static Logger logger = LoggerFactory.getLogger(Prop.class);

    private Properties properties = null;

    private static String rootClassPath = null;

    /**
     * Prop constructor.
     *
     * @see #Prop(String, String)
     */
    public Prop(String fileName) {
        this(fileName, Constant.encoding);
    }


    public Prop(String fileName, String encoding) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        load(fileName, inputStream, encoding);
    }

    /**
     * Prop constructor.
     *
     * @see #Prop(File, String)
     */
    public Prop(File file) {
        this(file, Constant.encoding);
    }

    /**
     * Prop constructor
     * <p/>
     * Example:<br>
     * Prop prop = new Prop(new File("/var/config/my_config.txt"), "UTF-8");<br>
     * String userName = prop.get("userName");
     *
     * @param file     the properties File object
     * @param encoding the encoding
     */
    public Prop(File file, String encoding) {
        if (file == null)
            throw new IllegalArgumentException("File can not be null.");
        String fileName = file.getName();
        if (!file.isFile())
            throw new IllegalArgumentException("Not a file : " + fileName);
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            load(fileName, inputStream, encoding);
        } catch (FileNotFoundException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    void load(String fileName, InputStream inputStream, String encoding) {
        if (inputStream == null)
            throw new IllegalArgumentException("Properties file not found in classpath: " + fileName);
        try {
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding == null ? "UTF-8" : encoding));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null) ? value : defaultValue;
    }

    public Integer getInt(String key) {
        String value = get(key);
        return (value != null) ? Integer.parseInt(value) : null;
    }

    public Integer getInt(String key, Integer defaultValue) {
        String value = get(key);
        return (value != null) ? Integer.parseInt(value) : defaultValue;
    }

    public Long getLong(String key) {
        String value = get(key);
        return (value != null) ? Long.parseLong(value) : null;
    }

    public Long getLong(String key, Long defaultValue) {
        String value = get(key);
        return (value != null) ? Long.parseLong(value) : defaultValue;
    }

    public Boolean getBoolean(String key) {
        String value = get(key);
        return (value != null) ? Boolean.parseBoolean(value) : null;
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = get(key);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public Properties getProperties() {
        return properties;
    }

    public static String getRootClassPath() {
        if (rootClassPath == null) {
            try {
                String path = Prop.class.getClassLoader().getResource("").toURI().getPath();
                rootClassPath = new File(path).getAbsolutePath();
            } catch (Exception e) {
                String path = Prop.class.getClassLoader().getResource("").getPath();
                rootClassPath = new File(path).getAbsolutePath();
            }
        }
        return rootClassPath;
    }
}
