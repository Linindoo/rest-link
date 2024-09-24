package cn.olange.restful.common;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import cn.olange.restful.common.resolver.JaxrsResolver;
import cn.olange.restful.common.resolver.OpenFeignResolver;
import cn.olange.restful.common.resolver.ServiceResolver;
import cn.olange.restful.common.resolver.SpringResolver;
import cn.olange.restful.method.RequestPath;
import cn.olange.restful.navigation.action.RestServiceItem;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 服务相关工具类
 */
public class ServiceHelper {
    public static final Logger LOG = Logger.getInstance(ServiceHelper.class);
    private static List<ServiceResolver> serviceResolvers = Arrays.asList(new JaxrsResolver(), new OpenFeignResolver(), new SpringResolver());

    public static List<RestServiceItem> buildRestServiceItemListUsingResolver(Module module) {

        List<RestServiceItem> itemList = new ArrayList<>();

        GlobalSearchScope globalSearchScope = GlobalSearchScope.moduleScope(module);

        for (ServiceResolver resolver : serviceResolvers) {
            List<RestServiceItem> allSupportedServiceItemsInModule = resolver.findAllSupportedServiceItemsInModule(globalSearchScope);

            itemList.addAll(allSupportedServiceItemsInModule);
        }

        return itemList;
    }

    @NotNull
    public static List<RestServiceItem> buildRestServiceItemListUsingResolver(Project project) {
        List<RestServiceItem> itemList = new ArrayList<>();

        GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(project);
        for (ServiceResolver resolver : serviceResolvers) {
            List<RestServiceItem> allSupportedServiceItemsInProject = resolver.findAllSupportedServiceItemsInProject(globalSearchScope);

            itemList.addAll(allSupportedServiceItemsInProject);
        }

        return itemList;
    }

    public static List<RestServiceItem> getServiceItemByMethod(PsiMethod psiMethod) {
        List<RestServiceItem> itemList = new ArrayList<>();
        List<RequestPath> classRequestPaths = new ArrayList<>();
        List<RequestPath> methodRequestPaths = new ArrayList<>();
        for (ServiceResolver resolver : serviceResolvers) {
            List<RequestPath> requestPaths = resolver.getMethodPaths(psiMethod);
            if (CollectionUtils.isNotEmpty(requestPaths)) {
                for (RequestPath requestPath : requestPaths) {
                    if (methodRequestPaths.stream().noneMatch(x -> x.getPath().equalsIgnoreCase(requestPath.getPath()))) {
                        methodRequestPaths.add(requestPath);
                    }
                }
            }
            PsiClass psiClass = psiMethod.getContainingClass();
            List<RequestPath> basePaths = resolver.getBasePaths(psiClass);
            if (CollectionUtils.isNotEmpty(basePaths)) {
                for (RequestPath basePath : basePaths) {
                    if (classRequestPaths.stream().noneMatch(x -> x.getPath().equalsIgnoreCase(basePath.getPath()))) {
                        classRequestPaths.add(basePath);
                    }
                }
            }
        }
        for (RequestPath methodRequestPath : methodRequestPaths) {
            if (CollectionUtils.isNotEmpty(classRequestPaths)) {
                for (RequestPath classRequestPath : classRequestPaths) {
                    String path =  classRequestPath.getPath();
                    RestServiceItem item = createRestServiceItem(psiMethod, path, methodRequestPath);
                    itemList.add(item);
                }
            } else {
                RestServiceItem item = createRestServiceItem(psiMethod, "", methodRequestPath);
                itemList.add(item);
            }
        }
        return itemList;
    }

    private static RestServiceItem createRestServiceItem(PsiElement psiMethod, String classUriPath, RequestPath requestMapping) {
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
