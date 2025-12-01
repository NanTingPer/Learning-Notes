# Yolo识别

利用`Microsoft.ML.OnnxRuntime`包可以调用`onnx`模型进行图像识别。

1. 创建`InferenceSession`对象，传入模型所在路径

   ```cs
   using var inser = new InferenceSession(ModelPath);
   ```

2. 创建输入张量，参数`name`就是`onnx`模型的输入名称，使用`Netron`这个应用打开模型 或者直接输出模型就能看到

   ```cs
   NamedOnnxValue input = NamedOnnxValue.CreateFromTensor("images", NormalizeImage(imageBytes));
   ```

3. 由于模型可能有多个输入，因此`Run`的参数要求是一个集合，可以直接使用集合表达式

   ```cs
   using var result = inser.Run([input]);
   ```

4. `Run`完后的输出可以使用索引获取，输出顺序可打开查看，我这边 `[0]` 是检测框，我只需要获取检测框就可以，建议使用名称获取具体的输出

   ```cs
   var outputvalue1 = result[0];
   // 此输出一般是 [1, x, 8400]
   // 其中x
   // 0 => x	图片x位置    (x, y) => 左上角
   // 1 => y	图片y位置
   // 2 => h	框高
   // 3 => w	框宽
   // 4 => conf	目标置信度
   // 5 ... 类型概率
   var resultTensor = outputvalue1.AsTensor<float>();
   ```

5. 根据返回的结果，在原图上画框框就行

   