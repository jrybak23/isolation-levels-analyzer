package com.example.integrationtests;

import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareResult;

import static org.skyscreamer.jsonassert.JSONCompareMode.STRICT;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class JSONAssertUtil {
    private static final int INDENT_SPACES = 2;

    /**
     * asserts JSON strings regardless formatting and order of fields
     */
    public static void assertJSON(String expectedJSON, String actualJSON) {
        JSONCompareResult result = compareJSON(expectedJSON, actualJSON);
        if (result.failed()) {
            //compare via JUnit assertEquals for better side-by-side comparison
            assertEquals(result.getMessage(), expectedJSON, pretty(actualJSON));
        }
    }

    private static JSONCompareResult compareJSON(String expectedJSON, String actualJSON) {
        try {
             return JSONCompare.compareJSON(expectedJSON, actualJSON, STRICT);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private static String pretty(String actualJSON) {
        if (isObject(actualJSON)) {
            return new JSONObject(actualJSON).toString(INDENT_SPACES);
        } else if (isArray(actualJSON)) {
            return new JSONArray(actualJSON).toString(INDENT_SPACES);
        } else {
            throw new RuntimeException("Unable to check if JSON object or array: " + actualJSON);
        }
    }

    private static boolean isArray(String actualJSON) {
        return actualJSON.charAt(0) == '[';
    }

    private static boolean isObject(String actualJSON) {
        return actualJSON.charAt(0) == '{';
    }

    private JSONAssertUtil() {
    }
}
