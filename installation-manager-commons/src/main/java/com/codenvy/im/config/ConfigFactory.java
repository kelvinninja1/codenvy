/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.im.config;

import com.codenvy.im.install.CdecInstallOptions;
import com.codenvy.im.install.DefaultOptions;
import com.codenvy.im.install.InstallOptions;
import com.google.inject.Singleton;

import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.copy;

/** @author Dmytro Nochevnov */
@Singleton
public class ConfigFactory {
    public static final String CDEC_SINGLE_NODE_PROPERTIES_FILE = "cdec-single-node.properties";

    private final String configPath;

    public ConfigFactory(@Named("installation-manager.config.path") String configPath) {
        this.configPath = configPath;

    }
    /**
     * Config factory.
     * If config file doesn't exist, create it with default content from file [classResourceDir]/[defaultConfigFileRelativePath]
     *
     * @throws java.io.IOException
     *         in case of the I/O error
     * @throws IllegalArgumentException
     *         unknown class of {@link com.codenvy.im.install.InstallOptions}
     */
    public Config loadOrCreateDefaultConfig(InstallOptions installOptions) throws IOException, IllegalArgumentException {
        // TODO validate config

        if (installOptions instanceof DefaultOptions) {
            return new DefaultConfig();

        } else if (installOptions instanceof CdecInstallOptions) {
            String propertiesFile = CDEC_SINGLE_NODE_PROPERTIES_FILE;

            if (!exists(propertiesFile)) {
                createDefaultConfig(propertiesFile);
            }
            return new CdecConfig(loadConfig(propertiesFile));
        }

        throw new IllegalArgumentException("There is no configuration for " + installOptions.getClass().getName());
    }

    private boolean exists(String propertiesFile) {
        return Files.exists(Paths.get(configPath).resolve(propertiesFile));
    }

    /** Creates config from the template. */
    private void createDefaultConfig(String propertiesFile) throws IOException {
        Path confFile = getConfFile(propertiesFile);
        Files.createDirectories(confFile.getParent());

        try (InputStream in = ConfigFactory.class.getClassLoader().getResourceAsStream(propertiesFile);
             OutputStream out = Files.newOutputStream(confFile)) {

            copy(in, out);
        }
    }

    private Path getConfFile(String propertiesFile) {
        return Paths.get(configPath).resolve(propertiesFile);
    }

    protected Map<String, String> loadConfig(String propertiesFile) throws ConfigException {
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(getConfFile(propertiesFile))) {
            properties.load(in);
        } catch (IOException e) {
            throw new ConfigException(format("Can't load properties: %s", e.getMessage()), e);
        }

        Map<String, String> propertiesCandidate = new HashMap<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString().toLowerCase();
            String value = (String)entry.getValue();

            propertiesCandidate.put(key, value);
        }

        return propertiesCandidate;
    }
}