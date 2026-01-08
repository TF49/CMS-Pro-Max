# IP配置指南

## 1. 问题描述

在开发环境中，当电脑的网络IP地址变化时，会导致以下问题：
- 前端通过网络IP访问时出现跨域错误
- 微信小程序无法连接到后端API
- 其他设备无法访问本地开发的应用

本指南提供临时和永久解决方案，帮助您处理IP地址变化的情况。

## 2. 临时解决方案

### 2.1 后端CORS配置更新

当IP地址变化时，需要手动更新后端CORS配置：

**步骤1：找到CORS配置文件**
```
文件路径：src/main/java/com/example/cms/config/CorsConfig.java
```

**步骤2：更新allowedOrigins列表**
```java
registry.addMapping("/**")
        .allowedOrigins(
            "http://localhost:8080", 
            "http://127.0.0.1:8080",
            "http://新的IP地址:8080"  // 添加新的网络IP
        )
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
```

**步骤3：重启后端服务**
```bash
# 停止当前服务（如果运行中）
# 然后重新启动
mvn spring-boot:run
```

### 2.2 微信小程序IP配置

**步骤1：找到小程序配置文件**
```
文件路径：cms-miniprogram/utils/request.js
```

**步骤2：更新baseUrl**
```javascript
const baseUrl = 'http://新的IP地址:9090'; // CMS后端API地址
```

**步骤3：更新main.js中的全局配置**
```javascript
app.config.globalProperties.$global = {
  baseUrl: 'http://新的IP地址:9090' // CMS后端API地址
}
```

## 3. 永久解决方案

### 3.1 方案1：允许所有IP（开发环境推荐）

**优点**：简单方便，无需每次更新IP
**缺点**：安全性较低，仅适用于开发环境

**配置方法**：
```java
// 将allowedOrigins替换为allowedOriginPatterns
registry.addMapping("/**")
        .allowedOriginPatterns("http://*")  // 允许所有HTTP请求
        // 或更精确的匹配：.allowedOriginPatterns("http://*:8080")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
```

### 3.2 方案2：动态获取客户端IP（生产环境推荐）

**优点**：安全性高，自动适应各种IP
**缺点**：配置复杂，需要自定义CORS过滤器

**配置方法**：
```java
// 自定义CORS过滤器
@Component
public class DynamicCorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        
        // 获取请求来源
        String origin = req.getHeader("Origin");
        if (origin != null) {
            res.setHeader("Access-Control-Allow-Origin", origin);
        }
        
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "*");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Max-Age", "3600");
        
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(request, response);
        }
    }
}
```

### 3.3 方案3：配置文件管理IP列表

**优点**：灵活可控，无需重启服务
**缺点**：需要额外开发配置管理功能

**配置方法**：

1. **添加配置文件**：
   ```yaml
   # src/main/resources/application.yml
   cors:
     allowed-origins:
       - http://localhost:8080
       - http://127.0.0.1:8080
       - http://172.18.18.96:8080
   ```

2. **读取配置**：
   ```java
   @ConfigurationProperties(prefix = "cors")
   @Component
   @Data
   public class CorsProperties {
       private List<String> allowedOrigins;
   }
   ```

3. **应用配置**：
   ```java
   @Autowired
   private CorsProperties corsProperties;
   
   @Bean
   public WebMvcConfigurer corsConfigurer() {
       return new WebMvcConfigurer() {
           @Override
           public void addCorsMappings(CorsRegistry registry) {
               registry.addMapping("/**")
                       .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
                       .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                       .allowedHeaders("*")
                       .allowCredentials(true)
                       .maxAge(3600);
           }
       };
   }
   ```

### 3.4 方案4：使用Nginx反向代理

**优点**：统一管理，支持负载均衡
**缺点**：需要额外部署Nginx

**配置示例**：
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端代理
    location / {
        root /path/to/your/vue/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:9090/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 4. 前端IP配置

### 4.1 Vue项目

**开发环境**：
- 通过`vue.config.js`配置代理，无需修改IP
- 配置示例：
  ```javascript
  devServer: {
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true
      }
    }
  }
  ```

**生产环境**：
- 使用相对路径或通过环境变量配置
- 配置示例：
  ```javascript
  // .env.production
  VUE_APP_BASE_API = '/api'
  ```

### 4.2 微信小程序

**开发环境**：
- 建议使用NATAPP等工具生成固定域名
- 配置示例：
  ```javascript
  const baseUrl = 'https://your-natapp-domain.com';
  ```

**生产环境**：
- 使用固定域名
- 配置示例：
  ```javascript
  const baseUrl = 'https://api.your-domain.com';
  ```

## 5. 注意事项

1. **开发环境**：推荐使用方案1（允许所有IP），简单方便
2. **生产环境**：必须使用固定IP或域名，推荐使用方案2或方案4
3. **微信小程序**：必须使用HTTPS协议（生产环境）
4. **IP获取方法**：
   - Windows：`ipconfig`命令查看IPv4地址
   - macOS/Linux：`ifconfig`或`ip addr`命令
5. **测试方法**：
   - 使用`curl`命令测试API：`curl -X GET http://your-ip:9090/api/test`
   - 使用浏览器访问：`http://your-ip:8080`

## 6. 常见问题

### 6.1 为什么配置后仍然报错？

- 检查浏览器缓存，清除缓存后重试
- 检查后端服务是否重启成功
- 检查防火墙设置，确保端口8080和9090开放

### 6.2 微信小程序无法连接后端？

- 检查小程序开发工具中的"不校验合法域名"选项是否开启
- 检查后端是否支持HTTPS（生产环境）
- 检查小程序配置中的baseUrl是否正确

### 6.3 其他设备无法访问？

- 检查电脑防火墙设置，允许端口访问
- 检查路由器设置，确保同一局域网
- 检查IP地址是否正确（避免使用127.0.0.1）

## 7. 总结

| 解决方案 | 适用场景 | 优点 | 缺点 |
|---------|---------|------|------|
| 手动更新IP | 临时开发 | 简单 | 频繁更新 |
| 允许所有IP | 开发环境 | 方便 | 安全性低 |
| 动态获取IP | 生产环境 | 安全 | 配置复杂 |
| 配置文件管理 | 测试环境 | 灵活 | 需重启服务 |
| Nginx反向代理 | 生产环境 | 统一管理 | 额外部署 |

根据实际需求选择合适的解决方案，开发环境推荐使用"允许所有IP"，生产环境推荐使用"动态获取IP"或"Nginx反向代理"。