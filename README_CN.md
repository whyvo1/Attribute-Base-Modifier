# 属性默认值修改器（ABM）

[MIT许可证](https://github.com/whyvo1/Attribute-Base-Modifier/blob/main/LICENSE)适用于所有分支。

### 注册规则

规则通过数据包中的JSON文件注册。

规则文件应当位于`data/<命名空间>/abm_rules/<名称>.json`。

若规则成功注册，其ID为`<命名空间>:<名称>`。

类似于其他数据包内容，所有规则在服务端重载数据时重载，具体地，当重载世界或使用`reload` 命令时。

JSON格式:

- `<root>` ***复合{}***
  - `include` ***复合{}***  包含过滤器。只有在此处声明的类型才会被包含。**可选**，缺省时所有类型都会被包括。
    - `type` ***字符串* | *数组[]***  列出需要被包含的生物。
      - ***String***  ID或标签（开头为`#`）。*例*：`minecraft:zombie`、`#minecraft:undead`。
    - `spawn_group` ***字符串***  这意味着此生成组的所有生物都会被包含。必须是`monster`、`creature`、`ambient`、`water_creature`、`underground_water_creature`、`water_ambient`、`misc`或`axolotls`之一。在和`type`共存时无效。
  - `exclude` ***复合{}***  排除过滤器。格式与`include`相同。**可选**，缺省时不排除任何类型。
  - `modifies` ***数组[]***  定义修改方式。
    - ***复合{}***  定义一个属性及其修改方式。若生物没有这个属性的默认值，修改会被跳过。
      - `attribute` ***字符串***  属性的ID。*例*：`minecraft:max_health`。
      - `modifiers` ***数组[]***  修改方式。基础值会按顺序依次计算。
        - ***复合{}***  一个修改方式。
          - `type` ***字符串***  修改的类型（操作符）。必须是`set`、`add`或`multiply`之一。
          - `value` ***数字***  修改的值。将被解析为一个double数值。
  - `default_enable` ***布尔值***  是否默认启用此规则。见“调试命令”获取更多信息。**可选**，默认为`true`。



### 调试命令

```
abmrule <规则ID> [enable|disable]
```

通过此命令，你可以启用或禁用一个规则。

**此命令用于调试。**

当规则被重载时，启用/禁用的状态会被重设为默认值，具体地，就是规则的JSON文件中定义的`default_enable`。
