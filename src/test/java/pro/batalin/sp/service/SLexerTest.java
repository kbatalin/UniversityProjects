package pro.batalin.sp.service;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.batalin.sp.service.model.Token;
import pro.batalin.sp.service.model.TokenType;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
class SLexerTest {

    /**
     * Проверка работы лексера.
     * Пробельные символы между лексемами игнорируются.
     */
    @Test
    void testSimple() throws Exception {
        final List<Token> expected = List.of(
                new Token(TokenType.BLOCK_START, "["),
                new Token(TokenType.VALUE, "map"),
                new Token(TokenType.DELIMITER, ","),
                new Token(TokenType.BLOCK_START, "["),
                new Token(TokenType.BLOCK_END, "]"),
                new Token(TokenType.BLOCK_END, "]")
        );
        checkTokens("['map', []]", expected);
        checkTokens("  [ 'map'  ,[  ]  ]  ", expected);
    }

    /**
     * Проверка работы лексера.
     * Пробельные символы внутри кавычек не игнорируются.
     */
    @Test
    void testSpaceInQuotes() throws Exception {
        final List<Token> expected = List.of(
                new Token(TokenType.BLOCK_START, "["),
                new Token(TokenType.VALUE, " ma  p"),
                new Token(TokenType.DELIMITER, ","),
                new Token(TokenType.BLOCK_START, "["),
                new Token(TokenType.BLOCK_END, "]"),
                new Token(TokenType.BLOCK_END, "]")
        );
        checkTokens("[' ma  p', \n [ \n]]", expected);
    }

    /**
     * null парсится как null.
     */
    @Test
    void testNull() throws Exception {
        final List<Token> expected = List.of(
                new Token(TokenType.BLOCK_START, "["),
                new Token(TokenType.VALUE, "int"),
                new Token(TokenType.VALUE, null),
                new Token(TokenType.BLOCK_END, "]")
        );
        checkTokens("['int' null]", expected);
    }

    private void checkTokens(final String inputStr, final List<Token> expected) throws IOException {
        try (SLexer parser = new SLexer(new ByteArrayInputStream(inputStr.getBytes()))) {
            final List<Token> actual = new ArrayList<>();
            Token token = null;
            while ((token = parser.nextToken()) != null) {
                actual.add(token);
            }
            Assertions.assertEquals(expected, actual);
        }
    }
}
