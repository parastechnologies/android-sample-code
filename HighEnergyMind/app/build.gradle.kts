plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    kotlin("kapt")
}

android {
    namespace = "com.highenergymind"
    compileSdk = 34


    lint {
        // Turns off checks for the issue IDs you specify.
        disable += "TypographyFractions" + "TypographyQuotes"
        // Turns on checks for the issue IDs you specify. These checks are in
        // addition to the default lint checks.
        enable += "RtlHardcoded" + "RtlCompat" + "RtlEnabled"
        // To enable checks for only a subset of issue IDs and ignore all others,
        // list the issue IDs with the 'check' property instead. This property overrides
        // any issue IDs you enable or disable using the properties above.
        checkOnly += "NewApi" + "InlinedApi"
        // If set to true, turns off analysis progress reporting by lint.
        quiet = true
        // If set to true (default), stops the build if errors are found.
        abortOnError = false
        // If set to true, lint only reports errors.
        ignoreWarnings = true
        // If set to true, lint also checks all dependencies as part of its analysis.
        // Recommended for projects consisting of an app with library dependencies.
        checkDependencies = true
    }
    defaultConfig {
        applicationId = "com.highenergymind"
        minSdk = 24
        targetSdk = 34
        versionCode = 9
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    dataBinding {
        enable = true
    }

    bundle {
        language {
            enableSplit = false
        }
    }


    signingConfigs {
        create("release") {
            storeFile = File(".jks")
            storePassword = ""
            keyAlias = ""
            keyPassword = ""
        }
    }

    android.buildFeatures.buildConfig = true
    buildTypes {
        release {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"BASE URL\""
            )


            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

        }
        debug {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"BASE URL\""
            )

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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    /** sdp /ssp lib. */
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")

    /** view pager*/
    implementation("com.tbuonomo:dotsindicator:5.0")

    // todo  Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")
    //  Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
    //  Navigation
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("com.hbb20:ccp:2.7.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    implementation("androidx.viewpager2:viewpager2:1.0.0")

    /** lotie animation */
    implementation("com.airbnb.android:lottie:5.2.0")
    /// flex layout 
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.github.worker8:radiogroupplus:1.0.1")
    // circular Image
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.android.material:material:1.11.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("com.github.zetbaitsu:Compressor:3.0.1")
    implementation("com.github.bfrachia:android-image-cropper:2.7.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    val media3Version = "1.3.0"

    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-exoplayer-dash:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    implementation("androidx.media3:media3-session:$media3Version")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    /** Revenue cat dependency **/
    implementation("com.revenuecat.purchases:purchases:7.0.0")
    implementation ("com.revenuecat.purchases:purchases-ui:7.1.0")

    val billing_version = "6.2.0"

    implementation("com.android.billingclient:billing-ktx:$billing_version")

    implementation("com.appsflyer:af-android-sdk:6.14.0")
    implementation("com.android.installreferrer:installreferrer:2.2")

    val paging_version = "3.3.0"

    implementation("androidx.paging:paging-runtime:$paging_version")
    implementation("com.facebook.shimmer:shimmer:0.5.0")


}