package de.bp2019.pusl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UnitTest for {@link LimitOffsetPageRequest}
 * 
 * @author Leon Chemnitz
 */
public class LimitOffsetPageRequestTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LimitOffsetPageRequestTest.class);

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testConstructor(){
        LOGGER.info("Testing Constructor");

        assertThrows(IllegalArgumentException.class, () -> new LimitOffsetPageRequest(0, 100));
        LOGGER.info("limit less than one checked");

        assertThrows(IllegalArgumentException.class, () -> new LimitOffsetPageRequest(100, -1));
        LOGGER.info("negative offset checked");
    }

    /**
     * @author Leon Chemnitz
     */
    @Test
    public void testPagination(){
        LOGGER.info("Testing pagination");

        LimitOffsetPageRequest lopr = new LimitOffsetPageRequest(10, 100);

        assertEquals(10, lopr.getPageNumber());
        LOGGER.info("page number checked");

        assertEquals(true, lopr.hasPrevious());
        LOGGER.info("has previous checked");

        lopr.next();
        lopr.previous();
        lopr.first();
        lopr.previousOrFirst();
    }
}