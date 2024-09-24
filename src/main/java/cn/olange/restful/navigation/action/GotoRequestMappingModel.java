package cn.olange.restful.navigation.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.gotoByName.CustomMatcherModel;
import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import cn.olange.restful.common.spring.AntPathMatcher;
import cn.olange.restful.method.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

/**
 * Model for "Go to | File" action
 */
public class GotoRequestMappingModel extends FilteringGotoByModel<HttpMethod> implements DumbAware, CustomMatcherModel {

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
        return propertiesComponent.isTrueValue("GoToRestService.OnlyCurrentModule");
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
        return new String[]{"/","?"};
    }


    @Nullable
    @Override
    public String getCheckBoxName() {
        return "Only This Module";
    }


    @Override
    public boolean willOpenEditor() {
        return true;
    }

    @Override
    public boolean matches(@NotNull String popupItem, @NotNull String userPattern) {
        String pattern = userPattern;
        if (("/").equals(userPattern)) {
            return true;
        }
        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + pattern, NameUtil.MatchingCaseSensitivity.NONE);
        boolean matches = matcher.matches(popupItem);
        if (!matches) {
            AntPathMatcher pathMatcher = new AntPathMatcher();
            matches = pathMatcher.match(popupItem,userPattern);
        }
        return matches;
    }

    @NotNull
    @Override
    public String removeModelSpecificMarkup(@NotNull String pattern) {
        return super.removeModelSpecificMarkup(pattern);
    }

    @Override
    public ListCellRenderer<?> getListCellRenderer() {

        return super.getListCellRenderer();
    }



}
