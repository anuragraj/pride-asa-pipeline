/* 
 * Copyright 2018 compomics.
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
package com.compomics.pride_asa_pipeline.core.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Kenneth Verheggen
 */
public class PrideWebserviceUtils {

    static final Pattern projectPattern = Pattern.compile("P[X,R]D[0-9]{6}");

    public static boolean ValidateAccession(String project) {
        Matcher m = projectPattern.matcher(project);
        return m.matches();
    }

    public static boolean isWebServiceReachable() {
        try {
            //make a URL to a known source
            URL url = new URL("https://www.ebi.ac.uk:443/pride/ws/archive");

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();

        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
