esp-idf

1. 设置python环境变量 `python` 需要指定到.exe 
2. 设置esp-idf环境变量 `idf_path` 指定到程序目录即可 `${idf_path}/tools`
3. 执行 `install.bat / sh / ps1` 安装
4. 执行 `export.sh / bat / ps1` 设置环境变量
5. 执行 `python tools\idf_tools.py install-python-env` 安装虚拟环境

```text
C:\Users\UserName\.espressif\python_env\idf5.5_py3.11_env\Scripts
```

6. 如果 执行cmake的时候不走他给的虚拟环境，就在cmake的function里面设置
   ```cmake
   set(python "C:/Users/UserName/.espressif/python_env/idf5.5_py3.11_env/Scripts/python.exe")
   ```

7. 执行脚本也可以走他的虚拟环境
   ```shell
   C:\Users\UserName\.espressif\python_env\idf5.5_py3.11_env\Scripts\python.exe D:\app\esp-idf-v5.5.2\tools\idf.py
   ```

8. 卸载

   ```sh
   C:\Users\UserName\.espressif\python_env\idf5.5_py3.11_env\Scripts\python.exe D:\app\esp-idf-v5.5.2\tools\idf_tools.py uninstall
   ```

   