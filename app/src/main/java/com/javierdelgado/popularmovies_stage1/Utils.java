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
