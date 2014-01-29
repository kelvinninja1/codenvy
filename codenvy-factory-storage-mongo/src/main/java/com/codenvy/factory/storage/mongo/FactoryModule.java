/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
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
package com.codenvy.factory.storage.mongo;

import com.codenvy.api.factory.FactoryStore;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.factory.MongoDbConfiguration;
import com.codenvy.factory.storage.InMemoryFactoryStore;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@DynaModule
public class FactoryModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(FactoryModule.class);

    @Override
    protected void configure() {
        bind(FactoryStore.class).toInstance(getFactoryStore());
    }

    private FactoryStore getFactoryStore() {

        if (System.getProperty("codenvy.local.conf.dir") != null) {
            File dbSettings =
                    new File(System.getProperty("codenvy.local.conf.dir"), "old/factory-storage-configuration.json");
            if (dbSettings.exists() && !dbSettings.isDirectory()) {
                try (InputStream is = new FileInputStream(dbSettings)) {
                    MongoDbConfiguration mConf = JsonHelper.fromJson(is, MongoDbConfiguration.class, null);
                    return new MongoDBFactoryStore(mConf);
                } catch (IOException | JsonParseException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                    throw new RuntimeException(
                            "Invalid mongo database configuration : " + dbSettings.getAbsolutePath());
                }

            }
        }
        LOG.warn("Persistent storage configuration not found, inmemory impl will be used.");
        return new InMemoryFactoryStore();
    }
}
