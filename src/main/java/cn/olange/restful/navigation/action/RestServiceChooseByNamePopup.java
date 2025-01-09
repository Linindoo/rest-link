package cn.olange.restful.navigation.action;

import com.intellij.ide.util.gotoByName.ChooseByNameItemProvider;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import cn.olange.restful.utils.ToolkitUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RestServiceChooseByNamePopup extends ChooseByNamePopup {
    public static final Key<RestServiceChooseByNamePopup> CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY = new Key<>("ChooseByNamePopup");

    protected RestServiceChooseByNamePopup(@Nullable Project project, @NotNull ChooseByNameModel model, @NotNull ChooseByNameItemProvider provider, @Nullable ChooseByNamePopup oldPopup, @Nullable String predefinedText, boolean mayRequestOpenInCurrentWindow, int initialIndex) {
        super(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);
    }

    public static RestServiceChooseByNamePopup createPopup(final Project project,
                                                           @NotNull final ChooseByNameModel model,
                                                           @NotNull ChooseByNameItemProvider provider,
                                                           @Nullable final String predefinedText,
                                                           boolean mayRequestOpenInCurrentWindow,
                                                           final int initialIndex) {
        if (!StringUtil.isEmptyOrSpaces(predefinedText)) {
            return new RestServiceChooseByNamePopup(project, model, provider, null, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);
        }

        final RestServiceChooseByNamePopup oldPopup = project == null ? null : project.getUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY);
        if (oldPopup != null) {
            oldPopup.close(false);
        }
        RestServiceChooseByNamePopup newPopup = new RestServiceChooseByNamePopup(project, model, provider, oldPopup, predefinedText, mayRequestOpenInCurrentWindow, initialIndex);

        if (project != null) {
            project.putUserData(CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY, newPopup);
        }
        return newPopup;
    }

}