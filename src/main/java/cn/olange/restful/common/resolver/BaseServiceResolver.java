package cn.olange.restful.common.resolver;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import cn.olange.restful.method.RequestPath;
import cn.olange.restful.navigation.action.RestServiceItem;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public abstract class BaseServiceResolver implements ServiceResolver{

    @Override
    public List<RestServiceItem> findAllSupportedServiceItemsInModule(GlobalSearchScope searchScope) {
        return getRestServiceItemList(searchScope.getProject(), searchScope);
    }


    public abstract List<RestServiceItem> getRestServiceItemList(Project project, GlobalSearchScope globalSearchScope) ;

    @Override
    public List<RestServiceItem> findAllSupportedServiceItemsInProject(GlobalSearchScope searchScope) {
        return getRestServiceItemList(searchScope.getProject(), searchScope);
    }

    @NotNull
    protected RestServiceItem createRestServiceItem(PsiElement psiMethod, String classUriPath, RequestPath requestMapping) {
        if (!classUriPath.startsWith("/")) {
            classUriPath = "/".concat(classUriPath);
        }
        if (!classUriPath.endsWith("/")) {
            classUriPath = classUriPath.concat("/");
        }

        String methodPath = requestMapping.getPath();

        if (methodPath.startsWith("/")) {
            methodPath = methodPath.substring(1, methodPath.length());
        }
        String requestPath = classUriPath + methodPath;
        if (requestPath.endsWith("/")) {
            requestPath = requestPath.substring(0, requestPath.length() - 1);
        }
        return new RestServiceItem(psiMethod, requestMapping.getMethod(), requestPath);
    }

}
