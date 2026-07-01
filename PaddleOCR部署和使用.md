# PaddleOCR部署和使用

## 资源

[gguf模型文件](https://huggingface.co/PaddlePaddle/PaddleOCR-VL-1.6-GGUF/tree/main)

[llama.cpp](https://github.com/ggml-org/llama.cpp/releases/tag/b9756)

## 部署

安装完成`llama.cpp`并下载完gguf后就可以使用`llama.cpp`启动服务了。[vlm部署](https://www.paddleocr.ai/latest/version3.x/pipeline_usage/PaddleOCR-VL.html#313)

```sh
llama-server \
	-m ../models/PaddleOCR-VL-1.6-GGUF.gguf \
	--mmproj ../models/PaddleOCR-VL-1.6-GGUF-mmproj.gguf \
	--host 0.0.0.0 \
	--port 8080 \
	--temp 0 \ #温度
	-ngl 0 # -ngl 0 使用纯CPU
```

```sh
llama-server -m ../models/PaddleOCR-VL-1.6-GGUF.gguf --mmproj ../models/PaddleOCR-VL-1.6-GGUF-mmproj.gguf --host 0.0.0.0 --port 8080 --temp 0 -ngl 0
```



## 上下文插入图片

访问api: `http://localhost:8080/v1/chat/completions`

```json
"messages": [
    {
        "role": "user",
        "content": [
            {
                "type": "text",
                "text": "这是一张表格图片，请仔细分析行列结构，然后用 Markdown 表格输出，表头和数据一一对应。"
            },
            {
                "type": "image_url",
                "image_url": {
                    "url": f"data:image/png;base64,{image_b64}"
                }
            }
        ]
    }
],
```

