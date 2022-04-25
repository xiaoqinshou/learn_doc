package fun.fons;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.util.HashSet;
import java.util.LinkedHashMap;

public class Test {
    public static void main(String[] args) {
        MapStateDescriptor<String, Test> userInfoStateDescriptor1 = new MapStateDescriptor<>(
                "user-info-state", // the state name
                TypeInformation.of(new TypeHint<String>() {
                }),
                // the state name
                TypeInformation.of(new TypeHint<Test>() {
                }));
            MapStateDescriptor<String, LinkedHashMap<String, String>> userInfoSmallStateDescriptor2 = new MapStateDescriptor<>(
                    "user-info-small-state", // the state name
                    TypeInformation.of(new TypeHint<String>() {
                    }),
                    // the state name
                    TypeInformation.of(new TypeHint<LinkedHashMap<String, String>>() {
                    }));
        MapStateDescriptor<Long, HashSet<String>> offlineUserTimeStateDescriptor3 = new MapStateDescriptor<>(
                "offline-user-time-state", // the state name
                TypeInformation.of(new TypeHint<Long>() {
                }),
                // the state name
                TypeInformation.of(new TypeHint<HashSet<String>>() {
                }));
        ValueStateDescriptor<Long> lastHandleTimeStateDescriptor4 = new ValueStateDescriptor<Long>(
                "last-handle-time-state", TypeInformation.of(new TypeHint<Long>() {
        }));

        MapStateDescriptor<String, Test> userInfoStateDescriptor = DescriptorUtils.getStateDescriptor(DescriptorTypeEnum.userInfo);
        MapStateDescriptor<String, LinkedHashMap<String, String>> userInfoSmallStateDescriptor = DescriptorUtils.getStateDescriptor(DescriptorTypeEnum.userInfoSmall);
        MapStateDescriptor<Long, HashSet<String>> offlineUserTimeStateDescriptor = DescriptorUtils.getStateDescriptor(DescriptorTypeEnum.offlineUserTime);
        ValueStateDescriptor<Long> lastHandleTimeStateDescriptor = DescriptorUtils.getStateDescriptor(DescriptorTypeEnum.lastHandleTime);
        System.out.println(userInfoStateDescriptor.equals(userInfoStateDescriptor1));
        System.out.println(userInfoSmallStateDescriptor.equals(userInfoSmallStateDescriptor2));
        System.out.println(offlineUserTimeStateDescriptor.equals(offlineUserTimeStateDescriptor3));
        System.out.println(lastHandleTimeStateDescriptor.equals(lastHandleTimeStateDescriptor4));
    }
}