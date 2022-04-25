package fun.fons;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.util.HashSet;
import java.util.LinkedHashMap;

public enum DescriptorTypeEnum {
    /* user Info */
    userInfo("user-info-state", MapStateDescriptor.class, new TypeHint<String>() {}, new TypeHint<Test>() {}),
    userInfoSmall("user-info-small-state", MapStateDescriptor.class, new TypeHint<String>() {}, new TypeHint<LinkedHashMap<String, String>>() {}),
    offlineUserTime("offline-user-time-state", MapStateDescriptor.class, new TypeHint<String>() {}, new TypeHint<HashSet<String>>() {}),
    lastHandleTime("last-handle-time-state", ValueStateDescriptor.class, new TypeHint<Long>() {});

    /**
    * 类型
    * */
    private String name;
    /**
    * class
    * */
    private Class<? extends StateDescriptor> clazz;
    /**
     * type class
     * */
    private TypeHint<? extends Object>[] types;
    DescriptorTypeEnum(String s, Class<? extends StateDescriptor> mapStateDescriptorClass, TypeHint... types) {
        this.name = s;
        this.clazz = mapStateDescriptorClass;
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public Class<? extends StateDescriptor> getClazz() {
        return clazz;
    }

    public TypeHint<?>[] getTypes() {
        return types;
    }

    public Class<?>[] getConstructor(){
        Class<?>[] classes = new Class[this.getTypes().length+1];
        classes[0] = this.getName().getClass();
        for(int i = 1;i<classes.length; i++){
            classes[i] = TypeInformation.class;
        }
        return classes;
    }

    public Object[] getConstructorFiled(){
        TypeHint<?>[] typeHints = this.getTypes();
        Object[] objects = new Object[typeHints.length+1];
        objects[0] = this.getName();
        for(int i = 1;i<objects.length; i++){
            objects[i] = TypeInformation.of(typeHints[i-1]);
        }
        return objects;
    }
}
