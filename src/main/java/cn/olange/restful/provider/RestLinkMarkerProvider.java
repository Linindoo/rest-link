package cn.olange.restful.provider;

import cn.olange.restful.common.ServiceHelper;
import cn.olange.restful.common.ToolkitIcons;
import cn.olange.restful.navigation.action.RestServiceItem;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RestLinkMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (element instanceof PsiNameIdentifierOwner) {

            List<RestServiceItem> serviceItems = parseService(element);
            if (CollectionUtils.isEmpty(serviceItems)) {
                return;
            }


            List<RestServiceItem> restServiceItems = ServiceHelper.buildRestServiceItemListUsingResolver(element.getProject());
            if (CollectionUtils.isEmpty(restServiceItems)) {
                return;
            }
            Collection<PsiElement> results = restServiceItems.stream().filter(x -> serviceItems.stream().anyMatch(y -> {
                if (!x.getUrl().equalsIgnoreCase(y.getUrl())) {
                    return false;
                }
                if (element.isEquivalentTo(x.getPsiMethod())) {
                    return false;
                }
                if (x.getMethod() == null || y.getMethod() == null) {
                    return true;
                }
                if (!x.getMethod().equals(y.getMethod())) {
                    return false;
                }
                return true;
            })).map(RestServiceItem::getPsiMethod).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(results)) {
                NavigationGutterIconBuilder<PsiElement> builder =
                    NavigationGutterIconBuilder.create(ToolkitIcons.LINK)
                        .setAlignment(GutterIconRenderer.Alignment.CENTER)
                        .setTargets(results)
                        .setTooltipTitle("Navigation to target link rest api");
                result.add(builder.createLineMarkerInfo(((PsiNameIdentifierOwner) element).getNameIdentifier()));
            }
        }
    }

    private List<RestServiceItem> parseService(PsiElement element) {
        if (element instanceof PsiClass) {
            return null;
        }
        if (element instanceof PsiMethod psiMethod) {
            return ServiceHelper.getServiceItemByMethod(psiMethod);
        }
        return null;
    }
}
