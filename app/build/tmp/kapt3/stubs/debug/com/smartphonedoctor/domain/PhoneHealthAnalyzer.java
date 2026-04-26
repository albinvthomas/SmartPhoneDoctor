package com.smartphonedoctor.domain;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004JD\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0012\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\b2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/smartphonedoctor/domain/PhoneHealthAnalyzer;", "", "healthScoreCalculator", "Lcom/smartphonedoctor/domain/HealthScoreCalculator;", "(Lcom/smartphonedoctor/domain/HealthScoreCalculator;)V", "analyze", "Lcom/smartphonedoctor/domain/model/ScanResult;", "batteryInfo", "Lcom/smartphonedoctor/domain/model/Result;", "Lcom/smartphonedoctor/domain/model/BatteryInfo;", "usageStats", "", "Lcom/smartphonedoctor/domain/model/AppUsageStat;", "storageInfo", "Lcom/smartphonedoctor/domain/model/DeviceStorageInfo;", "activityInfo", "Lcom/smartphonedoctor/domain/model/ActivityInfo;", "app_debug"})
public final class PhoneHealthAnalyzer {
    @org.jetbrains.annotations.NotNull()
    private final com.smartphonedoctor.domain.HealthScoreCalculator healthScoreCalculator = null;
    
    public PhoneHealthAnalyzer(@org.jetbrains.annotations.NotNull()
    com.smartphonedoctor.domain.HealthScoreCalculator healthScoreCalculator) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.smartphonedoctor.domain.model.ScanResult analyze(@org.jetbrains.annotations.NotNull()
    com.smartphonedoctor.domain.model.Result<com.smartphonedoctor.domain.model.BatteryInfo> batteryInfo, @org.jetbrains.annotations.NotNull()
    com.smartphonedoctor.domain.model.Result<? extends java.util.List<com.smartphonedoctor.domain.model.AppUsageStat>> usageStats, @org.jetbrains.annotations.NotNull()
    com.smartphonedoctor.domain.model.Result<com.smartphonedoctor.domain.model.DeviceStorageInfo> storageInfo, @org.jetbrains.annotations.NotNull()
    com.smartphonedoctor.domain.model.Result<com.smartphonedoctor.domain.model.ActivityInfo> activityInfo) {
        return null;
    }
}