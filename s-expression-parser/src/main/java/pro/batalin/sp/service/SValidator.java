package pro.batalin.sp.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import pro.batalin.sp.model.Symbol;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
public class SValidator {

    public void validate(final InputStream schemaInput, final InputStream dataInput) throws IOException {
        try (SParser dataParser = new SParser(new SLexer(dataInput), false);
             SParser schemaParser = new SParser(new SLexer(schemaInput), true)) {
            final Symbol data = dataParser.parse();
            final Symbol schema = schemaParser.parse();
            validate(schema, data);
        }
    }

    private void validate(final Symbol schema, final Symbol data) {
        Preconditions.checkArgument(schema != null, "schema is required");
        Preconditions.checkArgument(data != null, "data is required");

        Preconditions.checkArgument(schema.getType() == data.getType(),
                "different node types. Schema: %s, data: %s", schema.getType(), data.getType());

        switch (schema.getType()) {
            case MAP:
                validateMap(schema, data);
                break;

            case ARRAY:
                validateArray(schema, data);
                break;

            case INTEGER:
            case BOOLEAN:
            case FLOAT:
            case STRING:
                // do nothing. already checked in sParser
                break;

            default:
                throw new IllegalArgumentException("Invalid type: " + schema.getType());
        }
    }

    private void validateMap(final Symbol schema, final Symbol data) {
        final Map<String, Symbol> schemaMap = (Map<String, Symbol>) schema.getValue();
        final Map<String, Symbol> dataMap = (Map<String, Symbol>) data.getValue();

        Preconditions.checkArgument(schemaMap.keySet().equals(dataMap.keySet()),
                "Different keys. Schema: %s, data: %s", schemaMap.keySet(), dataMap.keySet());

        schemaMap.forEach((k, v) -> {
            final Symbol dataV = dataMap.get(k);
            validate(v, dataV);
        });
    }

    private void validateArray(final Symbol schema, final Symbol data) {
        final List<Symbol> schemaList = (List<Symbol>) schema.getValue();
        final List<Symbol> dataList = (List<Symbol>) schema.getValue();

        Preconditions.checkArgument(schemaList.size() == dataList.size(),
                "Different lists size. Schema: %s, data: %s", schemaList.size(), dataList.size());

        IntStream.range(0, schemaList.size()).forEach(i -> {
            validate(schemaList.get(i), dataList.get(i));
        });
    }
}
