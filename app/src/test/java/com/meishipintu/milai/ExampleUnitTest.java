package com.meishipintu.milai;

import android.util.Log;
import com.meishipintu.milai.utils.NumUtil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String result = NumUtil.NumberFormatAuto(100.0);
        assertEquals(result, "33.3");
    }


}