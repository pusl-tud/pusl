package de.bp2019.pusl.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CalculationServiceIT {    
    private static final Logger LOGGER = LoggerFactory.getLogger(CalculationServiceIT.class);

    @Test
    public void testRhino(){
        CalculationService rhinoService = new CalculationService();

        String script = "function calculate(grades){";
        script +=       " return grades[0] + grades[1];";
        script +=       "}";

        Object grades[] = {7, "test"};

        LOGGER.info(rhinoService.calculate(script,grades));
    }
}