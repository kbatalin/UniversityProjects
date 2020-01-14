package pro.batalin.sp.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Kirill Batalin (batalin@yandex-team.ru)
 */
@Data
@Builder
@EqualsAndHashCode
@ToString
public class Symbol {
    private SymbolType type;
    private Object value;
}
