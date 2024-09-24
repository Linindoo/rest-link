package cn.olange.restful.navigation.action;


import com.intellij.ide.util.gotoByName.ChooseByNameViewModel;
import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;
import cn.olange.restful.utils.ToolkitUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GotoRequestMappingProvider extends DefaultChooseByNameItemProvider {

     @Override
    public List<String> filterNames(@NotNull ChooseByNameViewModel base, @NotNull String[] names, @NotNull String pattern) {
        return super.filterNames(base, names, pattern);
    }


    public GotoRequestMappingProvider(@Nullable PsiElement context) {
        super(context);
    }
     @Override
    public boolean filterElements(@NotNull ChooseByNameViewModel base, @NotNull String pattern, boolean everywhere, @NotNull ProgressIndicator indicator, @NotNull Processor<Object> consumer) {

        pattern = ToolkitUtil.removeRedundancyMarkup(pattern);

        return super.filterElements(base, pattern, everywhere, indicator, consumer);
    }

}
