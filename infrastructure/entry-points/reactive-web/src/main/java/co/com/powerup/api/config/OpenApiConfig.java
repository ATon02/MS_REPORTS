package co.com.powerup.api.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import co.com.powerup.model.reporttotalizedrequests.ReportTotalizedRequests;

@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi publicApi(OpenApiCustomizer customizer) {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(customizer)
                .build();
    }

    @Bean
    @Primary
    public OpenApiCustomizer customizer() {
        return openApi -> {
            openApi.getComponents()
                    .addSecuritySchemes("bearerAuth",
                            new SecurityScheme()
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT"));
            openApi.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        
            PathItem requestTotal = new PathItem()
                    .get(new Operation()
                            .operationId("totalRequestsByStatus")
                            .tags(List.of("RequestClient"))
                            .summary("Obtiene el totalizado de solicitudes en un estado ya sea en cantidad o monto")
                            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                            .addParametersItem(new QueryParameter()
                                    .name("type")
                                    .description("Tipo de total a calcular (APPROVED_REQUESTS o APPROVED_AMOUNT)")
                                    .required(true)
                                    .schema(new StringSchema()
                                        ._enum(List.of("APPROVED_REQUESTS", "APPROVED_AMOUNT"))))
                            .addParametersItem(new QueryParameter()
                                    .name("statusId")
                                    .description("ID del estado a buscar")
                                    .required(true)
                                    .schema(new IntegerSchema()))
                            .responses(new ApiResponses()
                                    .addApiResponse("200", new ApiResponse()
                                            .description("Totales de solicitudes de clientes")
                                            .content(new Content()
                                                    .addMediaType("application/json",
                                                            new io.swagger.v3.oas.models.media.MediaType()
                                                                    .schema(new ArraySchema().items(
                                                                            new Schema<>().$ref(
                                                                                    "#/components/schemas/ResponseDataTotal"))))))));
            openApi.path("/api/v1/reports", requestTotal);
            

            openApi.getComponents()
                    .addSchemas("ResponseDataTotal", new Schema<ReportTotalizedRequests>()
                            .addProperty("status", new StringSchema())
                            .addProperty("type", new StringSchema())
                            .addProperty("value", new NumberSchema().format("double")));
        };
    }

}
