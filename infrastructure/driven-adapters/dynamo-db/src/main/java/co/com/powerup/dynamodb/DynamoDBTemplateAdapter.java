package co.com.powerup.dynamodb;

import co.com.powerup.dynamodb.entities.ReportTotalizedRequestsEntity;
import co.com.powerup.dynamodb.helper.TemplateAdapterOperations;
import co.com.powerup.model.reporttotalizedrequests.ReportTotalizedRequests;
import co.com.powerup.model.reporttotalizedrequests.gateways.ReportTotalizedRequestsRepository;
import lombok.extern.slf4j.Slf4j;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;

@Slf4j
@Repository
public class DynamoDBTemplateAdapter extends TemplateAdapterOperations<ReportTotalizedRequests /*domain model*/, String, ReportTotalizedRequestsEntity /*adapter model*/> implements ReportTotalizedRequestsRepository/* implements Gateway from domain */ {

    public DynamoDBTemplateAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(connectionFactory, mapper, d -> mapper.map(d, ReportTotalizedRequests.class /*domain model*/), "reports_totalized_requests","idx_emailUserGenerated" /*index is optional*/);
    }

    public Mono<List<ReportTotalizedRequests /*domain model*/>> getEntityBySomeKeys(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return query(queryExpression);
    }

    public Mono<List<ReportTotalizedRequests /*domain model*/>> getEntityBySomeKeysByIndex(String partitionKey, String sortKey) {
        QueryEnhancedRequest queryExpression = generateQueryExpression(partitionKey, sortKey);
        return queryByIndex(queryExpression, "idx_emailUserGenerated" /*index is optional if you define in constructor*/);
    }

    private QueryEnhancedRequest generateQueryExpression(String partitionKey, String sortKey) {
        return QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(partitionKey).build()))
                .queryConditional(QueryConditional.sortGreaterThanOrEqualTo(Key.builder().sortValue(sortKey).build()))
                .build();
    }

    @Override
    public Mono<ReportTotalizedRequests> save(ReportTotalizedRequests reportTotalizedRequests) {
        log.info("➡️ Entró al repository save() de ReportTotalizedRequestsRepository");
        return Mono.just(reportTotalizedRequests);
    }
}
