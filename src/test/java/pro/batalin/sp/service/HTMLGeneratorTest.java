package pro.batalin.sp.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.batalin.sp.model.Symbol;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
class HTMLGeneratorTest {

    /**
     * Задаем дерево.
     * Задаем xpath для поиска нужных элементов дерева.
     * Выводим в html элементы, которые нашлись по xpath.
     * Сравниваем с эталоном resources/pro/batalin/sp/service/html1.html.
     */
    @Test
    void testSimple() throws IOException {
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
        check(str, "html1.html", "/first", "/first/**/third");
    }

    private void check(final String data, final String expected, final String... sPaths) throws IOException {
        try (SParser parser = new SParser(new SLexer(new ByteArrayInputStream(data.getBytes())), false)) {
            final Symbol symbol = parser.parse();
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            new HtmlGenerator().generate(output, symbol, Arrays.asList(sPaths));
            final String result = new String(output.toByteArray(), StandardCharsets.UTF_8);

            final String expectedHTML = IOUtils.toString(getClass().getResource(expected), StandardCharsets.UTF_8);
            Assertions.assertEquals(expectedHTML.trim(), result.trim());
        }
    }
}
