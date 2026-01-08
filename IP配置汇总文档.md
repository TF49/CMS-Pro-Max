# 项目IP配置汇总文档

本文档记录了项目中所有需要配置IP的地方，方便随时更改IP地址。

## 1. 后端服务配置

### 1.1 application.properties
- **文件路径**: `src/main/resources/application.properties`
- **端口配置**:
  ```
  server.port=9090
  ```
- **数据库配置**:
  ```
  spring.datasource.url=jdbc:mysql://localhost:3306/population?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
  ```

## 2. 小程序前端配置

### 2.1 request.js
- **文件路径**: `cms-miniprogram/utils/request.js`
- **API基础URL**:
  ```javascript
  const baseUrl = 'http://127.0.0.1:9090'
  ```
- **说明**: 
  - 开发环境使用本地地址，真机调试请将 IP 换成电脑的局域网 IP
  - 例如：`const baseUrl = 'http://192.168.x.x:9090'`

## 3. Web前端配置

### 3.1 vue.config.js
- **文件路径**: `vue/vue.config.js`
- **代理配置**:
  ```javascript
  devServer: {
    host: '0.0.0.0',
    public: `${localIp}:8080`,
    disableHostCheck: true,
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:9090',  // 后端API地址
        changeOrigin: true
      },
      '/data': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      // ... 其他多个API端点都指向 'http://localhost:9090'
    }
  }
  ```

### 3.2 request.js
- **文件路径**: `vue/src/utils/request.js`
- **基础URL配置**:
  ```javascript
  baseURL: process.env.NODE_ENV === 'production' ? '/api' : ''  // 生产环境使用/api前缀，开发环境使用代理
  ```

## 4. Android客户端配置

### 4.1 ApiService.java
- **文件路径**: `cmsandroid/app/src/main/java/com/example/cms_android/network/ApiService.java`
- **基础URL配置**:
  ```java
  private static final String BASE_URL = "http://10.0.2.2:9090/";  // Android模拟器访问本地主机的特殊IP
  private static final String API_BASE_URL = BASE_URL + "api/";
  ```
- **说明**:
  - `10.0.2.2` 是Android模拟器访问宿主机的特殊IP地址
  - 如果使用真机调试，需要改为宿主机的局域网IP

## 5. 相关文档

### 5.1 IP配置指南
- **文件路径**: `IP配置指南.md`
- **内容**: 包含了详细的IP配置说明和各种场景下的配置方法

## 配置建议

### 1. 局域网部署
- 后端启动后，获取服务器的局域网IP地址（如 `192.168.1.100`）
- 修改上述配置文件中的IP地址为实际的局域网IP

### 2. 真机调试
- Android: 将 `10.0.2.2` 替换为开发机的局域网IP
- 小程序: 将 `127.0.0.1` 替换为开发机的局域网IP
- Web端: 确保后端服务允许局域网访问

### 3. 生产环境
- Android和小程序: 配置为生产服务器的IP或域名
- Web端: 通常使用域名，配置反向代理

## 注意事项

1. 修改IP后需要重启相关服务
2. Android模拟器使用 `10.0.2.2` 访问宿主机
3. 确保防火墙允许相应端口的访问
4. 小程序真机调试时需要将IP地址改为开发机的局域网IP
5. 确保后端服务端口（9090）和前端服务端口（8080）未被占用