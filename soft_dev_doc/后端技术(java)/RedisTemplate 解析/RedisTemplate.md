# RedisTemplate
&emsp;遇到个需求需要把 `redis` 中的把对象序列化数据格式，改为 `json` 串的数据格式，虽然说通常方法几百种，顺手就点开了 `RedisTemplate` 这个包康康。

## 序列化类图
```plantuml
interface RedisSerializer
class OxmSerializer implements RedisSerializer
class GenericToStringSerializer implements RedisSerializer
class GenericJackson2JsonRedisSerializer implements RedisSerializer
class StringRedisSerializer implements RedisSerializer
class JdkSerializationRedisSerializer implements RedisSerializer
class Jackson2JsonRedisSerializer implements RedisSerializer
class Converter

hide Converter
Converter -down-+ GenericToStringSerializer
```
* 以上是对象的基本接口序列化和反序列化为字节数组（二进制数据）类图
  1. **OxmSerializer**：主要引用 `spring-oxm` 包，用于将 `object` 序列化和反序列化为 `xml` 形式
  2. **GenericToStringSerializer**：序列化和反序列化时，调用 `spring-core` 中的类型转化器，将任意对象转换成字符串形式
  3. **GenericJackson2JsonRedisSerializer**：序列化和反序列化时，调用 `jackson` 将对象转换成 `json` 字符串
  4. **StringRedisSerializer**：简单的字符串序列化和反序列化。用的也最多不多赘述。
  5. **JdkSerializationRedisSerializer**：主要调用 `jdk` 中的 `Serializer`，进行序列化和反序列化
  6. **Jackson2JsonRedisSerializer**：同 **GenericToStringSerializer**
