package pro.batalin.sp.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.batalin.sp.model.Symbol;
import pro.batalin.sp.model.SymbolType;

/**
 * Проверка аналога xpath.
 *
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
class SNavigatorTest {

    /**
     * Достаем первый элемент по абсолютному пути.
     */
    @Test
    void testSimple() throws IOException {
        final Symbol expected = Symbol.builder().type(SymbolType.INTEGER).value(3L).build();
        check("['map' [ ['first' ['int' '3']] ]]", "/first", expected);
    }

    /**
     * Достаем несуществующий элемент.
     */
    @Test
    void testBad1Simple() throws IOException {
        check("['map' [ ['first' ['int' '3']] ]]", "/first/second");
    }

    /**
     * Достаем все.
     */
    @Test
    void testBad2Simple() throws IOException {
        final Symbol expected = Symbol.builder().type(SymbolType.INTEGER).value(3L).build();
        check("['map' [ ['first' ['int' '3']] ]]", "/**", expected);
    }

    /**
     * xpath с переменной глубиной.
     */
    @Test
    void testMap() throws IOException {
        final String str = "['map' [\n" +
                "    ['first' ['arr' [\n" +
                "            ['int' '4']\n" +
                "            ['map' [\n" +
                "                ['second' ['map' [\n" +
                "                        ['third' ['str' 'bla']]\n" +
                "                    ]\n" +
                "                ]]\n" +
                "            ]]\n" +
                "        ]\n" +
                "    ]]\n" +
                "]]";
        final Symbol expected = Symbol.builder().type(SymbolType.STRING).value("bla").build();
        check(str, "/first/**/third", expected);
    }

    /**
     * Модификация значения по xpath.
     */
    @Test
    void testSet() throws IOException {
        final String str = "['map' [\n" +
                "    ['first' ['arr' [\n" +
                "            ['int' '4']\n" +
                "            ['map' [\n" +
                "                ['second' ['map' [\n" +
                "                        ['third' ['str' 'bla']]\n" +
                "                    ]\n" +
                "                ]]\n" +
                "            ]]\n" +
                "        ]\n" +
                "    ]]\n" +
                "]]";
        final Symbol value = Symbol.builder().type(SymbolType.STRING).value("bla2").build();

        final HashMap<String, Symbol> secondMap = new HashMap<>();
        secondMap.put("third", value);
        final Symbol second = Symbol.builder().type(SymbolType.MAP).value(secondMap).build();
        final Symbol intV = Symbol.builder().type(SymbolType.INTEGER).value(4L).build();
        final HashMap<String, Symbol> firstMap = new HashMap<>();
        firstMap.put("second", second);
        final Symbol mapV = Symbol.builder().type(SymbolType.MAP).value(firstMap).build();
        final ArrayList<Symbol> firstList = new ArrayList<>();
        firstList.add(intV);
        firstList.add(mapV);
        final Symbol first = Symbol.builder().type(SymbolType.ARRAY).value(firstList).build();
        final HashMap<String, Symbol> rootMap = new HashMap<>();
        rootMap.put("first", first);
        final Symbol root = Symbol.builder().type(SymbolType.MAP).value(rootMap).build();

        checkSet(str, "/first/**/third", value, root);
    }

    /**
     * Несколько нод подходит под один путь.
     */
    @Test
    void testMap2() throws IOException {
        final String str = "['map' [\n" +
                "    ['first' ['arr' [\n" +
                "            ['int' '4']\n" +
                "            ['map' [\n" +
                "                ['second' ['map' [\n" +
                "                        ['second' ['str' 'bla']]\n" +
                "                    ]\n" +
                "                ]]\n" +
                "            ]]\n" +
                "        ]\n" +
                "    ]]\n" +
                "]]";
        final Symbol expected1 = Symbol.builder().type(SymbolType.STRING).value("bla").build();
        final Map<String, Symbol> mapVal = new HashMap<>();
        mapVal.put("second", expected1);
        final Symbol expected2 = Symbol.builder().type(SymbolType.MAP).value(mapVal).build();
        check(str, "/**/second", expected1, expected2);
    }

    /**
     * Относительный путь.
     */
    @Test
    void testRelative1() throws IOException {
        final String str = "['map' [\n" +
                "    ['first' ['arr' [\n" +
                "            ['int' '4']\n" +
                "            ['map' [\n" +
                "                ['second' ['map' [\n" +
                "                        ['third' ['str' 'bla']]\n" +
                "                    ]\n" +
                "                ]]\n" +
                "            ]]\n" +
                "        ]\n" +
                "    ]]\n" +
                "]]";
        final Symbol expected = Symbol.builder().type(SymbolType.STRING).value("bla").build();
        check(str, "//third", expected);
    }

    private void check(final String data, final String sPath, final Symbol... expected) throws IOException {
        try (SParser parser = new SParser(new SLexer(new ByteArrayInputStream(data.getBytes())), false)) {
            final Symbol symbol = parser.parse();
            final List<Symbol> actual = new SNavigator().select(symbol, sPath);
            Assertions.assertEquals(expected.length, actual.size());

            final Set<Symbol> actualSet = new HashSet<>(actual);
            Arrays.stream(expected).forEach(e -> {
                Assertions.assertTrue(actualSet.contains(e));
            });
        }
    }

    private void checkSet(final String data, final String sPath, final Symbol value, final Symbol expected) throws IOException {
        try (SParser parser = new SParser(new SLexer(new ByteArrayInputStream(data.getBytes())), false)) {
            final Symbol symbol = parser.parse();
            new SNavigator().set(symbol, sPath, value);
            Assertions.assertEquals(expected, symbol);
        }
    }
}
