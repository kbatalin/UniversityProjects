package pro.batalin.sp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.common.base.Preconditions;
import pro.batalin.sp.service.model.Token;
import pro.batalin.sp.service.model.TokenType;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
public class SLexer implements AutoCloseable {

    private final Reader reader;
    private char[] lastChar = new char[1];

    public SLexer(final InputStream input) {
        this.reader = new BufferedReader(new InputStreamReader(input));
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    public Token nextToken() throws IOException {
        Character current = nextNotSpaceChar();
        if (current == null) {
            return null;
        }

        final boolean withQuotes = current == '\'';

        final TokenType tokenType = TokenType.byCode(String.valueOf(current));
        if (tokenType != null && !withQuotes) {
            return new Token(tokenType, String.valueOf(current));
        }

        final StringBuilder builder = new StringBuilder().append(current);
        do {
            current = nextChar();
            if (current == null) {
                throw new IllegalArgumentException("close quote");
            }

            if (current == '\\') {
                current = nextChar();
                if (current == null) {
                    throw new IllegalArgumentException("close quote");
                }
            }

            builder.append(current);
        } while (needContinue(withQuotes, current, builder));

        if (withQuotes) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(0);
        }

        final String result = builder.toString();
        if (!withQuotes && "null".equals(result)) {
            return new Token(TokenType.VALUE, null);
        }

        return new Token(TokenType.VALUE, result);
    }

    private boolean needContinue(boolean withQuotes, char current, StringBuilder builder) {
        if (withQuotes) {
            return current != '\'';
        }

        if ("null".equals(builder.toString())) {
            return false;
        }

        Preconditions.checkArgument(!Character.isWhitespace(current), "unknown token");
        return true;
    }

    private Character nextNotSpaceChar() throws IOException {
        Character current = null;
        do {
            current = nextChar();
        } while (current != null && Character.isWhitespace(current));

        return current;
    }

    private Character nextChar() throws IOException {
        if (reader.read(lastChar) == -1) {
            return null;
        }

        return lastChar[0];
    }
}
