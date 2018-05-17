/*
 * Copyright 2018 davy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.compomics.pride_asa_pipeline.core.gui;

import com.compomics.pride_asa_pipeline.core.gui.controller.PipelineProgressController;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author davy
 */
public class PipelineProgressMonitor {

    private static PipelineProgressController pipelineProgressController;
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    public static void setPipelineProgressController(PipelineProgressController pipelineProgressController) {
        PipelineProgressMonitor.pipelineProgressController = pipelineProgressController;
    }

    public static void LogProcess(final String message, Level logLevel) {
        GetLogger().log(logLevel, message);
        if (pipelineProgressController != null) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    pipelineProgressController.setProgressInfoText(message);
                }
            });
        }
    }

    public static void error(Throwable ex) {
        GetLogger().error(ex);
    }

    public static void error(String message, Throwable ex) {
        GetLogger().error(message, ex);
    }

    public static void error(String message) {
        LogProcess(message, Level.ERROR);
    }

    public static void info(String message) {
        LogProcess(message, Level.INFO);
    }

    public static void debug(String message) {
        LogProcess(message, Level.DEBUG);
    }

    public static void warn(String message) {
        LogProcess(message, Level.WARN);
    }

    public static void fatal(String message) {
        LogProcess(message, Level.FATAL);
    }

    private static Logger GetLogger() {
        //TODO figure out if this is really the best way...
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String clazzName = stackTrace[3].getClassName();
        try {
            return Logger.getLogger(Class.forName(clazzName));
        } catch (ClassNotFoundException e) {
            return Logger.getLogger(PipelineProgressMonitor.class);
        }
    }

}
