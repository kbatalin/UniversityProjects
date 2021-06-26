package pro.batalin.sp.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.batalin.sp.model.Symbol;
import pro.batalin.sp.model.SymbolType;

/**
 * Тесты парсера.
 *
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
class SParserTest {

    /**
     * Парсинг int.
     */
    @Test
    void testInt() throws IOException {
        final Symbol expected = Symbol.builder().type(SymbolType.INTEGER).value(4L).build();
        checkSymbols("['int' '4']", expected);
    }

    /**
     * Парсинг float.
     */
    @Test
    void testFloat() throws IOException {
        final Symbol expected = Symbol.builder().type(SymbolType.FLOAT).value(4.4).build();
        checkSymbols("['float' '4.4']", expected);
    }

    /**
     * Парсинг null float.
     */
    @Test
    void testNullFloat() throws IOException {
        final Symbol expected = Symbol.builder().type(SymbolType.FLOAT).value(null).build();
        checkSymbols("['float' null]", expected);
    }

    /**
     * Парсинг null map.
     */
    @Test
    void testNullMap() throws IOException {
        final Symbol expected = Symbol.builder().type(SymbolType.MAP).value(null).build();
        checkSymbols("['map' null]", expected);
    }

    /**
     * Парсинг null array.
     */
    @Test
    void testNullArray() throws IOException {
        final Symbol expected = Symbol.builder().type(SymbolType.ARRAY).value(null).build();
        checkSymbols("['arr' null]", expected);
    }

    /**
     * Парсинг float.
     */
    @Test
    void testNull() throws IOException {
        final Symbol expected = Symbol.builder().type(SymbolType.FLOAT).value(4.4).build();
        checkSymbols("['float' '4.4']", expected);
    }

    /**
     * Парсинг boolean.
     */
    @Test
    void testBoolean() throws IOException {
        final Symbol expected = Symbol.builder().type(SymbolType.BOOLEAN).value(true).build();
        checkSymbols("['bool' 'true']", expected);
    }

    /**
     * Парсинг string.
     */
    @Test
    void testString() throws IOException {
        final Symbol expected = Symbol.builder().type(SymbolType.STRING).value("blabla").build();
        checkSymbols("['str' 'blabla']", expected);
    }

    /**
     * Парсинг array.
     */
    @Test
    void testArray() throws IOException {
        final Symbol first = Symbol.builder().type(SymbolType.INTEGER).value(3L).build();
        final Symbol second = Symbol.builder().type(SymbolType.STRING).value("bla").build();
        final List<Symbol> values = new ArrayList<>();
        values.add(first);
        values.add(second);
        final Symbol root = Symbol.builder().type(SymbolType.ARRAY).value(values).build();
        checkSymbols("['arr' [['int' '3'] ['str' 'bla']]]", root);
    }

    /**
     * Парсинг map.
     */
    @Test
    void testMap() throws IOException {
        final Symbol first = Symbol.builder().type(SymbolType.INTEGER).value(3L).build();
        final Symbol second = Symbol.builder().type(SymbolType.STRING).value("bla").build();
        final Map<String, Symbol> values = new HashMap<>();
        values.put("first", first);
        values.put("second", second);
        final Symbol root = Symbol.builder().type(SymbolType.MAP).value(values).build();
        checkSymbols("['map' [  ['first' ['int' '3']]  ['second' ['str' 'bla']] ]]", root);
    }

    /**
     * Парсинг всего вместе.
     */
    @Test
    void testIgorya() throws IOException {
        final Symbol pinch = Symbol.builder().type(SymbolType.STRING).value("персик").build();
        final Symbol burningAss = Symbol.builder().type(SymbolType.STRING).value("моё пылающее очко").build();
        final List<Symbol> arrValues = new ArrayList<>();
        arrValues.add(pinch);
        arrValues.add(burningAss);
        final Symbol arr = Symbol.builder().type(SymbolType.ARRAY).value(arrValues).build();
        final Symbol hateIt = Symbol.builder().type(SymbolType.BOOLEAN).value(true).build();

        final Map<String, Symbol> mapValues = new HashMap<>();
        mapValues.put("fruits", arr);
        mapValues.put("zaebalo", hateIt);
        final Symbol root = Symbol.builder().type(SymbolType.MAP).value(mapValues).build();
        checkSymbols("['map' [ ['fruits' ['arr' [ ['str' 'персик'] ['str' 'моё пылающее очко'] ] ]]  ['zaebalo' ['bool' 'true']]   ]]", root);
    }

    private void checkSymbols(final String inputStr, final Symbol expected) throws IOException {
        try (SParser parser = new SParser(new SLexer(new ByteArrayInputStream(inputStr.getBytes())), false)) {
            final Symbol actual = parser.parse();
            Assertions.assertEquals(expected, actual);
        }
    }

    /**
     * Парсинг схемы.
     */
    @Test
    void testSchema() throws IOException {
        final Symbol pinch = Symbol.builder().type(SymbolType.STRING).value(null).build();
        final Symbol burningAss = Symbol.builder().type(SymbolType.STRING).value(null).build();
        final List<Symbol> arrValues = new ArrayList<>();
        arrValues.add(pinch);
        arrValues.add(burningAss);
        final Symbol arr = Symbol.builder().type(SymbolType.ARRAY).value(arrValues).build();
        final Symbol hateIt = Symbol.builder().type(SymbolType.BOOLEAN).value(null).build();

        final Map<String, Symbol> mapValues = new HashMap<>();
        mapValues.put("fruits", arr);
        mapValues.put("zaebalo", hateIt);
        final Symbol root = Symbol.builder().type(SymbolType.MAP).value(mapValues).build();
        checkSchema("['map' [ ['fruits' ['arr' [ ['str'] ['str'] ] ]]  ['zaebalo' ['bool']]   ]]", root);
    }

    private void checkSchema(final String inputStr, final Symbol expected) throws IOException {
        try (SParser parser = new SParser(new SLexer(new ByteArrayInputStream(inputStr.getBytes())), true)) {
            final Symbol actual = parser.parse();
            Assertions.assertEquals(expected, actual);
        }
    }
}
