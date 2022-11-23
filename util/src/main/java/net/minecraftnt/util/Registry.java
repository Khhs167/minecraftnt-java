package net.minecraftnt.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

public class Registry<T> {

    private final HashMap<Identifier, T> registry_data = new HashMap<>();
    private final T default_value;

    public Registry(){
        this(null);
    }

    public Registry(T default_value){
        this.default_value = default_value;
    }


    public void clear(){
        registry_data.clear();
    }

    public T get(Identifier identifier){
        if(registry_data.containsKey(identifier))
            return registry_data.get(identifier);

        return default_value;
    }

    public T register(Identifier identifier, T value){
        return register(identifier, value, false);
    }

    public T register(Identifier identifier, T value, boolean override){
        if(registry_data.containsKey(identifier))
            return value;

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
