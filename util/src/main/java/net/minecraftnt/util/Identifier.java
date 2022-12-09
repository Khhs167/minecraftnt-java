package net.minecraftnt.util;

import java.util.Objects;

public class Identifier {

    private final String namespace;
    private final String name;
    private final int hash;


    public Identifier(String namespace, String name){
        this.namespace = namespace;
        this.name = name;
        hash = Objects.hash(namespace + ":" + name);
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
            return hash == other.hash;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return namespace + ":" + name;
    }
}
