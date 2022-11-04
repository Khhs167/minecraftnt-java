package net.minecraftnt.utility;

import java.util.Objects;

public class Identifier {

    private final String namespace;
    private final String name;


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
    public boolean equals(Object obj) {
        if(obj instanceof Identifier other){
            return Objects.equals(other.namespace, namespace) && Objects.equals(other.name, name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, name);
    }

    @Override
    public String toString() {
        return namespace + ":" + name;
    }
}
