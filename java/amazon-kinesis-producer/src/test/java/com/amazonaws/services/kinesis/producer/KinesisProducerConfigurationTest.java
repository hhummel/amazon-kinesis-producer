// Copyright 2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// Licensed under the Amazon Software License (the "License").
// You may not use this file except in compliance with the License.
// A copy of the License is located at
//
//  http://aws.amazon.com/asl
//
// or in the "license" file accompanying this file. This file is distributed
// on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
// express or implied. See the License for the specific language governing
// permissions and limitations under the License.

package com.amazonaws.services.kinesis.producer;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KinesisProducerConfigurationTest {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(KinesisProducerConfigurationTest.class);
    
    private static String writeFile(String contents) {
        try {
            File f = File.createTempFile(UUID.randomUUID().toString(), "");
            f.deleteOnExit();
            FileUtils.write(f, contents);
            return f.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String writeFile(Properties p) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            p.store(baos, "");
            baos.close();
            return writeFile(new String(baos.toByteArray(), "UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void loadString() {
        Properties p = new Properties();
        String v = UUID.randomUUID().toString();
        p.setProperty("MetricsNamespace", v);
        KinesisProducerConfiguration cfg = KinesisProducerConfiguration.fromPropertiesFile(writeFile(p));
        assertEquals(v, cfg.getMetricsNamespace());
    }
    
    @Test
    public void loadLong() {
        KinesisProducerConfiguration defaultConfig = new KinesisProducerConfiguration();
        Properties p = new Properties();
        long v = defaultConfig.getConnectTimeout() + 1;
        p.setProperty("ConnectTimeout", Long.toString(v));
        KinesisProducerConfiguration cfg = KinesisProducerConfiguration.fromPropertiesFile(writeFile(p));
        assertEquals(v, cfg.getConnectTimeout());
    }
    
    @Test
    public void loadBoolean() {
        KinesisProducerConfiguration defaultConfig = new KinesisProducerConfiguration();
        Properties p = new Properties();
        boolean v = !defaultConfig.isVerifyCertificate();
        p.setProperty("VerifyCertificate", Boolean.toString(v));
        KinesisProducerConfiguration cfg = KinesisProducerConfiguration.fromPropertiesFile(writeFile(p));
        assertEquals(v, cfg.isVerifyCertificate());
    }
    
    @Test
    public void unknownProperty() { 
        Properties p = new Properties();
        p.setProperty("xcdfndetnedtne5tje45", "Sfbsfrne34534");
        KinesisProducerConfiguration.fromPropertiesFile(writeFile(p));
        // should not throw exception
    }
}
