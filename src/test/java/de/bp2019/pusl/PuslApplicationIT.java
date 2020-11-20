package de.bp2019.pusl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Leon Chemnitz
 */
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class PuslApplicationIT {

	/**
	 * @author Leon Chemnitz
	 */
	@Test
	void contextLoads() {
		assert (true);
	}

}
