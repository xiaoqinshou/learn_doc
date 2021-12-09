package cn.utstarcom.isrs.common;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;


/**
 * 堆
 * 用于构建固定大小的堆
 * 可以获取倒序数组
 */
public class TopNHeap<T> {
    private volatile int size = 0;
    transient Object[] elementData;
    private final Function<T, Integer> function;

    public TopNHeap(int size) {
        elementData = new Object[size];
        function = T::hashCode;
    }

    public TopNHeap(int size, Function<T, Integer> function) {
        elementData = new Object[size];
        this.function = function;
    }

    /**
     * 向堆中插入数据
     */
    public synchronized void insert(T t) {
        if (size < elementData.length) {
            percolateUp(size, t);
            size++;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * 当堆满了 删除最小值，再插入
     */
    public synchronized void delAndInsert(T t) {
        if (size == elementData.length) {
            deleteMin();
        }
        insert(t);
    }

    /**
     * 当要插入的值小于最小值时，并不执行插入
     */
    public void nothingAndInsert(T t) {
        if (size >= elementData.length &&
                function.apply(t) < function.apply(getMin())) {
            // nothing
        } else {
            // 拿锁后再检测一遍
            synchronized (this) {
                if (size < elementData.length ||
                        function.apply(t) > function.apply(getMin())) {
                    delAndInsert(t);
                }
            }
        }
    }

    /**
     * 删除最小值
     */
    public synchronized void deleteMin() {
        if (elementData[0] != null) {
            int last = --size;
            @SuppressWarnings("unchecked")
            T moved = (T) elementData[last];
            elementData[last] = null;
            percolateDown(0, moved);
        }
    }

    /**
     * 获取最小值
     */
    @SuppressWarnings("unchecked")
    public T getMin() {
        return (T) elementData[0];
    }

    /**
     * 获取排行榜
     */
    public List<T> getTopNList() {
        @SuppressWarnings("unchecked")
        T[] newArr = (T[]) Arrays.copyOf(elementData, size);
        int len = size;
        for (int i = len - 1; i > 0; i--) {
            swap(newArr, 0, i);
            len--;
            heapify(newArr, 0, len);
        }
        return Arrays.asList(newArr);
    }

    @SuppressWarnings("unchecked")
    private void heapify(Object[] arr, int i, int len) {
        int left = (i << 1) + 1;
        int right = (i << 1) + 2;
        int smallest = i;
        if (left < len && function.apply((T) arr[left]) < function.apply((T) arr[smallest])) {
            smallest = left;
        }

        if (right < len && function.apply((T) arr[right]) < function.apply((T) arr[smallest])) {
            smallest = right;
        }

        if (smallest != i) {
            swap(arr, smallest, i);
            heapify(arr, smallest, len);
        }
    }

    private void swap(Object[] arr, int i, int j) {
        Object tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }


    @SuppressWarnings("unchecked")
    private void percolateDown(int index, T t) {
        int half = size >>> 1;
        while (index < half) {
            int child = (index << 1) + 1;
            Object c = elementData[child];
            int right = child + 1;
            if (right < size &&
                    function.apply((T) c) > function.apply((T) elementData[right])) {
                c = elementData[child = right];

            }
            if (function.apply(t) <= function.apply((T) c)) {
                break;
            }
            elementData[index] = c;
            index = child;
        }
        elementData[index] = t;
    }

    @SuppressWarnings("unchecked")
    private void percolateUp(int index, T t) {
        while (index > 0) {
            int parentIndex = parent(index);
            Object element = elementData[parentIndex];
            if (function.apply((T) element) <= function.apply(t)) {
                break;
            }
            elementData[index] = element;
            index = parentIndex;
        }
        elementData[index] = t;
    }

    private int parent(int i) {
        return Math.max((i - 1) >> 1, 0);
    }

    public int getSize() {
        return size;
    }

    public Function<T, Integer> getFunction() {
        return function;
    }
}
