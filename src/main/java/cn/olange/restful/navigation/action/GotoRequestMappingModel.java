package cn.olange.restful.navigation.action;

import cn.olange.restful.common.ServiceHelper;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.gotoByName.CustomMatcherModel;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import cn.olange.restful.method.HttpMethod;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Model for "Go to | File" action
 */
public class GotoRequestMappingModel extends FilteringGotoByModel<HttpMethod> implements DumbAware, CustomMatcherModel {
    public static final Logger logger = Logger.getInstance(GotoRequestMappingModel.class);

    protected GotoRequestMappingModel(@NotNull Project project, @NotNull ChooseByNameContributor[] contributors) {
        super(project, contributors);
    }

    @Nullable
    @Override
    protected HttpMethod filterValueFor(NavigationItem item) {
        if (item instanceof RestServiceItem) {
            return ((RestServiceItem) item).getMethod();
        }
        return null;
    }

    @Nullable
    @Override
    protected synchronized Collection<HttpMethod> getFilterItems() {
        return super.getFilterItems();

    }

    @Override
    public String getPromptText() {
        return "Enter service URL path :";
    }

    @Override
    public String getNotInMessage() {
        return "No matched method found";
    }

    @Override
    public String getNotFoundMessage() {
        return "Service path not found";
    }

    @Override
    public char getCheckBoxMnemonic() {
        return SystemInfo.isMac?'P':'n';
    }

    @Override
    public boolean loadInitialCheckBoxState() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(myProject);
//        return propertiesComponent.isTrueValue("GoToRestService.OnlyCurrentModule");
        return false;
    }

    @Override
    public void saveInitialCheckBoxState(boolean state) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(myProject);
        if (propertiesComponent.isTrueValue("GoToRestService.OnlyCurrentModule")) {
            propertiesComponent.setValue("GoToRestService.OnlyCurrentModule", Boolean.toString(state));
        }
    }

    @Nullable
    @Override
    public String getFullName(Object element) {
        return getElementName(element);
    }

    @NotNull
    @Override
    public String[] getSeparators() {
        return new String[]{};
    }


    @Nullable
    @Override
    public String getCheckBoxName() {
        return null;
    }


    @Override
    public boolean willOpenEditor() {
        return true;
    }

    @Override
    public boolean matches(@NotNull String popupItem, @NotNull String userPattern) {
        if (("/").equals(userPattern)) {
            return true;
        }
        if (StringUtils.isEmpty(popupItem)) {
            return false;
        }
        return StringUtils.trim(popupItem).contains(userPattern);
    }

    @NotNull
    @Override
    public String removeModelSpecificMarkup(@NotNull String pattern) {
        return super.removeModelSpecificMarkup(pattern);
    }

}
