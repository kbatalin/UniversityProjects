package pro.batalin.sp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import pro.batalin.sp.model.Symbol;
import pro.batalin.sp.model.SymbolType;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
public class SNavigator {

    private static final Pattern PATTERN_EXP = Pattern.compile("\\[([^\\s]+)\\s([^]]+)]");

    public void set(final Symbol root, final String query, final Symbol newValue) {
        final List<Symbol> nodes = select(root, query);
        nodes.stream()
                .peek(e -> {
                    Preconditions.checkArgument(e.getType() == newValue.getType(), "Different types");
                })
                .forEach(e -> e.setValue(newValue.getValue()));
    }

    public List<Symbol> select(final Symbol root, final String query) {
        Preconditions.checkArgument(root != null);
        Preconditions.checkArgument(query != null);

        final boolean relative = query.startsWith("//");
        final boolean absolute = query.startsWith("/");
        Preconditions.checkArgument(relative || absolute, "Invalid syntax");

        final String subQuery = query.substring(relative ? 2 : 1);
        final String[] parts = subQuery.split("/");

        if (relative) {
            return selectRelative(root, parts);
        } else {
            return selectAbsolute(root, parts, 0);
        }
    }

    private List<Symbol> selectRelative(final Symbol node, final String[] parts) {
        if (node == null) {
            return Collections.emptyList();
        }

        final List<Symbol> symbols = selectAbsolute(node, parts, 0);
        final List<Symbol> result = new ArrayList<>(symbols);

        switch (node.getType()) {
            case ARRAY:
                result.addAll(selectListRelative(node, parts));
                break;

            case MAP:
                result.addAll(selectMapRelative(node, parts));
                break;

            case STRING:
            case FLOAT:
            case BOOLEAN:
            case INTEGER:
                break;

            default:
                throw new IllegalArgumentException("Unknown type");
        }

        return result;
    }

    private List<Symbol> selectListRelative(final Symbol node, final String[] parts) {
        final List<Symbol> children = (List<Symbol>) node.getValue();

        return children.stream()
                .map(e -> selectRelative(e, parts))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Symbol> selectMapRelative(final Symbol node, final String[] parts) {
        final Map<String, Symbol> children = (Map<String, Symbol>) node.getValue();

        return children.values().stream()
                .map(e -> selectRelative(e, parts))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Symbol> selectAbsolute(final Symbol node, final String[] parts, final int current) {
        if (node == null) {
            return Collections.emptyList();
        }
        if (current >= parts.length) {
            return Collections.singletonList(node);
        }

        if (node.getType() == SymbolType.ARRAY) {
            return selectListAbsolute(node, parts, current);
        }

        if (node.getType() == SymbolType.MAP) {
            return selectMapAbsolute(node, parts, current);
        }

        // primitives
        return Collections.emptyList();
    }

    private List<Symbol> selectListAbsolute(final Symbol node, final String[] parts, final int current) {
        final List<Symbol> children = (List<Symbol>) node.getValue();

        return children.stream()
                .map(e -> selectAbsolute(e, parts, current))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Symbol> selectMapAbsolute(final Symbol node, final String[] parts, final int current) {
        final Map<String, Symbol> children = (Map<String, Symbol>) node.getValue();
        final String currentPart = parts[current];

        if ("**".equals(currentPart)) {
            return children.values().stream()
                    .map(e -> List.of(
                            selectAbsolute(e, parts, current),
                            selectAbsolute(e, parts, current + 1)
                    ))
                    .flatMap(Collection::stream)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        final Symbol child = children.get(currentPart);
        return selectAbsolute(child, parts, current + 1);
    }
}

