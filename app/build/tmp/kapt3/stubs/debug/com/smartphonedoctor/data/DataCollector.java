package com.smartphonedoctor.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0086@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0006H\u0086@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0006H\u0086@\u00a2\u0006\u0002\u0010\bJ\u001a\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u0006H\u0086@\u00a2\u0006\u0002\u0010\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/smartphonedoctor/data/DataCollector;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "collectActivityInfo", "Lcom/smartphonedoctor/domain/model/Result;", "Lcom/smartphonedoctor/domain/model/ActivityInfo;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "collectBatteryInfo", "Lcom/smartphonedoctor/domain/model/BatteryInfo;", "collectStorageStats", "Lcom/smartphonedoctor/domain/model/DeviceStorageInfo;", "collectUsageStats", "", "Lcom/smartphonedoctor/domain/model/AppUsageStat;", "app_debug"})
public final class DataCollector {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    
    public DataCollector(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object collectBatteryInfo(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.smartphonedoctor.domain.model.Result<com.smartphonedoctor.domain.model.BatteryInfo>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object collectUsageStats(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.smartphonedoctor.domain.model.Result<? extends java.util.List<com.smartphonedoctor.domain.model.AppUsageStat>>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object collectStorageStats(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.smartphonedoctor.domain.model.Result<com.smartphonedoctor.domain.model.DeviceStorageInfo>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object collectActivityInfo(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.smartphonedoctor.domain.model.Result<com.smartphonedoctor.domain.model.ActivityInfo>> $completion) {
        return null;
    }
}