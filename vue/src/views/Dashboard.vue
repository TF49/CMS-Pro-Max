<template>
  <div class="dashboard common-container">
    <!-- 系统介绍区域 -->
    <el-card class="welcome-card">
      <div class="welcome-header">
        <h1>欢迎使用人口普查管理系统</h1>
        <p v-if="userInfo.role === 'ADMIN'">全面的人口数据管理与分析平台</p>
        <p v-else>便捷的个人数据管理平台</p>
        <!-- 添加当前日期时间显示 -->
        <div class="current-time">
          {{ currentTime }}
        </div>
        <!-- 添加祝福语 -->
        <div class="greeting">
          {{ greeting }}
        </div>
      </div>
    </el-card>
    
    <!-- 管理员完整数据卡片 -->
    <el-row v-if="userInfo.role === 'ADMIN'" :gutter="20" class="dashboard-header">
      <el-col :span="6">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-blue">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">总人口</div>
              <div class="card-number">{{ summaryData.residentCount || 0 | numberFormat }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-green">
              <el-icon :size="30"><HomeFilled /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">户籍数</div>
              <div class="card-number">{{ summaryData.householdCount || 0 | numberFormat }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-yellow">
              <el-icon :size="30"><OfficeBuilding /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">就业人数</div>
              <div class="card-number">{{ summaryData.employmentCount || 0 | numberFormat }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-red">
              <el-icon :size="30"><Medal /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">社保参保</div>
              <div class="card-number">{{ summaryData.socialSecurityCount || 0 | numberFormat }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-purple">
              <el-icon :size="30"><School /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">教育人数</div>
              <div class="card-number">{{ summaryData.educationCount || 0 | numberFormat }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-cyan">
              <el-icon :size="30"><FirstAidKit /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">医疗人数</div>
              <div class="card-number">{{ summaryData.medicalCount || 0 | numberFormat }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-orange">
              <el-icon :size="30"><House /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">房产数量</div>
              <div class="card-number">{{ summaryData.propertyCount || 0 | numberFormat }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-indigo">
              <el-icon :size="30"><Van /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">车辆数量</div>
              <div class="card-number">{{ summaryData.vehicleCount || 0 | numberFormat }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 普通用户简化数据卡片 -->
    <el-row v-else :gutter="20" class="dashboard-header">
      <el-col :span="12">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-blue">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">居民信息</div>
              <div class="card-description">管理您的个人及家庭信息</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-green">
              <el-icon :size="30"><HomeFilled /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">户籍管理</div>
              <div class="card-description">管理您的户籍相关信息</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-yellow">
              <el-icon :size="30"><Briefcase /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">就业社保</div>
              <div class="card-description">管理您的就业和社保信息</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon bg-orange">
              <el-icon :size="30"><House /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-title">财产管理</div>
              <div class="card-description">管理您的房产和车辆信息</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 功能模块介绍区域 -->
    <div class="modules-section">
      <h2 class="section-title">功能模块</h2>
      
      <el-row :gutter="20">
        <!-- 人口管理模块 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="module-card" shadow="hover">
            <div class="module-header">
              <el-icon class="module-icon"><User /></el-icon>
              <h3>人口管理</h3>
            </div>
            <div class="module-description">
              <p>管理居民基本信息和户籍资料，包括人口统计、户籍变动等功能。</p>
            </div>
            <div class="module-actions improved-actions">
              <el-button type="primary" @click="navigateTo('/population/household')" class="module-button">
                <el-icon><HomeFilled /></el-icon>
                <span class="button-text">户籍管理</span>
              </el-button>
              <el-button type="success" @click="navigateTo('/population/resident')" class="module-button">
                <el-icon><User /></el-icon>
                <span class="button-text">居民管理</span>
              </el-button>
            </div>
          </el-card>
        </el-col>
        
        <!-- 社会管理模块 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="module-card" shadow="hover">
            <div class="module-header">
              <el-icon class="module-icon"><School /></el-icon>
              <h3>社会管理</h3>
            </div>
            <div class="module-description">
              <p>管理居民教育信息和医疗健康数据，提供教育资源和医疗服务统计。</p>
            </div>
            <div class="module-actions improved-actions">
              <el-button type="warning" @click="navigateTo('/social/education')" class="module-button">
                <el-icon><School /></el-icon>
                <span class="button-text">教育管理</span>
              </el-button>
              <el-button type="danger" @click="navigateTo('/social/medical')" class="module-button">
                <el-icon><FirstAidKit /></el-icon>
                <span class="button-text">医疗管理</span>
              </el-button>
            </div>
          </el-card>
        </el-col>
        
        <!-- 就业社保模块 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="module-card" shadow="hover">
            <div class="module-header">
              <el-icon class="module-icon"><Briefcase /></el-icon>
              <h3>就业社保</h3>
            </div>
            <div class="module-description">
              <p>管理居民就业信息和社会保险数据，提供就业趋势分析和社保统计。</p>
            </div>
            <div class="module-actions improved-actions">
              <el-button type="info" @click="navigateTo('/employment-security/employment')" class="module-button">
                <el-icon><Briefcase /></el-icon>
                <span class="button-text">就业管理</span>
              </el-button>
              <el-button type="primary" @click="navigateTo('/employment-security/social-security')" class="module-button">
                <el-icon><Medal /></el-icon>
                <span class="button-text">社保管理</span>
              </el-button>
            </div>
          </el-card>
        </el-col>
        
        <!-- 财产管理模块 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="module-card" shadow="hover">
            <div class="module-header">
              <el-icon class="module-icon"><House /></el-icon>
              <h3>财产管理</h3>
            </div>
            <div class="module-description">
              <p>管理居民房产和车辆信息，提供财产统计和分布分析功能。</p>
            </div>
            <div class="module-actions improved-actions">
              <el-button type="success" @click="navigateTo('/property/property-list')" class="module-button">
                <el-icon><House /></el-icon>
                <span class="button-text">房产管理</span>
              </el-button>
              <el-button type="warning" @click="navigateTo('/property/vehicle')" class="module-button">
                <el-icon><Van /></el-icon>
                <span class="button-text">车辆管理</span>
              </el-button>
            </div>
          </el-card>
        </el-col>
        
        <!-- 数据管理模块 -->
        <el-col :xs="24" :sm="24" :md="24" :lg="24" v-if="userInfo.role === 'ADMIN'">
          <el-card class="module-card" shadow="hover">
            <div class="module-header">
              <el-icon class="module-icon"><DataAnalysis /></el-icon>
              <h3>数据管理</h3>
            </div>
            <div class="module-description">
              <p>提供全面的数据统计和分析功能，包括人口趋势、年龄分布、教育医疗等多维度数据可视化。</p>
            </div>
            <div class="module-actions center-actions">
              <el-button type="primary" @click="navigateTo('/data/detail')" class="module-button large-button">
                <el-icon><DataAnalysis /></el-icon>
                查看数据分析
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 数据可视化区域 -->
    <div class="data-visualization-section" v-if="userInfo.role === 'ADMIN'">
      <h2 class="section-title">数据可视化</h2>
      
      <!-- 人口统计图表 -->
      <el-row :gutter="20">
        <el-col :span="12" :xs="24" :sm="12">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span>各地区人口统计</span>
            </div>
            <div ref="populationByRegionChart" class="chart-container"></div>
          </el-card>
        </el-col>
        
        <el-col :span="12" :xs="24" :sm="12">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span>年龄分布情况</span>
            </div>
            <div ref="ageDistributionChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span>近5年人口增长趋势</span>
            </div>
            <div ref="populationTrendChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 教育医疗图表 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12" :xs="24" :sm="12">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span>教育程度分布</span>
            </div>
            <div ref="educationLevelChart" class="chart-container"></div>
          </el-card>
        </el-col>
        
        <el-col :span="12" :xs="24" :sm="12">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span>医疗保险类型分布</span>
            </div>
            <div ref="medicalInsuranceChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 就业社保图表 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12" :xs="24" :sm="12">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span>就业情况统计</span>
            </div>
            <div ref="employmentChart" class="chart-container"></div>
          </el-card>
        </el-col>
        
        <el-col :span="12" :xs="24" :sm="12">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span>社保参保情况</span>
            </div>
            <div ref="socialSecurityChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 财产统计图表 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12" :xs="24" :sm="12">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span>房产类型分布</span>
            </div>
            <div ref="propertyChart" class="chart-container"></div>
          </el-card>
        </el-col>
        
        <el-col :span="12" :xs="24" :sm="12">
          <el-card class="chart-card" shadow="hover">
            <div slot="header" class="card-header">
              <span>车辆品牌分布</span>
            </div>
            <div ref="vehicleChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <el-row :gutter="20" class="dashboard-content" v-if="userInfo.role === 'ADMIN'">
      <el-col :span="24">
        <el-card class="data-card" shadow="hover">
          <div slot="header" class="card-header">
            <span>最新数据</span>
            <span class="update-time">数据更新时间：{{ summaryData.updateTime || '暂无数据' }}</span>
          </div>
          <div class="common-table-wrapper">
      <el-table :data="tableData" class="common-table" stripe>
            <el-table-column prop="date" label="日期" width="120"></el-table-column>
            <el-table-column prop="name" label="姓名" width="120"></el-table-column>
            <el-table-column prop="type" label="类型" width="120"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag v-if="scope && scope.row && scope.row.status" :type="scope.row.status === '已完成' ? 'success' : 'warning'">
                  {{ scope.row.status }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="content" label="内容" class-name="content-column"></el-table-column>
          </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { User, HomeFilled, OfficeBuilding, Medal, School, FirstAidKit, Briefcase, Ticket, House, DataAnalysis, Tools } from '@element-plus/icons-vue'
import { 
  getPopulationByRegion,
  getAgeDistribution,
  getEducationLevel,
  getMedicalInsurance,
  getPopulationTrend,
  getSummary,
  getIndustryDistribution,
  getSocialSecurityCoverage,
  getPropertyTypeDistribution,
  getVehicleBrandDistribution,
  getEmploymentStatusDistribution,
  getLatestData
} from '@/api/statistics'

export default {
  name: 'Dashboard',
  components: {
    User,
    HomeFilled,
    OfficeBuilding,
    Medal,
    School,
    FirstAidKit,
    Briefcase,
    Ticket,
    House,
    DataAnalysis
  },
  data() {
    return {
      currentTime: '',
      greeting: '祝您工作愉快，生活美满！',
      summaryData: {
        residentCount: 0,
        householdCount: 0,
        employmentCount: 0,
        socialSecurityCount: 0,
        educationCount: 0,
        medicalCount: 0,
        propertyCount: 0,
        vehicleCount: 0,
        updateTime: ''
      },
      userInfo: {
        username: '',
        role: 'USER'
      },
      tableData: []
    }
  },
  created() {
    // 从localStorage获取用户信息
    this.getUserInfo()
  },
  mounted() {
    this.updateCurrentTime()
    this.timeInterval = setInterval(this.updateCurrentTime, 1000) // 每秒更新时间
    
    // 根据用户角色决定是否初始化图表和加载数据
    if (this.userInfo.role === 'ADMIN') {
      this.initCharts() // 管理员初始化图表
      this.loadAllData() // 管理员加载所有数据
    } else {
      // 普通用户不初始化图表，避免DOM引用错误
      // 普通用户不加载统计数据，避免权限错误
      this.$message.success('登录成功，欢迎使用人口普查管理系统！')
    }
  },
  beforeDestroy() {
    // 清除定时器
    if (this.timeInterval) {
      clearInterval(this.timeInterval)
    }
    // 只有管理员才销毁图表实例，普通用户没有初始化图表
    if (this.userInfo.role === 'ADMIN') {
      this.destroyAllCharts()
    }
  },
  filters: {
    numberFormat(value) {
      if (!value) return '0'
      return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }
  },
  methods: {
    updateCurrentTime() {
      const now = new Date()
      this.currentTime = now.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        weekday: 'long'
      })
    },
    
    loadSummaryData() {
      getSummary().then(response => {
        this.summaryData = response.data
      }).catch(error => {
        console.error('获取系统统计摘要失败:', error)
        this.$message.error('获取系统统计摘要失败')
      })
    },
    
    initCharts() {
      // 初始化各地区人口统计图（水平条形图）
      this.populationByRegionChart = echarts.init(this.$refs.populationByRegionChart)
      const populationByRegionOption = {
        title: {
          text: '各地区人口统计',
          textStyle: {
            fontSize: 18,
            fontWeight: 'bold',
            color: '#333'
          },
          left: 'center',
          top: 10
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
            shadowStyle: {
              color: 'rgba(128, 179, 246, 0.3)'
            }
          },
          formatter: function (params) {
            return `
              <div style="padding: 8px 12px;">
                <div style="font-weight: bold; margin-bottom: 4px;">${params[0].name}</div>
                <div style="color: #188df0;">
                  <span style="display: inline-block; width: 10px; height: 10px; background: #188df0; border-radius: 50%; margin-right: 6px;"></span>
                  人口数量: <span style="font-weight: bold;">${params[0].value.toLocaleString()}</span> 人
                </div>
              </div>
            `
          },
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#188df0',
          borderWidth: 1,
          borderRadius: 8,
          textStyle: {
            color: '#333'
          }
        },
        grid: {
          left: '1%',
          right: '8%',
          bottom: '3%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          boundaryGap: [0, 0.01],
          axisTick: {
            show: false
          },
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0',
              type: 'dashed'
            }
          },
          axisLabel: {
            formatter: '{value} 人',
            color: '#666'
          }
        },
        yAxis: {
          type: 'category',
          data: [],
          axisTick: {
            alignWithLabel: true,
            lineStyle: {
              color: '#ccc'
            }
          },
          axisLabel: {
            fontSize: 11,
            color: '#666',
            fontWeight: 500,
            formatter: function(value) {
              // 直接显示完整地区名称
              return value
            },
            align: 'right',
            padding: [0, 10, 0, 0],
            width: 120
          },
          axisLine: {
            lineStyle: {
              color: '#e0e0e0'
            }
          }
        },
        series: [
          {
            name: '人口数量',
            type: 'bar',
            barWidth: '50%',
            data: [],
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#83bff6' },
                { offset: 1, color: '#188df0' }
              ]),
              borderRadius: [0, 6, 6, 0],
              borderWidth: 1,
              borderColor: '#188df0'
            },
            emphasis: {
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: '#63aaf5' },
                  { offset: 1, color: '#2378f7' }
                ]),
                shadowBlur: 10,
                shadowOffsetX: 2,
                shadowOffsetY: 0,
                shadowColor: 'rgba(24, 141, 240, 0.3)'
              }
            },
            label: {
              show: true,
              position: 'right',
              align: 'center',
              color: '#188df0',
              fontSize: 12,
              fontWeight: 'bold',
              formatter: '{c}',
              offset: [5, 0]
            },
            animationDelay: function (idx) {
              // 交错动画效果
              return idx * 50;
            },
            animationEasing: 'elasticOut'
          }
        ],
        animationDuration: 1500,
        animationEasing: 'cubicOut'
      }
      this.populationByRegionChart.setOption(populationByRegionOption)
      
      // 添加点击事件
      this.populationByRegionChart.on('click', (params) => {
        this.$message({
          message: `您查看了 ${params.name} 的人口数据: ${params.value} 人`,
          type: 'success',
          duration: 2000
        })
      })
      
      // 初始化年龄分布图（环形图）
      this.ageDistributionChart = echarts.init(this.$refs.ageDistributionChart)
      const ageDistributionOption = {
        title: {
          text: '年龄分布情况',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c}人 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'middle'
        },
        series: [
          {
            name: '年龄分布',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}: {d}%',
              position: 'outside'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '14',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: true
            },
            data: []
          }
        ]
      }
      this.ageDistributionChart.setOption(ageDistributionOption)
      
      // 添加点击事件
      this.ageDistributionChart.on('click', (params) => {
        this.$message.info(`您点击了${params.name}，人数为${params.value}人，占比${params.percent.toFixed(1)}%`)
      })
      
      // 初始化教育程度分布图（堆叠横向条形图）
      this.educationLevelChart = echarts.init(this.$refs.educationLevelChart)
      const educationLevelOption = {
        title: {
          text: '教育程度分布',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function (params) {
            let total = 0
            params.forEach(item => {
              total += item.value
            })
            let tooltip = params[0].name + '<br/>'
            params.forEach(item => {
              tooltip += item.marker + item.seriesName + ': ' + item.value + '人 (' + (item.value/total*100).toFixed(1) + '%)<br/>'
            })
            return tooltip
          }
        },
        legend: {
          data: ['男性', '女性'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: {
          type: 'category',
          data: []
        },
        series: [
          {
            name: '男性',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            itemStyle: {
              color: '#409EFF'
            },
            data: []
          },
          {
            name: '女性',
            type: 'bar',
            stack: 'total',
            label: {
              show: true
            },
            emphasis: {
              focus: 'series'
            },
            itemStyle: {
              color: '#F56C6C'
            },
            data: []
          }
        ]
      }
      this.educationLevelChart.setOption(educationLevelOption)
      
      // 添加点击事件
      this.educationLevelChart.on('click', (params) => {
        this.$message.info(`您点击了${params.name}的${params.seriesName}，人数为${params.value}人`)
      })
      
      // 初始化医疗保险类型分布图（玫瑰图）
      this.medicalInsuranceChart = echarts.init(this.$refs.medicalInsuranceChart)
      const medicalInsuranceOption = {
        title: {
          text: '医疗保险类型分布',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c}人 ({d}%)'
        },
        legend: {
          orient: 'horizontal',
          bottom: 10
        },
        series: [
          {
            name: '保险类型',
            type: 'pie',
            radius: ['30%', '70%'],
            center: ['50%', '50%'],
            roseType: 'radius',
            itemStyle: {
              borderRadius: 8,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}: {d}%'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '14',
                fontWeight: 'bold'
              }
            },
            data: []
          }
        ]
      }
      this.medicalInsuranceChart.setOption(medicalInsuranceOption)
      
      // 添加点击事件
      this.medicalInsuranceChart.on('click', (params) => {
        this.$message.info(`您点击了${params.name}，人数为${params.value}人，占比${params.percent.toFixed(1)}%`)
      })
      
      // 初始化人口增长趋势图（面积图）
      this.populationTrendChart = echarts.init(this.$refs.populationTrendChart)
      const populationTrendOption = {
        title: {
          text: '近5年人口增长趋势',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          formatter: function (params) {
            let tooltip = params[0].name + '<br/>'
            params.forEach(item => {
              tooltip += item.marker + item.seriesName + ': ' + item.value.toLocaleString() + '人<br/>'
            })
            return tooltip
          }
        },
        legend: {
          data: ['总人口', '男性', '女性'],
          top: 30
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: []
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: function (value) {
              return value.toLocaleString()
            }
          }
        },
        series: [
          {
            name: '总人口',
            type: 'line',
            data: [],
            smooth: true,
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.01)' }
              ])
            }
          },
          {
            name: '男性',
            type: 'line',
            data: [],
            smooth: true,
            itemStyle: {
              color: '#409EFF'
            },
            lineStyle: {
              type: 'dashed'
            }
          },
          {
            name: '女性',
            type: 'line',
            data: [],
            smooth: true,
            itemStyle: {
              color: '#F56C6C'
            },
            lineStyle: {
              type: 'dashed'
            }
          }
        ]
      }
      this.populationTrendChart.setOption(populationTrendOption)
      
      // 添加点击事件
      this.populationTrendChart.on('click', (params) => {
        this.$message.info(`您点击了${params.name}的${params.seriesName}，人口数量为${params.value.toLocaleString()}人`)
      })
      
      // 初始化就业情况图表（仪表盘）
      this.employmentChart = echarts.init(this.$refs.employmentChart)
      const employmentOption = {
        title: {
          text: '就业率',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        series: [
          {
            type: 'gauge',
            progress: {
              show: true,
              width: 18
            },
            axisLine: {
              lineStyle: {
                width: 18,
                color: [
                  [0.3, '#67C23A'],
                  [0.7, '#409EFF'],
                  [1, '#F56C6C']
                ]
              }
            },
            axisTick: {
              show: false
            },
            splitLine: {
              length: 15,
              lineStyle: {
                width: 2,
                color: '#999'
              }
            },
            axisLabel: {
              distance: 25,
              color: '#999',
              fontSize: 10
            },
            anchor: {
              show: true,
              showAbove: true,
              size: 18,
              itemStyle: {
                borderWidth: 10
              }
            },
            title: {
              show: false
            },
            detail: {
              valueAnimation: true,
              fontSize: 20,
              formatter: '{value}%'
            },
            data: [{ value: 0 }]
          }
        ]
      }
      this.employmentChart.setOption(employmentOption)
      
      // 添加点击事件
      this.employmentChart.on('click', (params) => {
        this.$message.info(`当前就业率为${params.value}%`)
      })
      
      // 初始化社保情况图表（雷达图）
      this.socialSecurityChart = echarts.init(this.$refs.socialSecurityChart)
      const socialSecurityOption = {
        title: {
          text: '社保参保情况',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        radar: {
          indicator: [
            { name: '养老保险', max: 1500 },
            { name: '医疗保险', max: 1500 },
            { name: '失业保险', max: 1500 },
            { name: '工伤保险', max: 1500 },
            { name: '生育保险', max: 1500 }
          ],
          radius: '60%'
        },
        series: [
          {
            name: '参保人数',
            type: 'radar',
            areaStyle: {
              opacity: 0.3
            },
            itemStyle: {
              color: '#67C23A'
            },
            data: [
              {
                value: [],
                name: '参保人数'
              }
            ]
          }
        ]
      }
      this.socialSecurityChart.setOption(socialSecurityOption)
      
      // 添加点击事件
      this.socialSecurityChart.on('click', (params) => {
        this.$message.info(`您点击了${params.name}，参保率分别为${params.value}%`)
      })
      
      // 初始化房产情况图表（嵌套饼图）
      this.propertyChart = echarts.init(this.$refs.propertyChart)
      const propertyOption = {
        title: {
          text: '房产类型分布',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'middle'
        },
        series: [
          {
            name: '房产类型',
            type: 'pie',
            selectedMode: 'single',
            radius: [0, '30%'],
            label: {
              position: 'inner',
              fontSize: 12
            },
            labelLine: {
              show: false
            },
            data: []
          },
          {
            name: '房产类型',
            type: 'pie',
            radius: ['40%', '55%'],
            label: {
              formatter: '{b}: {d}%'
            },
            data: []
          }
        ]
      }
      this.propertyChart.setOption(propertyOption)
      
      // 添加点击事件
      this.propertyChart.on('click', (params) => {
        this.$message.info(`您点击了${params.name}，数量为${params.value}套，占比${params.percent.toFixed(1)}%`)
      })
      
      // 初始化车辆情况图表（瀑布图）
      this.vehicleChart = echarts.init(this.$refs.vehicleChart)
      const vehicleOption = {
        title: {
          text: '车辆品牌分布',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          },
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function (params) {
            return params[0].name + ': ' + params[0].value + ' 辆'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '数量',
            type: 'bar',
            barWidth: '40%',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 1, color: '#188df0' }
              ])
            },
            label: {
              show: true,
              position: 'top',
              formatter: '{c} 辆'
            },
            data: []
          }
        ]
      }
      this.vehicleChart.setOption(vehicleOption)
      
      // 添加点击事件
      this.vehicleChart.on('click', (params) => {
        this.$message.info(`您点击了${params.name}，数量为${params.value}辆`)
      })
    },
    
    loadChartData() {
      // 加载各地区人口统计数据
      getPopulationByRegion().then(response => {
        const data = response.data
        this.populationByRegionChart.setOption({
          yAxis: {
            data: data.regions
          },
          series: [
            {
              name: '人口数量',
              data: data.populations
            }
          ]
        })
      }).catch(error => {
        console.error('获取各地区人口分布数据失败:', error)
        this.$message.error('获取各地区人口分布数据失败')
      })
      
      // 加载年龄分布数据
      getAgeDistribution().then(response => {
        const data = response.data
        this.ageDistributionChart.setOption({
          series: [
            {
              name: '年龄分布',
              data: data
            }
          ]
        })
      }).catch(error => {
        console.error('获取年龄分布数据失败:', error)
        this.$message.error('获取年龄分布数据失败')
      })
      
      // 加载教育程度分布数据
      getEducationLevel().then(response => {
        const data = response.data
        this.educationLevelChart.setOption({
          yAxis: {
            data: data.levels
          },
          series: [
            {
              name: '男性',
              data: data.maleData
            },
            {
              name: '女性',
              data: data.femaleData
            }
          ]
        })
      }).catch(error => {
        console.error('获取教育程度分布数据失败:', error)
        this.$message.error('获取教育程度分布数据失败')
      })
      
      // 加载医疗保险类型分布数据
      getMedicalInsurance().then(response => {
        const data = response.data
        this.medicalInsuranceChart.setOption({
          series: [
            {
              name: '保险类型',
              data: data
            }
          ]
        })
      }).catch(error => {
        console.error('获取医疗保险类型分布数据失败:', error)
        this.$message.error('获取医疗保险类型分布数据失败')
      })
      
      // 加载人口增长趋势数据
      getPopulationTrend().then(response => {
        const data = response.data
        this.populationTrendChart.setOption({
          xAxis: {
            data: data.years
          },
          series: [
            {
              name: '总人口',
              data: data.totalData
            },
            {
              name: '男性',
              data: data.maleData
            },
            {
              name: '女性',
              data: data.femaleData
            }
          ]
        })
      }).catch(error => {
        console.error('获取人口增长趋势数据失败:', error)
        this.$message.error('获取人口增长趋势数据失败')
        // 清空图表数据，确保只显示来自数据库的数据
        this.populationTrendChart.setOption({
          xAxis: {
            data: []
          },
          series: [
            {
              name: '总人口',
              data: []
            },
            {
              name: '男性',
              data: []
            },
            {
              name: '女性',
              data: []
            }
          ]
        })
      })
      
      // 加载就业数据图表
      getEmploymentStatusDistribution().then(response => {
        const data = response.data
        // 计算就业率（在职人数/总人数）
        const employedItem = data.find(item => item.name === '在职')
        const employed = employedItem ? employedItem.value : 0
        const total = data.reduce((sum, item) => sum + item.value, 0)
        const employmentRate = total > 0 ? Math.round((employed / total) * 100) : 0
        
        this.employmentChart.setOption({
          series: [
            {
              data: [{ value: employmentRate }]
            }
          ]
        })
      }).catch(error => {
        console.error('获取就业状态分布数据失败:', error)
        this.$message.error('获取就业状态分布数据失败')
        // 清空图表数据，确保只显示来自数据库的数据
        this.employmentChart.setOption({
          series: [
            {
              data: [{ value: 0 }]
            }
          ]
        })
      })
      
      // 加载社保数据图表
      getSocialSecurityCoverage().then(response => {
        const data = response.data
        // 优化雷达图配置，让数据更加饱满，对比更加鲜明
        this.socialSecurityChart.setOption({
          radar: {
            indicator: [
              { name: '养老保险', max: Math.max(...data.coverageRates) + 10 },
              { name: '医疗保险', max: Math.max(...data.coverageRates) + 10 },
              { name: '失业保险', max: Math.max(...data.coverageRates) + 10 },
              { name: '工伤保险', max: Math.max(...data.coverageRates) + 10 },
              { name: '生育保险', max: Math.max(...data.coverageRates) + 10 }
            ],
            radius: '60%'
          },
          series: [
            {
              name: '参保率',
              type: 'radar',
              areaStyle: {
                opacity: 0.3
              },
              itemStyle: {
                color: '#67C23A'
              },
              lineStyle: {
                width: 2
              },
              symbol: 'circle',
              symbolSize: 6,
              data: [
                {
                  value: data.coverageRates,
                  name: '参保率(%)'
                }
              ]
            }
          ]
        })
      }).catch(error => {
        console.error('获取社保参保率数据失败:', error)
        this.$message.error('获取社保参保率数据失败')
        // 清空图表数据，确保只显示来自数据库的数据
        this.socialSecurityChart.setOption({
          radar: {
            indicator: [
              { name: '养老保险', max: 100 },
              { name: '医疗保险', max: 100 },
              { name: '失业保险', max: 100 },
              { name: '工伤保险', max: 100 },
              { name: '生育保险', max: 100 }
            ],
            radius: '60%'
          },
          series: [
            {
              name: '参保率',
              type: 'radar',
              areaStyle: {
                opacity: 0.3
              },
              itemStyle: {
                color: '#67C23A'
              },
              lineStyle: {
                width: 2
              },
              symbol: 'circle',
              symbolSize: 6,
              data: [
                {
                  value: [0, 0, 0, 0, 0],
                  name: '参保率(%)'
                }
              ]
            }
          ]
        })
      })
      
      // 加载房产数据图表
      getPropertyTypeDistribution().then(response => {
        const data = response.data
        this.propertyChart.setOption({
          series: [
            {
              name: '房产类型',
              data: data.map(item => ({...item, selected: item.value > 200}))
            },
            {
              name: '房产类型',
              data: data
            }
          ]
        })
      }).catch(error => {
        console.error('获取房产类型分布数据失败:', error)
        this.$message.error('获取房产类型分布数据失败')
        // 清空图表数据，确保只显示来自数据库的数据
        this.propertyChart.setOption({
          series: [
            {
              name: '房产类型',
              data: []
            },
            {
              name: '房产类型',
              data: []
            }
          ]
        })
      })
      
      // 加载车辆数据图表
      getVehicleBrandDistribution().then(response => {
        const data = response.data
        const brands = data.map(item => item.name)
        const counts = data.map(item => item.value)
        this.vehicleChart.setOption({
          xAxis: {
            data: brands
          },
          series: [
            {
              name: '数量',
              data: counts
            }
          ]
        })
      }).catch(error => {
        console.error('获取车辆品牌分布数据失败:', error)
        this.$message.error('获取车辆品牌分布数据失败')
        // 清空图表数据，确保只显示来自数据库的数据
        this.vehicleChart.setOption({
          xAxis: {
            data: []
          },
          series: [
            {
              name: '数量',
              data: []
            }
          ]
        })
      })
      
      // 加载最新数据
      getLatestData().then(response => {
        const data = response.data
        this.tableData = data
      }).catch(error => {
        console.error('获取最新数据失败:', error)
        this.$message.error('获取最新数据失败')
      })
    },
    
    // 获取用户信息
    getUserInfo() {
      try {
        // 从localStorage获取用户信息
        const userInfoStr = localStorage.getItem('userInfo')
        if (userInfoStr && userInfoStr !== 'null') { // 确保不是null或'null'字符串
          this.userInfo = JSON.parse(userInfoStr)
        }
        // 如果userInfoStr为空或'null'，则使用默认的USER角色
      } catch (error) {
        console.error('解析用户信息失败:', error)
        this.userInfo = { username: 'user', role: 'USER' } // 默认使用普通用户角色
      }
    },
    
    // 导航方法
    navigateTo(path) {
      this.$router.push(path)
    },
    
    // 合并加载所有数据，减少重复请求
    loadAllData() {
      // 使用Promise.all并行加载数据，提高加载速度
      Promise.all([
        getSummary(),
        getPopulationByRegion(),
        getAgeDistribution(),
        getEducationLevel(),
        getMedicalInsurance(),
        getPopulationTrend(),
        getEmploymentStatusDistribution(),
        getSocialSecurityCoverage(),
        getPropertyTypeDistribution(),
        getVehicleBrandDistribution(),
        getLatestData()
      ]).then(results => {
        // 处理返回结果
        const [
          summaryResult,
          populationByRegionResult,
          ageDistributionResult,
          educationLevelResult,
          medicalInsuranceResult,
          populationTrendResult,
          employmentStatusDistributionResult,
          socialSecurityCoverageResult,
          propertyTypeDistributionResult,
          vehicleBrandDistributionResult,
          latestDataResult
        ] = results
        
        // 更新系统统计摘要
        this.summaryData = summaryResult.data
        
        // 更新各地区人口统计图表
        const populationByRegionData = populationByRegionResult.data
        this.populationByRegionChart.setOption({
          yAxis: {
            data: populationByRegionData.regions
          },
          series: [
            {
              name: '人口数量',
              data: populationByRegionData.populations
            }
          ]
        })
        
        // 更新年龄分布图表
        const ageDistributionData = ageDistributionResult.data
        this.ageDistributionChart.setOption({
          series: [
            {
              name: '年龄分布',
              data: ageDistributionData
            }
          ]
        })
        
        // 更新教育程度分布图表
        const educationLevelData = educationLevelResult.data
        this.educationLevelChart.setOption({
          yAxis: {
            data: educationLevelData.levels
          },
          series: [
            {
              name: '男性',
              data: educationLevelData.maleData
            },
            {
              name: '女性',
              data: educationLevelData.femaleData
            }
          ]
        })
        
        // 更新医疗保险类型分布图表
        const medicalInsuranceData = medicalInsuranceResult.data
        this.medicalInsuranceChart.setOption({
          series: [
            {
              name: '保险类型',
              data: medicalInsuranceData
            }
          ]
        })
        
        // 更新人口增长趋势图表
        const populationTrendData = populationTrendResult.data
        this.populationTrendChart.setOption({
          xAxis: {
            data: populationTrendData.years
          },
          series: [
            {
              name: '总人口',
              data: populationTrendData.totalData
            },
            {
              name: '男性',
              data: populationTrendData.maleData
            },
            {
              name: '女性',
              data: populationTrendData.femaleData
            }
          ]
        })
        
        // 更新就业数据图表
        const employmentStatusDistributionData = employmentStatusDistributionResult.data
        const employedItem = employmentStatusDistributionData.find(item => item.name === '在职')
        const employed = employedItem ? employedItem.value : 0
        const total = employmentStatusDistributionData.reduce((sum, item) => sum + item.value, 0)
        const employmentRate = total > 0 ? Math.round((employed / total) * 100) : 0
        this.employmentChart.setOption({
          series: [
            {
              data: [{ value: employmentRate }]
            }
          ]
        })
        
        // 更新社保数据图表
        const socialSecurityCoverageData = socialSecurityCoverageResult.data
        this.socialSecurityChart.setOption({
          radar: {
            indicator: [
              { name: '养老保险', max: Math.max(...socialSecurityCoverageData.coverageRates) + 10 },
              { name: '医疗保险', max: Math.max(...socialSecurityCoverageData.coverageRates) + 10 },
              { name: '失业保险', max: Math.max(...socialSecurityCoverageData.coverageRates) + 10 },
              { name: '工伤保险', max: Math.max(...socialSecurityCoverageData.coverageRates) + 10 },
              { name: '生育保险', max: Math.max(...socialSecurityCoverageData.coverageRates) + 10 }
            ],
            radius: '60%'
          },
          series: [
            {
              name: '参保率',
              type: 'radar',
              areaStyle: {
                opacity: 0.3
              },
              itemStyle: {
                color: '#67C23A'
              },
              lineStyle: {
                width: 2
              },
              symbol: 'circle',
              symbolSize: 6,
              data: [
                {
                  value: socialSecurityCoverageData.coverageRates,
                  name: '参保率(%)'
                }
              ]
            }
          ]
        })
        
        // 更新房产数据图表
        const propertyTypeDistributionData = propertyTypeDistributionResult.data
        this.propertyChart.setOption({
          series: [
            {
              name: '房产类型',
              data: propertyTypeDistributionData.map(item => ({...item, selected: item.value > 200}))
            },
            {
              name: '房产类型',
              data: propertyTypeDistributionData
            }
          ]
        })
        
        // 更新车辆数据图表
        const vehicleBrandDistributionData = vehicleBrandDistributionResult.data
        const brands = vehicleBrandDistributionData.map(item => item.name)
        const counts = vehicleBrandDistributionData.map(item => item.value)
        this.vehicleChart.setOption({
          xAxis: {
            data: brands
          },
          series: [
            {
              name: '数量',
              data: counts
            }
          ]
        })
        
        // 更新最新数据
        this.tableData = latestDataResult.data
      }).catch(error => {
        console.error('获取数据失败:', error)
        this.$message.error('获取数据失败')
      })
    },
    
    // 销毁所有图表实例
    destroyAllCharts() {
      // 图表实例列表 - 只包含实际创建的图表实例
      const chartInstances = [
        'populationByRegionChart',
        'ageDistributionChart',
        'educationLevelChart',
        'medicalInsuranceChart',
        'populationTrendChart',
        'employmentChart',
        'socialSecurityChart',
        'propertyChart',
        'vehicleChart'
      ]
      
      // 遍历销毁所有图表实例
      chartInstances.forEach(chartName => {
        if (this[chartName]) {
          this[chartName].dispose()
          this[chartName] = null // 清空引用
        }
      })
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background-color: #f0f2f5;
  height: calc(100vh - 84px);
  overflow-y: auto;
}

