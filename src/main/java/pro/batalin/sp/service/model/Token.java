package pro.batalin.sp.service.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Token {
    private final TokenType type;
    private final String value;
}
