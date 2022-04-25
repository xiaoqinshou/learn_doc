package fun.fons;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.StateDescriptor;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DescriptorFactoryUtils {
    private static Map<DescriptorTypeEnum, StateDescriptor> flinkStateDescriptor = new HashMap<>();

    public static <T extends StateDescriptor> T getStateDescriptor(DescriptorTypeEnum typeEnum) {
        if (!flinkStateDescriptor.containsKey(typeEnum)) {
            synchronized (DescriptorFactoryUtils.class) {
                if (!flinkStateDescriptor.containsKey(typeEnum)) {
                    StateDescriptor descriptor = null;
                    try {
                        descriptor = createStateDescriptor(typeEnum);
                        flinkStateDescriptor.put(typeEnum, descriptor);
                    } catch (Exception e) {
                        log.error("create {} descriptor is error: ", typeEnum.getName(), e);
                    }
                }
            }
        }
        return (T) flinkStateDescriptor.get(typeEnum);
    }

    private static StateDescriptor createStateDescriptor(DescriptorTypeEnum typeEnum) throws Exception {
        Class<?>[] classes = typeEnum.getConstructor();
        Constructor<? extends StateDescriptor> constructor =
                typeEnum.getClazz().getConstructor(classes);
        return constructor.newInstance(typeEnum.getConstructorFiled());
    }
}
