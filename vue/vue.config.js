const path = require('path')
const os = require('os')

// 获取本机局域网 IP
let localIp = 'localhost'
const interfaces = os.networkInterfaces()
for (const devName in interfaces) {
  const iface = interfaces[devName]
  for (let i = 0; i < iface.length; i++) {
    const alias = iface[i]
    if (alias.family === 'IPv4' && alias.address !== '127.0.0.1' && !alias.internal) {
      localIp = alias.address
    }
  }
}

module.exports = {
  // 配置需要转译的依赖包
  transpileDependencies: [
    '@stomp/stompjs'
  ],
  chainWebpack: config => {
    config
      .plugin('html')
      .tap(args => {
        args[0].title = '人口普查管理系统'
        return args
      })

    // 配置@别名指向src目录
    config.resolve.alias
      .set('@', path.resolve(__dirname, 'src'))

    // 移除console.log语句
    config.optimization.minimizer('terser').tap(options => {
      options[0].terserOptions.compress.drop_console = true
      return options
    })
  },
  devServer: {
    host: '0.0.0.0',
    public: `${localIp}:8080`,
    disableHostCheck: true,
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      '/data': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      '/statistics': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      '/employment': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      '/social-security': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      '/education': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      '/medical': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      '/resident': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      '/household': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      '/property': {
        target: 'http://localhost:9090',
        changeOrigin: true
      },
      '/vehicle': {
        target: 'http://localhost:9090',
        changeOrigin: true
      }
    }
  }
}