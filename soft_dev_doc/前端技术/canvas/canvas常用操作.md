# 常用操作
[toc]

### 图片旋转90°
```TS
const img = new Image()
img.src = pageData
img.onload = () => {
canvas.width = contentHeight
canvas.height = contentWidth
const ctx = canvas.getContext('2d');
ctx.save()
ctx.translate(contentHeight / 2, contentWidth / 2)
ctx.rotate(-Math.PI / 2)
ctx.translate(-contentHeight / 2, -contentWidth / 2);
ctx.drawImage(img, contentHeight / 2 - contentWidth / 2, contentWidth / 2 - contentHeight / 2);
ctx.restore()
resolve({data: canvas.toDataURL('image/jpeg', 1.0), width: contentHeight, height: contentWidth})
}
```