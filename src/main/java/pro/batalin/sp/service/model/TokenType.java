package pro.batalin.sp.service.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
@AllArgsConstructor
@Getter
public enum TokenType {
    BLOCK_START("["),
    BLOCK_END("]"),
    VALUE("'"),
    DELIMITER(",");

    private static final Map<String, TokenType> BY_CODE = Arrays.stream(values())
            .collect(Collectors.toMap(TokenType::getCode, Function.identity()));

    private final String code;

    public static TokenType byCode(final String code) {
        return BY_CODE.get(code);
    }
}
