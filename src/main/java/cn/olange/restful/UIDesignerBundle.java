package cn.olange.restful;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * @author zhoujia
 * @since 2024/10/16 14:51
 */
public class UIDesignerBundle extends DynamicBundle {

    @NonNls
    private static final String BUNDLE = "messages.RestLink";
    private static final UIDesignerBundle INSTANCE = new UIDesignerBundle();

    private UIDesignerBundle() { super(BUNDLE); }

    @NotNull
    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object ... params) {
        return INSTANCE.getMessage(key, params);
    }

}
