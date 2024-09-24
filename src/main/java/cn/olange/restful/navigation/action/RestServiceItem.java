package cn.olange.restful.navigation.action;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Computable;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import cn.olange.restful.common.ToolkitIcons;
import cn.olange.restful.method.HttpMethod;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RestServiceItem implements NavigationItem {
    private PsiMethod psiMethod;
    private PsiElement psiElement;
    private Module module;

    private HttpMethod method;

    private String url;

    private Navigatable navigationElement;
    public RestServiceItem(PsiElement psiElement, String requestMethod, String urlPath) {
        this.psiElement = psiElement;
        if (psiElement instanceof PsiMethod) {
            this.psiMethod = (PsiMethod) psiElement;
        }
        if (requestMethod != null) {
            method = HttpMethod.getByRequestMethod(requestMethod);
        }

        this.url = urlPath;
        if (psiElement instanceof Navigatable) {
            navigationElement = (Navigatable) psiElement;
        }
    }

    @Nullable
    @Override
    public String getName() {
        return this.url;
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new RestServiceItemPresentation();
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (navigationElement != null) {
            navigationElement.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return navigationElement.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }


    private class RestServiceItemPresentation implements ItemPresentation {
        @Nullable
        @Override
        public String getPresentableText() {
            return url;
        }

        @Nullable
        @Override
        public String getLocationString() {
            return ApplicationManager.getApplication()
                .runWriteAction(new Computable<>() {
                    String location = null;
                    @Override
                    public String compute() {
                        return null;
                    }

                    @Override
                    public String get() {
                        if (psiElement instanceof PsiMethod) {
                            PsiMethod psiMethod = ((PsiMethod) psiElement);
                            if (module != null) {
                                location =
                                    module.getName() +
                                        "#" +
                                        psiMethod
                                            .getContainingClass()
                                            .getName()
                                            .concat("#")
                                            .concat(psiMethod.getName());
                            } else {
                                location =
                                    psiMethod
                                        .getContainingClass()
                                        .getName()
                                        .concat("#")
                                        .concat(psiMethod.getName());
                            }
                        }
                        return "(" + location + ")";
                    }

                });
        }

        @Nullable
        @Override
        public Icon getIcon(boolean unused) {
            return ToolkitIcons.METHOD.get(method);
        }
    }

    public Module getModule() {
        return module;
    }

    public PsiMethod getPsiMethod() {
        return psiMethod;
    }

    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void  setModule(Module module) {
        this.module = module;
    }


    public PsiElement getPsiElement() {
        return psiElement;
    }
}
