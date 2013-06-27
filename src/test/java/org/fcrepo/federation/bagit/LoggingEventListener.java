/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.federation.bagit;

import java.util.concurrent.CountDownLatch;

import javax.jcr.observation.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This event listener is just for testing that events are working.
 * 
 * @author Gregory Jansen
 */
public class LoggingEventListener implements
        javax.jcr.observation.EventListener {

    private static final Logger logger = LoggerFactory
            .getLogger(LoggingEventListener.class);

    private final CountDownLatch latch;

    private final EventLogger log;

    public LoggingEventListener(CountDownLatch latch, EventLogger log) {
        this.latch = latch;
        this.log = log;
    }

    @Override
    public void onEvent(javax.jcr.observation.EventIterator events) {
        logger.debug("logging events: " + events.getSize());
        try {
            while (events.hasNext()) {
                Event event = events.nextEvent();
                this.log.log(event.getType(), event.getPath());
                latch.countDown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class EventLogger {

        public void log(int type, String path) {
            logger.debug("event: " + type + " " + path);
        }
    }
}
