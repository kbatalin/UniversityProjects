package pro.batalin.sp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import pro.batalin.sp.model.Symbol;
import pro.batalin.sp.model.SymbolType;
import pro.batalin.sp.service.model.Token;
import pro.batalin.sp.service.model.TokenType;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
@AllArgsConstructor
public class SParser implements AutoCloseable {

    private final SLexer lexer;
    private final boolean schema;

    @Override
    public void close() throws IOException {
        lexer.close();
    }

    public Symbol parse() throws IOException {
        Token token = lexer.nextToken();
        if (token == null) {
            return null;
        }
        return parseTypeBlock(token);
    }

    private Symbol parseTypeBlock(final Token startToken) throws IOException {
        // [
        Preconditions.checkArgument(startToken != null, "unexpected end");
        Preconditions.checkArgument(startToken.getType() == TokenType.BLOCK_START, "block start expected");

        // type, value
        final Token valueToken = lexer.nextToken();
        Preconditions.checkArgument(valueToken != null, "unexpected end");
        Preconditions.checkArgument(valueToken.getType() == TokenType.VALUE, "value expected");
        final SymbolType symbolType = SymbolType.byCode(valueToken.getValue());
        final Symbol symbol = Symbol.builder()
                .type(symbolType)
                .value(parseValue(symbolType))
                .build();

        // ]
        final Token endToken = lexer.nextToken();
        Preconditions.checkArgument(endToken != null, "unexpected end");
        Preconditions.checkArgument(endToken.getType() == TokenType.BLOCK_END, "block end expected");

        return symbol;
    }

    private Object parseValue(final SymbolType symbolType) throws IOException {
        switch (symbolType) {
            case MAP:
                return parseMapValues();

            case ARRAY:
                return parseArrayValues();

            case STRING:
                return parsePrimitive(e -> e);

            case FLOAT:
                return parsePrimitive(Double::valueOf);

            case BOOLEAN:
                return parsePrimitive(Boolean::valueOf);

            case INTEGER:
                return parsePrimitive(Long::valueOf);

            default:
                throw new IllegalArgumentException("Bqd type: " + symbolType);
        }
    }

    private Map<String, Symbol> parseMapValues() throws IOException {
        // [
        final Token startToken = lexer.nextToken();
        Preconditions.checkArgument(startToken != null, "unexpected end");
        if (startToken.getType() == TokenType.VALUE && startToken.getValue() == null) {
            return null;
        }
        Preconditions.checkArgument(startToken.getType() == TokenType.BLOCK_START, "block expected");

        // names, values
        final Map<String, Symbol> result = new HashMap<>();
        Token valueToken = null;
        do {
            valueToken = lexer.nextToken();
            Preconditions.checkArgument(valueToken != null, "unexpected end");
            if (valueToken.getType() == TokenType.BLOCK_END) {
                break;
            }

            // [
            Preconditions.checkArgument(valueToken.getType() == TokenType.BLOCK_START, "block start expected");

            valueToken = lexer.nextToken();
            Preconditions.checkArgument(valueToken != null, "unexpected end");
            Preconditions.checkArgument(valueToken.getType() == TokenType.VALUE, "value expected");
            final String name = valueToken.getValue();

            valueToken = lexer.nextToken();
            final Symbol symbol = parseTypeBlock(valueToken);
            result.put(name, symbol);

            valueToken = lexer.nextToken();
            Preconditions.checkArgument(valueToken != null, "unexpected end");
            Preconditions.checkArgument(valueToken.getType() == TokenType.BLOCK_END, "block end expected");
        } while (true);

        return result;
    }

    private List<Symbol> parseArrayValues() throws IOException {
        // [
        final Token startToken = lexer.nextToken();
        Preconditions.checkArgument(startToken != null, "unexpected end");
        if (startToken.getType() == TokenType.VALUE && startToken.getValue() == null) {
            return null;
        }
        Preconditions.checkArgument(startToken.getType() == TokenType.BLOCK_START, "block expected");

        // blocks [...]
        final List<Symbol> result = new ArrayList<>();
        Token valueToken = null;
        do {
            valueToken = lexer.nextToken();
            Preconditions.checkArgument(valueToken != null, "unexpected end");
            if (valueToken.getType() != TokenType.BLOCK_END) {
                final Symbol symbol = parseTypeBlock(valueToken);
                result.add(symbol);
            }
        } while (valueToken.getType() != TokenType.BLOCK_END);

        return result;
    }

    public Object parsePrimitive(final Function<String, Object> mapper) throws IOException {
        if (schema) {
            return null;
        }

        final Token token = lexer.nextToken();
        Preconditions.checkArgument(token != null, "unexpected end");
        Preconditions.checkArgument(token.getType() == TokenType.VALUE, "value expected");
        if (token.getValue() == null) {
            return null;
        }

        return mapper.apply(token.getValue());
    }
}
