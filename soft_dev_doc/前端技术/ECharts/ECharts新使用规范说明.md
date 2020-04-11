# ECharts个人使用规范
## Series数据属性
> 根据官方文档描述,Echarts 4 以后开始使用 DataSet 注入数据，在此统一注入数据使用 DataSet属性，抛弃 Series 中注入数据的方式，所以：
* 根据官方文档属性中，抛弃掉 series.dimensions(定义每个维度信息)，series.data属性，抛弃这种series中直接注入图标数据的方式，虽然编码时容易理解，但是对于维护数据，以及复用数据难以支持，容易增加程序的负担。

## DataSet数据管理属性
> 根据官方文档描述，原始数据一共有3种表达形式：
> 二维数组，其中第一行/列可以给出 维度名，也可以不给出，直接就是数据：
```javascript
[
    ['product', '2015', '2016', '2017'],
    ['Matcha Latte', 43.3, 85.8, 93.7],
    ['Milk Tea', 83.1, 73.4, 55.1],
    ['Cheese Cocoa', 86.4, 65.2, 82.5],
    ['Walnut Brownie', 72.4, 53.9, 39.1]
]
```
> 按行的 key-value 形式（对象数组），其中键（key）表明了 维度名：
```javascript
[
    {product: 'Matcha Latte', count: 823, score: 95.8},
    {product: 'Milk Tea', count: 235, score: 81.4},
    {product: 'Cheese Cocoa', count: 1042, score: 91.2},
    {product: 'Walnut Brownie', count: 988, score: 76.9}
]
```
> 按列的 key-value 形式，每一项表示二维表的 “一列”：
```javascript
{
    'product': ['Matcha Latte', 'Milk Tea', 'Cheese Cocoa', 'Walnut Brownie'],
    'count': [823, 235, 1042, 988],
    'score': [95.8, 81.4, 91.2, 76.9]
}
```

* 但是官方文档特别声明:
> 除了二维数组以外，dataset 也支持例如下面 key-value 方式的数据格式，这类格式也非常常见。但是这类格式中，目前并不支持 seriesLayoutBy 参数。
```javascript
dataset: [{
    // 按行的 key-value 形式（对象数组），这是个比较常见的格式。
    source: [
        {product: 'Matcha Latte', count: 823, score: 95.8},
        {product: 'Milk Tea', count: 235, score: 81.4},
        {product: 'Cheese Cocoa', count: 1042, score: 91.2},
        {product: 'Walnut Brownie', count: 988, score: 76.9}
    ]
}, {
    // 按列的 key-value 形式。
    source: {
        'product': ['Matcha Latte', 'Milk Tea', 'Cheese Cocoa', 'Walnut Brownie'],
        'count': [823, 235, 1042, 988],
        'score': [95.8, 81.4, 91.2, 76.9]
    }
}]
```
所以设置DataSet时，统一抛弃key-value的形式，统一使用二维数组的形式，有更好的转化效果。

## 支持图表

> 目前并非所有图表都支持 dataset。支持 dataset 的图表有： line、bar、pie、scatter、effectScatter、parallel、candlestick、map、funnel、custom。