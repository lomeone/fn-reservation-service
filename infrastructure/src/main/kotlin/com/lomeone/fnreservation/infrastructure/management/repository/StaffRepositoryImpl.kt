package com.lomeone.fnreservation.infrastructure.management.repository

import com.lomeone.fnreservation.domain.management.entity.Staff
import com.lomeone.fnreservation.domain.management.entity.StaffStatus
import com.lomeone.fnreservation.domain.management.repository.StaffRepository
import com.lomeone.fnreservation.infrastructure.management.exception.DynamoStaffPutItemNotFoundException
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.ZonedDateTime

@Repository
class StaffRepositoryImpl(
    dynamoDbEnhancedClient: DynamoDbEnhancedClient
) : StaffRepository {

    private val table = dynamoDbEnhancedClient.table("fn-staffs", TableSchema.fromBean(StaffDynamo::class.java))

    override fun save(staff: Staff): Staff {
        return if (isAlreadyExistStaff(staff)) {
            table.updateItem(StaffDynamo(staff)).toStaff()
        } else {
            table.putItem(StaffDynamo(staff))
            this.findById(staff.id) ?: throw DynamoStaffPutItemNotFoundException(
                detail = mapOf(
                    "id" to staff.id,
                    "storeBranch" to staff.storeBranch,
                    "name" to staff.name
                )
            )
        }
    }

    private fun isAlreadyExistStaff(staff: Staff) = findById(staff.id) != null

    override fun findById(id: String): Staff? =
        table.getItem(Key.builder().partitionValue(id).build())?.toStaff()

    override fun findByStoreBranchAndStatus(storeBranch: String, status: StaffStatus): List<Staff> {
        val items = table.scan(
            ScanEnhancedRequest.builder()
                .filterExpression(
                    Expression.builder()
                        .expression("storeBranch = :storeBranch AND #status = :status")
                        .putExpressionValue(":storeBranch", AttributeValue.fromS(storeBranch))
                        .putExpressionName("#status", "status")
                        .putExpressionValue(":status", AttributeValue.fromS(status.name))
                        .build()
                ).build()
        ).items()

        return items.map { it.toStaff() }
    }

    override fun findByStoreBranchAndName(storeBranch: String, name: String): Staff? {
        val items = table.scan(
            ScanEnhancedRequest.builder()
                .filterExpression(
                    Expression.builder()
                        .expression("storeBranch = :storeBranch AND #name = :name")
                        .putExpressionValue(":storeBranch", AttributeValue.fromS(storeBranch))
                        .putExpressionName("#name", "name")
                        .putExpressionValue(":name", AttributeValue.fromS(name))
                        .build()
                ).build()
        ).items()

        return items.firstOrNull()?.toStaff()
    }
}

@DynamoDbBean
class StaffDynamo(
    @get:DynamoDbPartitionKey
    var staff_id: String,
    var storeBranch: String,
    var name: String,
    var status: StaffStatus,
    var createdAt: ZonedDateTime,
    var updatedAt: ZonedDateTime
) {
    constructor() : this(
        staff_id = "",
        storeBranch = "",
        name = "",
        status = StaffStatus.ACTIVE,
        createdAt = ZonedDateTime.now(),
        updatedAt = ZonedDateTime.now()
    )

    constructor(staff: Staff) : this(
        staff_id = staff.id,
        storeBranch = staff.storeBranch,
        name = staff.name,
        status = staff.status,
        createdAt = staff.createdAt,
        updatedAt = ZonedDateTime.now()
    )

    fun toStaff(): Staff =
        Staff(
            id = this.staff_id,
            storeBranch = this.storeBranch,
            name = this.name,
            status = this.status,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
}
