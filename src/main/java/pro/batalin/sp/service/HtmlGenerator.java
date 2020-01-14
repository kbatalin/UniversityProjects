package pro.batalin.sp.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import pro.batalin.sp.model.Symbol;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
public class HtmlGenerator {

    public static final String TEMPLATE_FILE = "template.html";
    public static final String TABLE_PLACEHOLDER = "<table-placeholder/>";

    private final SNavigator sNavigator = new SNavigator();

    public void generate(final OutputStream output, final Symbol symbol, final List<String> sPaths) throws IOException {
        final URL templateFile = getClass().getResource(TEMPLATE_FILE);
        final String template = IOUtils.toString(templateFile, StandardCharsets.UTF_8);


        final Map<String, List<Symbol>> symbols = sPaths.stream()
                .collect(Collectors.toMap(Function.identity(), e -> sNavigator.select(symbol, e),
                        (a, b) -> a, LinkedHashMap::new));

        final String table = generateTable(symbols);
        final String result = template.replace(TABLE_PLACEHOLDER, table);
        IOUtils.write(result, output, StandardCharsets.UTF_8);
    }

    private String generateTable(final Map<String, List<Symbol>> symbols) {
        return symbols.entrySet().stream()
                .map(e -> String.format("    <tr><td>%s</td><td>%s</td></tr>", e.getKey(), symbolsToString(e.getValue())))
                .collect(Collectors.joining("\n", "<table border=\"solid black\">\n", "\n</table>"));
    }

    private String symbolsToString(final List<Symbol> nodes) {
        return nodes.stream()
                .map(this::symbolToString)
                .collect(Collectors.joining("<br/>"));
    }

    private String symbolToString(final Symbol node) {
        if (node == null) {
            return "null";
        }

        switch (node.getType()) {
            case INTEGER:
            case BOOLEAN:
            case FLOAT:
            case STRING:
                return String.valueOf(node.getValue());

            case ARRAY:
                return "[<br/>" + symbolsToString((List<Symbol>) node.getValue()) + "<br/>]";

            case MAP:
                return "{" + (((Map<String, Symbol>) node.getValue()).entrySet().stream().map(e -> e.getKey() + ": " + symbolToString(e.getValue())).collect(Collectors.joining("<br/>"))) + "}";

            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }
}
