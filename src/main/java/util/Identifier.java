package util;

public class Identifier {
    private String namespace;
    private String name;

    public Identifier(String namespace, String name){
        this.namespace = namespace;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public String toString() {
        return namespace + ":" + name;
    }
}
