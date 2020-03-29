package de.bp2019.pusl.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Leon Chemnitz
 */
public class UtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsTest.class);

    
    private interface i1 {};
    private interface i2 {};
    private class c implements i1 {};

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testImplementsInterface() {
        LOGGER.info("Testing implementsInterface");

        LOGGER.info("c implements i1");
        assertTrue(Utils.implementsInterface(c.class, i1.class));
        
        LOGGER.info("c does not implement i2");
        assertFalse(Utils.implementsInterface(c.class, i2.class));

        LOGGER.info("Test successful");
    }

    @Test
    public void testLocalDateToDate(){        
        LOGGER.info("Testing localDateToDate");

    }

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testIsMatrNumber(){
        LOGGER.info("Testing is MatrNumber");

        LOGGER.info("testing null");
        assertFalse(Utils.isMatrNumber(null));

        LOGGER.info("testing random alphabetic");
        assertFalse(Utils.isMatrNumber(RandomStringUtils.randomAlphabetic(7)));

        LOGGER.info("testing existing matrNumber");
        assertTrue(Utils.isMatrNumber("2920560"));

        LOGGER.info("testing faulty matrNumbers");
        assertFalse(Utils.isMatrNumber("123456"));
        assertFalse(Utils.isMatrNumber("1982743523456"));
        assertFalse(Utils.isMatrNumber("1982"));

        LOGGER.info("Test successful");
    }
}