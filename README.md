# benchmark

## MappedByteBuffer VS FileChannel

### 测试环境

- 处理器：2.5 GHz 四核Intel Core i7
- 内存：16 GB 1600 MHz DDR3
- SSD：APPLE SSD SM0512F
- 操作系统：macOS
- JVM：OpenJDK 17

运行时需要在 IntelliJ IDEA 的 `Run/Debug Configurations` 中添加如下 VM options ：

```
--add-exports=java.base/sun.nio.ch=ALL-UNNAMED
--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED
--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED
```

大家可以把自己的运行结果整理成 md 文件提交到这个工程目录下：`fileio/src/main/java/com/benchmark/fileio/report` 。

这样可以方便大家在不同的测试环境下比较 MappedByteBuffer 和 FileChannel 的文件读写性能。

### 提交的 MarkDown 文件格式

1. 测试环境
2. ReadWithPageCache 运行结果（截图即可）
3. WriteWithPageCache 运行结果（截图即可）
4. ReadWithOutPageCache 运行结果（截图即可）
5. WriteWithOutPageCache 运行结果（截图即可）