.dashboard-header {
  margin-bottom: 20px;
}

.dashboard-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.dashboard-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.card-content {
  display: flex;
  align-items: center;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  transition: all 0.3s ease;
}

.dashboard-card:hover .card-icon {
  transform: scale(1.1);
}

.bg-blue {
  background-color: rgba(64, 158, 255, 0.1);
  color: #409EFF;
}

.bg-green {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

.bg-yellow {
  background-color: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}

.bg-red {
  background-color: rgba(245, 108, 108, 0.1);
  color: #F56C6C;
}

.bg-purple {
  background-color: rgba(160, 122, 204, 0.1);
  color: #A07ACC;
}

.bg-cyan {
  background-color: rgba(82, 196, 26, 0.1);
  color: #52C41A;
}

.bg-orange {
  background-color: rgba(255, 138, 0, 0.1);
  color: #FF8A00;
}

.bg-indigo {
  background-color: rgba(114, 46, 209, 0.1);
  color: #722ED1;
}

.card-info {
  flex: 1;
  text-align: left;
}

.card-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 5px;
}

.card-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.chart-card,
.data-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.chart-card:hover,
.data-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chart-container {
  width: 100%;
  height: 300px;
}
  
  /* 功能模块样式 */
  .modules-section {
    margin-bottom: 20px;
  }
  
  .section-title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 15px;
    position: relative;
    padding-left: 10px;
  }
  
  .section-title::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 16px;
    background: linear-gradient(to bottom, #409EFF, #67C23A);
    border-radius: 2px;
  }
  
  .module-card {
    border-radius: 12px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
    padding: 20px;
    transition: all 0.3s ease;
  }
  
  .module-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  }
  
  .module-header {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
  }
  
  .module-icon {
    font-size: 24px;
    margin-right: 10px;
    color: #409EFF;
    transition: all 0.3s ease;
  }
  
  .module-card:hover .module-icon {
    transform: scale(1.2);
  }
  
  .module-header h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
  
  .module-description {
    margin-bottom: 20px;
  }
  
  .module-description p {
    color: #606266;
    line-height: 1.6;
    margin: 0;
  }
  
  /* 模块操作按钮区域样式 */
  .module-actions {
    display: flex;
    gap: 12px;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .module-actions.improved-actions {
    flex-direction: row;
    gap: 12px;
  }

  .module-actions.center-actions {
    justify-content: center;
  }

  /* 现代化按钮基础样式 */
  .module-button {
    padding: 12px 20px;
    font-size: 14px;
    font-weight: 500;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
    text-decoration: none;
    white-space: nowrap;
    min-width: 120px;
  }

  .module-button::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    transition: left 0.5s ease;
  }

  .module-button:hover::before {
    left: 100%;
  }

  /* 不同类型按钮的渐变背景 */
  .module-button[type="primary"] {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  }

  .module-button[type="primary"]:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
    background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  }

  .module-button[type="success"] {
    background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
    color: #2c3e50;
    box-shadow: 0 4px 15px rgba(132, 250, 176, 0.3);
  }

  .module-button[type="success"]:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(132, 250, 176, 0.4);
    background: linear-gradient(135deg, #76e9a6 0%, #81c9e6 100%);
  }

  .module-button[type="warning"] {
    background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
    color: #8b4513;
    box-shadow: 0 4px 15px rgba(252, 182, 159, 0.3);
  }

  .module-button[type="warning"]:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(252, 182, 159, 0.4);
    background: linear-gradient(135deg, #ffe5c4 0%, #f9ad91 100%);
  }

  .module-button[type="danger"] {
    background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
    color: #8b0000;
    box-shadow: 0 4px 15px rgba(255, 154, 158, 0.3);
  }

  .module-button[type="danger"]:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(255, 154, 158, 0.4);
    background: linear-gradient(135deg, #ff8a8e 0%, #febfe5 100%);
  }

  .module-button[type="info"] {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
    color: #2c3e50;
    box-shadow: 0 4px 15px rgba(168, 237, 234, 0.3);
  }

  .module-button[type="info"]:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(168, 237, 234, 0.4);
    background: linear-gradient(135deg, #9ae5dd 0%, #fecbd8 100%);
  }

  /* 全宽按钮 */
  .module-button.full-width {
    width: 100%;
    min-width: 100%;
    padding: 14px 20px;
    font-size: 15px;
  }

  /* 大型按钮 */
  .module-button.large-button {
    padding: 16px 48px;
    font-size: 18px;
    min-width: 200px;
    border-radius: 12px;
  }

  /* 激活状态 */
  .module-button:active {
    transform: translateY(-1px);
    transition: all 0.1s ease;
  }

  /* 禁用状态 */
  .module-button:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none !important;
  }

  .module-button:disabled:hover {
    transform: none !important;
    box-shadow: inherit !important;
  }

  /* 响应式设计 - 平板横屏 */
  @media (min-width: 769px) and (max-width: 1024px) {
    .module-actions {
      gap: 10px;
    }
    
    .module-button {
      min-width: 100px;
      padding: 10px 16px;
      font-size: 13px;
    }

    .module-button.large-button {
      padding: 14px 32px;
      font-size: 16px;
    }
  }

  /* 响应式设计 - 平板竖屏/大屏手机 */
  @media (max-width: 768px) {
    .module-card {
      margin-bottom: 24px;
      padding: 16px;
    }
    
    .module-actions {
      flex-direction: column;
      gap: 12px;
    }
    
    .module-button {
      width: 100%;
      min-width: 100%;
      padding: 14px 20px;
      font-size: 15px;
    }

    .module-button.large-button {
      padding: 16px 24px;
      font-size: 16px;
    }

    .module-header {
      margin-bottom: 12px;
    }

    .module-icon {
      font-size: 20px;
      margin-right: 8px;
    }

    .module-header h3 {
      font-size: 16px;
    }
  }

  /* 响应式设计 - 手机 */
  @media (max-width: 480px) {
    .module-card {
      margin-bottom: 20px;
      padding: 12px;
    }

    .module-header {
      margin-bottom: 10px;
    }
    
    .module-button {
      padding: 12px 16px;
      font-size: 14px;
      min-width: 100px;
    }
    
    .module-button.large-button {
      padding: 14px 20px;
      font-size: 15px;
    }

    .module-description p {
      font-size: 14px;
      line-height: 1.5;
    }

    .module-icon {
      font-size: 18px;
      margin-right: 6px;
    }

    .button-text {
      font-size: 13px;
      letter-spacing: 0.3px;
    }
  }

  /* 超小屏幕优化 */
  @media (max-width: 320px) {
    .module-card {
      padding: 10px;
    }
    
    .module-button {
      padding: 10px 14px;
      font-size: 13px;
    }
    
    .module-button.large-button {
      padding: 12px 18px;
      font-size: 14px;
    }

    .button-text {
      font-size: 12px;
    }
  }

  /* 横屏手机适配 */
  @media (max-height: 500px) and (orientation: landscape) {
    .welcome-card {
      padding: 10px 0;
    }
    
    .welcome-header {
      padding: 20px 10px;
    }
    
    .welcome-header h1 {
      font-size: 20px;
      margin-bottom: 8px;
    }
    
    .module-card {
      margin-bottom: 16px;
    }
  }

  /* 按钮动画效果 */
  @keyframes buttonPulse {
    0% {
      box-shadow: 0 0 0 0 rgba(102, 126, 234, 0.7);
    }
    70% {
      box-shadow: 0 0 0 10px rgba(102, 126, 234, 0);
    }
    100% {
      box-shadow: 0 0 0 0 rgba(102, 126, 234, 0);
    }
  }

  @keyframes buttonBounce {
    0%, 20%, 53%, 80%, 100% {
      transform: translate3d(0, 0, 0);
    }
    40%, 43% {
      transform: translate3d(0, -8px, 0);
    }
    70% {
      transform: translate3d(0, -4px, 0);
    }
    90% {
      transform: translate3d(0, -2px, 0);
    }
  }

  @keyframes rippleEffect {
    0% {
      transform: scale(0);
      opacity: 1;
    }
    100% {
      transform: scale(2);
      opacity: 0;
    }
  }

  @keyframes loadingSpin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }

  /* 特殊动画效果 */
  .module-button.pulse {
    animation: buttonPulse 2s infinite;
  }

  .module-button.bounce {
    animation: buttonBounce 1s ease-in-out;
  }

  /* 点击波纹效果 */
  .module-button .ripple {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.6);
    transform: scale(0);
    animation: rippleEffect 0.6s linear;
    pointer-events: none;
  }

  /* 图标动画 */
  .module-button .el-icon {
    transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  }

  .module-button:hover .el-icon {
    transform: scale(1.1) rotate(5deg);
  }

  .module-button:active .el-icon {
    transform: scale(0.95);
  }

  /* 加载状态 */
  .module-button.loading {
    pointer-events: none;
    opacity: 0.8;
  }

  .module-button.loading .button-text {
    opacity: 0;
  }

  .module-button.loading::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 20px;
    height: 20px;
    margin: -10px 0 0 -10px;
    border: 2px solid transparent;
    border-top: 2px solid currentColor;
    border-radius: 50%;
    animation: loadingSpin 1s linear infinite;
  }

  /* 按钮聚焦效果 */
  .module-button:focus {
    outline: none;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3);
  }

  /* 按钮组容器 */
  .button-group {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }

  .button-group .module-button {
    flex: 1;
    min-width: auto;
  }

  /* 按钮文字样式 */
  .button-text {
    font-weight: 600;
    letter-spacing: 0.5px;
    transition: all 0.3s ease;
  }

  /* 按钮悬停时的光晕效果 */
  .module-button::after {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
    border-radius: inherit;
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: -1;
  }

  .module-button:hover::after {
    opacity: 1;
  }

  /* 按钮禁用状态增强 */
  .module-button:disabled {
    position: relative;
    overflow: hidden;
  }

  .module-button:disabled::before {
    display: none;
  }

  /* 成功状态 */
  .module-button.success-state {
    background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%) !important;
    color: #2c3e50 !important;
  }

  /* 错误状态 */
  .module-button.error-state {
    background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%) !important;
    color: #8b0000 !important;
  }

  /* 按钮悬停时的上升效果 */
  .module-button:hover {
    transform: translateY(-3px);
  }

  /* 按钮点击时的按压效果 */
  .module-button:active {
    transform: translateY(-1px);
  }

  /* 渐变边框效果 */
  .module-button.gradient-border {
    background: white;
    color: #333;
    border: 2px solid transparent;
    background-clip: padding-box;
    position: relative;
  }

  .module-button.gradient-border::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: inherit;
    z-index: -1;
  }

  /* 毛玻璃效果 */
  .module-button.glass-effect {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: white;
  }

  /* 霓虹灯效果 */
  .module-button.neon-effect {
    background: transparent;
    border: 2px solid #00f5ff;
    color: #00f5ff;
    text-shadow: 0 0 5px #00f5ff;
    box-shadow: 
      0 0 5px #00f5ff,
      0 0 10px #00f5ff,
      0 0 15px #00f5ff;
  }

  .module-button.neon-effect:hover {
    background: rgba(0, 245, 255, 0.1);
    box-shadow: 
      0 0 5px #00f5ff,
      0 0 10px #00f5ff,
      0 0 15px #00f5ff,
      0 0 20px #00f5ff;
  }
  
  /* 欢迎卡片样式 */
  .welcome-card {
    border-radius: 12px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    transition: all 0.3s ease;
  }
  
  .welcome-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.2);
  }
  
  .welcome-header {
    text-align: center;
    padding: 40px 20px;
  }
  
  .welcome-header h1 {
    margin: 0 0 10px 0;
    font-size: 28px;
    font-weight: 600;
  }
  
  .welcome-header p {
    margin: 0;
    font-size: 16px;
    opacity: 0.9;
  }
  
  /* 当前时间样式 */
  .current-time {
    margin-top: 15px;
    font-size: 18px;
    font-weight: 500;
  }
  
  /* 祝福语样式 */
  .greeting {
    margin-top: 10px;
    font-size: 16px;
    font-weight: 500;
    opacity: 0.9;
  }
  
  /* 数据可视化区域样式 */
  .data-visualization-section {
    margin-bottom: 20px;
  }
  
  /* 图表标签页样式 */
  .el-tabs__item {
    font-weight: 500;
  }
  
  .el-tabs__item.is-active {
    font-weight: 600;
    color: #409EFF !important;
  }

  /* 表格容器样式 - 确保占满外部容器 */
  .dashboard-content {
    margin-bottom: 20px;
    height: auto;
    display: flex;
    flex-direction: column;
  }

  .dashboard-content .el-col {
    display: flex;
    flex-direction: column;
    height: 100%;
  }

  .dashboard-content .el-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 100%;
    min-height: 400px; /* 设置最小高度 */
  }

  .dashboard-content .el-card__body {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 0;
    height: calc(100% - 60px); /* 减去头部高度 */
  }

  .dashboard-content .card-header {
    padding: 18px 20px;
    border-bottom: 1px solid #ebeef5;
    flex-shrink: 0;
    background-color: #fafafa;
  }

  /* 表格样式 - 占满容器 */
  .dashboard-content .el-table {
    flex: 1;
    height: 100% !important;
    width: 100% !important;
    max-width: 100% !important;
  }

  .dashboard-content .el-table__header-wrapper {
    background-color: #f5f7fa;
    border-bottom: 2px solid #ebeef5;
  }

  .dashboard-content .el-table__header {
    width: 100% !important;
  }

  .dashboard-content .el-table__body-wrapper {
    flex: 1;
    overflow-y: auto;
    height: calc(100% - 50px) !important; /* 减去表头高度 */
  }

  .dashboard-content .el-table__body {
    width: 100% !important;
  }

  .dashboard-content .el-table__row {
    height: 50px;
  }

  .dashboard-content .el-table__cell {
    padding: 8px 12px;
    vertical-align: middle;
    word-wrap: break-word;
    word-break: break-all;
  }

  /* 确保表格内容列能够自适应宽度 */
  .dashboard-content .el-table-column.content-column {
    min-width: 200px;
    width: auto;
  }

  .dashboard-content .el-table-column.content-column .cell {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 1.5;
  }

  .dashboard-content .el-table-column.content-column:hover .cell {
    white-space: normal;
    overflow: visible;
    text-overflow: initial;
  }

  /* 表格响应式优化 */
  @media (max-width: 768px) {
    .dashboard-content .el-table__body-wrapper {
      height: calc(100% - 45px) !important;
    }

    .dashboard-content .el-table__row {
      height: 45px;
    }

    .dashboard-content .el-table__cell {
      padding: 6px 8px;
      font-size: 14px;
    }

    .dashboard-content .card-header {
      padding: 15px;
      font-size: 16px;
    }
  }

  /* 表格滚动条样式 */
  .dashboard-content .el-table__body-wrapper::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }

  .dashboard-content .el-table__body-wrapper::-webkit-scrollbar-thumb {
    background-color: #c0c4cc;
    border-radius: 4px;
  }

  .dashboard-content .el-table__body-wrapper::-webkit-scrollbar-thumb:hover {
    background-color: #909399;
  }

  .dashboard-content .el-table__body-wrapper::-webkit-scrollbar-track {
    background-color: #f5f7fa;
  }
.update-time {
  font-size: 12px;
  font-weight: normal;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>