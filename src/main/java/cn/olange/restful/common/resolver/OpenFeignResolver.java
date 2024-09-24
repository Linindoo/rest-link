package cn.olange.restful.common.resolver;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import cn.olange.restful.annotations.OpenFeignControllerAnnotation;
import cn.olange.restful.annotations.PathMappingAnnotation;
import cn.olange.restful.common.feign.FeignAnnotationHelper;
import cn.olange.restful.method.RequestPath;
import cn.olange.restful.navigation.action.RestServiceItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zhoujia
 * @since 2024/9/18 14:10
 */
public class OpenFeignResolver extends SpringResolver {

    @Override
    public List<RestServiceItem> getRestServiceItemList(Project project, GlobalSearchScope globalSearchScope) {
        List<RestServiceItem> itemList = new ArrayList<>();

        OpenFeignControllerAnnotation[] supportedAnnotations = OpenFeignControllerAnnotation.values();
        for (PathMappingAnnotation controllerAnnotation : supportedAnnotations) {

            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance().get(controllerAnnotation.getShortName(), project, globalSearchScope);
            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                PsiElement psiElement = psiModifierList.getParent();
                PsiClass psiClass = (PsiClass) psiElement;
                List<RestServiceItem> serviceItemList = getServiceItemList(psiClass);
                itemList.addAll(serviceItemList);
            }
        }
        return itemList;
    }

    @Override
    public List<RequestPath> getBasePaths(PsiClass psiClass) {
        return FeignAnnotationHelper.getRequestPaths(psiClass);
    }


}
