package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- Users ---
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Business Profiles ---
    @Query("SELECT * FROM business_profiles WHERE userId = :userId LIMIT 1")
    fun getBusinessProfileFlow(userId: Long): Flow<BusinessProfileEntity?>

    @Query("SELECT * FROM business_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getBusinessProfile(userId: Long): BusinessProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinessProfile(profile: BusinessProfileEntity): Long

    @Update
    suspend fun updateBusinessProfile(profile: BusinessProfileEntity)

    // --- Monthly Metrics ---
    @Query("SELECT * FROM monthly_metrics WHERE businessId = :businessId ORDER BY monthIndex ASC")
    fun getMonthlyMetricsFlow(businessId: Long): Flow<List<MonthlyMetricEntity>>

    @Query("SELECT * FROM monthly_metrics WHERE businessId = :businessId ORDER BY monthIndex ASC")
    suspend fun getMonthlyMetricsList(businessId: Long): List<MonthlyMetricEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyMetric(metric: MonthlyMetricEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMonthlyMetrics(metrics: List<MonthlyMetricEntity>)

    @Query("DELETE FROM monthly_metrics WHERE businessId = :businessId")
    suspend fun clearMonthlyMetrics(businessId: Long)

    // --- Product Performance ---
    @Query("SELECT * FROM product_performances WHERE businessId = :businessId ORDER BY revenue DESC")
    fun getProductsFlow(businessId: Long): Flow<List<ProductPerformanceEntity>>

    @Query("SELECT * FROM product_performances WHERE businessId = :businessId ORDER BY revenue DESC")
    suspend fun getProductsList(businessId: Long): List<ProductPerformanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductPerformanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<ProductPerformanceEntity>)

    @Query("DELETE FROM product_performances WHERE businessId = :businessId")
    suspend fun clearProducts(businessId: Long)

    // --- Customer Segments ---
    @Query("SELECT * FROM customer_segments WHERE businessId = :businessId ORDER BY customerCount DESC")
    fun getSegmentsFlow(businessId: Long): Flow<List<CustomerSegmentEntity>>

    @Query("SELECT * FROM customer_segments WHERE businessId = :businessId ORDER BY customerCount DESC")
    suspend fun getSegmentsList(businessId: Long): List<CustomerSegmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSegments(segments: List<CustomerSegmentEntity>)

    @Query("DELETE FROM customer_segments WHERE businessId = :businessId")
    suspend fun clearSegments(businessId: Long)

    // --- Marketing Channels ---
    @Query("SELECT * FROM marketing_channels WHERE businessId = :businessId ORDER BY roas DESC")
    fun getChannelsFlow(businessId: Long): Flow<List<MarketingChannelEntity>>

    @Query("SELECT * FROM marketing_channels WHERE businessId = :businessId ORDER BY roas DESC")
    suspend fun getChannelsList(businessId: Long): List<MarketingChannelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: MarketingChannelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChannels(channels: List<MarketingChannelEntity>)

    @Query("DELETE FROM marketing_channels WHERE businessId = :businessId")
    suspend fun clearChannels(businessId: Long)

    // --- Recommendations ---
    @Query("SELECT * FROM recommendations WHERE businessId = :businessId ORDER BY createdTimestamp DESC")
    fun getRecommendationsFlow(businessId: Long): Flow<List<BusinessRecommendationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendation(rec: BusinessRecommendationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRecommendations(recs: List<BusinessRecommendationEntity>)

    @Query("UPDATE recommendations SET status = :newStatus WHERE id = :id")
    suspend fun updateRecommendationStatus(id: Long, newStatus: String)

    @Query("DELETE FROM recommendations WHERE businessId = :businessId")
    suspend fun clearRecommendations(businessId: Long)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages WHERE businessId = :businessId ORDER BY timestamp ASC")
    fun getChatMessagesFlow(businessId: Long): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(msg: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE businessId = :businessId")
    suspend fun clearChatMessages(businessId: Long)
}
