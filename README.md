# PanelView

## Usage

```kotlin
val data = listOf<PanelView.Item>(
              PanelView.Item("舒适性", 1.0, 0.5),
              PanelView.Item("内饰", 1.0, 0.3),
              PanelView.Item("空间", 1.0, 0.9),
              PanelView.Item("动力", 1.0, 0.6),
              PanelView.Item("操控", 1.0, 0.8),
              PanelView.Item("配置", 1.0, 0.7),
              PanelView.Item("外观", 1.0, 0.6)
      )
view.placeHolder.apply {
    this.data = data
    radius = 120.dp
    textSize = 19.dp
    textColor = Color.parseColor("#333333")
    layer3StokeWidth = 4.dp
    layer3StokeColor = color(R.color.colorLayer3Stroke)
    layer3Color = color(R.color.colorLayer3)
    layer2Color = color(R.color.colorLayer2)
    layer1Color = color(R.color.colorLayer1)
}
```

![Preview](/assets/preview1.png)
