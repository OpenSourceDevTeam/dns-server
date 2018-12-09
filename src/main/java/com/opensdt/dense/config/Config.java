/*
 * Copyright (c) 2018 OpenSourceDevTeam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opensdt.dense.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class Config {

    private static Logger logger = LoggerFactory.getLogger(Config.class);

    private static Config instance = new Config();

    private Properties properties;

    private Config() {
        try {
            properties = new Properties();
            properties.load(new FileInputStream(new File("config.properties")));
        } catch (IOException e) {
            logger.warn("Unable to load config.properties file. Using default values if possible");
        }
    }

    public static String getString(String key, String def) {
        String property = instance.properties.getProperty(key);
        if (property == null) {
            property = System.getProperty(key);
        }

        return property != null ? property : def;
    }

    public static int getInteger(String key, int def) {
        String property = instance.properties.getProperty(key);
        if (property == null) {
            property = System.getProperty(key);
        }

        try {
            return Integer.parseInt(property);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
