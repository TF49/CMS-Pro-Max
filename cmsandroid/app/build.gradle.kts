plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cms_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cms_android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // 添加网络请求库
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // 添加JSON解析库
    implementation("com.google.code.gson:gson:2.10.1")
    
    // 添加RecyclerView依赖
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // 添加卡片视图依赖
    implementation("androidx.cardview:cardview:1.0.0")
    
    // 添加下拉刷新依赖
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    // 添加WebSocket客户端库
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}