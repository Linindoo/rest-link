package cn.olange.restful.annotations;


public enum OpenFeignControllerAnnotation implements PathMappingAnnotation {

    CONTROLLER("FeignClient", "org.springframework.cloud.openfeign.FeignClient");

    OpenFeignControllerAnnotation(String shortName, String qualifiedName) {
        this.shortName = shortName;
        this.qualifiedName = qualifiedName;
    }

    private String shortName;
    private String qualifiedName;

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getShortName() {
        return shortName;
    }

}