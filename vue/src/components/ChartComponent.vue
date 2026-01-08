<template>
  <div ref="chartRef" class="chart-container"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'ChartComponent',
  props: {
    // 图表配置项
    option: {
      type: Object,
      required: true
    },
    // 图表高度
    height: {
      type: String,
      default: '300px'
    },
    // 图表主题
    theme: {
      type: String,
      default: 'default'
    }
  },
  data() {
    return {
      chartInstance: null
    }
  },
  mounted() {
    this.initChart()
  },
  beforeDestroy() {
    this.destroyChart()
  },
  watch: {
    // 监听图表配置变化，重新渲染图表
    option: {
      handler(newOption) {
        this.updateChart(newOption)
      },
      deep: true
    }
  },
  methods: {
    // 初始化图表
    initChart() {
      if (!this.chartInstance) {
        this.chartInstance = echarts.init(this.$refs.chartRef, this.theme)
        // 设置图表配置
        this.chartInstance.setOption(this.option)
        // 添加窗口大小变化监听
        window.addEventListener('resize', this.handleResize)
      }
    },
    // 更新图表
    updateChart(option) {
      if (this.chartInstance) {
        this.chartInstance.setOption(option, true)
      }
    },
    // 销毁图表
    destroyChart() {
      if (this.chartInstance) {
        this.chartInstance.dispose()
        this.chartInstance = null
        window.removeEventListener('resize', this.handleResize)
      }
    },
    // 处理窗口大小变化
    handleResize() {
      if (this.chartInstance) {
        this.chartInstance.resize()
      }
    },
    // 获取图表实例
    getChartInstance() {
      return this.chartInstance
    }
  }
}
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 300px;
  transition: height 0.3s ease;
}
</style>