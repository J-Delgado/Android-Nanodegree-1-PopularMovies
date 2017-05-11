/*
 * Copyright (C) 2017 Javier Delgado
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

package com.javierdelgado.popularmovies_stage1;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Delga on 03/05/2017.
 */

public class Utils {

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param stream The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getString(InputStream stream) {

        Scanner scanner = new Scanner(stream);
        scanner.useDelimiter("\\A");

        boolean hasInput = scanner.hasNext();
        if (hasInput) {
            return scanner.next();
        } else {
            return null;
        }
    }

}
