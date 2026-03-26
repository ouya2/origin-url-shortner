package com.origin.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class ShortCodeGeneratorTest {
    
    private final ShortCodeGenerator generator = new ShortCodeGenerator();

    @Test
    void generate_shouldReturnCodeWithExpectedLength() {
        String code = generator.generate();

        assertThat(code).hasSize(6);
    }

    @Test
    void generate_shouldReturnOnlyBase62Characters() {
        String code = generator.generate();

        assertThat(code).matches("^[0-9A-Za-z]{6}$");
    }

    @Test
    void generate_shouldProduceDifferentCodesAcrossCalls() {
        String first = generator.generate();
        String second = generator.generate();

        assertThat(first).isNotEqualTo(second);
    }
}
