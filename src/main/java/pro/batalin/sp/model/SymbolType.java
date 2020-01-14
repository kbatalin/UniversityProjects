package pro.batalin.sp.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
@Getter
@AllArgsConstructor
public enum SymbolType {

    STRING("str"),
    BOOLEAN("bool"),
    INTEGER("int"),
    FLOAT("float"),
    ARRAY("arr"),
    MAP("map");

    private static final Map<String, SymbolType> BY_CODE = Arrays.stream(values())
            .collect(Collectors.toMap(SymbolType::getCode, Function.identity()));

    private final String code;

    public static SymbolType byCode(final String code) {
        final SymbolType type = BY_CODE.get(code);
        Preconditions.checkArgument(type != null, "Bad type: " + code);
        return type;
    }
}
