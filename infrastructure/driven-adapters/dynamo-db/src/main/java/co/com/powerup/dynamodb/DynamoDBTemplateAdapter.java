package co.com.powerup.dynamodb;

import co.com.powerup.dynamodb.entities.ReportTotalizedRequestsEntity;
import co.com.powerup.dynamodb.helper.TemplateAdapterOperations;
import co.com.powerup.model.reporttotalizedrequests.ReportTotalizedRequests;
import co.com.powerup.model.reporttotalizedrequests.gateways.ReportTotalizedRequestsRepository;
import lombok.extern.slf4j.Slf4j;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;

@Slf4j
@Repository
public class DynamoDBTemplateAdapter extends TemplateAdapterOperations<ReportTotalizedRequests, String, ReportTotalizedRequestsEntity> implements ReportTotalizedRequestsRepository {
    

    public DynamoDBTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper, 
                @Value("${aws.dynamodb.tables.reports}") String tableNameReports) {
        super(connectionFactory, mapper, d -> mapper.map(d, ReportTotalizedRequests.class), tableNameReports,"idx_emailUserGenerated");
    }

    public Mono<List<ReportTotalizedRequests>> getEntityBySomeKeys(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return query(queryExpression);
    }

    public Mono<List<ReportTotalizedRequests>> getEntityBySomeKeysByIndex(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return queryByIndex(queryExpression, "idx_emailUserGenerated");
    }

    private QueryEnhancedRequest generateQueryExpression(String partitionKey, String sortKey) {
        return QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(partitionKey).build()))
                .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(Key.builder().sortValue(sortKey).build()))
                .build();
    }

    @Override
    public Mono<ReportTotalizedRequests> save(ReportTotalizedRequests reportTotalizedRequests) {
        log.info("Intentando guardar ReportTotalizedRequests generado por: {}", reportTotalizedRequests.getUserGenerated());
        return super.save(reportTotalizedRequests)
                .doOnSuccess(saved -> log.info("Guardado exitoso en DynamoDB"))
                .doOnError(error -> log.error("Error al guardar en DynamoDB: {}", error.getMessage(), error));
    }
}
