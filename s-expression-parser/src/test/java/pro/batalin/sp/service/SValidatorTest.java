package pro.batalin.sp.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Тесты валидатора по схеме.
 *
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
class SValidatorTest {

    /**
     * Соответствие схемы.
     */
    @Test
    void testOk() throws IOException {
        checkSchema("['int']", "['int' '5']");
    }

    /**
     * Не совадает тип.
     */
    @Test
    void testBadType() throws IOException {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> checkSchema("['int']", "['string' 'bla']")
        );
    }

    /**
     * Кривое значение.
     */
    @Test
    void testBadValue() throws IOException {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> checkSchema("['int']", "['int' 'bla']")
        );
    }

    /**
     * Не совпадает тип.
     */
    @Test
    void testBadBooleanValue() throws IOException {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> checkSchema("['str']", "['bool' 'true']")
        );
    }

    /**
     * Не совпадает ключ мапы,
     */
    @Test
    void testBadMap1() throws IOException {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> checkSchema("['map' [ ['first' ['int']] ]]", "['map' [ ['second' ['int']] ]]")
        );
    }

    /**
     * Не совпадает тип ноды мапы.
     */
    @Test
    void testBadMap2() throws IOException {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> checkSchema("['map' [ ['first' ['int']] ]]", "['map' [ ['first' ['string']] ]]")
        );
    }

    /**
     * Невалидный синтаксис мапы.
     */
    @Test
    void testBadMap3() throws IOException {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> checkSchema("['map' [ ['first' ['int']] ]]", "['map' [ ['int' '2'] ]")
        );
    }

    private void checkSchema(final String schema, final String data) throws IOException {
        try (ByteArrayInputStream schemaInput = new ByteArrayInputStream(schema.getBytes());
             ByteArrayInputStream dataInput = new ByteArrayInputStream(data.getBytes())) {
            new SValidator().validate(schemaInput, dataInput);
        }
    }
}
