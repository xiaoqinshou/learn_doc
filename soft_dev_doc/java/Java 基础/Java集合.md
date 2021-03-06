# JAVA 集合
&emsp;作为15年入学的带砖生，摸爬滚打也几年了，也应该要从 API 工程师，慢慢转行成程序猿了。刚好趁着这段时间准备复习，从头学习一遍，从实践经验来丰富一遍理论知识。
## JAVA集合类类图
### 列表，栈，队列全家桶
![](./images/2020-08-05-20-11-38.png)
![](./images/2020-08-06-19-33-05.png)
* 首先慢慢分析这张类图
1. Iterator 迭代器抽象, 由于源码比较少就直接贴出来，具体了解[迭代器模式](../../设计模式/迭代器模式.md)
```java
// 正常的一个迭代器抽象，以及迭代器必须有的几个方法
public interface Iterator<E> {
    // 判断是否有下一个节点
    boolean hasNext();
    // 获取下一个节点，并且指针移向下一个位置
    E next();
    // 删除当前节点
    default void remove() {
        throw new UnsupportedOperationException("remove");
    }
    default void forEachRemaining(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        while (hasNext())
            action.accept(next());
    }
}
```
2. Iterable 创建迭代器抽象类，用于创建迭代器，而抽象出来的抽象类(代码也不多贴出来)。
```java
// 创建迭代器的抽象类
public interface Iterable<T> {
    // 生成返回一个属于自己的迭代器
    Iterator<T> iterator();
    // 这个主要就是1.8 中加入的函数式编程了 这里传进来的其实就是一个方法
    default void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (T t : this) {
            action.accept(t);
        }
    }
    // 1.8 新加入的分流器，主要是Spliterators 生成根据迭代器生成对应的分流器
    // Spliterator 提供分流以后进行函数式编程的入口
    // 不过多展开。后期有时间再去看。
    default Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 0);
    }
}
```

3. Collection 中文直译就是集合，所以它里面主要是抽象出了集合类所有的共有的特性，用于给不同的集合子类进行统一实现
```java
public interface Collection<E> extends Iterable<E> {
    // 该集合的大小
    int size();

    // 该集合是否为空
    boolean isEmpty();

    // 该集合是否存在某种元素，查找
    boolean contains(Object o);

    // 生成自己的迭代器，由上层继承下来的
    Iterator<E> iterator();

    // 自身集合转数组
    Object[] toArray();

    // 英文不好看不太懂注释，方法名叫的也挺随便的
    // 看了一遍代码，简述一下此方法抽象的作用
    // 当传进来的数组长度小于该集合时，创建一个新的 T[] 数组，并将该集合所有的内容塞进新的数组并返回
    // 当传进来的数组长度大于等于该集合时，将该集合的所有数据顺序不变的塞到传进来的数组 a 中。(ArrayList中的处理方式)大于的部分将第一个设为下标为集合最大值的数组下标设为null
    <T> T[] toArray(T[] a);

    // 为自己添加一个节点
    boolean add(E e);

    // 删除自己一个节点
    boolean remove(Object o);

    // 判断本身是否包含另一个集合
    boolean containsAll(Collection<?> c);

    // 将该集合添加进进里面
    boolean addAll(Collection<? extends E> c);


    boolean removeAll(Collection<?> c);


    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }


    boolean retainAll(Collection<?> c);


    void clear();


    boolean equals(Object o);


    int hashCode();


    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, 0);
    }


    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }


    default Stream<E> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}
```
* 昨天写过一次，不小心覆盖掉了，懒得再写一遍了，List代码挺简单的稍微再翻一翻源码看一眼就懂了，不赘述了。
* Dqueue 也就是队列的一些抽象接口

### ArrayList
* 基于数组表，数据结构的原理，[表，栈，队列](../../数据结构/表，栈，队列.md)
* 因为 JAVA 数组大小固定(初始容量10)，源码中存在数组的扩容机制(个人理解):
    1. 正常扩容：每次是上一次的 1.5 倍(**oldSize+(oldSize >> 1)**)
    2. 超载扩容: 当一次添加一个集合，通过一次扩容也无法满足时，直接扩容到最大值的容量(**oldSize+newSize**)
    3. 最大容量：`Integer.Max`(`0x7fffffff`),最后一次扩容如果大于`Integer.Max - 8`, 则用实际大小去判断数组长度为 `Integer.Max`或者是
`Integer.Max - 8`
```java
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
```

### LinkedList
* 基于双向链表，数据结构的原理，[表，栈，队列](../../数据结构/表，栈，队列.md)。
* Java 中的数据结构定义如下：
```java
    transient int size = 0;

    transient Node<E> first;

    transient Node<E> last;

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
```

### Vector
* 与ArrayList 原理上相同，就是在各个能引起并发问题位置的地方(例如：插入，删除，获取大小，扩容等操作)加入了 `synchronized` 同步锁

### Stack
* 数据结构其实是数组表，继承 **Vector** 从而实现栈的特性，栈特性查看[表，栈，队列](../../数据结构/表，栈，队列.md)。

### ArrayQueue
* 基于数组表做的队列，原理详情看[表，栈，队列](../../数据结构/表，栈，队列.md)。

### PriorityQueue(优先队列)
* 理论查看[表，栈，队列](../../数据结构/表，栈，队列.md)。
* 优先队列，默认大小 11，小于64 翻倍扩容，大于64与array list一样 0.5倍扩容。
```java
public class PriorityQueue<E> extends AbstractQueue<E>{
  private static final int DEFAULT_INITIAL_CAPACITY = 11;
  transient Object[] queue;
  private int size = 0;

  // 向上修正排序算法，每次入队会执行一次判断新节点是否是优先级最高
  @SuppressWarnings("unchecked")
  private void siftUpComparable(int k, E x) {
      Comparable<? super E> key = (Comparable<? super E>) x;
      while (k > 0) {
          int parent = (k - 1) >>> 1;
          Object e = queue[parent];
          if (key.compareTo((E) e) >= 0)
              break;
          queue[k] = e;
          k = parent;
      }
      queue[k] = key;
  }

  // 向下修正，判断删除后的节点, 向下修正寻找优先度最高的节点放在队列的首位，同时将队尾的元素放在优先级最高元素之前的位置
  @SuppressWarnings("unchecked")
  private void siftDownComparable(int k, E x) {
      Comparable<? super E> key = (Comparable<? super E>)x;
      int half = size >>> 1;        // loop while a non-leaf
      while (k < half) {
          int child = (k << 1) + 1; // assume left child is least
          Object c = queue[child];
          int right = child + 1;
          if (right < size &&
              ((Comparable<? super E>) c).compareTo((E) queue[right]) > 0)
              c = queue[child = right];
          if (key.compareTo((E) c) <= 0)
              break;
          queue[k] = c;
          k = child;
      }
      queue[k] = key;
  }

  // 出队，每次出队时在重新平衡一下最后的位置
  public E poll() {
      if (size == 0)
          return null;
      int s = --size;
      modCount++;
      E result = (E) queue[0];
      E x = (E) queue[s];
      queue[s] = null;
      if (s != 0)
          siftDown(0, x);
      return result;
  }
}
```

### ArrayDeque(双端队列)
* 顾名思义两头都可以入队和出队，
