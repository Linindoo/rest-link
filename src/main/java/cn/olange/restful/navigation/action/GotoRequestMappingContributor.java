package cn.olange.restful.navigation.action;


import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import cn.olange.restful.common.ServiceHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GotoRequestMappingContributor implements ChooseByNameContributor {
    Module myModule;

    private List<RestServiceItem> navItem;

    public GotoRequestMappingContributor(Module myModule) {
        this.myModule = myModule;
    }

    @NotNull
    @Override
    public String[] getNames(Project project, boolean onlyThisModuleChecked) {
        String[] names = null;
        List<RestServiceItem> itemList;
        if (onlyThisModuleChecked && myModule != null) {
            itemList = ServiceHelper.buildRestServiceItemListUsingResolver(myModule);
        } else {
            itemList = ServiceHelper.buildRestServiceItemListUsingResolver(project);
        }

        navItem = itemList;

        names = new String[itemList.size()];

        for (int i = 0; i < itemList.size(); i++) {
            RestServiceItem requestMappingNavigationItem = itemList.get(i);
            names[i] = requestMappingNavigationItem.getName();
        }

        return names;
    }


    @NotNull
    @Override
    public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean onlyThisModuleChecked) {
        return navItem.stream().filter(item -> name.equals(item.getName())).toArray(NavigationItem[]::new);
    }
}
