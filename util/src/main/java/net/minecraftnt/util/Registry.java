package net.minecraftnt.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

public class Registry<T> {

    public Registry(Identifier nullIdentifier) {
        NULL = nullIdentifier;
    }

    public Registry() {
        this(null);
    }

    private final HashMap<Identifier, T> registry_data = new HashMap<>();
    private final Identifier NULL;

    public T get(Identifier identifier){
        T value = registry_data.get(identifier);
        if(value != null)
            return value;
        return registry_data.get(NULL);
    }

    public T register(Identifier identifier, T value){
        registry_data.put(identifier, value);
        return value;
    }

    public Identifier registerImmediate(Identifier identifier, T value) {
        register(identifier, value);
        return identifier;
    }

    public Collection<T> values() {
        return registry_data.values();
    }

    public Collection<Identifier> identifiers() {
        return registry_data.keySet();
    }

    public boolean execute(String function, Class<T> clazz){
        try {
            Method method = clazz.getMethod(function);

            for(T value : registry_data.values()){
                method.invoke(value);
            }

            return true;

        } catch (Throwable t) {
            return false;
        }

    }

}
